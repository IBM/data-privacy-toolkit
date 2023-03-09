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

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigurationManager implements Serializable {
    private final MaskingConfiguration defaults;
    private final Map<String, MaskingConfiguration> fields;

    /**
     * Instantiates a new Configuration manager.
     */
    public ConfigurationManager() {
        this(new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Configuration manager.
     *
     * @param defaults the defaults
     */
    public ConfigurationManager(MaskingConfiguration defaults) {
        this.defaults = defaults;
        this.fields = new HashMap<>();
        defaults.setConfigurationManager(this);
    }

    /**
     * Load configuration manager.
     *
     * @param configurationTree the configuration tree
     * @return the configuration manager
     */
    public static ConfigurationManager load(JsonNode configurationTree) {
        final ConfigurationManager configurationManager = new ConfigurationManager();

        if (configurationTree.has("_defaults")) {
            loadDefaults(configurationManager, configurationTree.get("_defaults"));
        }

        if (configurationTree.has("_fields")) {
            loadFields(configurationManager, configurationTree.get("_fields"));
        }

        return configurationManager;
    }

    private static void loadDefaults(final ConfigurationManager configurationManager, final JsonNode defaults) {
        Iterator<Map.Entry<String, JsonNode>> values = defaults.fields();

        while (values.hasNext()) {
            Map.Entry<String, JsonNode> value = values.next();

            configurationManager.defaults.setValue(value.getKey(), getValue(value.getValue()));
        }
    }

    private static Object getValue(JsonNode value) {
        if (value.isBoolean())
            return value.asBoolean();
        if (value.isShort() || value.isInt() || value.isIntegralNumber())
            return value.asInt();

        if (value.isDouble() || value.isFloat()) {
            return value.asDouble();
        }

        if (value.isArray() || value.isObject()) {
            return value;
        }

        return value.textValue();
    }

    private static void loadFields(ConfigurationManager configurationManager, JsonNode fields) {
        Iterator<Map.Entry<String, JsonNode>> fieldConfigurations = fields.fields();

        while (fieldConfigurations.hasNext()) {
            Map.Entry<String, JsonNode> fieldConfiguration = fieldConfigurations.next();

            configurationManager.fields.put(fieldConfiguration.getKey(),
                    new FieldMaskingConfiguration(configurationManager.defaults, parseFieldOptions(fieldConfiguration.getValue())
                    ));
        }
    }

    private static Map<String, ConfigurationOption> parseFieldOptions(JsonNode fieldOptions) {
        Map<String, ConfigurationOption> options = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> optionsIterator = fieldOptions.fields();

        while (optionsIterator.hasNext()) {
            Map.Entry<String, JsonNode> option = optionsIterator.next();

            options.put(option.getKey(), new ConfigurationOption(getValue(option.getValue()), null, null));
        }

        return options;
    }

    /**
     * Gets field configuration.
     *
     * @param fieldName the field name
     * @return the field configuration
     */
    public MaskingConfiguration getFieldConfiguration(final String fieldName) {
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }

        return defaults;
    }

    /**
     * Gets default configuration.
     *
     * @return the default configuration
     */
    public MaskingConfiguration getDefaultConfiguration() {
        return defaults;
    }
}
