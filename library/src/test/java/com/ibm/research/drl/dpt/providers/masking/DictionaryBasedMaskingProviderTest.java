/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DictionaryBasedMaskingProviderTest {

    @Test
    public void testMask() throws Exception {
        
        Set<String> terms = new HashSet<>(Arrays.asList("one", "two", "three", "four"));

        File tempFile2 = File.createTempFile("dict", ".csv");
        String filename = tempFile2.getCanonicalPath();
        System.out.println(filename); 
        
        try (FileOutputStream fos = new FileOutputStream(tempFile2); PrintStream printStream = new PrintStream(fos)) {
            for (String term : terms) {
                printStream.println(term);
            }
        }

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("dictionaryBased.mask.filename", filename);
        
        MaskingProvider maskingProvider = new DictionaryBasedMaskingProvider(maskingConfiguration);
       
        Set<String> maskedValues = new HashSet<>();
        for(int i = 0; i < 1000; i++) {
            String maskedValue = maskingProvider.mask("foobar");
            maskedValues.add(maskedValue);
            assertTrue(terms.contains(maskedValue));
        }
        
        assertTrue(maskedValues.size() > 0);
        
    }
}
