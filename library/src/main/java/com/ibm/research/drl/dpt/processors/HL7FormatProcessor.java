/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
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

public class HL7FormatProcessor extends AbstractFormatProcessor {


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
