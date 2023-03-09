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
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAttachment;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.FHIRAttachmentMaskingProvider;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FHIRAttachmentMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testBasic() throws Exception {
        String json = "{\n" +
                "      \"contentType\": \"image/jpeg\",\n" +
                "      \"data\": \"datahere\"," +
                "      \"title\": \"myPhoto\"," +
                "      \"url\": \"Binary/f006\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRAttachment attachment = objectMapper.readValue(json, FHIRAttachment.class);

        assertEquals("myPhoto", attachment.getTitle());
        assertEquals("datahere", attachment.getData());

        FHIRAttachmentMaskingProvider fhirAttachmentMaskingProvider = new FHIRAttachmentMaskingProvider(
                new DefaultMaskingConfiguration(), new HashSet<String>(), "/foo", this.factory);
        FHIRAttachment maskedAttachment = fhirAttachmentMaskingProvider.mask(attachment);

        assertEquals("", maskedAttachment.getTitle());
        assertNull(maskedAttachment.getData());
        assertEquals("", maskedAttachment.getUrl());
    }

    @Test
    public void testPreserveTitle() throws Exception {
        String json = "{\n" +
                "      \"contentType\": \"image/jpeg\",\n" +
                "      \"data\": \"datahere\"," +
                "      \"title\": \"myPhoto\"," +
                "      \"url\": \"Binary/f006\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRAttachment attachment = objectMapper.readValue(json, FHIRAttachment.class);

        assertEquals("myPhoto", attachment.getTitle());

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.attachment.removeTitle", false);
        FHIRAttachmentMaskingProvider fhirAttachmentMaskingProvider = new FHIRAttachmentMaskingProvider(
                maskingConfiguration, new HashSet<String>(), "/foo", this.factory);
        FHIRAttachment maskedAttachment = fhirAttachmentMaskingProvider.mask(attachment);

        assertEquals("myPhoto", maskedAttachment.getTitle());
    }

    @Test
    public void testPreserveData() throws Exception {
        String json = "{\n" +
                "      \"contentType\": \"image/jpeg\",\n" +
                "      \"data\": \"datahere\"," +
                "      \"hash\": \"hashhere\"," +
                "      \"size\": \"8\"," +
                "      \"title\": \"myPhoto\"," +
                "      \"url\": \"Binary/f006\"\n" +
                "    }";

        ObjectMapper objectMapper = new ObjectMapper();
        FHIRAttachment attachment = objectMapper.readValue(json, FHIRAttachment.class);

        assertEquals("datahere", attachment.getData());

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fhir.attachment.removeData", false);
        FHIRAttachmentMaskingProvider fhirAttachmentMaskingProvider = new FHIRAttachmentMaskingProvider(
                maskingConfiguration, new HashSet<String>(), "/foo", this.factory);
        FHIRAttachment maskedAttachment = fhirAttachmentMaskingProvider.mask(attachment);

        assertEquals("datahere", maskedAttachment.getData());
        assertEquals("hashhere", maskedAttachment.getHash());
        assertEquals("8", maskedAttachment.getSize());
    }
}


