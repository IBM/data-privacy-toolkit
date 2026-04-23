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

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationLossMetricFactory;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.rest.exceptions.UnknownAlgorithmName;
import com.ibm.research.drl.dpt.rest.models.AnonymizationAlgorithmDescription;
import com.ibm.research.drl.dpt.rest.models.CompleteAlgorithmConfiguration;
import com.ibm.research.drl.dpt.rest.models.IdentifierDescription;
import com.ibm.research.drl.dpt.rest.models.InformationLossMetricDescription;
import com.ibm.research.drl.dpt.rest.models.RiskIdentificationDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/information")
public class Information {
    private IdentifierFactory identifierFactory;
    public Information(@Autowired IdentifierFactory identifierFactory) {
        this.identifierFactory = identifierFactory;
    }

    @GetMapping("/identifiers")
    public Iterable<IdentifierDescription> listIdentifiers() {
        return this.identifierFactory.availableIdentifiers().stream().
                map( identifier -> new IdentifierDescription(
                        identifier.getClass().getSimpleName(),
                        identifier.getDescription())).
                collect(Collectors.toList());
    }

    @GetMapping("/maskingProviders")
    public Iterable<ProviderType> listSupportedMaskingProviders() {
        return ProviderType.publicValues();
    }

    @GetMapping("/configuration")
    public DefaultMaskingConfiguration getDefaultMaskingConfiguration() {
        return new DefaultMaskingConfiguration();
    }

    @GetMapping("/informationLoss")
    public Iterable<InformationLossMetricDescription> listInformationLoss() {
        return InformationLossMetricFactory.getAvailableMetrics().stream().
                map( metric -> new InformationLossMetricDescription(
                        metric.getName(),
                        metric.getShortName(),
                        metric.supportsSuppressedDatasets())).collect(Collectors.toList());

    }

    @GetMapping("/riskIdentification")
    public Collection<RiskIdentificationDescription> listRiskIdentificationProviders() {
        return Arrays.asList(
                new RiskIdentificationDescription("DUCC"),
                new RiskIdentificationDescription("MTRA"),
                new RiskIdentificationDescription("FPVI")
        );
    }

    @GetMapping("/hierarchy/{providerName}")
    public Iterable<List<String>> hierarchyInformation(@PathVariable("providerName") final String providerName) {
        ProviderType providerType = ProviderType.valueOf(providerName.toUpperCase());

        if (providerType == null) {
            return Collections.emptyList();
        }

        GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(providerType);

        if (hierarchy == null) {
            return Collections.emptyList();
        }

        if (!(hierarchy instanceof MaterializedHierarchy)) {
            return Collections.emptyList();
        }

        MaterializedHierarchy materializedHierarchy = (MaterializedHierarchy)hierarchy;

        return materializedHierarchy.getTerms();
    }

    @GetMapping("/anonymizationProviders")
    public Iterable<AnonymizationAlgorithmDescription> listSupportedAnonymizationProviders() {
        return Arrays.asList(
                new AnonymizationAlgorithmDescription("Mondrian", ""),
                new AnonymizationAlgorithmDescription("OLA", "Optimal Lattice Anonymizatino")
        );
    }

    @GetMapping("/defaultConfiguration/{algorithmName}")
    public CompleteAlgorithmConfiguration getConfigurationForAnonymizationAlgorithm(
            @PathVariable("algorithmName")
            String algorithmName
    ) {
        return getDefaultConfiguration(algorithmName);
    }

    public static CompleteAlgorithmConfiguration getDefaultConfiguration(String algorithmName) {
        CompleteAlgorithmConfiguration configuration = new CompleteAlgorithmConfiguration();

        switch (algorithmName.toLowerCase()) {
            case "mondrian":
                configuration.setOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.l, 3);
                configuration.setOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.k, 3);
                break;
            case "ola":
                configuration.setOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.k, 3);
                configuration.setOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.l, 2);
                configuration.setOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.lDiversityAlgorithm, "distinct");
                configuration.setOption(CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION.suppressionRate, 5.0);
                break;
            default:
                throw new UnknownAlgorithmName();
        }

        return configuration;
    }
}
