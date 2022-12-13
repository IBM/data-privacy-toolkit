/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;

/**
 * The interface Masking configuration.
 *
 * @author stefanob
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
}
