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
package com.ibm.research.drl.dpt.toolkit.anonymization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.anonymization.SensitiveColumnInformation;
import com.ibm.research.drl.dpt.anonymization.constraints.DistinctLDiversity;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.GenderHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.MaritalStatusHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.RaceHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.ReligionHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.ZIPCodeMaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.informationloss.AverageEquivalenceClassSize;
import com.ibm.research.drl.dpt.anonymization.informationloss.CategoricalPrecision;
import com.ibm.research.drl.dpt.anonymization.informationloss.Discernibility;
import com.ibm.research.drl.dpt.anonymization.informationloss.DiscernibilityStar;
import com.ibm.research.drl.dpt.anonymization.informationloss.GeneralizedLossMetric;
import com.ibm.research.drl.dpt.anonymization.informationloss.GlobalCertaintyPenalty;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetric;
import com.ibm.research.drl.dpt.anonymization.informationloss.NonUniformEntropy;
import com.ibm.research.drl.dpt.anonymization.informationloss.NumericalPrecision;
import com.ibm.research.drl.dpt.anonymization.informationloss.SensitiveSimilarityMeasure;
import com.ibm.research.drl.dpt.anonymization.kmeans.StrategyOptions;
import com.ibm.research.drl.dpt.anonymization.mondrian.CategoricalSplitStrategy;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class AnonymizationTaskOptionsTest {
    @Test
    public void serializationExample() throws JsonProcessingException {
        AnonymizationTaskOptions options = new AnonymizationTaskOptions(
                AnonymizationTaskOptions.AnonymizationAlgorithm.OLA,
                "/dev/null",
                Arrays.asList(
                        new KAnonymity(5),
                        new DistinctLDiversity(5)
                ),
                Arrays.asList(
                        new DefaultColumnInformation(false),
                        new CategoricalInformation(
                                GenderHierarchy.getInstance(),
                                ColumnType.QUASI, false
                        ),
                        new SensitiveColumnInformation(false)
                ),
                0.0,
                AnonymizationTaskOptions.InformationLossMetric.CP,
                CategoricalSplitStrategy.HIERARCHY_BASED,
                StrategyOptions.DUMMY,
                0.0
        );

        String  output = JsonUtils.MAPPER.writeValueAsString(options);

        assertNotNull(output);
        assertFalse(output.trim().isEmpty());
    }

    @Test
    public void testDeserialization() throws Exception {
        String input = "{\"algorithm\":\"OLA\",\"privacyConstraints\":[{\"type\":\"KAnonymity\",\"k\":5},{\"type\":\"DistinctLDiversity\",\"l\":5}],\"columnInformation\":[{\"class\":\"DefaultColumnInformation\",\"forLinking\":false},{\"class\":\"CategoricalInformation\",\"hierarchy\":{\"terms\":[[\"Male\",\"*\"],[\"Female\",\"*\"]]},\"columnType\":\"QUASI\",\"weight\":1.0,\"maximumLevel\":-1,\"forLinking\":false},{\"class\":\"SensitiveColumnInformation\",\"forLinking\":false}],\"suppressionRate\":0.0,\"informationLoss\":\"CP\",\"categoricalSplitStrategy\":\"HIERARCHY_BASED\",\"percentage\":0.0,\"pathToTrashFile\":\"/dev/null\",\"strategyOptions\":\"DUMMY\"}";

        AnonymizationTaskOptions options = JsonUtils.MAPPER.readValue(input, AnonymizationTaskOptions.class);

        assertNotNull(options);
    }

    @Test
    @Disabled
    public void generateTaskOptionsForTesting() throws Exception {
        AnonymizationTaskOptions options = new AnonymizationTaskOptions(
                AnonymizationTaskOptions.AnonymizationAlgorithm.OLA,
                null,
                List.of(new KAnonymity(5)),
                Arrays.asList(
                        new DefaultColumnInformation(false),
                        new DefaultColumnInformation(false),
                        new DefaultColumnInformation(false),
                        new DefaultColumnInformation(false),
                        new DefaultColumnInformation(false),
                        new CategoricalInformation(ZIPCodeMaterializedHierarchy.getInstance(), ColumnType.QUASI),
                        new CategoricalInformation(GenderHierarchy.getInstance(), ColumnType.QUASI),
                        new CategoricalInformation(RaceHierarchy.getInstance(), ColumnType.QUASI),
                        new CategoricalInformation(ReligionHierarchy.getInstance(), ColumnType.QUASI),
                        new CategoricalInformation(MaritalStatusHierarchy.getInstance(), ColumnType.QUASI),
                        new SensitiveColumnInformation(false)
                ),
                0.0,
                AnonymizationTaskOptions.InformationLossMetric.CP,
                CategoricalSplitStrategy.HIERARCHY_BASED,
                StrategyOptions.DUMMY,
                0.0
        );

        String s = JsonUtils.MAPPER.writeValueAsString(options);

        System.out.println(s);
    }

    @Test
    @Disabled
    public void getAllInformationMetricsTypes() {
        List<InformationMetric> metrics = Arrays.asList(
                new AverageEquivalenceClassSize(),
                new CategoricalPrecision(),
                new Discernibility(),
                new DiscernibilityStar(),
                new GeneralizedLossMetric(),
                new GlobalCertaintyPenalty(),
                new NonUniformEntropy(),
                new NumericalPrecision(),
                new SensitiveSimilarityMeasure()
        );

        for (InformationMetric ilm : metrics) {
            System.out.println(ilm.getShortName());
        }
    }
}
