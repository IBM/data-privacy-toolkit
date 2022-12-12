/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;

import java.util.Set;

public class ZIPCodeCompBasedHierarchy implements GeneralizationHierarchy {
    private final String topTerm = "*****";
    private final int height = 6;

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public long getTotalLeaves() {
        return 100000;
    }

    @Override
    public int leavesForNode(String value) {
        int level = getNodeLevel(value);

        return (int)Math.pow(10, level);
    }

    @Override
    public Set<String> getNodeLeaves(String value) {
        return null;
    }

    @Override
    public int getNodeLevel(String value) {
        int level = 0;

        for(int i = (value.length() - 1); i >= 0; i++) {
            if (value.charAt(i) == '*') {
                level++;
            }
        }

        return level;
    }

    @Override
    public String getTopTerm() {
        return topTerm;
    }

    @Override
    public String encode(String value, int level, boolean randomizeOnFail) {
        if (level <= 0) {
            return value;
        }

        if (level >= this.height) {
            return this.topTerm;
        }

        if (value.length() != 5) {
            return topTerm;
        }

        StringBuilder prefix = new StringBuilder(value.substring(0, value.length() - level));

        for(int i = 0; i < level; i++) {
            prefix.append("*");
        }

        return prefix.toString();
    }
}

