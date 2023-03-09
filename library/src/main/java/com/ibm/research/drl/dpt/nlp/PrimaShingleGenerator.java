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

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PrimaShingleGenerator {
    private final int min_shingle_size;
    private final int max_shingle_size;

    public PrimaShingleGenerator(int min_shingle_size, int max_shingle_size) {
        this.min_shingle_size = min_shingle_size;
        this.max_shingle_size = max_shingle_size;
    }

    public Iterable<PrimaShingle> generate(Span[] spans) {
        return new Iterable<>() {
            int start = 0;
            int end = 0;

            @Override
            public Iterator<PrimaShingle> iterator() {
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return start < spans.length;
                    }

                    @Override
                    public PrimaShingle next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        Span startSpan = spans[start];
                        Span endSpan = spans[end++];

                        Span span = new Span(startSpan.getStart(), endSpan.getEnd(), startSpan.getType(), startSpan.getEnd());
                        int size = end - start + 1;

                        if ((end - start) > max_shingle_size || end == spans.length) {
                            start += 1;
                            end = start;
                        }

                        return new PrimaShingle(span, size);
                    }
                };
            }
        };
    }
}
