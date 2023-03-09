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
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultMaskingConfigurationTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Test
    public void settingVaue() throws Exception {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();

        assertFalse(configuration.getBooleanValue("country.mask.closest"));

        configuration.setValue("country.mask.closest", true);

        String s = mapper.writeValueAsString(configuration);

        DefaultMaskingConfiguration configuration1 = mapper.readValue(s, DefaultMaskingConfiguration.class);

        assertTrue(configuration.getBooleanValue("country.mask.closest"));

        assertThat(mapper.writeValueAsString(configuration1), is(s));
    }

    @Test
    public void testGetConfigurationManager() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        ConfigurationManager configurationManager = maskingConfiguration.getConfigurationManager();

        assertNull(configurationManager);
    }

    @Test
    public void json() throws Exception {
        String s = mapper.writeValueAsString(new DefaultMaskingConfiguration());
        assertThat(mapper.writeValueAsString(mapper.readValue(s, DefaultMaskingConfiguration.class)), is(s));
    }

    private String getType(Object object) {
        if (object == null) {
            return "NULL";
        }

        return object.getClass().getSimpleName();
    }

    @Test
    @Disabled
    public void dumpOptionMap() {

        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        for(String key: maskingConfiguration.getOptions().keySet()) {
            System.out.println(key);
        }

    }

    @Test
    @Disabled
    public void dumpValues() {
        final class KeyOptionPair {
            private final ConfigurationOption option;
            private final String key;

            public ConfigurationOption getOption() {
                return option;
            }

            public String getKey() {
                return key;
            }

            private KeyOptionPair(String key, ConfigurationOption option) {
                this.key = key;
                this.option = option;
            }
        }

        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        Map<String, List<KeyOptionPair>> byCategory = new HashMap<>();

        for(Map.Entry<String, ConfigurationOption> entry: maskingConfiguration.getOptions().entrySet()) {
            ConfigurationOption option = entry.getValue();
            String category = option.getCategory();

            byCategory.computeIfAbsent(category, k -> new ArrayList<>());

            byCategory.get(category).add(new KeyOptionPair(entry.getKey(), option));
        }

        for(Map.Entry<String, List<KeyOptionPair>> entry: byCategory.entrySet()) {
            String category = entry.getKey();
            List<KeyOptionPair> keyOptionPairList = entry.getValue();

            System.out.println("#### " + category);
            System.out.println("|Option name|Type|Description|Default value|");
            System.out.println("|-----------|----|-----------|-------------|");
            for(KeyOptionPair keyOptionPair: keyOptionPairList) {
                Object value = keyOptionPair.getOption().getValue();
                System.out.println("|" + keyOptionPair.getKey() + "|" + getType(value) + "|" +  keyOptionPair.getOption().getDescription() + "|" + value + "|");
            }
        }
    }
}
