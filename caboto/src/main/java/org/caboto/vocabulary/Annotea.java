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
package org.caboto.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Annotea {

    private static Model m_model = ModelFactory.createDefaultModel();

    public static final String NS = "http://www.w3.org/2000/10/annotation-ns#";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = m_model.createResource(NS);

    public static final Property annotates =
            m_model.createProperty("http://www.w3.org/2000/10/annotation-ns#annotates");

    public static final Property author =
            m_model.createProperty("http://www.w3.org/2000/10/annotation-ns#author");

    public static final Property body =
            m_model.createProperty("http://www.w3.org/2000/10/annotation-ns#body");

    public static final Property context =
            m_model.createProperty("http://www.w3.org/2000/10/annotation-ns#context");

    public static final Property created =
            m_model.createProperty("http://www.w3.org/2000/10/annotation-ns#created");

    public static final Property modified =
            m_model.createProperty("http://www.w3.org/2000/10/annotation-ns#modified");

    public static final Property related =
            m_model.createProperty("http://www.w3.org/2000/10/annotation-ns#related");

}
