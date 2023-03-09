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
import com.ibm.research.drl.dpt.managers.ICDv9Manager;
import com.ibm.research.drl.dpt.models.ICD;

import java.util.Collection;

public class ICDv9Hierarchy extends MaterializedHierarchy {
    private final static ICDv9Manager ICD_V9_MANAGER = ICDv9Manager.getInstance();

    private static final ICDv9Hierarchy instance = new ICDv9Hierarchy();

    public static ICDv9Hierarchy getInstance() {
        return instance;
    }

    private ICDv9Hierarchy() {
        super();

        Collection<ICD> icdList = ICD_V9_MANAGER.getItemList();
        for (ICD icd : icdList) {
            String[] terms = new String[4];
            terms[0] = icd.getCode();
            terms[1] = icd.getCategoryCode();
            terms[2] = icd.getChapterCode();
            terms[3] = "*";
            add(terms);
        }
    }
}

