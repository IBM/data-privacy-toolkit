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
package com.ibm.research.drl.dpt.anonymization.ola;

/**
 * Interface for checking anonymity and computing suppression rates in OLA.
 */
public interface AnonymityChecker {
    /**
     * Calculates the suppression rate for the given lattice node.
     *
     * @param node the lattice node to evaluate
     * @return the suppression rate as a value between 0.0 and 1.0
     */
    double calculateSuppressionRate(LatticeNode node);
}

