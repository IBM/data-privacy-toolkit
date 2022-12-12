/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.util.*;

public class MaterializedHierarchy implements GeneralizationHierarchy {
    private final Map<String, GeneralizationNode> nodes;
    private final List<List<String>> terms;
    private final Map<String, List<String>> leaves;
    private final Map<String, Integer> indices;
    private int termsAdded;
    private String topTerm;
    private int hierarchyHeight;

    public Integer getIndex(String term) {
        return this.indices.get(term.toLowerCase());
    }

    public MaterializedHierarchy() {
        this.terms = new ArrayList<>();
        this.nodes = new HashMap<>();
        this.leaves = new HashMap<>();
        this.indices = new HashMap<>();
        this.termsAdded = 0;
        this.hierarchyHeight = 0;
    }

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
    public String getTopTerm() {
        return this.topTerm;
    }

    public void add(String ... hierarchy) {
        add(Arrays.asList(hierarchy));
    }

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

       for(int i = 0; i < list.size(); i++) {
           String s = list.get(i).toUpperCase();
           boolean isLeaf = false;
           if (i == list.size() - 1) {
               isLeaf = true;
           }

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
