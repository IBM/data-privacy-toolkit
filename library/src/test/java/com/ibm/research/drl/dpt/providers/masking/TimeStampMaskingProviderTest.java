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
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class TimeStampMaskingProviderTest {
    @Test
    public void testDropMinuteAndSeconds() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        TimeStampMaskingProvider maskingProvider = new TimeStampMaskingProvider(null, configuration);

        String original = "2018-10-22 12:34:12";
        String masked = maskingProvider.mask(original);

        assertNotEquals(original, masked);
    }

    @Test
    public void testDropMinuteAndSecondsCorrectFormat() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("timestamp.mask.format", "uuuu-MM-dd HH:mm:ss");
        configuration.setValue("timestamp.mask.year", false);
        configuration.setValue("timestamp.mask.month", false);
        configuration.setValue("timestamp.mask.day", false);
        configuration.setValue("timestamp.mask.hour", false);
        configuration.setValue("timestamp.mask.minute", true);
        configuration.setValue("timestamp.mask.second", true);


        TimeStampMaskingProvider maskingProvider = new TimeStampMaskingProvider(null, configuration);

        String original = "2018-10-22 12:34:12";
        String masked = maskingProvider.mask(original);

        assertNotEquals(original, masked);
        assertThat(masked, is("2018-10-22 12:00:00"));
    }
}