/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.generators;

import java.util.*;

/**
 * The type Layer generator.
 *
 */
public class LayerGenerator implements IPVGenerator {
    private final int nAttributes;
    private final List<Integer> counters;
    private final Set<ItemSet> banned;
    private ItemSet nextItem;
    private boolean generationCompleted;

    /**
     * Instantiates a new Layer generator.
     *
     * @param nAttributes the n attributes
     */
    public LayerGenerator(final int nAttributes) {
        this.nAttributes = nAttributes;
        this.banned = new TreeSet<>();
        this.counters = new ArrayList<>();
        this.counters.add(0);
        generationCompleted = false;
    }

    @Override
    public boolean isBanned(ItemSet candidate) {
        for (ItemSet is : banned) if (is.isSubSetOf(candidate)) return true;

        return false;
    }

    /**
     *
     * @return true iif it went to the next layer
     */
    private boolean increment() {
        int idx = 0;

        do {
            int m = max(idx);
            if (m > counters.get(idx)) {
                counters.set(idx, counters.get(idx) + 1);
                reset(idx);

                return false;
            } else {
                ++idx;
            }
        } while (counters.size() > idx);

        // larger than expected -> resize
        counters.add(init(counters.size() - 1));
        reset(counters.size() - 1);

        return true;
    }

    private void reset(int idx) {
        for (int i = idx; 0 < i; --i)
            counters.set(i - 1, counters.get(i) + 1);
    }

    private int init(int i) {
        return counters.size() - i - 1;
    }

    private int max(int idx) {
        return nAttributes - idx - 1;
    }

    private ItemSet createItemSet(List<Integer> counters) {
        ItemSet is = new ItemSet(counters.get(0));

        for (int i = 1; i < counters.size(); ++i)
            is.addItem(counters.get(i));

        return is;
    }

    @Override
    public void ban(final ItemSet itemSet) {
        Iterator<ItemSet> iterator = banned.iterator();

        while (iterator.hasNext()) {
            ItemSet bannedItemSet = iterator.next();
            if (itemSet.isSubSetOf(bannedItemSet)) {
                iterator.remove();
            } else if (bannedItemSet.isSubSetOf(itemSet))
                return;
        }

        banned.add(itemSet);
    }

    @Override
    public Collection<ItemSet> getBanned() {
        return banned;
    }

    @Override
    public boolean hasNext() {
        if (generationCompleted) return false;

        if (null != nextItem) return true;

        boolean steppedOver = false;
        do {
            if (nAttributes < counters.size()) {
                generationCompleted = false;
                return false;
            }

            ItemSet ret = createItemSet(counters);

            if (increment()) {
                if (steppedOver) {
                    // completed a layer without returning -> GENERATION COMPLETED!
                    generationCompleted = true;
                    return false;
                }

                steppedOver = true;
            }

            if (!isBanned(ret)) {
                nextItem = ret;
                return true;
            }
        } while (true);
    }

    @Override
    public ItemSet next() {
        if (generationCompleted || null == nextItem && !hasNext()) throw new NoSuchElementException();

        final ItemSet toReturn = nextItem;
        nextItem = null;

        return toReturn;
    }
}
