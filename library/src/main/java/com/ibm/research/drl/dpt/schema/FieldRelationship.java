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
package com.ibm.research.drl.dpt.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.models.ValueClass;

import java.io.Serializable;
import java.util.List;

public class FieldRelationship implements Serializable {
    private final String fieldName;
    private final ValueClass valueClass;
    private final RelationshipOperand[] operands;
    private final RelationshipType relationshipType;

    /**
     * Instantiates a new Field relationship.
     *
     * @param valueClass the value class
     * @param type       the type
     * @param fieldName  the field name
     * @param operands   the operands
     */
    public FieldRelationship(ValueClass valueClass, RelationshipType type, String fieldName, RelationshipOperand[] operands) {
        this.valueClass = valueClass;
        this.fieldName = fieldName;
        this.operands = operands;
        this.relationshipType = type;
    }

    /**
     * Instantiates a new Field relationship.
     *
     * @param valueClass       the value class
     * @param relationshipType the relationship type
     * @param fieldName        the field name
     * @param operands         the operands
     */
    @JsonCreator
    public FieldRelationship(@JsonProperty("valueClass") ValueClass valueClass,
                             @JsonProperty("relationshipType") RelationshipType relationshipType,
                             @JsonProperty("fieldName") String fieldName,
                             @JsonProperty("operands") List<RelationshipOperand> operands) {
        this.valueClass = valueClass;
        this.fieldName = fieldName;
        this.relationshipType = relationshipType;

        RelationshipOperand[] arr = new RelationshipOperand[operands.size()];
        arr = operands.toArray(arr);

        this.operands = arr;
    }


    /**
     * Gets value class.
     *
     * @return the value class
     */
    public ValueClass getValueClass() {
        return this.valueClass;
    }

    /**
     * Gets field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Gets relationship type.
     *
     * @return the relationship type
     */
    public RelationshipType getRelationshipType() {
        return this.relationshipType;
    }

    /**
     * Get operands relationship operand [ ].
     *
     * @return the relationship operand [ ]
     */
    public RelationshipOperand[] getOperands() {
        return this.operands;
    }

    @Override
    public String toString() {
        StringBuilder operandNames = new StringBuilder();
        for (RelationshipOperand operand : this.operands) {
            operandNames.append(operand.getName()).append(",");
        }

        return String.format("Field %s (%s) has relationship %s with %s", fieldName,
                valueClass.name(), relationshipType.name(), operandNames);
    }
}
