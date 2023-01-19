/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.Partition;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class Geometric implements DPMechanism {
    protected Random rnd = new SecureRandom();
    protected double shape;
    protected double epsilon;
    protected double lowerBound;
    protected double upperBound;

    @Override
    public void setOptions(AnonymizationAlgorithmOptions options) {
        if (!(options instanceof DifferentialPrivacyMechanismOptions)) throw new IllegalArgumentException("Expecting instance of DifferentialPrivacyMechanismOptions");

        this.epsilon = ((DifferentialPrivacyMechanismOptions) options).getEpsilon();

        if (this.epsilon < 0.0) {
            throw new RuntimeException("Epsilon parameter must be positive");
        }
        List<Double> bounds = ((DifferentialPrivacyMechanismOptions) options).getBounds();
        this.lowerBound = bounds.get(0);
        this.upperBound = bounds.get(1);

        if (this.lowerBound > this.upperBound) {
            throw new RuntimeException("Upper bound must be greater than lower bound");
        }

        this.shape = (this.upperBound - this.lowerBound)/this.epsilon;
    }

    @Override
    public void analyseForParams(List<Partition> equivalenceClasses, int columnIndex) {
        this.lowerBound = Double.POSITIVE_INFINITY;
        this.upperBound = Double.NEGATIVE_INFINITY;

        for (Partition partition : equivalenceClasses) {
            for (List<String> row : partition.getMember()) {
                double value = Double.parseDouble(row.get(columnIndex));

                if (value < this.lowerBound) {
                    this.lowerBound = Math.floor(value);
                }

                if (value > this.upperBound) {
                    this.upperBound = Math.ceil(value);
                }
            }
        }

        double diameter = this.upperBound - this.lowerBound;

        this.shape = diameter/this.epsilon;
    }

    @Override
    public double randomise(double value) {
        double u = this.rnd.nextDouble() - 0.5;
        double sgn = (u < 0) ? -1 : 1;
        u *= sgn * (Math.exp(1 / this.shape) + 1) / (Math.exp(1 / this.shape) - 1);

        double cumProb = -0.5;
        int i = -1;

        while (u > cumProb) {
            i += 1;
            cumProb += Math.exp(- i / this.shape);
        }

        return value + (sgn * i);
    }

    @Override
    public String getName() {
        return "Native Geometric mechanism";
    }
}


