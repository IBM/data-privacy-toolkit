/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;

/**
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class MapWithRandomPick<K,V> implements Serializable {
    private final Map<K,V> map;
    private final List<K> keyList = new ArrayList<K>();
    private final SecureRandom random;

    /**
     * Instantiates a new Map with random pick.
     *
     * @param map the map
     */
    public MapWithRandomPick(Map<K,V> map) {
        this.map = map;
        this.random = new SecureRandom();
    }

    /**
     * Sets key list.
     */
    public void setKeyList() {
        Iterator<K> iterator = map.keySet().iterator();

        keyList.clear();
        while(iterator.hasNext()) {
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
