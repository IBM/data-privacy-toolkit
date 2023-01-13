/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Function;

public interface FreeTextFormatProcessor {
    default List<IdentifiedEntity> identifyDocument(InputStream inputStream, NLPAnnotator identifier, DatasetOptions datasetOptions) throws IOException {
        throw new UnsupportedOperationException("Not implemented for this file format");
    }

    default void maskDocument(InputStream dataset, OutputStream output, MaskingProvider maskingProvider) {
        throw new UnsupportedOperationException("Not implemented for this file format");
    }

    default void applyFunction(InputStream inputStream, PrintStream output, NLPAnnotator identifier, DatasetOptions datasetOptions,
                               Function<IdentifiedEntity, String> function) {
        throw new UnsupportedOperationException("Not implemented for this file format");
    }
}
