/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

import java.util.Collection;

public class LatticeNode {
    private final int[] values;
    private double suppressionRate;
    private Boolean isAnonymous = null;
    private Double informationLoss = null;
    private boolean tagged = false;

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public Double getInformationLoss() {
        return informationLoss;
    }

    public void setInformationLoss(Double informationLoss) {
        this.informationLoss = informationLoss;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    /**
     * Gets suppression rate.
     *
     * @return the suppression rate
     */
    public double getSuppressionRate() {
        return suppressionRate;
    }

    /**
     * Sets suppression rate.
     *
     * @param suppressionRate the suppression rate
     */
    public void setSuppressionRate(double suppressionRate) {
        this.suppressionRate = suppressionRate;
    }

    /**
     * Get values int [ ].
     *
     * @return the int [ ]
     */
    public int[] getValues() {
        return values;
    }


    /**
     * Sum int.
     *
     * @return the int
     */
    public int sum() {
        int sum = 0;

        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }

        return sum;
    }

    @Override
    public boolean equals(Object o) {
        return !(null == o || !(o instanceof LatticeNode)) && equals((LatticeNode) o);
    }

    /**
     * Equals boolean.
     *
     * @param otherNode the other node
     * @return the boolean
     */
    public boolean equals(LatticeNode otherNode) {
        int[] otherValues = otherNode.getValues();

        if (values.length != otherValues.length) {
            return false;
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] != otherValues[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Is descendent boolean.
     *
     * @param otherNode the other node
     * @return the boolean
     */
    public boolean isDescendent(LatticeNode otherNode) {
        if (this.equals(otherNode)) {
            return false;
        }

        int[] otherValues = otherNode.getValues();

        for (int i = 0; i < values.length; i++) {
            if (values[i] < otherValues[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        int i;
        for (i = 0; i < (values.length - 1); i++) {
            builder.append(values[i] + "");
            builder.append(":");
        }

        builder.append(values[i] + "");
        return builder.toString();
    }

    /**
     * Instantiates a new Lattice node.
     *
     * @param values the values
     */
    public LatticeNode(int[] values) {
        this.values = values;
    }

    /**
     * Instantiates a new Lattice node.
     *
     * @param v the v
     */
    public LatticeNode(Collection<Integer> v) {
        this.values = new int[v.size()];
        int index = 0;
        for (Integer i : v) {
            this.values[index] = i.intValue();
            index++;
        }
    }
}

