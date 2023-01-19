/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.util.Tuple;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class Binary implements DPMechanism {
    private static final Logger log = LogManager.getLogger(Binary.class);

    private final Random rnd = new SecureRandom();
    private double epsilon;
    private Tuple<String, String> binaryValues;

    @Override
    public void setOptions(AnonymizationAlgorithmOptions options) {
        if (!(options instanceof DifferentialPrivacyMechanismOptions)) throw new IllegalArgumentException("Expecting instance of DifferentialPrivacyMechanismOptions");

        this.epsilon = ((DifferentialPrivacyMechanismOptions) options).getEpsilon();

        if (this.epsilon < 0.0) {
            log.error("Epsilon parameter must be positive");
            throw new MisconfigurationException("Epsilon parameter must be positive");
        }

        this.binaryValues = ((DifferentialPrivacyMechanismOptions) options).getBinaryValues();

        if (this.binaryValues == null) {
            log.error("Binary inaryValues must be specified");
            throw new MisconfigurationException("Binary binaryValues must be specified");
        }

        if (this.binaryValues.getFirst() == null) {
            log.error("undefined first binary value");
            throw new MisconfigurationException("undefined first binary value");
        }

        if (this.binaryValues.getSecond() == null) {
            log.error("undefined second binary value");
            throw new MisconfigurationException("undefined second binary value");
        }

        if (this.binaryValues.getFirst().equalsIgnoreCase(this.binaryValues.getSecond())) {
            log.error("binary values are the same");
            throw new MisconfigurationException("binary values are the same");
        }
    }

    @Override
    public void analyseForParams(List<Partition> equivalenceClasses, int columnIndex) {
        /* there is nothing to analyze here */
    }

    @Override
    public String randomise(String value) {
        boolean boolValue = value.equals(this.binaryValues.getFirst());
        double u = rnd.nextDouble();

        boolean noisyBoolValue;

        if (u <= 1/(1 + Math.exp(this.epsilon))) {
            noisyBoolValue = !boolValue;
        } else {
            noisyBoolValue = boolValue;
        }

        return (noisyBoolValue) ? this.binaryValues.getFirst() : this.binaryValues.getSecond();
    }

    @Override
    public double randomise(double value) {
        throw new UnsupportedOperationException("Not a numerical mechanism");
    }

    @Override
    public String getName() {
        return "Differential privacy mechanism for Binary data";
    }
}

