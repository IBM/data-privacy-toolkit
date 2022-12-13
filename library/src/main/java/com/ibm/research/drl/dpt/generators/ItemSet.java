/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.generators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemSet implements Comparable<ItemSet>, Serializable {
    private int size;
    private final BitSet bitSet;

    /**
     * Instantiates a new Item set.
     */
    public ItemSet() {
        this.bitSet = new BitSet();
    }

    /**
     * Instantiates a new Item set.
     *
     * @param items the items
     */
    public ItemSet(int... items) {
        if (Objects.isNull(items) || items.length == 0)
            throw new IllegalArgumentException("Items must be not null and more that 0");

        this.bitSet = new BitSet(items.length);

        for (int item : items) {
            addItem(item);
        }
    }

    /**
     * Instantiates a new Item set.
     *
     * @param set1 the set 1
     * @param set2 the set 2
     */
    public ItemSet(ItemSet set1, ItemSet set2) {
        this.bitSet = new BitSet(Math.max(set1.size(), set2.size()));
        addAll(set1);
        addAll(set2);
    }

    /**
     * Instantiates a new Item set.
     *
     * @param K    the k
     * @param item the item
     */
    public ItemSet(ItemSet K, int item) {
        this.bitSet = new BitSet(K.size());

        addAll(K);
        addItem(item);
    }

    /**
     * Instantiates a new Item set.
     *
     * @param k the k
     */
    public ItemSet(ItemSet k) {
        this.size = k.size;
        this.bitSet = new BitSet(k.bitSet.length());
        this.bitSet.or(k.bitSet);
    }

    /**
     * Add item.
     *
     * @param item the item
     */
    @JsonIgnore
    public void addItem(int item) {
        if (!bitSet.get(item)) {
            bitSet.set(item);
            size += 1L;
        }
    }

    /**
     * Remove item.
     *
     * @param item the item
     */
    public void removeItem(int item) {
        if (bitSet.get(item)) {
            bitSet.set(item, false);
            size -= 1L;
        }
    }

    /**
     * Add all.
     *
     * @param other the other
     */
    public void addAll(ItemSet other) {
        bitSet.or(other.bitSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemSet itemSet = (ItemSet) o;

        return (this.isSubSetOf(itemSet) && itemSet.isSubSetOf(this));

//        if (size != itemSet.size) return false;
//        return bitSet != null ? bitSet.equals(itemSet.bitSet) : itemSet.bitSet == null;
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + (bitSet != null ? bitSet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        boolean flag = false;
        for (int i = 0; i < bitSet.length(); ++i) {
            if (bitSet.get(i)) {
                if (flag) {
                    builder.append(',');
                } else {
                    flag = true;
                }
                builder.append(i);
            }
        }

        builder.append(']');

        return builder.toString();
    }

    @Override
    public int compareTo(ItemSet o) {
        if (null == o) return +1;

        int c = o.size - this.size;
        if (0 != c) {
            return c;
        }
        for (int i = 0, m = Math.max(bitSet.length(), o.bitSet.length()); i < m; ++i) {
            if (this.bitSet.get(i)) {
                if (!o.bitSet.get(i)) return -1;
            } else {
                if (o.bitSet.get(i)) return +1;
            }
        }
        return 0;
    }

    /**
     * Can be extend with boolean.
     *
     * @param item the item
     * @return the boolean
     */
    @JsonIgnore
    public boolean canBeExtendWith(int item) {
        return !bitSet.get(item);
    }

    /**
     * Is sub set of boolean.
     *
     * @param other the other
     * @return the boolean
     */
    public boolean isSubSetOf(ItemSet other) {
        for (int i = 0; i < bitSet.length(); ++i) {
            if (bitSet.get(i) && !other.bitSet.get(i)) return false;
        }

        return true;
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return size;
    }

    /**
     * Gets items.
     *
     * @return the items
     */
    public Collection<Integer> getItems() {
        Collection<Integer> items = new ArrayList<>(size);

        for (int i = 0; i < bitSet.length(); ++i) {
            if (bitSet.get(i))
                items.add(i);
        }

        return items;
    }

    /**
     * Sets items.
     *
     * @param items the items
     */
    public void setItems(Collection<Integer> items) {
        for (Integer item : items) {
            addItem(item);
        }
    }
}

