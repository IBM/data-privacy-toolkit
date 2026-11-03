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

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;

import java.util.*;

/**
 * OLA (Optimal Lattice Anonymization) lattice structure that organises generalization nodes
 * for k-anonymity search.
 */
public class Lattice {
    private final List<ColumnInformation> quasiColumnInformationList;
    private final int[] maxLevel;
    private final double suppressionRate;
    private final AnonymityChecker simpleAnonymityChecker;

    private Map<Integer, Set<LatticeNode>> lattice;
    private Map<LatticeNode, LatticeNode> allNodes;

    private int totalNodes = 0;
    private int nodesChecked = 0;
    private int latticeMaxLevel = 0;

    private int tagsPerformed = 0;

    private final int[] maximumExplorationLevel;

    /**
     * Gets lattice max level.
     *
     * @return the lattice max level
     */
    public int getLatticeMaxLevel() {
        return latticeMaxLevel;
    }

    /**
     * Gets total nodes.
     *
     * @return the total nodes
     */
    public int getTotalNodes() {
        return totalNodes;
    }

    /**
     * Gets nodes checked.
     *
     * @return the nodes checked
     */
    public int getNodesChecked() {
        return nodesChecked;
    }

    /**
     * Returns the number of tagging operations performed during exploration.
     *
     * @return the number of tags performed
     */
    public int getTagsPerformed() {
        return tagsPerformed;
    }

    /**
     * Returns the internal lattice map (level → set of nodes).
     *
     * @return the lattice
     */
    public Map<Integer, Set<LatticeNode>> getLattice() {
        return this.lattice;
    }

    /**
     * Returns the maximum node in the lattice (the node with all dimensions at their maximum level).
     *
     * @return the maximum lattice node
     */
    public LatticeNode getMaxNode() {
        return new LatticeNode(getMaximumLevels());
    }

    /**
     * Computes the Cartesian product of the given level arrays, returning each combination as a list.
     *
     * @param levels a 2-D array where {@code levels[i]} contains the possible values for dimension {@code i}
     * @return all combinations as a list of integer lists
     */
    public static List<List<Integer>> calculateProduct(int[][] levels) {
        List<List<Integer>> productResult = new ArrayList<>();

        for (int k = 0; k < levels[0].length; k++) {
            int v = levels[0][k];

            List<Integer> l = new ArrayList<>();
            l.add(v);

            productResult.add(l);
        }

        for (int i = 1; i < levels.length; i++) {
            List<List<Integer>> result = new ArrayList<>();

            for (int k = 0; k < levels[i].length; k++) {
                int v = levels[i][k];

                for (List<Integer> l : productResult) {
                    List<Integer> newList = new ArrayList<>(l);
                    newList.add(v);
                    result.add(newList);
                }
            }

            productResult = result;
        }

        return productResult;
    }

    private void initialize() {
        this.lattice = new HashMap<>();
        this.allNodes = new HashMap<>();

        int[][] levels = new int[quasiColumnInformationList.size()][];

        for (int i = 0; i < quasiColumnInformationList.size(); i++) {
            int maxCurrentLevel = maxLevel[i];
            levels[i] = new int[maxCurrentLevel];
            for (int k = 0; k < maxCurrentLevel; k++) {
                levels[i][k] = k;
            }
        }

        List<List<Integer>> productResult = calculateProduct(levels);
        for (List<Integer> l : productResult) {
            LatticeNode node = new LatticeNode(l);
            int level = node.sum();

            if (level >= latticeMaxLevel) {
                latticeMaxLevel = level;
            }

            Set<LatticeNode> nodes = lattice.get(level);
            if (nodes == null) {
                lattice.put(level, new HashSet<>());
                nodes = lattice.get(level);
            }

            totalNodes++;
            nodes.add(node);
            allNodes.put(node, node);
        }
    }

    private int[] getMaximumLevels() {
        int[] levels = new int[quasiColumnInformationList.size()];
        System.arraycopy(maxLevel, 0, levels, 0, levels.length);
        return levels;
    }

    /**
     * Gets successors.
     *
     * @param node         the node
     * @param isKAnonymous the is k anonymous
     * @return the successors
     */
    public Collection<LatticeNode> getSuccessors(LatticeNode node, boolean isKAnonymous) {
        Collection<LatticeNode> successors = new ArrayList<>();

        int step = isKAnonymous ? 1 : -1;
        int targetLevel = node.sum() + step;

        if (targetLevel < 0) {
            return successors;
        }

        int[] values = node.getValues();
        int[][] candidateValues = new int[values.length][2];

        for (int k = 0; k < values.length; k++) {
            candidateValues[k][0] = values[k];
            candidateValues[k][1] = values[k] + step;
        }

        Collection<LatticeNode> possibleNodes = new ArrayList<>();

        List<List<Integer>> productResult = calculateProduct(candidateValues);
        for (List<Integer> list : productResult) {
            possibleNodes.add(new LatticeNode(list));
        }

        for (LatticeNode n : possibleNodes) {
            int level = n.sum();
            if (level == targetLevel && level <= latticeMaxLevel && allNodes.containsKey(n)) {
                successors.add(allNodes.get(n));
            }
        }

        return successors;
    }

    /**
     * Tag nodes.
     *
     * @param node         the node
     * @param isKAnonymous the is k anonymous
     */
    public void tagNodes(LatticeNode node, boolean isKAnonymous) {

        node.setAnonymous(isKAnonymous);
        node.setTagged(true);
        tagsPerformed++;

        for (LatticeNode n : getSuccessors(node, isKAnonymous)) {
            if (!n.isTagged()) {
                tagNodes(n, isKAnonymous);
            }
        }
    }

    /**
     * Check k anonymity boolean.
     *
     * @param node the node
     * @return the boolean
     */
    private boolean checkKAnonymity(LatticeNode node) {
        double nodeSuppressionRate = simpleAnonymityChecker.calculateSuppressionRate(node);
        node.setSuppressionRate(nodeSuppressionRate);
        return nodeSuppressionRate <= this.suppressionRate;
    }


    /**
     * Select lowest loss lattice node.
     *
     * @return the lattice node
     */
    public List<LatticeNode> getKMinimal() {
        Set<Integer> levels = lattice.keySet();
        Integer[] levelsArray = new Integer[levels.size()];
        Arrays.sort(levels.toArray(levelsArray));

        List<LatticeNode> matches = new ArrayList<>();

        for (Integer level : levelsArray) {
            Set<LatticeNode> nodes = lattice.get(level);
            for (LatticeNode node : nodes) {
                if (node.getAnonymous() && matchesMaximumExplorationLevel(node, this.maximumExplorationLevel)) {
                    matches.add(node);
                }
            }
        }

        return matches;
    }

    /**
     * Returns all anonymous nodes in the lattice, regardless of exploration level.
     *
     * @return the list of all anonymous lattice nodes
     */
    public List<LatticeNode> reportLossOnAllNodes() {
        Set<Integer> levels = lattice.keySet();
        Integer[] levelsArray = new Integer[levels.size()];
        Arrays.sort(levels.toArray(levelsArray));

        List<LatticeNode> matches = new ArrayList<>();

        for (Integer level : levelsArray) {
            Set<LatticeNode> nodes = lattice.get(level);
            for (LatticeNode node : nodes) {
                if (node.getAnonymous()) {
                    matches.add(node);
                }
            }
        }

        return matches;
    }

    /**
     * Returns whether the given node's generalization levels are all within the specified maximum levels.
     *
     * @param node                    the lattice node to test
     * @param maximumExplorationLevel the per-dimension maximum levels ({@code -1} means unconstrained)
     * @return {@code true} if the node is within bounds for every dimension
     */
    public static boolean matchesMaximumExplorationLevel(LatticeNode node, int[] maximumExplorationLevel) {

        int[] nodeLevels = node.getValues();

        if (nodeLevels.length != maximumExplorationLevel.length) {
            return false;
        }

        for (int i = 0; i < nodeLevels.length; i++) {
            if (maximumExplorationLevel[i] == -1) {
                continue;
            }

            if (nodeLevels[i] > maximumExplorationLevel[i]) {
                return false;
            }
        }

        return true;
    }

    private void explore(int minLevel, int maxLevel, int currentDepth) {

        if (minLevel == maxLevel) {
            return;
        }

        int currentLevel = (maxLevel + minLevel) / 2;

        Set<LatticeNode> nodes = lattice.get(currentLevel);
        if (nodes == null) {
            return;
        }

        for (final LatticeNode node : nodes) {
            Boolean isKAnonymous = node.getAnonymous();
            if (isKAnonymous == null) {
                nodesChecked++;
                isKAnonymous = checkKAnonymity(node);
                tagNodes(node, isKAnonymous);

                if (isKAnonymous) {
                    explore(minLevel, currentLevel, currentDepth + 1);
                } else {
                    explore(currentLevel, maxLevel, currentDepth + 1);
                }
            }

        }
    }

    private void checkForHoles() {
        Set<Integer> levels = lattice.keySet();
        Integer[] levelsArray = new Integer[levels.size()];
        Arrays.sort(levels.toArray(levelsArray));

        for (Integer level : levelsArray) {
            Set<LatticeNode> nodes = lattice.get(level);
            for (LatticeNode n : nodes) {
                if (n.getAnonymous() == null) {
                    nodesChecked++;
                    boolean isKAnonymous = checkKAnonymity(n);
                    tagNodes(n, isKAnonymous);
                }
            }
        }
    }

    /**
     * Explore.
     */
    public void explore() {
        explore(0, latticeMaxLevel, 0);
        checkForHoles();
    }

    /**
     * Constructs a Lattice from the given anonymity checker, column information, and suppression rate.
     *
     * @param anonymityChecker      the checker used to evaluate k-anonymity for a lattice node
     * @param columnInformationList the list of column information entries
     * @param suppressionRate       the maximum allowed suppression rate
     */
    public Lattice(AnonymityChecker anonymityChecker, List<ColumnInformation> columnInformationList, double suppressionRate) {
        this.suppressionRate = suppressionRate;

        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        int quasiIdentifiersLength = quasiColumns.size();
        this.quasiColumnInformationList = new ArrayList<>(quasiIdentifiersLength);

        this.simpleAnonymityChecker = anonymityChecker;

        for (final int quasiColumn : quasiColumns) {
            quasiColumnInformationList.add(columnInformationList.get(quasiColumn));
        }

        this.maxLevel = new int[quasiIdentifiersLength];
        this.maximumExplorationLevel = new int[quasiIdentifiersLength];

        for (int i = 0; i < quasiIdentifiersLength; i++) {
            CategoricalInformation categoricalInformation = (CategoricalInformation) quasiColumnInformationList.get(i);
            this.maxLevel[i] = categoricalInformation.getHierarchy().getHeight();
            this.maximumExplorationLevel[i] = categoricalInformation.getMaximumLevel();
        }

        initialize();
    }
}
