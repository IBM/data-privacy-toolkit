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
package com.ibm.research.drl.dpt.anonymization.hierarchies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.util.*;

/**
 * A generalization hierarchy stored as a materialized set of term paths, supporting
 * encoding (generalization) and lookup operations.
 */
public class MaterializedHierarchy implements GeneralizationHierarchy {
    /** Nodes keyed by upper-cased value. */
    private final Map<String, GeneralizationNode> nodes;
    /** All term paths (each path goes from leaf to root). */
    private final List<List<String>> terms;
    /** Term paths keyed by lower-cased leaf value. */
    private final Map<String, List<String>> leaves;
    /** Insertion-order index of each leaf, keyed by lower-cased value. */
    private final Map<String, Integer> indices;
    /** Number of term paths added so far. */
    private int termsAdded;
    /** The top (most general) term of this hierarchy. */
    private String topTerm;
    /** The maximum number of levels across all added term paths. */
    private int hierarchyHeight;

    /**
     * Returns the insertion-order index of the given term (case-insensitive), or {@code null} if absent.
     *
     * @param term the term to look up
     * @return the index, or {@code null}
     */
    public Integer getIndex(String term) {
        return this.indices.get(term.toLowerCase());
    }

    /**
     * Constructs an empty MaterializedHierarchy.
     */
    public MaterializedHierarchy() {
        this.terms = new ArrayList<>();
        this.nodes = new HashMap<>();
        this.leaves = new HashMap<>();
        this.indices = new HashMap<>();
        this.termsAdded = 0;
        this.hierarchyHeight = 0;
    }

    /**
     * Constructs a MaterializedHierarchy from a list of term paths.
     *
     * @param terms the list of term paths (each path goes from leaf to root)
     */
    @JsonCreator
    public MaterializedHierarchy(
            @JsonProperty("terms") List<List<String>> terms
    ) {
        this();
        for (List<String> hierarchy : terms) {
            add(hierarchy);
        }
    }

    /**
     * Gets terms.
     *
     * @return the terms
     */
    public List<List<String>> getTerms() {
        return this.terms;
    }

    /**
     * Gets top term.
     *
     * @return the top term
     */
    @JsonIgnore
    @Override
    public String getTopTerm() {
        return this.topTerm;
    }

    /**
     * Adds a term path expressed as a var-arg array.
     *
     * @param hierarchy the terms from leaf to root
     */
    public void add(String... hierarchy) {
        add(Arrays.asList(hierarchy));
    }

    /**
     * Adds a term path.
     *
     * @param hierarchy the list of terms from leaf to root
     */
    public void add(List<String> hierarchy) {
        String leaf = hierarchy.get(0);
        this.leaves.put(leaf.toLowerCase(), hierarchy);
        this.terms.add(hierarchy);
        this.indices.put(leaf.toLowerCase(), this.termsAdded);

        if (this.termsAdded == 0) {
            this.topTerm = hierarchy.get(hierarchy.size() - 1);
            nodes.put(this.topTerm.toUpperCase(), new GeneralizationNode(this.topTerm, null, false, hierarchy.size()));
        }

        this.hierarchyHeight = Math.max(this.hierarchyHeight, hierarchy.size());

        addNode(hierarchy);
        this.termsAdded++;
    }

    @Override
    public int getNodeLevel(String value) {
        GeneralizationNode node = nodes.get(value.toUpperCase());

        if (node == null) {
            return -1;
        }

        return this.hierarchyHeight - (node.getParents().size() + 1);
    }

    private void addNode(List<String> listOriginal) {
        List<String> list = new ArrayList<>(listOriginal);


       /*
       our input is a list of terms like :
           Married, Coupled, *
           Widowed, Alone, *
       */
        Collections.reverse(list);

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i).toUpperCase();
            boolean isLeaf = i == list.size() - 1;

            if (!nodes.containsKey(s)) {
                String previous = list.get(i - 1).toUpperCase();
                nodes.put(s, new GeneralizationNode(s, nodes.get(previous), isLeaf, list.size()));
            }
        }
    }

    @Override
    @JsonIgnore
    public int getHeight() {
        return this.terms.get(0).size();
    }

    @Override
    @JsonIgnore
    public long getTotalLeaves() {
        return this.leaves.size();
    }

    @Override
    public int leavesForNode(String value) {
        GeneralizationNode node = this.nodes.get(value.toUpperCase());
        if (node == null) {
            return 0;
        }

        return node.getNumberOfLeaves();
    }

    @Override
    public Set<String> getNodeLeaves(String value) {
        GeneralizationNode node = this.nodes.get(value.toUpperCase());
        if (node == null) {
            return null;
        }

        return node.getLeaveValues();
    }

    /**
     * Returns the {@link GeneralizationNode} for the given value, or {@code null} if not found.
     *
     * @param value the term value (case-insensitive)
     * @return the node, or {@code null}
     */
    public GeneralizationNode getNode(String value) {
        return this.nodes.get(value.toUpperCase());
    }

    @Override
    public String encode(String value, int level, boolean randomizeOnFail) {
        if (level == this.hierarchyHeight) {
            return this.topTerm;
        }

        GeneralizationNode node = this.nodes.get(value.toUpperCase());

        if (node == null) {
            if (randomizeOnFail) {
                return RandomGenerators.randomUIDGenerator(12);
            }
            return getTopTerm();
        }

        if (level == 0) {
            return value.toUpperCase();
        }

        level--;

        List<GeneralizationNode> hierarchy = node.getParents();

        if (level >= hierarchy.size()) {
            level = hierarchy.size() - 1;
        }

        return hierarchy.get(level).getValue().toUpperCase();
    }
}
