/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
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
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.caboto.domain.Annotation;
import org.caboto.profile.MockProfileRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AnnotationValidationTest.java 122 2008-05-19 14:32:05Z mike.a.jones $
 */
public class AnnotationValidationTest extends TestCase {

    @Before
    public void setUp() {

        // instantiate validator
        validator = new AnnotationValidatorImpl(new MockProfileRepositoryImpl());

        // instantiate an annotation
        annotation = new Annotation();
        annotation.setType("SimpleComment");
        annotation.setAuthor("http://caboto.org/person/MikeJ/");
        annotation.setAnnotates("http://example.org/thing");
        annotation.setGraphId("http://caboto.org/person/MikeJ/public/");

        // the body of the annotation
        Map<String, List<String>> body = new HashMap<String, List<String>>();
        body.put("title", new ArrayList<String>());
        body.get("title").add("This is a test annotation");
        body.put("description", new ArrayList<String>());
        body.get("description").add("Lorem ipsum dolor sit amet, consectetur adipisicing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        annotation.setBody(body);

        // instantiate the errors interface
        errors = new BeanPropertyBindingResult(annotation, "Annotation");

    }

    @Test
    public void testSupportsClass() {
        assertTrue("Class not supported by validator",
                validator.supports(Annotation.class));
    }

    @Test
    public void testPublicGraph() {

        validator.validate(annotation, errors);

        assertEquals("There should be no errors", 0, errors.getFieldErrorCount());
    }

    @Test
    public void testPrivateGraph() {
        
        validator.validate(annotation, errors);

        assertEquals("There should be no errors", 0, errors.getFieldErrorCount());
    }

    @Test
    public void testIncorrectGraph() {

        annotation.setGraphId("http://caboto.org/person/MikeJ/puublicc/");

        validator.validate(annotation, errors);

        assertEquals("There should be 1 error", 1, errors.getErrorCount());

        ObjectError error = errors.getGlobalError();

        assertEquals("Unexpected error code returned", "annotation.graph.unexpected",
                error.getCode());
    }

    @Test
    public void testIncorrectType() {

        // change the type to something unsupported
        annotation.setType("WibbleAnnotation");

        validator.validate(annotation, errors);

        assertEquals("There should only be one error", 1, errors.getErrorCount());

        ObjectError error = errors.getGlobalError();

        assertEquals("Unexpected error code returned", "annotation.type.unkown", error.getCode());

    }

    @Test
    public void testMissingAuthor() {

        annotation.setAuthor("");

        validator.validate(annotation, errors);

        List errorList = errors.getFieldErrors("body");

        assertEquals("There should be one error", 1, errorList.size());

        FieldError fieldError = (FieldError) errorList.get(0);

        assertEquals("Unexpected error code returned", "annotation.author",
                fieldError.getCode());

    }

    @Test
    public void testMissingAnnotates() {

        annotation.setAnnotates("");

        validator.validate(annotation, errors);

        List errorList = errors.getFieldErrors("body");

        assertEquals("There should be one error", 1, errorList.size());

        FieldError fieldError = (FieldError) errorList.get(0);

        assertEquals("Unexpected error code returned", "annotation.annotates",
                fieldError.getCode());

    }

    @Test
    public void testIncorrectBody() {

    	annotation.getBody().put("extraField", new ArrayList<String>());
        annotation.getBody().get("extraField").add("Some value or other");

        validator.validate(annotation, errors);

        List errorList = errors.getFieldErrors("body");

        // there are two errors - an incorrect number of keys and
        // keys not found in the annotation profile
//        assertEquals("There should be two errors", 2, errorList.size());
        assertEquals("There should be one error", 1, errorList.size());

        FieldError fieldError = (FieldError) errorList.get(0);

//        assertEquals("Unexpected error code returned", "annotation.body.missmatch",
//                fieldError.getCode());

//        FieldError fieldError2 = (FieldError) errorList.get(1);

        assertEquals("Unexpected error code returned", "annotation.body.unexpectedVals",
//                fieldError2.getCode());
                fieldError.getCode());
    }

    @Test
    public void testMissingBodyValue() {

    	annotation.getBody().put("title", new ArrayList<String>());
        annotation.getBody().get("title").add("");

        validator.validate(annotation, errors);

        List errorList = errors.getFieldErrors("body");

        assertEquals("There should be one error", 1, errorList.size());

        FieldError fieldError = (FieldError) errorList.get(0);

        assertEquals("Unexpected error code returned", "annotation.body.missingRequiredVal",
                fieldError.getCode());

    }


    private AnnotationValidatorImpl validator;
    private Annotation annotation;
    private Errors errors;

}
