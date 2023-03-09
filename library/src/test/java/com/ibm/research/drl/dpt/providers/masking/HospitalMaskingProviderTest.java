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

import com.ibm.research.drl.dpt.managers.HospitalManager;
import com.ibm.research.drl.dpt.models.Hospital;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HospitalMaskingProviderTest {

    @Test
    public void testMask() {
        HospitalManager hospitalManager = HospitalManager.getInstance();
        MaskingProvider maskingProvider = new HospitalMaskingProvider();
        String hospitalName = "York Hospital";

        int randomizationOK = 0;
        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(hospitalName);

            if (!maskedValue.equalsIgnoreCase(hospitalName)) {
                randomizationOK++;
            }

            Hospital original = hospitalManager.getKey(hospitalName);
            Hospital masked = hospitalManager.getKey(maskedValue);

            assertEquals(original.getNameCountryCode(), masked.getNameCountryCode());
        }

        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testLocalization() throws Exception {
        String greekHospital = "ΠΕΠΑΓΝΗ";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.HOSPITAL_NAMES, Collections.singletonList("gr"));
        Set<String> greekHospitals = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekHospitals.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        MaskingProvider maskingProvider = new HospitalMaskingProvider();

        int randomizationOK = 0;
        for(int i = 0; i < 100; i++) {
            String maskedHospital = maskingProvider.mask(greekHospital);
            if (!maskedHospital.equalsIgnoreCase(greekHospital)) {
                randomizationOK++;
            }

            boolean isMatch = greekHospitals.contains(maskedHospital.toUpperCase());

            if (!isMatch) {
                System.out.println(maskedHospital);
            }

            assertTrue(isMatch);
        }

        assertTrue(randomizationOK > 0);
    }
}

