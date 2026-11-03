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
package com.ibm.research.drl.dpt.spark.informationloss;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetricOptions;
import org.apache.spark.sql.Dataset;

import java.util.List;


public interface InformationMetricSpark {

    String getName();
    String getShortName();

    Double getLowerBound();
    Double getUpperBound();

    boolean supportsNumerical();
    boolean supportsCategorical();
    boolean supportsSuppressedDatasets();

    Double report();

    InformationMetricSpark initialize(Dataset<String> original, Dataset<String> anonymized, List<ColumnInformation> columnInformationList,
                                      int k, InformationMetricOptions options);
}
