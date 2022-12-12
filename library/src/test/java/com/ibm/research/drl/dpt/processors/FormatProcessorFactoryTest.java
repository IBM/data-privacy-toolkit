/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Collections;
import java.util.Set;

public class FormatProcessorFactoryTest {
    
    @Test
    @Disabled
    public void testCSVFormatProcessor() throws Exception {
        
        //we get the format processor
        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.CSV);
        
        //the input file to process
        InputStream inputStream = new FileInputStream("/tmp/input.txt");

        //the output file that will store the masked result
        OutputStream outputStream = new FileOutputStream("/tmp/output.txt");
        PrintStream printStream = new PrintStream(outputStream);
        
        //the masking configuration for the providers
        ConfigurationManager configurationManager = ConfigurationManager.load(new ObjectMapper().readTree(new FileInputStream("/tmp/maskingConfiguration.json")));
        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, Collections.emptyMap());
        
        //the data masking options
        DataMaskingOptions dataMaskingOptions = new ObjectMapper().readValue(new FileInputStream("/tmp/maskingOptions.json"), DataMaskingOptions.class);
        
        //we dont have anything pre-masked
        Set<String> alreadyMaskedFields = Collections.emptySet();
        
        formatProcessor.maskStream(inputStream, printStream, factory, dataMaskingOptions, alreadyMaskedFields, null);
    }
}

