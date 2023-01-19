/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

public class TruncatedGeometric extends Geometric {
    @Override
    public double randomise(double value) {
        double noisyNumValue = super.randomise(value);

        if (noisyNumValue < this.lowerBound) {
            noisyNumValue = this.lowerBound;
        } else if (noisyNumValue > this.upperBound) {
            noisyNumValue = this.upperBound;
        }

        return noisyNumValue;
    }

    @Override
    public String getName() { return "Truncated Geometric mechanism"; }
}


