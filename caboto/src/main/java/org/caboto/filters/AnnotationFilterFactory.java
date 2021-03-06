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

package org.caboto.filters;

import com.hp.hpl.jena.query.Query;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author pldms
 */
public class AnnotationFilterFactory {
    public static AnnotationFilter[]
            getFromParameters(Map<String, List<String>> parameters) {
        final List<AnnotationFilter> filters = new LinkedList();
        for (Entry<String, List<String>> attVal: parameters.entrySet()) {
        	if (attVal.getKey().equalsIgnoreCase("search")) {
        		for (String value: attVal.getValue()) {
        			filters.add(new LarqAnnotationFilter(value));
        		}
        	}
                else if (attVal.getKey().equalsIgnoreCase("type")) {
        		for (String value: attVal.getValue()) {
        			filters.add(new TypeAnnotationFilter(value));
        		}
        	}
                else if (attVal.getKey().equalsIgnoreCase("from")) {
        		for (String value: attVal.getValue()) {
                            if(value != null && value.trim().length() > 0)
        			filters.add(new DateAnnotationFilter(value, true));
        		}
        	}
                else if (attVal.getKey().equalsIgnoreCase("to")) {
        		for (String value: attVal.getValue()) {
                            if(value != null && value.trim().length() > 0)
        			filters.add(new DateAnnotationFilter(value, false));
        		}
        	}
        	else if (attVal.getKey().contains(":")) {
                for (String value: attVal.getValue()) {
                    filters.add(new PropValAnnotationFilter(attVal.getKey(),
                            value));
                }
            }
        }
         // life is too short to understand why this nonsense is needed
        return filters.toArray(new AnnotationFilter[0]);
    }

    public static void applyFilters(Query original,
            String annotationBodyVar, String annotationHeadVar, AnnotationFilter... filters) {
        for (AnnotationFilter filter: filters)
            filter.augmentQuery(original, annotationBodyVar, annotationHeadVar);
    }
}
