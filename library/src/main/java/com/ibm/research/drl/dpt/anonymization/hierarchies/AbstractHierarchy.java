/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies;

import com.fasterxml.jackson.databind.JsonNode;


public abstract class AbstractHierarchy implements GeneralizationHierarchy {
    public AbstractHierarchy(JsonNode node) {
    }
}
