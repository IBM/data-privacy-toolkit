/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import opennlp.tools.util.Span;

public class PrimaShingle {
    private final Span span;
    private final int size;

    public PrimaShingle(Span span, int size) {
        this.span = span;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public Span getSpan() {
        return span;
    }
}

