/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.anonymization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.anonymization.SensitiveColumnInformation;
import com.ibm.research.drl.dpt.anonymization.constraints.DistinctLDiversity;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.*;
import com.ibm.research.drl.dpt.anonymization.informationloss.*;
import com.ibm.research.drl.dpt.anonymization.kmeans.StrategyOptions;
import com.ibm.research.drl.dpt.anonymization.mondrian.CategoricalSplitStrategy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class AnonymizationTaskOptionsTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void serializationExample() throws Exception {
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

        String  output = mapper.writeValueAsString(options);

        assertNotNull(output);
        assertFalse(output.trim().isEmpty());
    }

    @Test
    public void testDeserialization() throws Exception {
        String input = "{\"algorithm\":\"OLA\",\"privacyConstraints\":[{\"type\":\"KAnonymity\",\"k\":5},{\"type\":\"DistinctLDiversity\",\"l\":5}],\"columnInformation\":[{\"class\":\"DefaultColumnInformation\",\"forLinking\":false},{\"class\":\"CategoricalInformation\",\"hierarchy\":{\"terms\":[[\"Male\",\"*\"],[\"Female\",\"*\"]]},\"columnType\":\"QUASI\",\"weight\":1.0,\"maximumLevel\":-1,\"forLinking\":false},{\"class\":\"SensitiveColumnInformation\",\"forLinking\":false}],\"suppressionRate\":0.0,\"informationLoss\":\"CP\",\"categoricalSplitStrategy\":\"HIERARCHY_BASED\",\"percentage\":0.0,\"pathToTrashFile\":\"/dev/null\",\"strategyOptions\":\"DUMMY\"}";

        AnonymizationTaskOptions options = mapper.readValue(input, AnonymizationTaskOptions.class);

        assertNotNull(options);
    }

    @Test
    @Disabled
    public void generateTaskOptionsForTesting() throws Exception {
        AnonymizationTaskOptions options = new AnonymizationTaskOptions(
                AnonymizationTaskOptions.AnonymizationAlgorithm.OLA,
                null,
                Arrays.asList(new KAnonymity(5)),
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

        String s = mapper.writeValueAsString(options);

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
