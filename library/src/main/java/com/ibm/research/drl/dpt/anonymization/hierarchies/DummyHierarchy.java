/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.Set;

public final class DummyHierarchy extends AbstractHierarchy {
    public DummyHierarchy(JsonNode node) {
        super(node);
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public long getTotalLeaves() {
        return 0;
    }

    @Override
    public int leavesForNode(String value) {
        return 0;
    }

    @Override
    public Set<String> getNodeLeaves(String value) {
        return Collections.singleton(value);
    }

    @Override
    public int getNodeLevel(String value) {
        return 0;
    }

    @Override
    public String getTopTerm() {
        return "*";
    }

    @Override
    public String encode(String value, int level, boolean randomizeOnFail) {
        return "value";
    }
}

