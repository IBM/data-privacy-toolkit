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
import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithm;
import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.constraints.DistinctLDiversity;
import com.ibm.research.drl.dpt.anonymization.constraints.EntropyLDiversity;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.constraints.RecursiveCLDiversity;
import com.ibm.research.drl.dpt.anonymization.differentialprivacy.DPMechanism;
import com.ibm.research.drl.dpt.anonymization.differentialprivacy.Laplace;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationLossMetricFactory;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetric;
import com.ibm.research.drl.dpt.anonymization.mondrian.Mondrian;
import com.ibm.research.drl.dpt.anonymization.mondrian.MondrianOptions;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.generators.ItemSet;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.rest.exceptions.InvalidRequestException;
import com.ibm.research.drl.dpt.rest.models.AnonymizationResult;
import com.ibm.research.drl.dpt.rest.models.CompleteAlgorithmConfiguration;
import com.ibm.research.drl.dpt.rest.models.InformationLossResultDescription;
import com.ibm.research.drl.dpt.rest.models.MaskingProviderDescription;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feature/")
public class Anonymize {
    private static final Logger logger = LogManager.getLogger(Anonymize.class);

    private final ObjectMapper mapper;

    public Anonymize(@Autowired ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostMapping(value = "/anonymize/{algorithmName}/{hasHeader}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnonymizationResult anonymizeDataset(@PathVariable("algorithmName") String algorithmName,
                                                @PathVariable("hasHeader") boolean hasHeader,
                                                @RequestParam("lossMetric") String lossMetric,
                                                @RequestParam("configuration") String configurationSpecs,
                                                @RequestParam("kQuasi") String kQuasiS,
                                                @RequestParam("eQuasi") String eQuasiS,
                                                @RequestParam("sensitive") String sensitiveS,
                                                @RequestParam("fields") String fieldsS,
                                                @RequestParam("dataset") String datasetStream) {
        try (Reader inputStreamReader = new StringReader(datasetStream)) {
            logger.info("lossMetric: {}", lossMetric);
            logger.info("configuration: {}", configurationSpecs);
            logger.info("kQuasi: {}", kQuasiS);
            logger.info("eQuasi: {}", eQuasiS);
            logger.info("sensitive: {}", sensitiveS);
            logger.info("fields: {}", fieldsS);

            final IPVDataset dataset = IPVDataset.load(inputStreamReader, hasHeader, ',', '"', false);

            final AnonymizationAlgorithm anonymizationAlgorithm = getAlgorithm(algorithmName);

            InformationMetric metric = InformationLossMetricFactory.getInstance(lossMetric);
            if (metric == null) {
                throw new InvalidRequestException("Metric " + lossMetric + " not known");
            }

            final CompleteAlgorithmConfiguration configuration = getConfiguration(algorithmName, configurationSpecs);
            final List<PrivacyConstraint> privacyConstraints = buildConstraints(configuration);

            IPVDataset anonymized = anonymizationAlgorithm.initialize(dataset,
                    buildVulnerabilities(kQuasiS.trim()),
                    buildSensitiveList(sensitiveS),
                    buildFieldTypes(fieldsS.trim()),
                    privacyConstraints,
                    buildOptions(algorithmName, configuration)).apply();

            final List<Integer> eQuasis = buildEQuasis(eQuasiS);

            if (!eQuasis.isEmpty()) {
                List<List<String>> newData = applyLocalDifferentialPrivacy(anonymizationAlgorithm, eQuasis, configuration);
                anonymized = new IPVDataset(newData, anonymized.getSchema(), anonymized.hasColumnNames());
            }

            final List<ColumnInformation> columnInformation = anonymizationAlgorithm.getColumnInformationList();

            metric = metric.initialize(dataset, anonymized,
                    anonymizationAlgorithm.getOriginalPartitions(),
                    anonymizationAlgorithm.getAnonymizedPartitions(),
                    columnInformation, null);

            Double result = metric.report();
            Double lowerBound = metric.getLowerBound();
            Double upperBound = metric.getUpperBound();

            return new AnonymizationResult(anonymized.toString(),
                    new InformationLossResultDescription(result, lowerBound, upperBound),
                    columnInformation,
                    anonymizationAlgorithm.reportSuppressionRate());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InvalidRequestException(e.getMessage());
        }
    }

    private List<List<String>> applyLocalDifferentialPrivacy(AnonymizationAlgorithm algorithm, List<Integer> eQuasis, CompleteAlgorithmConfiguration privacyConstraints) {
        int k = ((Number) (privacyConstraints.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.k))).intValue();
        double e = ((Number) (privacyConstraints.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.epsilon))).doubleValue();

        List<List<String>> anonymizedDataset = new ArrayList<>();

        for (Partition equivalenceClass : algorithm.getAnonymizedPartitions()) {
            if (equivalenceClass.size() < k) continue;

            IPVDataset members = equivalenceClass.getMember();

            for (int eQuasi : eQuasis) {
                DPMechanism mechanism = new Laplace();
                mechanism.analyseForParams(Collections.singletonList(equivalenceClass), eQuasi);


                for (List<String> record : members) {
                    List<String> newRecord = new ArrayList<>(record);
                    newRecord.set(eQuasi, mechanism.randomise(newRecord.get(eQuasi)));

                    anonymizedDataset.add(newRecord);
                }
            }
        }
        return anonymizedDataset;
    }

    private List<Integer> buildEQuasis(String eQuasiS) throws IOException {
        if (null == eQuasiS || eQuasiS.isEmpty()) return Collections.emptyList();
        return mapper.readValue(eQuasiS, new TypeReference<>() {});
    }


    private AnonymizationAlgorithm getAlgorithm(final String algorithmName) {
        switch (algorithmName.toLowerCase()) {
            case "mondrian":
                return new Mondrian();
            case "ola":
                return new OLA();
        }
        throw new IllegalArgumentException("Unsupported algorithm " + algorithmName);
    }

    private AnonymizationAlgorithmOptions buildOptions(final String algorithmName, final CompleteAlgorithmConfiguration options) throws IOException {
        switch (algorithmName.toLowerCase()) {
            case "mondrian":
                return new MondrianOptions();
            case "ola":
                return new OLAOptions(Double.parseDouble(options.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.suppressionRate).toString()));
            default:
                throw new IllegalArgumentException("Unsupported algorithm " + algorithmName);
        }
    }

    private Collection<IPVVulnerability> buildVulnerabilities(String kQuasiString) throws IOException {
        List<Integer> kQuasis = mapper.readValue(kQuasiString, new TypeReference<>() {});

        ItemSet itemSet = new ItemSet();
        kQuasis.forEach(itemSet::addItem);

        return Collections.singletonList(new IPVVulnerability(itemSet));
    }

    private Map<String, ProviderType> buildFieldTypes(String fieldTypesS) throws IOException {
        Map<String, MaskingProviderDescription> simpleTypes = mapper.readValue(fieldTypesS, new TypeReference<>() {});

        Map<String, ProviderType> types = new HashMap<>(simpleTypes.size());

        for (final Map.Entry<String, MaskingProviderDescription> entry : simpleTypes.entrySet()) {
            types.put(entry.getKey(), ProviderType.valueOf(entry.getValue().getName()));
        }

        return types;
    }

    private Collection<String> buildSensitiveList(String sensitiveS) throws IOException {
        if (null == sensitiveS || sensitiveS.isEmpty()) return Collections.emptyList();

        return mapper.readValue(sensitiveS, new TypeReference<List<String>>() {
        });
    }

    private List<PrivacyConstraint> buildConstraints(CompleteAlgorithmConfiguration configuration) throws IOException {
        final List<PrivacyConstraint> privacyConstraints = new ArrayList<>();

        if (configuration.hasOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.l) && configuration.hasOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.lDiversityAlgorithm)) {
            String lDiversityAlgorithmName = (String) configuration.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.lDiversityAlgorithm);
            switch (lDiversityAlgorithmName) {
                case "distinct":
                    privacyConstraints.add(new DistinctLDiversity(((Number) configuration.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.l)).intValue()));
                    break;
                case "entropy":
                    privacyConstraints.add(new EntropyLDiversity(((Number) configuration.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.l)).intValue()));
                    break;
                case "recursiveCL":
                    if (configuration.hasOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.c)) {
                        privacyConstraints.add(new RecursiveCLDiversity(((Number) configuration.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.l)).intValue(), ((Number) configuration.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.c)).doubleValue()));
                        break;
                    }
                default:
                    throw new InvalidRequestException("Unknown l-diversity algorithm name: " + CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.lDiversityAlgorithm);
            }
        }

        if (configuration.hasOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.k)) {
            privacyConstraints.add(new KAnonymity(((Number) configuration.getOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.k)).intValue()));
        }

        return privacyConstraints;
    }

    private CompleteAlgorithmConfiguration getConfiguration(String algorithmName, String constraintsSpecs) throws IOException {
        if (null != constraintsSpecs && ! constraintsSpecs.isEmpty()) {
            return mapper.readValue(constraintsSpecs, CompleteAlgorithmConfiguration.class);
        } else {
            return Information.getDefaultConfiguration(algorithmName);
        }
    }
}

