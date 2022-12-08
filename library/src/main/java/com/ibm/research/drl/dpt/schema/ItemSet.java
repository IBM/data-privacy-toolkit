/*******************************************************************
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
*******************************************************************/
package com.ibm.research.drl.dpt.schema;

import java.util.BitSet;

/**
 * The type Item set.
 *
 */
public class ItemSet extends BitSet implements Comparable<ItemSet> {
    /**
     * Instantiates a new Item set.
     *
     * @param nbits the nbits
     */
    public ItemSet(int nbits) {
        super(nbits);
    }

    /**
     * Instantiates a new Item set.
     *
     * @param itemSet the item set
     */
    public ItemSet(final String itemSet) {
        super();

        for (final String bitIndex : itemSet.replace('{', ' ').replace('}', ' ').split(",")) {
            this.set(Integer.parseInt(bitIndex.trim()));
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(final ItemSet other) {
        if (null == other) {
            return 1;
        } else {
            return this.toString().compareTo(other.toString());
        }
    }
}
