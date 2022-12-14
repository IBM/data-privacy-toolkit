/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.identification;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentificationOptions {
    private final static Logger logger = LoggerFactory.getLogger(IdentificationOptions.class);

    private final boolean getFirstN;
    private final int N;

    private final boolean debug;

    private final boolean dumpSample;
    private final boolean doSampling;
    private final double sampleFraction;

    private final DataTypeFormat inputFormat;
    private final DatasetOptions datasetOptions;
    private final String specificField;
    private final IdentifierFactory identifierFactory;
   
    private final boolean splitByCharacterRequirements;

    public boolean isSplitByCharacterRequirements() {
        return splitByCharacterRequirements;
    }

    @JsonProperty("N") // needed to enforce uppercase N
    public int getN() {
        return N;
    }

    public boolean isGetFirstN() {
        return getFirstN;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isDumpSample() {
        return dumpSample;
    }

    public boolean isDoSampling() {
        return doSampling;
    }

    public double getSampleFraction() {
        return sampleFraction;
    }

    public String getSpecificField() {
        return specificField;
    }

    public IdentifierFactory getIdentifierFactory() {
        return identifierFactory;
    }

    public DataTypeFormat getInputFormat() {
        return inputFormat;
    }

    public DatasetOptions getDatasetOptions() {
        return datasetOptions;
    }

    @JsonCreator
    public IdentificationOptions(
            @JsonProperty(value = "getFirstN") boolean getFirstN,
            @JsonProperty(value = "N") int N,
            @JsonProperty(value = "debug") boolean debug,
            @JsonProperty(value = "dumpSample") boolean dumpSample,
            @JsonProperty(value = "doSampling") boolean doSampling,
            @JsonProperty(value = "sampleFraction") double sampleFraction,
            @JsonProperty(value = "inputFormat") DataTypeFormat inputFormat,
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "inputFormat"
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV")
            })
            @JsonProperty(value = "datasetOptions") DatasetOptions datasetOptions,
            @JsonProperty(value = "specificField") String specificField,
            @JsonSerialize(using = CustomIdentifierFactorySerializer.class)
            @JsonDeserialize(using = CustomIdentifierFactoryDeserializer.class)
            @JsonProperty(value = "identifierFactory") IdentifierFactory identifierFactory,
            @JsonProperty(value = "splitByCharacterRequirements") boolean splitByCharacterRequirements
    ) {
        if (getFirstN) {
            if (N <= 0) {
                throw new MisconfigurationException("N must be >0");
            }
        }

        if (getFirstN && doSampling) {
            throw new MisconfigurationException("both doSampling and getFirstN are set to true. Choose only one strategy");
        }

        if (doSampling) {
            if (sampleFraction < 0 || sampleFraction > 1) {
                throw new MisconfigurationException("sampleFraction must be between 0 and 1 (inclusive)");
            }
        }

        this.getFirstN = getFirstN;
        this.N = N;
        this.debug = debug;
        this.dumpSample = dumpSample;
        this.doSampling = doSampling;
        this.sampleFraction = sampleFraction;
        this.inputFormat = inputFormat;
        this.datasetOptions = datasetOptions;
        this.specificField = specificField;
        if (identifierFactory == null) {
            logger.info("Using default identification factory");
            this.identifierFactory = IdentifierFactory.getDefaultIdentifierFactory();
        } else {
            logger.info("Using specified identification factory");
            this.identifierFactory = identifierFactory;
        }
        this.splitByCharacterRequirements = splitByCharacterRequirements;
    }
    
    public static IdentificationOptions fromJson(TreeNode n) throws JsonProcessingException {
        // Wee need this FAIL_ON_UNKNOWN_PROPERTIES for now - something changed with the latest 2.11.3 and it now fails by default with unknown properties
        // i.e. for now we need mixed-json that can be deserialized in two different classes with different props
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false).treeToValue(n, IdentificationOptions.class);
    }
}
