/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.Partition;

import java.io.Serializable;
import java.util.List;

public interface DPMechanism extends Serializable {
    void setOptions(AnonymizationAlgorithmOptions options);

    void analyseForParams(List<Partition> equivalenceClasses, int columnIndex);

    default String randomise(String value) {
        double numericalValue = Double.parseDouble(value);

        return Double.toString(randomise(numericalValue));
    };

    double randomise(double value);

    String getName();
}

