/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * The type Field masking configuration.
 *
 * @author stefanob
 */
public class FieldMaskingConfiguration implements MaskingConfiguration, Serializable {
    private final MaskingConfiguration parent;

    private final Map<String, ConfigurationOption> fieldOptions;

    /**
     * Instantiates a new Field masking configuration.
     *
     * @param parent       the parent
     * @param fieldOptions the field options
     */
    public FieldMaskingConfiguration(MaskingConfiguration parent, Map<String, ConfigurationOption> fieldOptions) {
        this.parent = parent;
        this.fieldOptions = fieldOptions;
    }

    @Override
    @Deprecated
    public Object getValue(String key) {
        if (fieldOptions.containsKey(key))
            return fieldOptions.get(key).getValue();
        return parent.getValue(key);
    }

    @Override
    public int getIntValue(String key) {
        return fieldOptions.containsKey(key) ? ((Number) fieldOptions.get(key).getValue()).intValue() : parent.getIntValue(key);
    }

    @Override
    public double getDoubleValue(String key) {
        return fieldOptions.containsKey(key) ? ((Number) fieldOptions.get(key).getValue()).doubleValue() : parent.getDoubleValue(key);
    }

    @Override
    public boolean getBooleanValue(String key) {
        return fieldOptions.containsKey(key) ? (boolean) fieldOptions.get(key).getValue() : parent.getBooleanValue(key);
    }

    @Override
    public String getStringValue(String key) {
        return fieldOptions.containsKey(key) ? (String) fieldOptions.get(key).getValue() : parent.getStringValue(key);
    }

    @Override
    public JsonNode getJsonNodeValue(String key) {
        return fieldOptions.containsKey(key) ? (JsonNode) fieldOptions.get(key).getValue() : parent.getJsonNodeValue(key);
    }

    @Override
    public Collection<String> getStringValueWithPrefixMatch(String prefix) {
        Collection<String> values = new ArrayList<>();

        for(String key: fieldOptions.keySet()) {
            if (key.startsWith(prefix)) {
                values.add(getStringValue(key));
            }
        }

        return values;
    }

    @Override
    public void setValue(String key, Object value) {
        if (!fieldOptions.containsKey(key)) {
            fieldOptions.put(key, new ConfigurationOption(null, null));
        }

        fieldOptions.get(key).setValue(value);
    }

    @Override
    public ConfigurationManager getConfigurationManager() {
        return parent.getConfigurationManager();
    }
}
