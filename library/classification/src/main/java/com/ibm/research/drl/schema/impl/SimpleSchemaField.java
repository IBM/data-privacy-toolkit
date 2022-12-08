/*******************************************************************
* IBM Confidential                                                *
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
* The source code for this program is not published or otherwise  *
* divested of its trade secrets, irrespective of what has         *
* been deposited with the U.S. Copyright Office.                  *
*******************************************************************/
package com.ibm.research.drl.schema.impl;

import com.ibm.research.drl.schema.IPVSchemaField;
import com.ibm.research.drl.schema.IPVSchemaFieldType;

import java.io.Serializable;
import java.util.Objects;

/**
 * The type Simple schema field.
 *
 * @author stefanob
 */
public class SimpleSchemaField implements IPVSchemaField, Serializable {
    private final String name;
    private final IPVSchemaFieldType type;

    /**
     * Instantiates a new Simple schema field.
     *
     * @param name the name
     * @param type the type
     */
    public SimpleSchemaField(String name, IPVSchemaFieldType type) {

        this.name = name;
        this.type = type;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public IPVSchemaFieldType getType() {
        return type;
    }

    @Override
    public String toString() {
        //return String.format("%s:%s", name, type.name());
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleSchemaField)) return false;
        SimpleSchemaField that = (SimpleSchemaField) o;
        return Objects.equals(getName(), that.getName()) &&
                getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType());
    }
}
