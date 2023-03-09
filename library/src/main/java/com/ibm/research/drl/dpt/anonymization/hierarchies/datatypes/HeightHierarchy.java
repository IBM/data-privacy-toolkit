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
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;


import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;

import java.util.Set;

public class HeightHierarchy implements GeneralizationHierarchy {
    private static final HeightHierarchy instance = new HeightHierarchy();

    public static HeightHierarchy getInstance() {
        return instance;
    }

    private final String topTerm = "*";
    private final int[] steps = {1, 2, 4, 8, 10, 20, 30};

    @Override
    public int getHeight() {
        return steps.length + 2; // +2 for the original value and the top term
    }

    @Override
    public long getTotalLeaves() {
        return 100_000;
    }

    @Override
    public int leavesForNode(String value) {
        throw new RuntimeException("Cannot calculate it");
    }

    @Override
    public Set<String> getNodeLeaves(String value) {
        return null;
    }

    @Override
    public int getNodeLevel(String value) {
        String[] tokens = value.split("-");
        if (tokens.length == 1) {
            if (topTerm.equals(value)) {
                return getHeight() - 1;
            }

            return 0;
        }

        int start = Integer.parseInt(tokens[0]);
        int end = Integer.parseInt(tokens[1]);

        int diff = end - start;

        for (int i = 0; i < steps.length; i++) {
            if (diff == steps[i]) {
                return 1 + i;
            }
        }

        return getHeight();
    }

    @Override
    public String getTopTerm() {
        return topTerm;
    }

    @Override
    public String encode(String valueString, int level, boolean randomizeOnFail) {
        if (level <= 0) {
            return valueString;
        }

        if (level >= (getHeight() - 1)) {
            return this.topTerm;
        }

        double value = Double.parseDouble(valueString);

        int step = steps[level - 1];

        int base = (int) value;
        int start = base - base % step;
        int end = start + step;

        return String.format("%d-%d", start, end);
    }
}
