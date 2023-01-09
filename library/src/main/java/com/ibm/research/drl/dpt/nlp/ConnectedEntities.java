/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;


import java.util.Set;

public class ConnectedEntities {
    private final String first;
    private final String second;
    private final String endType;
    private final String endSubtype;
    private final Set<String> particles;
    
    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String getEndType() {
        return endType;
    }

    public String getEndSubtype() {
        return endSubtype;
    }

    public Set<String> getParticles() {
        return particles;
    }

    public ConnectedEntities(String first, String second, String endType, String endSubtype, Set<String> particles) {
        this.first = first;
        this.second = second;
        this.endType = endType;
        this.endSubtype = endSubtype;
        this.particles = particles;
    }
}
