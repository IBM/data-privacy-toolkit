/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.datasets.IPVDataset;

public interface Partition {
    /**
     * Size int.
     *
     * @return the int
     */
    int size();

    /**
     * Gets normalized width.
     *
     * @param qidColumn the qid column
     * @return the normalized width
     */
    double getNormalizedWidth(int qidColumn);

    /**
     * Gets member.
     *
     * @return the member
     */
    IPVDataset getMember();

    boolean isAnonymous();

    void setAnonymous(boolean value);
}

