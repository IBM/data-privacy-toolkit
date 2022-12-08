/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import java.io.Serializable;
import java.util.Objects;

public final class Tuple<K, V> implements Serializable {
    private final K first;
    private final V second;

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
    public Tuple(K first, V second) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(first, tuple.first) &&
                Objects.equals(second, tuple.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
