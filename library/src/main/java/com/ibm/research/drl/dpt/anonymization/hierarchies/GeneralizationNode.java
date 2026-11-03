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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A node in a generalization hierarchy tree. */
public class GeneralizationNode implements Serializable {
    /** The value stored at this node (upper-cased). */
    private final String value;
    /** Whether this node is a leaf in the hierarchy. */
    private final boolean isLeaf;

    /** Number of leaf nodes reachable from this node. */
    private int numberOfLeaves;
    /** Depth of this node from the top (root is at the maximum depth). */
    private int level;
    /** Ancestors of this node from the immediate parent to the root. */
    private List<GeneralizationNode> parents;
    /** Direct children of this node. */
    private final List<GeneralizationNode> children;

    /** Map from covered leaf values (upper-cased) to their nodes. */
    private final Map<String, GeneralizationNode> coverMap = new HashMap<>();

    /**
     * Returns the set of leaf values covered by this node.
     *
     * @return set of leaf values
     */
    public Set<String> getLeaveValues() {
        Set<String> results = new HashSet<>();
        if (isLeaf) {
            return results;
        }
        for (GeneralizationNode n : this.children) {
            if (n.isLeaf()) {
                results.add(n.getValue());
            } else {
                results.addAll(n.getLeaveValues());
            }
        }

        return results;
    }

    /**
     * Returns the list of leaf nodes covered by this node.
     *
     * @return list of leaf nodes
     */
    public List<GeneralizationNode> getLeaveNodes() {
        List<GeneralizationNode> results;

        if (isLeaf) {
            return Collections.emptyList();
        }

        results = new ArrayList<>();

        for (GeneralizationNode n : this.children) {
            if (n.isLeaf()) {
                results.add(n);
            } else {
                results.addAll(n.getLeaveNodes());
            }
        }

        return results;
    }

    /**
     * Returns whether this node is a leaf.
     *
     * @return true if this is a leaf node
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Constructs a GeneralizationNode.
     *
     * @param value     the value stored at this node
     * @param parent    the parent node, or {@code null} if this is the root
     * @param isLeaf    whether this node is a leaf
     * @param maxHeight the maximum height of the hierarchy
     */
    public GeneralizationNode(String value, GeneralizationNode parent, boolean isLeaf, int maxHeight) {
        this.value = value.toUpperCase();
        this.isLeaf = isLeaf;
        this.numberOfLeaves = 0;
        this.level = maxHeight - 1;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();

        coverMap.put(value.toUpperCase(), this);

        if (parent != null) {
            this.parents = new ArrayList<>(parent.getParents());
            this.parents.add(0, parent);
            parent.getChildren().add(this);
            this.level = parent.getLevel() - 1;

            for (GeneralizationNode p : this.parents) {
                p.getCoverMap().put(this.value, this);
                if (isLeaf) {
                    p.increaseLeafNumber();
                }
            }
        }
    }

    /**
     * Increase leaf number.
     */
    public void increaseLeafNumber() {
        this.numberOfLeaves += 1;
    }

    /**
     * Gets cover map.
     *
     * @return the cover map
     */
    public Map<String, GeneralizationNode> getCoverMap() {
        return this.coverMap;
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Gets parents.
     *
     * @return the parents
     */
    public List<GeneralizationNode> getParents() {
        return this.parents;
    }

    /**
     * Gets children.
     *
     * @return the children
     */
    public List<GeneralizationNode> getChildren() {
        return children;
    }

    /**
     * Length int.
     *
     * @return the int
     */
    public int length() {
        return this.numberOfLeaves;
    }

    /**
     * Gets number of leaves.
     *
     * @return the number of leaves
     */
    public int getNumberOfLeaves() {
        return this.numberOfLeaves;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Cover boolean.
     *
     * @param qidValue the qid value
     * @return the boolean
     */
    public boolean cover(String qidValue) {
        return coverMap.containsKey(qidValue.toUpperCase());
    }
}
