/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
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
