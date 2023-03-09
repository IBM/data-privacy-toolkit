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
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;

import java.util.*;

public class ClusteringAnonUtils {

    public static List<String> createAnonymizedRow(List<String> centroids, List<String> originalRow, List<ColumnInformation> columnInformationList) {
        List<String> row = new ArrayList<>();

        for (int j = 0; j < columnInformationList.size(); j++) {
            ColumnInformation columnInformation = columnInformationList.get(j);
            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                String centroidValue = centroids.get(j);
                row.add(centroidValue);
            } else {
                row.add(originalRow.get(j));
            }
        }

        return row;
    }

    public static String calculateCommonAncestor(Set<String> values, GeneralizationHierarchy hierarchy) {

        if (values.size() == 1) {
            return values.iterator().next().toUpperCase();
        }

        List<Map<String, Integer>> counters = new ArrayList<>();
        for (int i = 0; i < hierarchy.getHeight(); i++) {
            counters.add(new HashMap<>());
        }

        for (String value : values) {
            int currentLevel = hierarchy.getNodeLevel(value);

            if (currentLevel == -1) {
                break;
            }

            for (int i = currentLevel; i < hierarchy.getHeight(); i++) {
                String ancestor = hierarchy.encode(value, i - currentLevel, true);

                Integer counter = counters.get(i).get(ancestor);
                if (counter == null) {
                    counter = 0;
                }

                counters.get(i).put(ancestor, counter + 1);
            }
        }

        for (Map<String, Integer> levelMap : counters) {
            for (Map.Entry<String, Integer> entry : levelMap.entrySet()) {
                Integer counter = entry.getValue();
                if (counter == values.size()) {
                    return entry.getKey();
                }
            }
        }

        return hierarchy.getTopTerm();

    }

    public static List<List<String>> calculateCategoricalCentroids(
            List<List<Set<String>>> categoricalVariables, List<ColumnInformation> columnInformationList) {

        List<List<String>> centroids = new ArrayList<>();

        for (List<Set<String>> clusterList : categoricalVariables) {
            List<String> clusterCentroids = new ArrayList<>();

            for (int i = 0; i < clusterList.size(); i++) {
                Set<String> values = clusterList.get(i);
                ColumnInformation columnInformation = columnInformationList.get(i);

                if (values.size() == 0 || !columnInformation.isCategorical()) {
                    clusterCentroids.add(null);
                } else {
                    GeneralizationHierarchy hierarchy = ((CategoricalInformation) columnInformation).getHierarchy();
                    clusterCentroids.add(calculateCommonAncestor(values, hierarchy));
                }

            }

            centroids.add(clusterCentroids);
        }


        return centroids;
    }
}

