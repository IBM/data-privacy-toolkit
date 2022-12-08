/*******************************************************************
* IBM Confidential                                                *
*                                                                 *
* Copyright IBM Corp. 2021                                        *
*                                                                 *
* The source code for this program is not published or otherwise  *
* divested of its trade secrets, irrespective of what has         *
* been deposited with the U.S. Copyright Office.                  *
*******************************************************************/
package com.ibm.research.drl.prima.util;

/**
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class MutableTuple<K, V> {
    /**
     * The First.
     */
    K first;
    /**
     * The Second.
     */
    V second;

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    /**
     * Instantiates a new Tuple.
     *
     * @param first  the first
     * @param second the second
     */
    public MutableTuple(K first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets first.
     *
     * @return the first
     */
    public K getFirst() {
        return first;
    }

    /**
     * Gets second.
     *
     * @return the second
     */
    public V getSecond() {
        return second;
    }
    
    public void setFirst(K newValue) {
        this.first = newValue;
    }
    
    public void setSecond(V newValue) {
        this.second = newValue;
    }
}
