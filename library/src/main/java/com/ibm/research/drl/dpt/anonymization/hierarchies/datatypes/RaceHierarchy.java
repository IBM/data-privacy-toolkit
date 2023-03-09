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
import com.ibm.research.drl.dpt.managers.RaceManager;
import com.ibm.research.drl.dpt.models.Race;

import java.util.Collection;

public class RaceHierarchy extends MaterializedHierarchy {
    private final static RaceManager RACE_MANAGER = RaceManager.getInstance();

    private static final RaceHierarchy instance = new RaceHierarchy();

    public static RaceHierarchy getInstance() {
        return instance;
    }

    private RaceHierarchy() {
        super();

        Collection<Race> races = RACE_MANAGER.getItemList();
        for (Race race : races) {
            String[] terms = new String[2];
            terms[0] = race.getName();
            terms[1] = "*";
            add(terms);
        }

        String[] terms = new String[2];
        terms[0] = "Other";
        terms[1] = "*";
        add(terms);
    }
}

