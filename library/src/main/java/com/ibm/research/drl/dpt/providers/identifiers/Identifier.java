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
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Collection;
import java.util.Collections;

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
    default Collection<ProviderType> getLinkedTypes() {
        return Collections.emptyList();
    }

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
