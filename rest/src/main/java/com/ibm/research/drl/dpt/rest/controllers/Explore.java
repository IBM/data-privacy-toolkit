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
package com.ibm.research.drl.dpt.rest.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.informationloss.AECSOptions;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationLossMetricFactory;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetric;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.generators.ItemSet;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.rest.exceptions.InvalidRequestException;
import com.ibm.research.drl.dpt.rest.models.ExplorationResult;
import com.ibm.research.drl.dpt.rest.models.ExplorationResult.InformationLossValue;
import com.ibm.research.drl.dpt.rest.models.MaskingProviderDescription;
import com.ibm.research.drl.dpt.risk.BinomialRiskMetric;
import com.ibm.research.drl.dpt.risk.FKRatioMetric;
import com.ibm.research.drl.dpt.risk.KRatioMetric;
import com.ibm.research.drl.dpt.risk.RiskMetric;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * Anonymizes a dataset with OLA for a grid of (k, suppression) pairs and reports, for each pair, the information
 * loss and the re-identification risk, so that the caller can choose the parameters of the anonymization.
 */
@RestController
@RequestMapping("/api/feature/")
public class Explore {
    private static final Logger logger = LoggerFactory.getLogger(Explore.class);

    /** The largest number of (k, suppression) pairs explored in one request. */
    static final int MAX_COMBINATIONS = 100;

    /** Used when the caller does not know the size of the population the dataset was sampled from. */
    private static final int DEFAULT_POPULATION_FACTOR = 10;

    // The OLA implementation reports "no solution" only with this message.
    private static final String NO_SOLUTION = "unable to find a suitable generalization";

    // Risk metrics that stay affordable on large datasets. The Holohan metrics take time proportional to the size of
    // the population for each equivalence class, the hypergeometric one is constant for the data this service sees,
    // and the approximation metric shares its short name with the binomial one.
    private static final List<Supplier<RiskMetric>> RISK_METRICS = List.of(
            KRatioMetric::new,
            FKRatioMetric::new,
            BinomialRiskMetric::new
    );

    private final ObjectMapper mapper;

    public Explore(@Autowired ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostMapping(value = "/explore/{hasHeader}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ExplorationResult> explore(
            @PathVariable("hasHeader") boolean hasHeader,
            @RequestParam("minK") int minK,
            @RequestParam("maxK") int maxK,
            @RequestParam("kInterval") int kInterval,
            @RequestParam("minSuppression") double minSuppression,
            @RequestParam("maxSuppression") double maxSuppression,
            @RequestParam("suppressionInterval") double suppressionInterval,
            @RequestParam(value = "population", required = false) Integer population,
            @RequestParam("fields") String fieldsS,
            @RequestParam("vulnerabilities") String vulnerabilitiesS,
            @RequestParam(value = "sensitive", defaultValue = "[]") String sensitiveS,
            @RequestParam(value = "linkingAttributes", required = false) String linkingS,
            @RequestParam("data") String data) {
        final List<Integer> ks = values(minK, maxK, kInterval);
        final List<Double> suppressions = values(minSuppression, maxSuppression, suppressionInterval);
        if ((long) ks.size() * suppressions.size() > MAX_COMBINATIONS) {
            throw new InvalidRequestException("Too many combinations of k and suppression: at most " + MAX_COMBINATIONS);
        }
        if (null != population && population <= 0) {
            throw new InvalidRequestException("The population must be greater than 0");
        }

        try (StringReader reader = new StringReader(data)) {
            final IPVDataset dataset = IPVDataset.load(reader, hasHeader, ',', '"', false);

            final Set<Integer> quasi = columns(vulnerabilitiesS, dataset);
            if (quasi.isEmpty()) {
                throw new InvalidRequestException("At least one quasi-identifier is needed");
            }
            final Set<Integer> sensitive = columns(sensitiveS, dataset);
            final Set<Integer> linking = null == linkingS || linkingS.isBlank() ? quasi : columns(linkingS, dataset);
            if (linking.isEmpty()) {
                throw new InvalidRequestException("At least one linking attribute is needed");
            }

            final Exploration exploration = new Exploration(
                    dataset,
                    quasi,
                    sensitive,
                    linking,
                    fieldTypes(fieldsS),
                    null == population ? DEFAULT_POPULATION_FACTOR * dataset.getNumberOfRows() : population);

            logger.info("Exploring {} combinations on a dataset of {} rows and {} columns", ks.size() * suppressions.size(),
                    dataset.getNumberOfRows(), dataset.getNumberOfColumns());

            final List<ExplorationResult> results = new ArrayList<>();
            for (int k : ks) {
                for (double suppression : suppressions) {
                    exploration.run(k, suppression).ifPresent(results::add);
                }
            }

            return results;
        } catch (IOException e) {
            logger.error("Unable to read the request", e);
            throw new InvalidRequestException("Invalid request");
        }
    }

    /** The values from {@code min} to {@code max}, {@code step} apart. */
    private static List<Integer> values(int min, int max, int step) {
        if (min < 2) throw new InvalidRequestException("The minimum k must be at least 2");
        if (max < min) throw new InvalidRequestException("The maximum k must not be smaller than the minimum");
        if (step < 1) throw new InvalidRequestException("The k interval must be at least 1");

        final List<Integer> values = new ArrayList<>();
        for (long value = min; value <= max && values.size() <= MAX_COMBINATIONS; value += step) {
            values.add((int) value);
        }
        return values;
    }

    private static List<Double> values(double min, double max, double step) {
        if (!(min >= 0 && max <= 100)) throw new InvalidRequestException("The suppression must be between 0 and 100");
        if (max < min) throw new InvalidRequestException("The maximum suppression must not be smaller than the minimum");
        if (!(step > 0)) throw new InvalidRequestException("The suppression interval must be greater than 0");

        final List<Double> values = new ArrayList<>();
        for (int i = 0; values.size() <= MAX_COMBINATIONS; i++) {
            final double value = Math.round((min + i * step) * 1e6) / 1e6;
            if (value > max) break;
            values.add(value);
        }
        return values;
    }

    private Set<Integer> columns(String json, IPVDataset dataset) throws IOException {
        final List<Integer> columns = mapper.readValue(json, new TypeReference<>() {});

        for (int column : columns) {
            if (column < 0 || column >= dataset.getNumberOfColumns()) {
                throw new InvalidRequestException("Unknown column " + column);
            }
        }

        return new TreeSet<>(columns);
    }

    private Map<String, ProviderType> fieldTypes(String json) throws IOException {
        final Map<String, MaskingProviderDescription> described = mapper.readValue(json, new TypeReference<>() {});

        final Map<String, ProviderType> types = new HashMap<>(described.size());
        described.forEach((column, type) -> types.put(column, ProviderType.valueOf(type.getName())));
        return types;
    }

    /** What is the same for every pair of parameters. */
    private static final class Exploration {
        private final IPVDataset dataset;
        private final Collection<IPVVulnerability> vulnerabilities;
        private final Collection<String> sensitiveNames;
        private final List<ColumnInformation> linkingColumns;
        private final Map<String, ProviderType> fieldTypes;
        private final Map<String, String> riskOptions;

        Exploration(IPVDataset dataset, Set<Integer> quasi, Set<Integer> sensitive, Set<Integer> linking,
                    Map<String, ProviderType> fieldTypes, int population) {
            this.dataset = dataset;
            this.fieldTypes = fieldTypes;

            final ItemSet items = new ItemSet();
            quasi.forEach(items::addItem);
            this.vulnerabilities = Collections.singletonList(new IPVVulnerability(items));

            this.sensitiveNames = new ArrayList<>();
            sensitive.forEach(column -> sensitiveNames.add(dataset.getSchema().getFields().get(column).getName()));

            // The risk metrics only need to know which columns are used to link records.
            this.linkingColumns = new ArrayList<>(dataset.getNumberOfColumns());
            for (int column = 0; column < dataset.getNumberOfColumns(); column++) {
                linkingColumns.add(new DefaultColumnInformation(linking.contains(column)));
            }

            this.riskOptions = Map.of(
                    KRatioMetric.GAMMA, "1.0",
                    FKRatioMetric.POPULATION, Integer.toString(population),
                    BinomialRiskMetric.POPULATION, Integer.toString(population),
                    BinomialRiskMetric.USE_GLOBAL_P, "true");
        }

        /** The outcome for one pair of parameters, or nothing if no generalization satisfies them. */
        Optional<ExplorationResult> run(int k, double suppression) {
            if (k > dataset.getNumberOfRows()) {
                return Optional.empty();
            }

            final OLA algorithm = new OLA();
            final List<PrivacyConstraint> constraints = List.of(new KAnonymity(k));

            final IPVDataset anonymized;
            try {
                anonymized = algorithm.initialize(dataset, vulnerabilities, sensitiveNames, fieldTypes, constraints,
                        new OLAOptions(suppression)).apply();
            } catch (RuntimeException e) {
                if (null != e.getMessage() && e.getMessage().startsWith(NO_SOLUTION)) {
                    logger.info("No generalization for k={} and suppression={}", k, suppression);
                    return Optional.empty();
                }
                throw e;
            }

            final Map<String, InformationLossValue> informationLoss = new LinkedHashMap<>();
            for (InformationMetric available : InformationLossMetricFactory.getAvailableMetrics()) {
                final InformationMetric metric = InformationLossMetricFactory.getInstance(available.getShortName());
                try {
                    final double value = metric.initialize(dataset, anonymized,
                            algorithm.getOriginalPartitions(), algorithm.getAnonymizedPartitions(),
                            algorithm.getColumnInformationList(), new AECSOptions(false, k)).report();
                    if (Double.isFinite(value)) {
                        informationLoss.put(metric.getShortName(), new InformationLossValue(value));
                    }
                } catch (RuntimeException e) {
                    // not every metric applies to every dataset (for instance, one needs a sensitive column)
                    logger.info("Information loss metric {} not available: {}", metric.getShortName(), e.getClass().getSimpleName());
                }
            }

            final Map<String, Double> risks = new LinkedHashMap<>();
            for (Supplier<RiskMetric> supplier : RISK_METRICS) {
                final RiskMetric metric = supplier.get();
                try {
                    final double value = metric.initialize(dataset, anonymized, linkingColumns, k, riskOptions).report();
                    if (Double.isFinite(value)) {
                        risks.put(metric.getShortName(), value);
                    }
                } catch (RuntimeException e) {
                    logger.info("Risk metric {} not available: {}", metric.getShortName(), e.getClass().getSimpleName());
                }
            }

            return Optional.of(new ExplorationResult(
                    k,
                    suppression,
                    algorithm.reportSuppressionRate(),
                    Arrays.toString(algorithm.reportBestNode().getValues()),
                    informationLoss,
                    risks));
        }
    }
}
