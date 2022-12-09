/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;


public class Counter {
    public long counter;

    public Counter() {
        this(1L);
    }
    
    public Counter(long initial) {
        counter = initial;
    }
}
