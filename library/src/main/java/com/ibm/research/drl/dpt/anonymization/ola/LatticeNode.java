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

import java.util.Collection;

/**
 * Represents a single node in the OLA generalization lattice, carrying per-dimension
 * generalization levels and anonymity status.
 */
public class LatticeNode {
    private final int[] values;
    private double suppressionRate;
    private Boolean isAnonymous = null;
    private Double informationLoss = null;
    private boolean tagged = false;

    /**
     * Returns whether this node has been tagged during lattice exploration.
     *
     * @return {@code true} if tagged
     */
    public boolean isTagged() {
        return tagged;
    }

    /**
     * Sets the tagged flag for this node.
     *
     * @param tagged {@code true} to mark this node as tagged
     */
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    /**
     * Returns the information loss value computed for this node, or {@code null} if not yet computed.
     *
     * @return the information loss, or {@code null}
     */
    public Double getInformationLoss() {
        return informationLoss;
    }

    /**
     * Sets the information loss value for this node.
     *
     * @param informationLoss the information loss value
     */
    public void setInformationLoss(Double informationLoss) {
        this.informationLoss = informationLoss;
    }

    /**
     * Returns whether this node is anonymous, or {@code null} if not yet evaluated.
     *
     * @return {@code true} if anonymous, {@code false} if not, or {@code null} if unknown
     */
    public Boolean getAnonymous() {
        return isAnonymous;
    }

    /**
     * Sets the anonymity status of this node.
     *
     * @param anonymous {@code true} if anonymous, {@code false} otherwise
     */
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

