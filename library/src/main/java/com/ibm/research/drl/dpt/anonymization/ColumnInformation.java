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
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "CategoricalInformation", value = CategoricalInformation.class),
        @JsonSubTypes.Type(name = "DefaultColumnInformation", value = DefaultColumnInformation.class),
        @JsonSubTypes.Type(name = "NumericalRange", value = NumericalRange.class),
        @JsonSubTypes.Type(name = "SensitiveColumnInformation", value = SensitiveColumnInformation.class),
})
public interface ColumnInformation extends Serializable {
    /**
     * Is categorical boolean.
     *
     * @return the boolean
     */
    boolean isCategorical();

    double getWeight();

    boolean isForLinking();

    /**
     * Gets column type.
     *
     * @return the column type
     */
    ColumnType getColumnType();

    /**
     * Gets representation.
     *
     * @return the representation
     */
    @JsonIgnore
    String getRepresentation();
}
