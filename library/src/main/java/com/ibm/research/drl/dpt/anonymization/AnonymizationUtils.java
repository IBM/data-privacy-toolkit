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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.generators.ItemSet;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;

/**
 * Utility class for anonymization-related operations.
 * Provides methods for handling privacy constraints, generating partitions,
 * and performing various operations on datasets and column information.
 */
public class AnonymizationUtils {
    private static final Logger logger = LogManager.getLogger(AnonymizationUtils.class);

    /**
     * Constructs a new AnonymizationUtils. This is a utility class with only static methods.
     */
    public AnonymizationUtils() {
    }

    /**
     * Extracts the k value from the list of privacy constraints.
     *
     * @param privacyConstraints the list of privacy constraints
     * @return the k value from the first KAnonymity constraint found
     */
    public static int getK(List<PrivacyConstraint> privacyConstraints) {

        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            if (privacyConstraint instanceof KAnonymity ka) {
                return ka.getK();
            }
        }

        throw new RuntimeException("no k-anonymity constraint is present");
    }

    /**
     * Merge vulnerabilities ipv vulnerability.
     *
     * @param vulnerabilities the vulnerabilities
     * @return the ipv vulnerability
     */
    public static IPVVulnerability mergeVulnerabilities(Collection<IPVVulnerability> vulnerabilities) {
        Iterator<IPVVulnerability> iterator = vulnerabilities.iterator();
        ItemSet merged = new ItemSet();

        while (iterator.hasNext()) {
            IPVVulnerability vulnerability = iterator.next();
            ItemSet itemSet = vulnerability.getItemSet();
            merged.addAll(itemSet);
        }

        return new IPVVulnerability(merged);
    }

    /**
     * Gets columns by type.
     *
     * @param columnInformationList the column information list
     * @param columnType            the column type
     * @return the columns by type
     */
    public static List<Integer> getColumnsByType(List<ColumnInformation> columnInformationList, ColumnType columnType) {
        List<Integer> columns = new ArrayList<>();
        int index = 0;

        for (ColumnInformation columnInformation : columnInformationList) {
            if (columnInformation.getColumnType() == columnType) {
                columns.add(index);
            }

            index++;
        }
        return columns;
    }

    /**
     * Counts the number of columns in the provided list that match the specified column type.
     *
     * @param columnInformationList the list of column information objects to be checked
     * @param columnType the column type to count
     * @return the number of columns in the list that match the specified column type
     */
    public static int countColumnsByType(List<ColumnInformation> columnInformationList, ColumnType columnType) {
        int count = 0;

        for (ColumnInformation columnInformation : columnInformationList) {
            if (columnInformation.getColumnType() == columnType) {
                count++;
            }
        }

        return count;
    }

    /**
     * Generates a composite key string from quasi-identifier column values of the given row.
     *
     * @param row          the dataset row
     * @param quasiColumns indices of the quasi-identifier columns
     * @return the composite key string
     */
    public static String generateEQKey(List<String> row, List<Integer> quasiColumns) {
        StringBuilder key = new StringBuilder();

        for (Integer quasiColumn : quasiColumns) {
            String value = row.get(quasiColumn);
            key.append(value);
            key.append(":");
        }

        return key.toString();
    }

    /**
     * Generates a map of equivalence class keys to their record counts.
     *
     * @param dataset               the dataset
     * @param columnInformationList the column information list
     * @return a map from equivalence class key to count
     */
    public static Map<String, Integer> generateEQCounters(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        Map<String, Integer> counters = new HashMap<>();

        for (List<String> row : dataset) {
            StringBuilder builder = new StringBuilder();

            for (int quasiColumn : quasiColumns) {
                builder.append(row.get(quasiColumn));
                builder.append(":");
            }

            counters.merge(builder.toString(), 1, Integer::sum);
        }

        return counters;
    }

    /**
     * Generates a hash value for the quasi-identifier values in the first row of a partition.
     *
     * @param partition             the partition
     * @param columnInformationList the column information list
     * @return a hash string representing the quasi-identifier values
     */
    public static String generateHashValue(Partition partition, List<ColumnInformation> columnInformationList) {
        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        List<String> row = partition.getMember().getRow(0);

        List<String> quasiValues = new ArrayList<>(quasiColumns.size());
        for (Integer quasiColumn : quasiColumns) {
            int columnIndex = quasiColumn;
            String value = row.get(columnIndex);
            quasiValues.add(value);
        }

        String key = StringUtils.join(quasiValues, ':');
        return Integer.toString(key.hashCode());
    }

    /**
     * Generates partitions from a dataset based on specific column indices.
     *
     * @param anonymized   the dataset
     * @param quasiColumns the column indices to use for partitioning
     * @return list of partitions
     */
    public static List<Partition> generatePartitionsByColumnIndex(IPVDataset anonymized, List<Integer> quasiColumns) {
        Map<String, List<List<String>>> partitionsMap = new HashMap<>();
        int n = anonymized.getNumberOfRows();

        for (int i = 0; i < n; i++) {
            List<String> row = anonymized.getRow(i);

            StringBuilder builder = new StringBuilder();
            try {
                for (Integer quasiColumn : quasiColumns) {
                    int columnIndex = quasiColumn;
                    String value = row.get(columnIndex);
                    builder.append(value);
                    builder.append(":");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("Error accessing row {}", row);
                throw e;
            }

            String key = builder.toString();
            List<List<String>> member = partitionsMap.get(key);

            if (member == null) {
                partitionsMap.put(key, new ArrayList<>());
                member = partitionsMap.get(key);
            }

            member.add(row);
        }

        List<Partition> partitions = new ArrayList<>();

        for (List<List<String>> l : partitionsMap.values()) {
            partitions.add(new InMemoryPartition(l));
        }

        return partitions;
    }

    /**
     * Generates partitions from a dataset based on the quasi-identifier columns.
     *
     * @param anonymized            the dataset
     * @param columnInformationList the column information list
     * @return list of partitions
     */
    public static List<Partition> generatePartitions(IPVDataset anonymized, List<ColumnInformation> columnInformationList) {
        List<Integer> quasiColumns = getColumnsByType(columnInformationList, ColumnType.QUASI);
        return generatePartitionsByColumnIndex(anonymized, quasiColumns);
    }

    /**
     * Generates partitions from a dataset based on the linking columns.
     *
     * @param anonymized            the dataset
     * @param columnInformationList the column information list
     * @return list of partitions for linking
     */
    public static List<Partition> generatePartitionsForLinking(IPVDataset anonymized, List<ColumnInformation> columnInformationList) {
        List<Integer> linkingColumns = getLinkingColumns(columnInformationList);

        if (linkingColumns.isEmpty()) throw new RuntimeException("No linking column information specified");

        return generatePartitionsByColumnIndex(anonymized, linkingColumns);
    }

    private static List<Integer> getLinkingColumns(List<ColumnInformation> columnInformationList) {
        final List<Integer> linkingColumns = new ArrayList<>();

        for (int i = 0; i < columnInformationList.size(); ++i) {
            if (columnInformationList.get(i).isForLinking()) {
                linkingColumns.add(i);
            }
        }

        return linkingColumns;
    }

    /**
     * Builds a bitmask of content requirements for all privacy constraints.
     *
     * @param privacyConstraints the list of privacy constraints
     * @return the combined content requirements bitmask
     */
    public static int buildPrivacyConstraintContentRequirements(List<PrivacyConstraint> privacyConstraints) {
        int requirements = 0;

        for (PrivacyConstraint constraint : privacyConstraints) {
            requirements |= constraint.contentRequirements();
        }

        return requirements;
    }

    /**
     * Checks whether all privacy constraints are satisfied for the given partition.
     *
     * @param privacyConstraints the privacy constraints to check
     * @param partition          the partition to evaluate
     * @param sensitiveColumns   indices of sensitive columns
     * @return true if all constraints are satisfied
     */
    public static boolean checkPrivacyConstraints(List<PrivacyConstraint> privacyConstraints, Partition partition, List<Integer> sensitiveColumns) {
        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            if (!privacyConstraint.check(partition, sensitiveColumns)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Initializes all privacy constraints with the given dataset and column information.
     *
     * @param dataset               the dataset
     * @param columnInformationList the column information list
     * @param privacyConstraints    the privacy constraints to initialize
     */
    public static void initializeConstraints(IPVDataset dataset, List<ColumnInformation> columnInformationList,
                                             List<PrivacyConstraint> privacyConstraints) {
        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            privacyConstraint.initialize(dataset, columnInformationList);
        }
    }
}
