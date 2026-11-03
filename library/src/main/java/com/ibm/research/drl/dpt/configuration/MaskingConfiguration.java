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

import java.util.Collection;

/**
 * The interface Masking configuration.
 *
 */
public interface MaskingConfiguration {
    /**
     * Gets value.
     *
     * @param key the key
     * @return the value
     */
    @Deprecated
    Object getValue(String key);

    /**
     * Gets int value.
     *
     * @param key the key
     * @return the int value
     */
    int getIntValue(String key);

    /**
     * Gets double value.
     *
     * @param key the key
     * @return the double value
     */
    double getDoubleValue(String key);

    /**
     * Gets boolean value.
     *
     * @param key the key
     * @return the boolean value
     */
    boolean getBooleanValue(String key);

    /**
     * Gets string value.
     *
     * @param key the key
     * @return the string value
     */
    String getStringValue(String key);

    /**
     * Gets a JSON node value.
     *
     * @param key the key
     * @return the JSON node value
     */
    JsonNode getJsonNodeValue(String key);

    /**
     * Gets all string values whose keys start with the given prefix.
     *
     * @param key the key prefix
     * @return the matching string values
     */
    Collection<String> getStringValueWithPrefixMatch(String key);

    /**
     * Sets value.
     *
     * @param key   the key
     * @param value the value
     */
    void setValue(String key, Object value);

    /**
     * Returns the configuration manager that generated this configuration.
     *
     * @return the configuration manager
     */
    ConfigurationManager getConfigurationManager();

    /**
     * Sets the configuration manager for this configuration.
     *
     * @param configurationManager the configuration manager
     */
    void setConfigurationManager(ConfigurationManager configurationManager);
}
