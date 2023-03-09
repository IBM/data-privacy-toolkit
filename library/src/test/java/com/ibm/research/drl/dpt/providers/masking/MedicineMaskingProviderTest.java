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

import com.ibm.research.drl.dpt.providers.identifiers.MedicineIdentifier;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MedicineMaskingProviderTest {

    @Test
    public void testMask() {
        MaskingProvider maskingProvider = new MedicineMaskingProvider();

        String value = "Panadol Extra Strength";
        String maskedValue = maskingProvider.mask(value);
        assertTrue(new MedicineIdentifier().isOfThisType(maskedValue));
    }

    @Test
    public void testLocalization() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingProvider maskingProvider = new MedicineMaskingProvider();

        String greekOriginalValue = "Παρακεταμόλη";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.MEDICINES, Arrays.asList("gr"));
        Set<String> greekValues = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekValues.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(greekOriginalValue);
            assertTrue(greekValues.contains(maskedValue.toUpperCase()));
        }
    }
}

