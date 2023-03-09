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

