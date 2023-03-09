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
package com.ibm.research.drl.dpt.providers.masking.persistence;

import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.EmailMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FileBackedPersistentMaskingProviderTest {
    @Test
    public void testPersistence() {

        String email1 = "joedoe1@foo.com";
        String email2 = "joedoe2@foo.com";

        MaskingProvider emailMaskingProvider = new EmailMaskingProvider(new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("persistence.file", "/tmp");
        configuration.setValue("persistence.namespace", "AAAA");

        FileBackedPersistentMaskingProvider provider = new FileBackedPersistentMaskingProvider(emailMaskingProvider, configuration);

        String maskedEmail1_once = provider.mask(email1);
        String maskedEmail1_twice = provider.mask(email1);
        assertEquals(maskedEmail1_once, maskedEmail1_twice);

        String maskedEmail2_once = provider.mask(email2);
        assertNotEquals(maskedEmail2_once, maskedEmail1_once);
    }
}
