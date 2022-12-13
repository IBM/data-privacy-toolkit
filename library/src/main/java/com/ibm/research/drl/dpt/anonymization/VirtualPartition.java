/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.datasets.IPVDataset;

public class VirtualPartition implements Partition {
    private final int size;
    private boolean anonymous;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public double getNormalizedWidth(int qidColumn) {
        return 0;
    }

    @Override
    public IPVDataset getMember() {
        return null;
    }

    @Override
    public boolean isAnonymous() {
        return anonymous;
    }

    @Override
    public void setAnonymous(boolean value) {
        this.anonymous = value;
    }

    public VirtualPartition(int size) {
        this.size = size;
    }
}

