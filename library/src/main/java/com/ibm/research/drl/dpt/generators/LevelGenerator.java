/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.generators;

import java.util.*;

public class LevelGenerator implements IPVGenerator {
    private final Set<ItemSet> banned;

    private final int K;
    private final int N;

    private List<Integer> columns;
    private List<Integer> currentCombination;
    private int[] bitVector;
    private int endIndex = 0;
    private ItemSet nextItem;

    public LevelGenerator(Collection<ItemSet> banned, int nAttributes, int k) {
        this.K = k;
        this.N = nAttributes;

        this.banned = new TreeSet<>();
        this.banned.addAll(banned);
        this.currentCombination = new ArrayList<>();
        this.bitVector = new int[K + 1];

        for (int i = 0; i <= K; i++) {
            bitVector[i] = i;
        }

        for (int i = 0; i < K; i++) {
            this.currentCombination.add(null);
        }

        if (N > 0) {
            endIndex = 1;
        }

        this.columns = new ArrayList<>();
        for(int i = 0; i < nAttributes; i++) {
            this.columns.add(i);
        }
    }

    public LevelGenerator(int nAttributes, int level) {
        this(new HashSet<ItemSet>(), nAttributes, level);
    }

    @Override
    public void ban(ItemSet itemSet) {

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
        return this.banned;
    }

    @Override
    public boolean isBanned(ItemSet candidate) {
        for (ItemSet is : banned) if (is.isSubSetOf(candidate)) return true;
        return false;
    }

    @Override
    public boolean hasNext() {
        do {
            if ((endIndex == 0) || (K > N)) {
                return false;
            }

            generateNext();

            nextItem = createItemSet(currentCombination);

            if(!isBanned(nextItem)) {
                return true;
            }
        } while(true);
    }

    private ItemSet createItemSet(List<Integer> counters) {
        ItemSet is = new ItemSet(counters.get(0));

        for (int i = 1; i < counters.size(); ++i)
            is.addItem(counters.get(i));

        return is;
    }

    private void generateNext() {
        for (int i = 1; i <= K; i++) {
            int index = bitVector[i] - 1;
            if (N > 0) {
                currentCombination.set(i - 1, columns.get(index));
            }
        }

        endIndex = K;

        while (bitVector[endIndex] == N - K + endIndex) {
            endIndex--;
            if (endIndex == 0)
                break;
        }

        bitVector[endIndex]++;
        for (int i = endIndex + 1; i <= K; i++) {
            bitVector[i] = bitVector[i - 1] + 1;
        }
    }

    @Override
    public ItemSet next() {
        return nextItem;
    }
}

