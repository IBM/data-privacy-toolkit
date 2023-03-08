/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.differentialprivacy.DPMechanism;
import com.ibm.research.drl.dpt.anonymization.differentialprivacy.DPMechanismFactory;
import com.ibm.research.drl.dpt.anonymization.differentialprivacy.DifferentialPrivacyMechanismOptions;
import com.ibm.research.drl.dpt.anonymization.differentialprivacy.Mechanism;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.security.SecureRandom;
import java.util.Map;


public class DifferentialPrivacyMaskingProvider implements MaskingProvider  {
    private final static Logger logger = LogManager.getLogger(DifferentialPrivacyMaskingProvider.class);

    private final DPMechanism mechanism;

    public DifferentialPrivacyMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setEpsilon(configuration.getDoubleValue("differentialPrivacy.parameter.epsilon"));

        Mechanism mechanism = Mechanism.valueOf(configuration.getStringValue("differentialPrivacy.mechanism"));
        switch (mechanism) {
            case BINARY:
                String value1 = configuration.getStringValue("differentialPrivacy.binary.value1");
                String value2 = configuration.getStringValue("differentialPrivacy.binary.value2");
                options.setBinaryValues(value1, value2);
                break;
            case CATEGORICAL:
                String hierarchyName = configuration.getStringValue("differentialPrivacy.categorical.hierarchyName");
                JsonNode hierarchyMapNode = configuration.getJsonNodeValue("differentialPrivacy.categorical.hierarchyMap");

                Map<String, GeneralizationHierarchy> hierarchyMap = AnonymizationOptions.hierarchiesFromJSON(hierarchyMapNode);
                GeneralizationHierarchy hierarchy = hierarchyMap.get(hierarchyName);
                if (hierarchy == null) {
                    String msg = "Unable to find the hierarchy with name: " + hierarchyName;
                    logger.error(msg);
                    throw new MisconfigurationException(msg);
                }
                options.setHierarchy(hierarchy);
                break;
            case LAPLACE_NATIVE:
            case LAPLACE_BOUNDED:
            case LAPLACE_TRUNCATED:
                double lowerBound = configuration.getDoubleValue("differentialPrivacy.range.min");
                double upperBound = configuration.getDoubleValue("differentialPrivacy.range.max");
                options.setBounds(lowerBound, upperBound);
                break;
        }
        this.mechanism = DPMechanismFactory.getMechanism(mechanism);
        this.mechanism.setOptions(options);
    }

    public DifferentialPrivacyMaskingProvider(DefaultMaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    @Override
    public String mask(String value) {
        return mechanism.randomise(value);
    }
}
