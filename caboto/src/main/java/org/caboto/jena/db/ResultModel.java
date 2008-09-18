/*
 * @(#)ResultModel.java
 * Created: 12 Sep 2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package org.caboto.jena.db;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * A utility class for passing the results so that the execution can be closed
 * later
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ResultModel {

    private Model model = null;

    private QueryExecution queryExecution = null;

    private Data data = null;

    /**
     * Creates a new ResultModel
     * @param model The model containing the results
     * @param queryExecution The query execution
     * @param data The data queried
     */
    protected ResultModel(Model model, QueryExecution queryExecution,
            Data data) {
        this.model = model;
        this.queryExecution = queryExecution;
        this.data = data;
    }

    /**
     * Returns the results
     * @return The result model
     */
    public Model getResults() {
        return model;
    }

    /**
     * Frees resources
     */
    public void close() {
        queryExecution.close();
        data.close();
    }

    /**
     *
     * @see java.lang.Object#finalize()
     */
    protected void finalize() {
        close();
    }
}
