/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.schema;

import java.util.BitSet;

/**
 * The type Item set.
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
