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
package com.ibm.research.drl.dpt.anonymization.mondrian;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.*;

public class MondrianPartition implements Partition {

    private final IPVDataset member;
    private final List<String> middle;
    private final List<Interval> width;
    private final int[] allow;
    private final int qiLen;
    private final List<ColumnInformation> columnInformationList;
    private final List<Integer> quasiColumns;
    private final List<Integer> nonQuasiColumns;
    private final List<Integer> sensitiveColumns;
    private final List<PrivacyConstraint> privacyConstraints;
    private boolean isAnon;
    private final CategoricalSplitStrategy categoricalSplitStrategy;

    /**
     * Is splittable boolean.
     *
     * @return the boolean
     */
    public boolean isSplittable() {
        int sum = 0;
        for (int anAllow : allow) {
            sum += anAllow;
        }

        return sum != 0;
    }

    public IPVDataset getMember() {
        return this.member;
    }

    @Override
    public boolean isAnonymous() {
        return isAnon;
    }

    @Override
    public void setAnonymous(boolean value) {
        this.isAnon = value;
    }

    /**
     * Gets middle.
     *
     * @return the middle
     */
    public List<String> getMiddle() {
        return middle;
    }

    public int size() {
        return member.getNumberOfRows();
    }

    public void disallow(int quasiIndex) {
        allow[quasiIndex] = 0;
    }

    /**
     * Length int.
     *
     * @return the int
     */
    public int length() {
        return this.member.getNumberOfRows();
    }

    private boolean checkConstraints(Partition partition) {

        Partition anonymizedPartition = null;

        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            if (privacyConstraint.requiresAnonymizedPartition()) {
                anonymizedPartition = Mondrian.anonymizePartition((MondrianPartition) partition,
                        this.quasiColumns, this.nonQuasiColumns, this.columnInformationList, this.categoricalSplitStrategy);
                break;
            }
        }

        for (PrivacyConstraint privacyConstraint : privacyConstraints) {
            if (!privacyConstraint.requiresAnonymizedPartition()) {
                if (!privacyConstraint.check(partition, sensitiveColumns)) {
                    return false;
                }
            } else {
                if (!privacyConstraint.check(anonymizedPartition, sensitiveColumns)) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<Double> extractValues(int columnIndex) {
        List<Double> values = new ArrayList<>();
        Set<String> valueSet = new HashSet<>();

        int numberOfRows = this.member.getNumberOfRows();

        for (int i = 0; i < numberOfRows; i++) {
            String v = this.member.get(i, columnIndex);
            if (!valueSet.contains(v)) {
                values.add(Double.parseDouble(v));
                valueSet.add(v);
            }
        }

        return values;
    }

    private double median(List<Double> values) {
        double median;
        if (values.size() % 2 == 0)
            median = (values.get(values.size() / 2) + values.get(values.size() / 2 - 1)) / 2.0;
        else
            median = values.get(values.size() / 2);

        return median;
    }

    private MedianInformation calculateMedianForNumerical(List<Double> values) {
        Collections.sort(values);

        double median = median(values);
        double low = values.get(0);
        double high = values.get(values.size() - 1);

        return new MedianInformation(low, high, median);
    }

    public static String generateMiddleKey(double low, double high) {
        if (low == high) {
            return Double.toString(low);
        }

        return low + "-" + high;
    }

    public List<MondrianPartition> splitNumerical(int quasiIndex, int level) {
        int columnIndex = this.quasiColumns.get(quasiIndex);
        List<Double> values = extractValues(columnIndex);

        MedianInformation medianInformation = calculateMedianForNumerical(values);
        double median = medianInformation.getSplitValueNumerical();

        middle.set(columnIndex, generateMiddleKey(medianInformation.getLow(), medianInformation.getHigh()));
        width.set(columnIndex, new Interval(medianInformation.getLow(), medianInformation.getHigh()));

        int numberOfRows = this.member.getNumberOfRows();

        List<List<String>> leftValues = new ArrayList<>();
        List<List<String>> rightValues = new ArrayList<>();

        double leftMax = Double.MIN_VALUE;
        double leftMin = Double.MAX_VALUE;

        double rightMax = Double.MIN_VALUE;
        double rightMin = Double.MAX_VALUE;

        for (int i = 0; i < numberOfRows; i++) {
            double v = Double.parseDouble(this.member.get(i, columnIndex));

            if (v < median) { //assign left
                leftValues.add(this.member.getRow(i));

                leftMax = Math.max(v, leftMax);
                leftMin = Math.min(v, leftMin);
            } else { //assign right
                rightValues.add(this.member.getRow(i));

                rightMax = Math.max(v, rightMax);
                rightMin = Math.min(v, rightMin);
            }
        }

        if (leftValues.isEmpty() || rightValues.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> leftMiddle = new ArrayList<>(middle);
        leftMiddle.set(columnIndex, generateMiddleKey(leftMin, leftMax));

        List<String> rightMiddle = new ArrayList<>(middle);
        rightMiddle.set(columnIndex, generateMiddleKey(rightMin, rightMax));

        List<Interval> leftWidth = copyList(width);
        leftWidth.set(columnIndex, new Interval(leftMin, leftMax));

        List<Interval> rightWidth = copyList(width);
        rightWidth.set(columnIndex, new Interval(rightMin, rightMax));

        IPVDataset leftData = new IPVDataset(leftValues,
                null,
                false);
        IPVDataset rightData = new IPVDataset(rightValues,
                null,
                false);

        MondrianPartition lhs = new MondrianPartition(leftData, leftMiddle, leftWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);
        MondrianPartition rhs = new MondrianPartition(rightData, rightMiddle, rightWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);

        if (!checkConstraints(lhs) || !checkConstraints(rhs)) {
            return Collections.emptyList();
        }

        return Arrays.asList(lhs, rhs);
    }

    private List<Interval> copyList(List<Interval> w) {
        List<Interval> newList = new ArrayList<>(w.size());

        for (Interval interval : w) {
            if (interval == null) {
                newList.add(null);
            } else {
                newList.add(interval.clone());
            }
        }

        return newList;
    }

    private List<MondrianPartition> splitCategorical(int quasiIndex, int level) {
        switch (categoricalSplitStrategy) {
            case ORDER_BASED:
                return splitCategoricalOrderBased(quasiIndex, level);
            case HIERARCHY_BASED:
            default:
                return splitCategoricalHierarchyBased(quasiIndex);
        }
    }

    private Integer getIndexForValue(String v, MaterializedHierarchy materializedHierarchy) {
        Integer index = materializedHierarchy.getIndex(v);
        if (index == null) {
            index = -1;
        }

        return index;
    }

    private List<Double> extractCategoricalIndices(int columnIndex, MaterializedHierarchy materializedHierarchy) {
        Set<Integer> indices = new HashSet<>();
        int numberOfRows = this.member.getNumberOfRows();

        for (int i = 0; i < numberOfRows; i++) {
            String v = this.member.get(i, columnIndex);
            indices.add(getIndexForValue(v, materializedHierarchy));
        }

        List<Double> results = new ArrayList<>();
        for (Integer index : indices) {
            results.add((double) index);
        }

        return results;
    }

    private List<MondrianPartition> splitCategoricalOrderBased(int quasiIndex, int level) {
        int columnIndex = this.quasiColumns.get(quasiIndex);
        MaterializedHierarchy materializedHierarchy = (MaterializedHierarchy)
                ((CategoricalInformation) this.columnInformationList.get(columnIndex)).getHierarchy();

        List<Double> categoricalIndices = extractCategoricalIndices(columnIndex, materializedHierarchy);
        Collections.sort(categoricalIndices);
        double median = median(categoricalIndices);

        List<List<String>> leftValues = new ArrayList<>();
        List<List<String>> rightValues = new ArrayList<>();

        double rightMin = Double.MAX_VALUE;
        double rightMax = Double.MIN_VALUE;
        double leftMin = Double.MAX_VALUE;
        double leftMax = Double.MIN_VALUE;

        int numberOfRows = this.member.getNumberOfRows();

        for (int i = 0; i < numberOfRows; i++) {
            String v = this.member.get(i, columnIndex);
            double index = getIndexForValue(v, materializedHierarchy);

            if (index < median) {
                if (index <= leftMin) {
                    leftMin = index;
                }

                if (index >= leftMax) {
                    leftMax = index;
                }

                leftValues.add(this.member.getRow(i));
            } else {
                if (index <= rightMin) {
                    rightMin = index;
                }

                if (index >= rightMax) {
                    rightMax = index;
                }

                rightValues.add(this.member.getRow(i));
            }
        }

        if (leftValues.isEmpty() || rightValues.isEmpty()) {
            return Collections.emptyList();
        }


        List<String> leftMiddle = new ArrayList<>(middle);
        leftMiddle.set(columnIndex, null);

        List<String> rightMiddle = new ArrayList<>(middle);
        rightMiddle.set(columnIndex, null);

        List<Interval> leftWidth = copyList(width);
        leftWidth.set(columnIndex, new Interval(leftMin, leftMax));

        List<Interval> rightWidth = copyList(width);
        rightWidth.set(columnIndex, new Interval(rightMin, rightMax));

        IPVDataset leftData = new IPVDataset(
                leftValues,
                null,
                false
        );
        IPVDataset rightData = new IPVDataset(
                rightValues,
                null,
                false
        );

        MondrianPartition lhs = new MondrianPartition(leftData, leftMiddle, leftWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);
        MondrianPartition rhs = new MondrianPartition(rightData, rightMiddle, rightWidth, columnInformationList, privacyConstraints, categoricalSplitStrategy);

        if (!checkConstraints(lhs) || !checkConstraints(rhs)) {
            return Collections.emptyList();
        }

        return Arrays.asList(lhs, rhs);
    }


    private List<MondrianPartition> splitCategoricalHierarchyBased(int quasiIndex) {

        int columnIndex = this.quasiColumns.get(quasiIndex);
        MaterializedHierarchy materializedHierarchy = (MaterializedHierarchy)
                ((CategoricalInformation) this.columnInformationList.get(columnIndex)).getHierarchy();

        String middleValue = this.middle.get(columnIndex);
        List<GeneralizationNode> children = materializedHierarchy.getNode(middleValue).getChildren();

        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<List<String>>> groups = new ArrayList<>(children.size());
        for (int i = 0; i < children.size(); i++) {
            groups.add(new ArrayList<>());
        }

        int numberOfRows = this.member.getNumberOfRows();
        for (int i = 0; i < numberOfRows; i++) {
            String v = this.member.get(i, columnIndex);


            for (int j = 0; j < children.size(); j++) {
                GeneralizationNode n = children.get(j);
                if (n.cover(v)) {
                    groups.get(j).add(this.member.getRow(i));
                    break;
                }
            }
        }

        List<MondrianPartition> partitions = new ArrayList<>();

        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).isEmpty()) {
                continue;
            }

            String childValue = children.get(i).getValue();
            List<String> partitionMiddle = new ArrayList<>(middle);
            partitionMiddle.set(columnIndex, childValue);

            List<Interval> partitionWidth = copyList(width);
            partitionWidth.set(columnIndex, new Interval(0d, (double) children.get(i).length()));

            IPVDataset partitionData = new IPVDataset(
                    groups.get(i),
                    null,
                    false
            );

            MondrianPartition partition = new MondrianPartition(partitionData, partitionMiddle, partitionWidth,
                    columnInformationList, privacyConstraints, categoricalSplitStrategy);

            if (!checkConstraints(partition)) {
                return Collections.emptyList();
            }

            partitions.add(partition);
        }

        return partitions;
    }

    public List<MondrianPartition> split(int quasiIndex, int level) {
        ColumnInformation columnInformation = columnInformationList.get(this.quasiColumns.get(quasiIndex));

        if (!columnInformation.isCategorical()) {
            return splitNumerical(quasiIndex, level);
        } else {
            return splitCategorical(quasiIndex, level);
        }
    }

    public double getNormalizedWidth(int columnIndex) {
        ColumnInformation columnInformation = columnInformationList.get(columnIndex);
        double dividend;

        if (columnInformation.isCategorical()) {
            CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;
            String topTerm = categoricalInformation.getHierarchy().getTopTerm();
            dividend = ((MaterializedHierarchy) categoricalInformation.getHierarchy()).getNode(topTerm).length();
        } else {
            NumericalRange numericalInformation = (NumericalRange) columnInformation;
            dividend = numericalInformation.getRange();
        }

        return width.get(columnIndex).getRange() / dividend;
    }

    /**
     * Choose dimension int.
     *
     * @return the int
     */
    public int chooseDimension() {
        int maxWidth = -1;
        int maxDim = -1;

        for (int i = 0; i < qiLen; i++) {
            if (allow[i] == 0) {
                continue;
            }

            int normalizedWidth = (int) getNormalizedWidth(this.quasiColumns.get(i));
            if (normalizedWidth > maxWidth) {
                maxWidth = normalizedWidth;
                maxDim = i;
            }
        }

        if (maxWidth > 1) {
            throw new RuntimeException("maxWidth > 1");
        }

        if (maxDim == -1) {
            throw new RuntimeException("maxDim == -1");
        }

        return maxDim;
    }

    /**
     * Instantiates a new Mondrian partition.
     *
     * @param data                  the data
     * @param middle                the middle
     * @param columnInformationList the column information list
     */
    public MondrianPartition(IPVDataset data, List<String> middle, List<Interval> width,
                             List<ColumnInformation> columnInformationList,
                             List<PrivacyConstraint> privacyConstraints, CategoricalSplitStrategy categoricalSplitStrategy) {

        this.member = data;
        this.middle = middle;
        this.width = width;
        this.quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        this.nonQuasiColumns = Mondrian.getNonQuasiColumns(data.getNumberOfColumns(), this.quasiColumns);
        this.sensitiveColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.SENSITIVE);

        this.qiLen = quasiColumns.size();
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
        this.categoricalSplitStrategy = categoricalSplitStrategy;

        this.allow = new int[qiLen];
        Arrays.fill(allow, 1);
    }
}
