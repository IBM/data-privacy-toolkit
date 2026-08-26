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
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.Partition;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for differential privacy mechanisms.
 */
public interface DPMechanism extends Serializable {
    /**
     * Configures this mechanism with the given algorithm options.
     *
     * @param options the anonymization algorithm options
     */
    void setOptions(AnonymizationAlgorithmOptions options);

    /**
     * Analyses the equivalence classes to derive mechanism parameters for a given column.
     *
     * @param equivalenceClasses the list of partitions
     * @param columnIndex        the column index to analyse
     */
    void analyseForParams(List<Partition> equivalenceClasses, int columnIndex);

    /**
     * Randomises the given string value by parsing it as a double and applying the mechanism.
     *
     * @param value the string value to randomise
     * @return the randomised value as a string
     */
    default String randomise(String value) {
        double numericalValue = Double.parseDouble(value);

        return Double.toString(randomise(numericalValue));
    };

    /**
     * Randomises the given numeric value.
     *
     * @param value the numeric value to randomise
     * @return the randomised value
     */
    double randomise(double value);

    /**
     * Returns the name of this mechanism.
     *
     * @return the mechanism name
     */
    String getName();
}

