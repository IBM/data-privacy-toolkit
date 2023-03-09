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
package com.ibm.research.drl.dpt.linkability;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class DatasetLinker {
    private static final Logger logger = LogManager.getLogger(DatasetLinker.class);

    private int numberOfRows;
    private Map<String, Set<Integer>>[] targetIndices;
    private TreeMap<Double, Set<Integer>>[] targetIndicesNumerical; //we explicitly declare TreeMap since we need the ordered iterator

    public DatasetLinker(InputStream target, Collection<LinkInfo> targetColumns) throws IOException {
        buildTargetIndex(target, targetColumns);
    }

    private String createKey(String value) {
        return value.toUpperCase();
    }

    private void buildTargetIndex(InputStream target, Collection<LinkInfo> linkInfos) throws IOException {
        logger.debug("Building target index for {}", linkInfos);

        int nCols = Integer.MIN_VALUE;

        for (LinkInfo info : linkInfos) {
            if (info.getTargetIndex() > nCols) nCols = info.getTargetIndex();
        }

        nCols += 1;

        logger.debug("Minimum number of required columns: {}", nCols);

        targetIndices = (Map<String, Set<Integer>>[]) new Map[nCols];
        targetIndicesNumerical = (TreeMap<Double, Set<Integer>>[]) new TreeMap[nCols];

        for (LinkInfo info : linkInfos) {
            if (info.isNumerical()) {
                targetIndicesNumerical[info.getTargetIndex()] = new TreeMap<>();
            } else {
                targetIndices[info.getTargetIndex()] = new HashMap<>();
            }
        }

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withColumnSeparator(',').withSkipFirstDataRow(false);
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        try (Reader reader = new InputStreamReader(target)) {
            MappingIterator<String[]> it = mapper.readerFor(String[].class).with(schema).readValues(reader);
            int i = 0;
            while (it.hasNext()) {
                String[] nextRow = it.next();

                for (LinkInfo info : linkInfos) {
                    int targetColumn = info.getTargetIndex();

                    if (!info.isNumerical()) {
                        String key = createKey(nextRow[targetColumn]);

                        if (info.isPrefixMatch()) {
                            if (key.length() > info.getPrefixMatchLength())
                                key = key.substring(0, info.getPrefixMatchLength());
                        }

                        addIntoIndex(targetIndices[targetColumn], key, i);
                    } else {
                        Double value = Double.parseDouble(nextRow[targetColumn]);
                        addIntoIndex(targetIndicesNumerical[targetColumn], value, i);
                    }
                }
                i += 1;
            }

            this.numberOfRows = i;

            int j = 0;
            for (Map<String, Set<Integer>> index : targetIndices) {
                if (null != index) {
                    //logger.info("Index {} size: {}", ++j, index.size());
                }
            }
        }
    }

    private void addIntoIndex(Map<Double, Set<Integer>> index, Double value, int rowId) {
        Set<Integer> set = index.get(value);

        if (null == set) {
            set = new HashSet<>();

            index.put(value, set);
        }

        set.add(rowId);
    }

    private void addIntoIndex(Map<String, Set<Integer>> index, String key, int rowId) {
        Set<Integer> set = index.get(key);

        if (null == set) {
            set = new HashSet<>();

            index.put(key, set);
        }

        set.add(rowId);
    }

    public Set<Integer> matchValueRange(Double minValue, Double maxValue, int targetIndex) {
        Map<Double, Set<Integer>> map = targetIndicesNumerical[targetIndex];
        Set<Integer> results = new HashSet<>();

        for (Map.Entry<Double, Set<Integer>> entry : map.entrySet()) {
            Double v = entry.getKey();

            if (v >= minValue && v <= maxValue) {
                results.addAll(entry.getValue());
            }
        }

        return results;
    }

    public Set<Integer> matchValue(Double value, int targetIndex) {
        return targetIndicesNumerical[targetIndex].get(value);
    }

    public Set<Integer> matchValue(String value, int targetIndex, boolean isWithPrefix) {
        final Map<String, Set<Integer>> index = targetIndices[targetIndex];

        String key = createKey(value);

        Set<Integer> rows = index.get(key);

        return rows;
    }

    private <T> Set<T> intersect(Set<T> first, Set<T> second) {
        Set<T> small;
        Set<T> large;

        if (first.size() < second.size()) {
            small = first;
            large = second;
        } else {
            small = second;
            large = first;
        }

        Set<T> result = new HashSet<>(small.size());

        for (T entry : small) {
            if (large.contains(entry)) {
                result.add(entry);
            }
        }

        return result;
    }

    private <T> Integer intersectAll(List<Set<T>> sets) {
        if (sets.size() == 1) {
            return sets.get(0).size();
        }

        Collections.sort(sets, new Comparator<Set<T>>() {
            @Override
            public int compare(Set<T> o1, Set<T> o2) {
                return Integer.compare(o1.size(), o2.size());
            }
        });

        int results = 0;

        outer:
        for (T entry : sets.get(0)) {
            for (int i = 1; i < sets.size(); i++) {
                Set<T> setI = sets.get(i);
                if (!setI.contains(entry)) {
                    continue outer;
                }
            }

            results += 1;
        }

        return results;
    }

    public Integer matchRow(List<String> sourceRow, Collection<LinkInfo> linkInformation) {
        List<Set<Integer>> toBeLinked = new ArrayList<>();

        for (LinkInfo info : linkInformation) {
            int sourceIndex = info.getSourceIndex();
            int targetIndex = info.getTargetIndex();

            String value = sourceRow.get(sourceIndex);
            Set<Integer> targetRows;

            if (!info.isNumerical()) {
                String key = createKey(value);

                if (key.equals(info.getWildcharPattern())) {
                    continue;
                }

                targetRows = matchValue(value, targetIndex, info.isPrefixMatch());
            } else {
                Double numericValue = Double.parseDouble(value);
                targetRows = matchValue(numericValue, targetIndex);
            }

            if (targetRows == null) {
                return 0;
            }

            toBeLinked.add(targetRows);
        }

        if (toBeLinked.isEmpty()) {
            return null;
        }

        return intersectAll(toBeLinked);
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }
}
