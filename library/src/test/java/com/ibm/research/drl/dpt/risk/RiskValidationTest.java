/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;


import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

;

public class RiskValidationTest {
    private static final Logger logger = LogManager.getLogger(RiskValidationTest.class);

    public static List<ColumnInformation> getFloridaColumnInformation() {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                ColumnType.QUASI, true));

        return columnInformation;
    }
    
    @Test
    @Disabled
    public void testRiskMetrics() throws Exception {
        int minK = 2;
        int maxK = 15;
        int kInterval = 1;

        double minSuppression = 5.0;
        double maxSuppression = 5.0;
        double suppressionInterval = 1.0;
        
        int populationSize = 13443;
        double gamma = 0.1;
        double confidence = 0.95;
        
        final Map<String, String> riskMetricOptions = new HashMap<>();
        riskMetricOptions.put("N", Integer.toString(populationSize));
        riskMetricOptions.put("gamma", Double.toString(gamma));
        riskMetricOptions.put("useGlobalP", Boolean.toString(Boolean.FALSE));
        riskMetricOptions.put("population", Integer.toString(populationSize));
        riskMetricOptions.put("HRMConfidence", Double.toString(confidence));
       
        String populationResourceName = "/population_10k.txt";
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/sample_1k.txt"), false, ',', '"', false); //TODO: create
        
        List<ColumnInformation> columnInformation = getFloridaColumnInformation(); 
        
        IntStream.rangeClosed(minK, maxK)
                .boxed()
                .forEach(k -> {
                    for (double suppression = minSuppression; suppression <= maxSuppression; suppression += suppressionInterval) {
                        final List<PrivacyConstraint> privacyConstraints = Collections.singletonList(new KAnonymity(k));
                        final OLAOptions options = new OLAOptions(suppression);

                        final OLA ola = new OLA();
                        ola.initialize(originalDataset,
                                columnInformation,
                                privacyConstraints,
                                options);

                        logger.debug("Applying OLA with k = {} and suppression = {}", k, suppression);

                        IPVDataset anonymizedDataset = ola.apply();

                        final List<RiskMetric> metrics = Arrays.asList(
                                new KRatioMetric(),
                                new FKRatioMetric(),
                                new BinomialRiskMetric()
                        );

                        for (RiskMetric metric : metrics) {
                            String shortName = metric.getShortName();
                            double riskValue = metric.initialize(originalDataset, anonymizedDataset, columnInformation, k, riskMetricOptions).report();
                            System.out.printf("%d\t%f\t%s\t%f%n", k, suppression, shortName, riskValue);
                        }

                        try {
                            double realRisk = calculateRealRisk("/population_10k.txt", anonymizedDataset, columnInformation, 
                                    ola.reportBestNode().getValues());
                            System.out.printf("%d\t%f\t%s\t%f%n", k, suppression, "REAL", realRisk);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        System.out.println();
                    }
                });
    }

    private double calculateRealRisk(String populationResourceName, IPVDataset anonymizedDataset, 
                                     List<ColumnInformation> columnInformation, int[] levels) throws IOException {

        InputStream originalIS = this.getClass().getResourceAsStream(populationResourceName);
        
        Map<String, Integer> anonEQCounters = AnonymizationUtils.generateEQCounters(anonymizedDataset, columnInformation);
        Map<String, Integer> populationEQCounters = DatasetGeneralizer.generalizeCSVAndCountEQ(originalIS, columnInformation, levels);

        int minimumLink = anonEQCounters.values().stream().mapToInt(Integer::intValue).min().orElse(Integer.MAX_VALUE);
        
        for(String key: anonEQCounters.keySet()) {
            Integer linkedWith = populationEQCounters.get(key);
            minimumLink = Math.min(minimumLink, linkedWith);
        }
        
        return 1.0/(double)minimumLink;
    }
}

