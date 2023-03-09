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
package com.ibm.research.drl.dpt.util;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;

/**
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class MapWithRandomPick<K, V> implements Serializable {
    private final Map<K, V> map;
    private final List<K> keyList = new ArrayList<K>();
    private final SecureRandom random;

    /**
     * Instantiates a new Map with random pick.
     *
     * @param map the map
     */
    public MapWithRandomPick(Map<K, V> map) {
        this.map = map;
        this.random = new SecureRandom();
    }

    /**
     * Sets key list.
     */
    public void setKeyList() {
        Iterator<K> iterator = map.keySet().iterator();

        keyList.clear();
        while (iterator.hasNext()) {
            K item = iterator.next();
            keyList.add(item);
        }
    }

    /**
     * Gets map.
     *
     * @return the map
     */
    public Map<K, V> getMap() {
        return this.map;
    }

    /**
     * Gets random key.
     *
     * @return the random key
     */
    public K getRandomKey() {
        int index = random.nextInt(keyList.size());
        return keyList.get(index);
    }

    /**
     * Gets random value.
     *
     * @return the random value
     */
    public V getRandomValue() {
        K key = getRandomKey();
        return map.get(key);
    }
}
