/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;

public class InMemoryPartition implements Partition {
    private final IPVDataset dataset;
    private boolean isAnon;

    @Override
    public int size() {
        return dataset.getNumberOfRows();
    }

    @Override
    public double getNormalizedWidth(int qidColumn) {
        return 0;
    }

    @Override
    public IPVDataset getMember() {
        return this.dataset;
    }

    @Override
    public boolean isAnonymous() {
        return isAnon;
    }

    @Override
    public void setAnonymous(boolean value) {
        this.isAnon = value;
    }

    /**
     * Instantiates a new Ola partition.
     *
     * @param values the values
     */
    public InMemoryPartition(List<List<String>> values) {
        this.dataset = new IPVDataset(
                values,
                null,
                false
        );
    }

    public InMemoryPartition(int numberOfColumns) {
        this.dataset = new IPVDataset(
                numberOfColumns);
    }

    public InMemoryPartition(IPVDataset dataset) {
        this.dataset = dataset;
    }
}
