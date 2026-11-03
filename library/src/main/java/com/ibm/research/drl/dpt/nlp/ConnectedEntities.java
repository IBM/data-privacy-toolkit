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
package com.ibm.research.drl.dpt.nlp;


import java.util.Set;

/**
 * Represents a pair of connected NLP entities with relationship metadata.
 */
public class ConnectedEntities {
    private final String first;
    private final String second;
    private final String endType;
    private final String endSubtype;
    private final Set<String> particles;

    /**
     * Returns the first entity.
     *
     * @return the first entity string
     */
    public String getFirst() {
        return first;
    }

    /**
     * Returns the second entity.
     *
     * @return the second entity string
     */
    public String getSecond() {
        return second;
    }

    /**
     * Returns the end type of the relationship.
     *
     * @return the end type
     */
    public String getEndType() {
        return endType;
    }

    /**
     * Returns the end subtype of the relationship.
     *
     * @return the end subtype
     */
    public String getEndSubtype() {
        return endSubtype;
    }

    /**
     * Returns the particles connecting the two entities.
     *
     * @return the set of particle strings
     */
    public Set<String> getParticles() {
        return particles;
    }

    /**
     * Constructs a ConnectedEntities instance.
     *
     * @param first      the first entity
     * @param second     the second entity
     * @param endType    the relationship end type
     * @param endSubtype the relationship end subtype
     * @param particles  the connecting particles
     */
    public ConnectedEntities(String first, String second, String endType, String endSubtype, Set<String> particles) {
        this.first = first;
        this.second = second;
        this.endType = endType;
        this.endSubtype = endSubtype;
        this.particles = particles;
    }
}
