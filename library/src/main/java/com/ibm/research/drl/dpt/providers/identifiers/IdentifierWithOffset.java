/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.util.Tuple;

public interface IdentifierWithOffset {
    Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String data);
}
