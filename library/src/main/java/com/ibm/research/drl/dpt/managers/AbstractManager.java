/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import java.io.Serializable;
import java.util.Collection;

public abstract class AbstractManager<K> implements Manager, Serializable {
    public abstract Collection<K> getItemList();
}
