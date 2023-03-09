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
package com.ibm.research.drl.dpt.nlp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class MultiSourceNLPAnnotator extends AbstractNLPAnnotator {
    protected List<IdentifiedEntity> mergeEntityListsAndOverlappingEntities(List<IdentifiedEntity> entityList) {
        if (entityList.isEmpty()) return entityList;

        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getStart));
        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));

        int j = 0;
        IdentifiedEntity t1 = entityList.get(j);
        for (int i = 1; i < entityList.size();) {
            final IdentifiedEntity t2 = entityList.get(i);

            if (t1.getEnd() == t2.getEnd()) {
                // merge
                final Set<IdentifiedEntityType> types = mergeTypes(t1.getType(), t2.getType());
                final Set<PartOfSpeechType> pos = mergePartOfSpeech(t1.getPos(), t2.getPos());

                IdentifiedEntity newT1;

                if (t1.getStart() <= t2.getStart()) {
                    newT1 = new IdentifiedEntity(t1.getText(), t1.getStart(), t1.getEnd(), types, pos);
                } else {
                    newT1 = new IdentifiedEntity(t2.getText(), t2.getStart(), t1.getEnd(), types, pos);
                }

                t1 = newT1;

                entityList.remove(i);
                entityList.set(j, t1);

                continue;
            }

            // swap and continue
            t1 = t2;
            j = i;
            i += 1;
        }

        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));
        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        j = 0;
        t1 = entityList.get(j);
        for (int i = 1; i < entityList.size();) {
            final IdentifiedEntity t2 = entityList.get(i);

            if (t1.getStart() == t2.getStart()) {
                // merge
                final Set<IdentifiedEntityType> types = mergeTypes(t1.getType(), t2.getType());
                final Set<PartOfSpeechType> pos = mergePartOfSpeech(t1.getPos(), t2.getPos());

                IdentifiedEntity newT1;

                if (t1.getEnd() < t2.getEnd()) {
                    newT1 = new IdentifiedEntity(t2.getText(), t1.getStart(), t2.getEnd(), types, pos);
                } else {
                    newT1 = new IdentifiedEntity(t1.getText(), t1.getStart(), t1.getEnd(), types, pos);
                }

                t1 = newT1;

                entityList.remove(i);
                entityList.set(j, t1);

                continue;
            }

            // swap and continue
            t1 = t2;
            j = i;
            i += 1;
        }

        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getEnd));
        entityList.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

        j = 0;
        t1 = entityList.get(j);
        for (int i = 1; i < entityList.size();) {
            final IdentifiedEntity t2 = entityList.get(i);

            if (t1.getStart() <= t2.getStart() && t1.getEnd() >= t2.getEnd()) {
                // merge
                final Set<IdentifiedEntityType> types = mergeTypes(t1.getType(), t2.getType());
                final Set<PartOfSpeechType> pos = mergePartOfSpeech(t1.getPos(), t2.getPos());

                IdentifiedEntity newT1;

                if (t1.getEnd() < t2.getEnd()) {
                    newT1 = new IdentifiedEntity(t2.getText(), t1.getStart(), t2.getEnd(), types, pos);
                } else {
                    newT1 = new IdentifiedEntity(t1.getText(), t1.getStart(), t1.getEnd(), types, pos);
                }

                t1 = newT1;

                entityList.remove(i);
                entityList.set(j, t1);

                continue;
            }

            // swap and continue
            t1 = t2;
            j = i;
            i += 1;
        }

        return entityList;
    }

    protected List<IdentifiedEntity> mergeEntityListsAndOverlappingEntities(List<IdentifiedEntity> a, List<IdentifiedEntity> b) {
        final List<IdentifiedEntity> mergedEntities = new ArrayList<>(a.size() + b.size());

        mergedEntities.addAll(a);
        mergedEntities.addAll(b);

       return mergeEntityListsAndOverlappingEntities(mergedEntities);
    }

    protected Set<PartOfSpeechType> mergePartOfSpeech(Set<PartOfSpeechType> pos1, Set<PartOfSpeechType> pos2) {
        final Set<PartOfSpeechType> pos = new HashSet<>();

        if (pos1.size() > 1 || ! pos1.contains(PartOfSpeechType.UNKNOWN)) pos.addAll(pos1);
        if (pos2.size() > 1 || ! pos2.contains(PartOfSpeechType.UNKNOWN)) pos.addAll(pos2);

        if (pos.isEmpty()) pos.add(PartOfSpeechType.UNKNOWN);

        return pos;
    }

    protected Set<IdentifiedEntityType> mergeTypes(Set<IdentifiedEntityType> types1, Set<IdentifiedEntityType> types2) {
        final Set<IdentifiedEntityType> types = new HashSet<>();

        types.addAll(types1);
        types.addAll(types2);

        return types;
    }
}
