/*
 * Copyright (c) 2008, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.caboto.validation;

import org.caboto.domain.Annotation;
import org.caboto.profile.Profile;
import org.caboto.profile.ProfileEntry;
import org.caboto.profile.ProfileRepository;
import org.caboto.profile.ProfileRepositoryException;
import org.caboto.CabotoUtility;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AnnotationValidatorImpl.java 181 2008-05-30 13:54:34Z mike.a.jones $
 */
public final class AnnotationValidatorImpl implements Validator {

    public AnnotationValidatorImpl(final ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public boolean supports(final Class aClass) {
        return Annotation.class.equals(aClass);
    }

    public void validate(final Object o, final Errors errors) {

        try {

            // get the object we need to validate
            Annotation annotation = (Annotation) o;

            // only proceed if we have a chance of determining the type
            if (determinableType(annotation, errors)) {

                Profile profile = profileRepository.findProfile(annotation.getType());

                // have we found the profile
                if (profile == null) {

                    errors.reject("annotation.type.unkown", new String[]{annotation.getType()}, "");

                } else if (!expectedGraphType(annotation.getGraphId())) { // not public or private?

                    errors.reject("annotation.graph.unexpected");

                } else {

                    // prototype appends a _method to the parameter - we can ignore this
                    if (annotation.getBody().get("_method") != null) {
                        annotation.getBody().remove("_method");
                    }

                    if (annotation.getBody().get("_") != null) {
                        annotation.getBody().remove("_");
                    }

                    // --- Validate values that are provided by the REST interface

                    // (1) check that we have an author

                    if (annotation.getAuthor() == null || annotation.getAuthor().length() == 0) {
                        errors.rejectValue("body", "annotation.author", "");
                    }

                    if (annotation.getAnnotates() == null || annotation.getAnnotates().length() == 0) {
                        errors.rejectValue("body", "annotation.annotates", "");
                    }

                    // --- Validate the values body Map

                    // (1) check that we have the expected number of entries in the Map

                    if (annotation.getBody().size() != profile.getProfileEntries().size()) {
                        errors.rejectValue("body", "annotation.body.missmatch",
                                new Integer[]{profile.getProfileEntries().size(),
                                        annotation.getBody().size()}, "");
                    }

                    // (2) check that the keys sent in the Map match the ids in the profile entries


                    Set<String> bodyKeys = annotation.getBody().keySet();

                    //for (String key : bodyKeys) {
                    //    System.out.println("> " + key);
                    //}

                    //System.out.println("<<>>: " + annotation.getBody().get("_"));

                    Set<String> bodyKeysCopy = new HashSet<String>(); // make a deep copy of the key set
                    for (String key : bodyKeys) {
                        bodyKeysCopy.add(key);
                    }

                    Set<String> profileKeys = new HashSet<String>(); // create a set of profile entry Ids

                    List<ProfileEntry> profiles = profile.getProfileEntries();

                    for (ProfileEntry profileEntry : profiles) {
                        profileKeys.add(profileEntry.getId());
                    }

                    bodyKeysCopy.removeAll(profileKeys); // remainder keys are unexpected

                    if (bodyKeysCopy.size() > 0) { // generate error messages

                        StringBuilder msg = new StringBuilder();
                        for (String val : bodyKeysCopy) {
                            msg.append(val).append("; ");
                        }

                        errors.rejectValue("body", "annotation.body.unexpectedVals",
                                new String[]{msg.toString()}, "");
                    }

                    // (3) check that we have required values where specified in the profile entries
                    for (ProfileEntry entry : profiles) {

                        String bodyValue = annotation.getBody().get(entry.getId());

                        if (entry.isRequired()) {

                            if (bodyValue == null || bodyValue.length() == 0) {
                                errors.rejectValue("body", "annotation.body.missingRequiredVal",
                                        new String[]{entry.getId()}, "");
                            }
                        }
                    }
                }
            }

        } catch (ProfileRepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether or not the type is null or empty - usually caused by garbage being sent.
     * If we don't have a type we can look for a profile to validate against.
     *
     * @param annotation annotation object representing the request
     * @param errors     the error object to add errors
     * @return whether or not there is a type we can search on
     */
    private boolean determinableType(Annotation annotation, Errors errors) {

        if (annotation.getType() == null || annotation.getType().equals("")) {
            errors.reject("annotation.type.indeterminable");
            return false;
        }
        return true;
    }

    /**
     * Checks that we have the correct graph type - it should be public or private.
     *
     * @param graphUri the graph that needs to be checked.
     * @return whether or not we are dealing with an expected graph type.
     */
    private boolean expectedGraphType(String graphUri) {

        return CabotoUtility.isPublicResource(graphUri) ||
                CabotoUtility.isPrivateResource(graphUri);
    }


    private ProfileRepository profileRepository;
}
