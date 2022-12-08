/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

import java.io.Serializable;

public class ZIPCode implements Serializable {
    public String getCode() {
        return code;
    }

    public Integer getPopulation() {
        return population;
    }

    private final String code;
    private final Integer population;

    public ZIPCode(String code, Integer population) {
        this.code = code;
        this.population = population;
    }
}


