/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.io.Serializable;

public class RelationshipOperand implements Serializable {
    private final String name;
    private final ProviderType type;

    /**
     * Instantiates a new Relationship operand.
     *
     * @param name the name
     * @param type the type
     */
    @JsonCreator
    public RelationshipOperand(@JsonProperty("name") String name, @JsonProperty("type") ProviderType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Instantiates a new Relationship operand.
     *
     * @param name the name
     */
    public RelationshipOperand(String name) {
        this(name, null);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public ProviderType getType() {
        return this.type;
    }

    public String toString() {
        if (this.type != null)
            return this.name + ":" + this.type.name();
        return this.name + ":null";
    }
}
