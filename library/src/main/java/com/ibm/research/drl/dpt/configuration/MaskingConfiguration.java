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

    JsonNode getJsonNodeValue(String key);

    Collection<String> getStringValueWithPrefixMatch(String key);

    /**
     * Sets value.
     *
     * @param key   the key
     * @param value the value
     */
    void setValue(String key, Object value);

    /**
     * @return the configuration manager that generated this configuration
     */
    ConfigurationManager getConfigurationManager();

    void setConfigurationManager(ConfigurationManager configurationManager);
}
