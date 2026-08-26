/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.nlp;

import opennlp.tools.util.Span;

/** A text shingle (contiguous token span) used by the PRIMA NLP annotator. */
public class PrimaShingle {
    private final Span span;
    private final int size;

    /**
     * Constructs a PrimaShingle.
     *
     * @param span the token span covered by this shingle
     * @param size the number of tokens in this shingle
     */
    public PrimaShingle(Span span, int size) {
        this.span = span;
        this.size = size;
    }

    /**
     * Returns the number of tokens in this shingle.
     *
     * @return the shingle size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the token span covered by this shingle.
     *
     * @return the span
     */
    public Span getSpan() {
        return span;
    }
}

