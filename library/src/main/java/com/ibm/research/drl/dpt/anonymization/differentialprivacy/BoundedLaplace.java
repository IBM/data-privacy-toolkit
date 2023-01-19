/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

public class BoundedLaplace extends Laplace {
    private double cdf(double x) {
        if (x < 0) {
            return 0.5 * Math.exp(x / this.shape);
        } else {
            return 1 - 0.5 * Math.exp(- x / this.shape);
        }
    }

    @Override
    public double randomise(double value) {
        double u = this.rnd.nextDouble();

        value = Math.min(this.upperBound, value);
        value = Math.max(this.lowerBound, value);

        u *= cdf(this.upperBound - value) - cdf(this.lowerBound - value);
        u += cdf(this.lowerBound - value);
        u -= 0.5;

        return value - this.shape * Math.signum(u) * Math.log(1 - 2 * Math.abs(u));
    }

    @Override
    public String getName() { return "Bounded Laplace mechanism"; }
}

