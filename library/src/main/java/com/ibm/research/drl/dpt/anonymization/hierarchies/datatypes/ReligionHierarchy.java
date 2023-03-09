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
import com.ibm.research.drl.dpt.managers.ReligionManager;
import com.ibm.research.drl.dpt.models.Religion;

import java.io.Serializable;
import java.util.Collection;

public class ReligionHierarchy extends MaterializedHierarchy implements Serializable {
    private static final ReligionManager manager = ReligionManager.getInstance();
    private static final ReligionHierarchy instance = new ReligionHierarchy();

    public static ReligionHierarchy getInstance() {
        return instance;
    }

    private ReligionHierarchy() {
        super();

        Collection<Religion> religions = manager.getItemList();

        for (final Religion religion : religions) {
            String[] terms = new String[3];
            terms[0] = religion.getName();
            terms[1] = religion.getGroup();
            terms[2] = "*";
            add(terms);
        }

        String[] terms = new String[3];
        terms[0] = "Other";
        terms[1] = "Other-Grp";
        terms[2] = "*";
        add(terms);
    }
}