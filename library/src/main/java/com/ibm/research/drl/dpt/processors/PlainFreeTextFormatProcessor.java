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
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class PlainFreeTextFormatProcessor implements FreeTextFormatProcessor {
    private final static Logger logger = LogManager.getLogger(PlainFreeTextFormatProcessor.class);

    @Override
    public List<IdentifiedEntity> identifyDocument(InputStream inputStream, NLPAnnotator identifier, DatasetOptions datasetOptions) {
        try {
            String input = IOUtils.toString(new InputStreamReader(inputStream));
            return identifier.identify(input, null);
        } catch (IOException e) {
            logger.error("Error identifying entities");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void maskDocument(InputStream inputStream, OutputStream output, MaskingProvider maskingProvider) {
        try {
            String input = IOUtils.toString(new InputStreamReader(inputStream));

            String masked = maskingProvider.mask(input);
            output.write(masked.getBytes());
        } catch (IOException e) {
            logger.error("Error identifying entities");
            throw new RuntimeException(e);
        }
    }
}
