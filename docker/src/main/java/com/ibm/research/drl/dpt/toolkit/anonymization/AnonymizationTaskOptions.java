/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.anonymization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.kmeans.StrategyOptions;
import com.ibm.research.drl.dpt.anonymization.mondrian.CategoricalSplitStrategy;
import com.ibm.research.drl.dpt.toolkit.task.TaskOptions;

import java.util.List;

public class AnonymizationTaskOptions extends TaskOptions {
    private final AnonymizationAlgorithm algorithm;
    private final String pathToTrashFile;
    private final List<PrivacyConstraint> privacyConstraints;
    private final List<ColumnInformation> columnInformation;
    private final double suppressionRate;
    private final InformationLossMetric informationLoss;
    private final CategoricalSplitStrategy categoricalSplitStrategy;
    private final StrategyOptions strategyOptions;
    private final double percentage;

    public enum AnonymizationAlgorithm {
        OLA,
        MONDRIAN,
        KMAP,
        KMEANS,
        SAMPLING
    }

    public enum InformationLossMetric {
        AECS,
        CP,
        DM,
        DMSTAR,
        GLM,
        GCP,
        NUE,
        NP,
        SSM,
    }

    @JsonCreator
    public AnonymizationTaskOptions(
            @JsonProperty("algorithm") AnonymizationAlgorithm algorithm,
            @JsonProperty("trashFile") String pathToTrashFile,
            @JsonProperty("privacyConstraints") List<PrivacyConstraint> privacyConstraints,
            @JsonProperty("columnInformation") List<ColumnInformation> columnInformation,
            @JsonProperty("suppressionRate") double suppressionRate,
            @JsonProperty("informationLoss") InformationLossMetric informationLoss,
            @JsonProperty("categoricalSplitStrategy")CategoricalSplitStrategy categoricalSplitStrategy,
            @JsonProperty("strategyOption") StrategyOptions strategyOptions,
            @JsonProperty("percentage") double percentage) {
        this.algorithm = algorithm;
        this.pathToTrashFile = pathToTrashFile;
        this.privacyConstraints = privacyConstraints;
        this.columnInformation = columnInformation;

        this.suppressionRate = suppressionRate;
        this.informationLoss = informationLoss;
        this.categoricalSplitStrategy = categoricalSplitStrategy;
        this.strategyOptions = strategyOptions;
        this.percentage = percentage;
    }

    public AnonymizationAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getPathToTrashFile() {
        return pathToTrashFile;
    }

    public List<ColumnInformation> getColumnInformation() {
        return columnInformation;
    }

    public List<PrivacyConstraint> getPrivacyConstraints() {
        return privacyConstraints;
    }

    public double getSuppressionRate() {
        return suppressionRate;
    }

    public InformationLossMetric getInformationLoss() {
        return informationLoss;
    }

    public CategoricalSplitStrategy getCategoricalSplitStrategy() {
        return categoricalSplitStrategy;
    }

    public StrategyOptions getStrategyOptions() {
        return strategyOptions;
    }

    public double getPercentage() {
        return percentage;
    }
}
