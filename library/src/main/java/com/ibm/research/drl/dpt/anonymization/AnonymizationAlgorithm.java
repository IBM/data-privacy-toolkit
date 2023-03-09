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
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AnonymizationAlgorithm {
    TransformationType getTransformationType();

    List<ColumnInformation> getColumnInformationList();

    /* returns the EQ classes based on the original records */
    List<Partition> getOriginalPartitions();

    /* returns the EQ classes based on the anonymized records */
    List<Partition> getAnonymizedPartitions();

    /**
     * Initialize anonymization algorithm.
     *
     * @param dataset         the dataset
     * @param vulnerabilities the vulnerabilities
     * @param sensitiveFields the sensitive fields
     * @param fieldTypes      the field types
     * @param options         the options
     * @return the anonymization algorithm
     */
    AnonymizationAlgorithm initialize(IPVDataset dataset, Collection<IPVVulnerability> vulnerabilities,
                                      Collection<String> sensitiveFields, Map<String, ProviderType> fieldTypes,
                                      List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options);

    /**
     * Initialize anonymization algorithm.
     *
     * @param dataset               the dataset
     * @param columnInformationList the column information list
     * @param options               the options
     * @return the anonymization algorithm
     */
    AnonymizationAlgorithm initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList,
                                      List<PrivacyConstraint> privacyConstraints, AnonymizationAlgorithmOptions options);

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets description.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Apply ipv dataset.
     *
     * @return the ipv dataset
     */
    IPVDataset apply();

    /**
     * Report suppression rate double.
     *
     * @return the double
     */
    double reportSuppressionRate();
}
