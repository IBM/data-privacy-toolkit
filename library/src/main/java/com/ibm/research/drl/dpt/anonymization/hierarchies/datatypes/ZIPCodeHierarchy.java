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
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ZIPCodeHierarchy implements GeneralizationHierarchy, Serializable {
    private static final long serialVersionUID = 5771549821136802771L;
    private static final ZIPCodeHierarchy instance = new ZIPCodeHierarchy();

    public static ZIPCodeHierarchy getInstance() {
        return instance;
    }

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

        return (int) Math.pow(10, level);
    }

    @Override
    public Set<String> getNodeLeaves(String value) {
        int level = getNodeLevel(value);

        if (level == 0) { //this is a leaf in the tree
            return Collections.singleton(value);
        }

        //we calculate how many ZIP codes are in the leaves 
        //based on the level. If the value is 1234*, then the 
        //level is 1, so we have 10^(1) = 10 remaining codes
        int remainingZIPCodes = (int) Math.pow(10, level);

        // if the value is 1234*, then the prefix we preserve is 1234
        String prefix = value.substring(0, 5 - level);

        Set<String> leaves = new HashSet<>();

        for (int i = 0; i < remainingZIPCodes; i++) {
            //we left pad so the final value will be a 5-digit code
            leaves.add(prefix + StringUtils.leftPad(i + "", level, '0'));
        }

        return leaves;
    }

    @Override
    public int getNodeLevel(String value) {
        int level = 0;

        for (int i = (value.length() - 1); i >= 0; i--) {
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

        for (int i = 0; i < level; i++) {
            prefix.append("*");
        }

        return prefix.toString();
    }
}

