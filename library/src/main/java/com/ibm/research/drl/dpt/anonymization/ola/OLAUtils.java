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
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Tuple;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class OLAUtils {

    private static String createRowKey(List<String> row, List<Integer> quasiColumns) {
        List<String> quasiValues = new ArrayList<>(quasiColumns.size());
        for (Integer quasiColumn : quasiColumns) {
            int columnIndex = quasiColumn;
            String value = row.get(columnIndex);
            quasiValues.add(value);
        }

        return StringUtils.join(quasiValues, ':');
    }

    public static Tuple<List<Partition>, List<Partition>> generatePartitions(IPVDataset original, IPVDataset anonymized,
                                                                             List<ColumnInformation> columnInformationList) {

        Map<String, InMemoryPartition> anonymizedPartitionsMap = new HashMap<>();
        Map<String, InMemoryPartition> originalPartitionsMap = new HashMap<>();

        int n = anonymized.getNumberOfRows();
        int numberOfColumns = anonymized.getNumberOfColumns();

        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);

        for (int i = 0; i < n; i++) {
            List<String> originalRow = original.getRow(i);
            List<String> anonymizedRow = anonymized.getRow(i);

            String key = createRowKey(anonymizedRow, quasiColumns);

            InMemoryPartition anonPartition = anonymizedPartitionsMap.get(key);
            InMemoryPartition originalPartition = originalPartitionsMap.get(key);

            if (anonPartition == null) {
                anonymizedPartitionsMap.put(key, new InMemoryPartition(numberOfColumns));
                originalPartitionsMap.put(key, new InMemoryPartition(numberOfColumns));

                anonPartition = anonymizedPartitionsMap.get(key);
                originalPartition = originalPartitionsMap.get(key);
            }

            anonPartition.getMember().addRow(anonymizedRow);
            originalPartition.getMember().addRow(originalRow);
        }

        Set<String> keys = anonymizedPartitionsMap.keySet();

        List<Partition> originalPartitions = new ArrayList<>();
        List<Partition> anonymizedPartitions = new ArrayList<>();

        for (String key : keys) {
            originalPartitions.add(originalPartitionsMap.get(key));
            anonymizedPartitions.add(anonymizedPartitionsMap.get(key));
        }

        return new Tuple<>(originalPartitions, anonymizedPartitions);
    }

}

