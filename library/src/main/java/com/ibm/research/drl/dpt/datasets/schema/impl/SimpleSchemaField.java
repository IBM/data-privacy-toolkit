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
package com.ibm.research.drl.dpt.datasets.schema.impl;

import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;

import java.io.Serializable;
import java.util.Objects;

/**
 * The type Simple schema field.
 *
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
