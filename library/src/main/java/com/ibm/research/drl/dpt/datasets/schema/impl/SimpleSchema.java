/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.datasets.schema.impl;

import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Simple schema.
 *
 */
public class SimpleSchema implements IPVSchema, Serializable {
    private final String id;
    private final List<SimpleSchemaField> fields;

    /**
     * Instantiates a new Simple schema.
     *
     * @param id the id
     */
    public SimpleSchema(String id) {
        this(id, new ArrayList<SimpleSchemaField>());
    }

    /**
     * Instantiates a new Simple schema.
     *
     * @param id     the id
     * @param fields the fields
     */
    public SimpleSchema(String id, List<SimpleSchemaField> fields) {
        this.id = id;
        this.fields = fields;
    }

    /**
     * Add schema field simple schema.
     *
     * @param field the field
     * @return the simple schema
     */
    public SimpleSchema addSchemaField(SimpleSchemaField field) {
        if (!fieldExists(field)) fields.add(field);

        return this;
    }

    /**
     * Add schema field simple schema.
     *
     * @param name the name
     * @param type the type
     * @return the simple schema
     */
    public SimpleSchema addSchemaField(String name, IPVSchemaFieldType type) {
        return addSchemaField(new SimpleSchemaField(name, type));
    }

    private boolean fieldExists(SimpleSchemaField other) {
        for (IPVSchemaField field : fields) {
            if (field.getName().equals(other.getName())) return true;
        }

        return false;
    }

    @Override
    public String getSchemaIdentifier() {
        return id;
    }

    @Override
    public List<? extends IPVSchemaField> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "SimpleSchema{ " + id + ", [" + fields + "]}";
    }
}
