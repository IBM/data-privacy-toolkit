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


import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.records.HL7Record;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;

public class HL7FormatProcessor extends FormatProcessor {


    @Override
    public boolean supportsStreams() {
        return true;
    }

    public Record maskRecord(Record record, MaskingProviderFactory maskingProvidersFactory, Set<String> alreadyMaskedFields, DataMaskingOptions dataMaskingOptions) {
        MaskingConfiguration maskingConfiguration = maskingProvidersFactory.getConfigurationForField("");
        Record masked = super.maskRecord(record, maskingProvidersFactory, alreadyMaskedFields, dataMaskingOptions);

        return masked;
    }

    @Override
    protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions dataOptions, int firstN) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataset))) {
            HapiContext context = new DefaultHapiContext();
            Parser parser = context.getGenericParser();
            Message message = parser.parse(IOUtils.toString(reader));

            return Collections.singletonList(new HL7Record(message));
        } catch (HL7Exception e) {
            throw new IOException("Error extracting the message: " + e.getMessage());
        }
    }
}
