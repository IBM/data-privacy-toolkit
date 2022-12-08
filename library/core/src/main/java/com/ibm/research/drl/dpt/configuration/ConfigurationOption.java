/*******************************************************************
* IBM Confidential                                                *
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
* The source code for this program is not published or otherwise  *
* divested of its trade secrets, irrespective of what has         *
* been deposited with the U.S. Copyright Office.                  *
*******************************************************************/
package com.ibm.research.drl.prima.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class ConfigurationOption implements Serializable {
    private static final ObjectMapper mapper = new ObjectMapper();

    private String description;
    private String category;
    private Object value;

    /**
     * Instantiates a new Configuration option.
     *
     * @param value       the value
     * @param description the description
     * @param category    the category
     */
    @JsonCreator
    public ConfigurationOption(@JsonProperty("value") Object value, @JsonProperty("description") String description, @JsonProperty("category") String category) {
        this.description = description;
        this.value = value;
        this.category = category;
    }

    /* for serialization only */
    private ConfigurationOption() {
        description = null;
        category = null;
        value = null;
    }

    /**
     * Instantiates a new Configuration option.
     *
     * @param value       the value
     * @param description the description
     */
    public ConfigurationOption(Object value, String description) {
        this.description = description;
        this.value = value;
        this.category = "Misc.";
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public String getCategory() {
        return this.category;
    }


    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(description);
        out.writeObject(category);

        if (Objects.isNull(value) || value instanceof Serializable) {
            out.writeObject(value);
        } else if ( value instanceof JsonNode ) {
            out.writeObject( value.toString().getBytes());
        } else {
            throw new RuntimeException("Not serializable: " + value.getClass().getCanonicalName());
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.description = (String) in.readObject();
        this.category = (String) in.readObject();

        Object value = in.readObject();

        if (value instanceof byte[]) {
            this.value = mapper.readTree((byte[]) value);
        } else {
            this.value = value;
        }
    }
}
