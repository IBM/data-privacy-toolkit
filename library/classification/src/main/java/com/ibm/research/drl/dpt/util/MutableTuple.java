/*******************************************************************
*                                                                 *
* Copyright IBM Corp. 2021                                        *
*                                                                 *
*******************************************************************/
package com.ibm.research.drl.dpt.util;

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
