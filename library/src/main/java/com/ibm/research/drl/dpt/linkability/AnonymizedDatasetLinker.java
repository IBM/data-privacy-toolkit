/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.linkability;


import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class AnonymizedDatasetLinker {
    private final static Logger logger = LogManager.getLogger(AnonymizedDatasetLinker.class);

    private final DatasetLinker datasetLinker;

    public AnonymizedDatasetLinker(InputStream target, Collection<LinkInfo> linkInformation) throws IOException {
        this.datasetLinker = new DatasetLinker(target, linkInformation);
    }

    public Integer matchAnonymizedRow(List<String> anonymizedRow, Collection<LinkInfo> linkInformation, List<ColumnInformation> columnInformations) {
        List<List<Set<Integer>>> matching = new ArrayList<>(linkInformation.size());

        for (LinkInfo info : linkInformation) {
            List<Set<Integer>> infoMatching = new ArrayList<>();

            int sourceIndex = info.getSourceIndex();
            int targetIndex = info.getTargetIndex();

            String value = anonymizedRow.get(sourceIndex);

            ColumnInformation columnInformation = columnInformations.get(sourceIndex);
            if (columnInformation instanceof NumericalRange) {
                String[] tokens = value.split("-");
                Set<Integer> matchedRows;

                if (tokens.length == 1) {
                    Double numericValue = Double.parseDouble(value);
                    matchedRows = datasetLinker.matchValue(numericValue, targetIndex);
                } else {
                    Double minValue = Double.parseDouble(tokens[0]);
                    Double maxValue = Double.parseDouble(tokens[1]);
                    matchedRows = datasetLinker.matchValueRange(minValue, maxValue, targetIndex);
                }

                if (matchedRows == null) {
                    return 0;
                } else {
                    infoMatching.add(matchedRows);
                }
            } else {
                if (value.equals(info.getWildcharPattern())) {
                    continue;
                }

                if (!(columnInformation instanceof CategoricalInformation)) {
                    Set<Integer> matchedRows = datasetLinker.matchValue(value, targetIndex, info.isPrefixMatch());
                    if (matchedRows == null) {
                        return 0;
                    } else {
                        infoMatching.add(matchedRows);
                    }
                } else {
                    CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;
                    int level = categoricalInformation.getHierarchy().getNodeLevel(value);

                    if (level <= 0) {
                        Set<Integer> matched = datasetLinker.matchValue(value, targetIndex, false);
                        if (matched == null) {
                            return 0;
                        }
                        infoMatching.add(matched);
                    } else {
                        Set<String> leaves = categoricalInformation.getHierarchy().getNodeLeaves(value);

                        if (leaves == null) {
                            logger.info("null leaves for >" + value + "< source index is " + sourceIndex);
                        }

                        for (String leaf : Objects.requireNonNull(leaves)) {
                            Set<Integer> leafMatches = datasetLinker.matchValue(leaf, targetIndex, false);

                            if (leafMatches != null) {
                                infoMatching.add(leafMatches);
                            }
                        }

                        if (infoMatching.isEmpty()) {
                            return 0;
                        }
                    }
                }
            }

            matching.add(infoMatching);
        }

        if (matching.isEmpty()) {
            return datasetLinker.getNumberOfRows();
        }

        return intersectAll(matching);
    }

    private <T> Integer intersectAll(List<List<Set<T>>> setGroups) {
        if (setGroups.size() == 1) {
            int size = 0;

            for (Set<T> setGroup : setGroups.get(0)) {
                size += setGroup.size();
            }

            return size;
        }

        int result = 0;

        setGroups.sort(new Comparator<List<Set<T>>>() {
            private int sizeOf(List<Set<T>> sets) {
                int size = 0;

                for (Set<T> set : sets) {
                    size += set.size();
                }

                return size;
            }

            @Override
            public int compare(List<Set<T>> o1, List<Set<T>> o2) {
                return Integer.compare(sizeOf(o1), sizeOf(o2));
            }
        });

        for (Set<T> setGroup : setGroups.get(0)) {
            outer:
            for (T entry : setGroup) {
                inner:
                for (int i = 1; i < setGroups.size(); ++i) {
                    for (Set<T> setGroupI : setGroups.get(i)) {
                        if (setGroupI.contains(entry)) {
                            continue inner;
                        }
                    }
                    continue outer;
                }
                result += 1;
            }
        }

        return result;
    }

    public List<Integer> matchesPerRecord(IPVDataset source, Collection<LinkInfo> linkInformation) {
        final List<Integer> matchResults = new ArrayList<>();

        List<Integer> matchColumns = new ArrayList<>();
        for (LinkInfo info : linkInformation) {
            int sourceIndex = info.getSourceIndex();
            matchColumns.add(sourceIndex);
        }

        final List<Partition> partitions = AnonymizationUtils.generatePartitionsByColumnIndex(source, matchColumns);

        for (Partition partition : partitions) {
            IPVDataset members = partition.getMember();

            if (members.getNumberOfRows() == 0) {
                continue;
            }

            Integer matches = datasetLinker.matchRow(members.getRow(0), linkInformation);

            for (int i = 0; i < members.getNumberOfRows(); i++) {
                matchResults.add(matches);
            }
        }

        return matchResults;
    }

    public List<Integer> matchesPerRecord(IPVDataset source, Collection<LinkInfo> linkInformation, List<ColumnInformation> columnInformations) {
        final List<Integer> matchResults = new ArrayList<>();

        List<Integer> matchColumns = new ArrayList<>();
        for (LinkInfo info : linkInformation) {
            int sourceIndex = info.getSourceIndex();
            matchColumns.add(sourceIndex);
        }

        final List<Partition> partitions = AnonymizationUtils.generatePartitionsByColumnIndex(source, matchColumns);

        for (Partition partition : partitions) {
            IPVDataset members = partition.getMember();

            if (members.getNumberOfRows() == 0) {
                continue;
            }

            Integer matches = matchAnonymizedRow(members.getRow(0), linkInformation, columnInformations);

            for (int i = 0; i < members.getNumberOfRows(); i++) {
                matchResults.add(matches);
            }
        }

        return matchResults;
    }

    public List<Integer> matchesPerRecord(Partition partition, Collection<LinkInfo> linkInformation, List<ColumnInformation> columnInformations) {
        final List<Integer> matchResults = new ArrayList<>();

        IPVDataset members = partition.getMember();

        if (members.getNumberOfRows() == 0) {
            return Collections.emptyList();
        }

        for (int i = 0; i < members.getNumberOfRows(); i++) {
            Integer matches = matchAnonymizedRow(members.getRow(i), linkInformation, columnInformations);
            matchResults.add(matches);
        }

        return matchResults;
    }
}
