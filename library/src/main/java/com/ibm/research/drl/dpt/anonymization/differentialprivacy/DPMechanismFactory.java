/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;


public class DPMechanismFactory {
    public static DPMechanism getMechanism(Mechanism mechanism) {
        switch (mechanism) {
            case BINARY:
                return new Binary();
            case CATEGORICAL:
                return new Categorical();
            case LAPLACE_NATIVE:
                return new Laplace();
            case LAPLACE_BOUNDED:
                return new BoundedLaplace();
            case LAPLACE_TRUNCATED:
                return new TruncatedLaplace();
            case GEOMETRIC_NATIVE:
                return new Geometric();
            case GEOMETRIC_TRUNCATED:
                return new TruncatedGeometric();
            default:
                throw new RuntimeException("Unknown mechanism: " + mechanism.name());
        }
    }
}

