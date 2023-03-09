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
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.DateTimeMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.security.SecureRandom;

public class TMMaskingProvider implements MaskingProvider {
    private final DateTimeMaskingProvider dateTimeMaskingProvider;

    /**
     * Instantiates a new Tm masking provider.
     */
    public TMMaskingProvider(MaskingConfiguration maskingConfiguration) {
        /*
        One or more of the components MM, SS, or FFFFFF may be unspecified as long as every component
        to the right of an unspecified component is also unspecified,
        which indicates that the value is not precise to the precision of those unspecified components.
        The FFFFFF component, if present, shall contain 1 to 6 digits. If FFFFFF is unspecified the preceding "."
        shall not be included.

        Examples:
            "070907.0705 " represents a time of 7 hours, 9 minutes and 7.0705 seconds.
            "1010" represents a time of 10 hours, and 10 minutes.
            "021 " is an invalid value.
        */
        /*TODO: see how we can model this, maybe datetime.format.fixed might need to be extended */
        maskingConfiguration.setValue("datetime.format.fixed", "HHmmss.SSSS");
        dateTimeMaskingProvider = new DateTimeMaskingProvider(maskingConfiguration);
    }

    public TMMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this(maskingConfiguration);
    }

    @Override
    public String mask(String identifier) {
        return dateTimeMaskingProvider.mask(identifier);
    }
}

