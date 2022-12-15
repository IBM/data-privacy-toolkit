/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Collection;

public interface Identifier {
    /**
     * Gets type.
     *
     * @return the type
     */
    ProviderType getType();

    /**
     * Is of this type boolean.
     *
     * @param data the data
     * @return the boolean
     */
    boolean isOfThisType(String data);

    /**
     * Is appropriate name boolean.
     *
     * @param fieldName the field name
     * @return the boolean
     */
    boolean isAppropriateName(String fieldName);

    /**
     * Gets description.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Gets linked types.
     *
     * @return the linked types
     */
    Collection<ProviderType> getLinkedTypes();

    /**
     * Gets priority.
     *
     * @return the priority
     */
    int getPriority();

    int getMinimumCharacterRequirements();

    int getMinimumLength();

    int getMaximumLength();

    default boolean isPOSIndependent() {
        return false;
    }
}
