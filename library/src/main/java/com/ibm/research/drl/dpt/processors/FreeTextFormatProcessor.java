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

/** Processor interface for free-text format documents. */
public interface FreeTextFormatProcessor {
    /**
     * Identifies entities in the given document.
     *
     * @param inputStream    the document input stream
     * @param identifier     the NLP annotator
     * @param datasetOptions dataset options
     * @return list of identified entities
     * @throws IOException if reading fails
     */
    default List<IdentifiedEntity> identifyDocument(InputStream inputStream, NLPAnnotator identifier, DatasetOptions datasetOptions) throws IOException {
        throw new UnsupportedOperationException("Not implemented for this file format");
    }

    /**
     * Masks the given document and writes to the output.
     *
     * @param dataset         the document input stream
     * @param output          the output stream
     * @param maskingProvider the masking provider
     */
    default void maskDocument(InputStream dataset, OutputStream output, MaskingProvider maskingProvider) {
        throw new UnsupportedOperationException("Not implemented for this file format");
    }

    default void applyFunction(InputStream inputStream, PrintStream output, NLPAnnotator identifier, DatasetOptions datasetOptions,
                               Function<IdentifiedEntity, String> function) {
        throw new UnsupportedOperationException("Not implemented for this file format");
    }
}
