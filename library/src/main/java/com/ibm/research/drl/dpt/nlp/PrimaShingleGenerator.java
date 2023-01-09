/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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
