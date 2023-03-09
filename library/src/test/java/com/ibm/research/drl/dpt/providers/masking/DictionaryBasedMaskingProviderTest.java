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
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DictionaryBasedMaskingProviderTest {

    @Test
    public void testMask() throws Exception {
        Set<String> terms = new HashSet<>(Arrays.asList("one", "two", "three", "four"));

        Path tempFile2 = Files.createTempFile("dict", ".csv");
        String filename = tempFile2.toAbsolutePath().toString();
        
        try (
                OutputStream fos = Files.newOutputStream(tempFile2);
                PrintStream printStream = new PrintStream(fos)) {
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
