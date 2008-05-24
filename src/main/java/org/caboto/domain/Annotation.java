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
package org.caboto.domain;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 *
 **/
public class Annotation {


    public Annotation() {
    }

    public Annotation(String id, String graphId, String annotates, String author,
                      Date created, Map<String, String> body, String type) {
        this.id = id;
        this.graphId = graphId;
        this.annotates = annotates;
        this.author = author;
        this.created = created;
        this.body = body;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public String getAnnotates() {
        return annotates;
    }

    public void setAnnotates(String annotates) {
        this.annotates = annotates;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return new StringBuffer("[id: ").append(id).append(";\n")
                .append("graphId: ").append(graphId).append(";\n")
                .append("annotates: ").append(annotates).append(";\n")
                .append("author: ").append(author).append(";\n")
                .append("created: ").append(created.toString()).append(";\n")
                .append("type: ").append(type).append(";\n")
                .append("body: ").append(body.toString())
                .append("]").toString();
    }


    private String id;
    private String graphId;
    private String annotates;
    private String author;
    private Date created;
    private Map<String, String> body = new HashMap<String, String>();
    private String type = "";
}
