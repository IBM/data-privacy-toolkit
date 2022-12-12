/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies;

import java.io.Serializable;
import java.util.*;

public class GeneralizationNode implements Serializable {
    private final String value;
    private final boolean isLeaf;

    private int numberOfLeaves;
    private int level;
    private List<GeneralizationNode> parents;
    private final List<GeneralizationNode> children;

    private final Map<String, GeneralizationNode> coverMap = new HashMap<>();

    /* TODO: add test*/
    public Set<String> getLeaveValues() {
        Set<String> results = new HashSet<>();
        if (isLeaf) {
            return results;
        }
        for(GeneralizationNode n: this.children) {
            if (n.isLeaf()) {
                results.add(n.getValue());
            }
            else {
                results.addAll(n.getLeaveValues());
            }
        }

        return results;
    }

    /* TODO: add test*/
    public List<GeneralizationNode> getLeaveNodes() {
        List<GeneralizationNode> results;
        
        if (isLeaf) {
            return Collections.emptyList();
        }
        
        results = new ArrayList<>();
        
        for(GeneralizationNode n: this.children) {
            if (n.isLeaf()) {
                results.add(n);
            }
            else {
                results.addAll(n.getLeaveNodes());
            }
        }

        return results;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Instantiates a new Generalization node.
     *
     * @param value  the value
     * @param parent the parent
     * @param isLeaf the is leaf
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

            for(GeneralizationNode p: this.parents) {
                p.getCoverMap().put(this.value, this);
                if (isLeaf == true) {
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
