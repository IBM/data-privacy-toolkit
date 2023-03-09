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

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;

public class ZIPCodeMaterializedHierarchy extends MaterializedHierarchy {
    private static final ZIPCodeMaterializedHierarchy instance = new ZIPCodeMaterializedHierarchy();

    public static ZIPCodeMaterializedHierarchy getInstance() {
        return instance;
    }

    private ZIPCodeMaterializedHierarchy() {
        super();

        for (int i = 0; i < 100_000; i++) {
            String[] terms = new String[6];

            String zipcode = String.format("%05d", i);
            terms[0] = zipcode;
            terms[1] = zipcode.substring(0, 4) + "*";
            terms[2] = zipcode.substring(0, 3) + "**";
            terms[3] = zipcode.substring(0, 2) + "***";
            terms[4] = zipcode.charAt(0) + "****";
            terms[5] = "*";
            add(terms);
        }
    }
}

