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
import com.ibm.research.drl.dpt.managers.NamesManager;
import com.ibm.research.drl.dpt.providers.identifiers.LatitudeLongitudeIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class LatitudeLongitudeMaskingProviderTest {
    private static final NamesManager.Names names = NamesManager.instance();

    @Test
    public void testMask() throws Exception {
        MaskingProvider latlonMaskingProvider = new LatitudeLongitudeMaskingProvider();
        LatitudeLongitudeIdentifier latitudeLongitudeIdentifier = new LatitudeLongitudeIdentifier();

        String gpsCoords = "10.0000,12.0000";
        String maskedResult = latlonMaskingProvider.mask(gpsCoords);
        assertNotEquals(maskedResult, gpsCoords);
        assertTrue(latitudeLongitudeIdentifier.isOfThisType(maskedResult), maskedResult);
        assertTrue(latitudeLongitudeIdentifier.isGPSFormat(maskedResult), maskedResult);

        String compassCoords = "N90.00.00 E180.00.00";
        maskedResult = latlonMaskingProvider.mask(compassCoords);
        assertNotEquals(maskedResult, compassCoords);
        assertTrue(latitudeLongitudeIdentifier.isOfThisType(maskedResult));
        assertTrue(latitudeLongitudeIdentifier.isCompassFormat(maskedResult));

        String dmsCoords = "12:30'23.256547S 12:30'23.256547E";
        assertTrue(latitudeLongitudeIdentifier.isOfThisType(dmsCoords));
        maskedResult = latlonMaskingProvider.mask(dmsCoords);
        assertNotEquals(maskedResult, dmsCoords);
        assertTrue(latitudeLongitudeIdentifier.isOfThisType(maskedResult));
        assertTrue(latitudeLongitudeIdentifier.isDMSFormat(maskedResult));
    }

    @Test
    public void testAssertThatInvalidArgumentIsCaught() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            MaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("latlon.offset.minimumRadius", 5);
            MaskingProvider latlonMaskingProvider = new LatitudeLongitudeMaskingProvider(configuration);
        });
    }

    @Test
    public void testMaskInvalidValue() throws Exception {
        MaskingProvider latlonMaskingProvider = new LatitudeLongitudeMaskingProvider();
        LatitudeLongitudeIdentifier latitudeLongitudeIdentifier = new LatitudeLongitudeIdentifier();

        String invalidValue = "junk";
        String maskedResult = latlonMaskingProvider.mask(invalidValue);

        assertNotEquals(maskedResult, invalidValue);
        assertTrue(latitudeLongitudeIdentifier.isOfThisType(maskedResult));
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration
        };

        String[] originalValues = new String[]{
                "10.0000,12.0000"
        };

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            LatitudeLongitudeMaskingProvider maskingProvider = new LatitudeLongitudeMaskingProvider(maskingConfiguration);

            for (String originalValue : originalValues) {
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedValue = maskingProvider.mask(originalValue);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.printf("%s: %s: %d operations took %d milliseconds (%f per op)%n",
                        maskingConfiguration.getName(), originalValue, N, diff, (double) diff / N);
            }
        }
    }
}
