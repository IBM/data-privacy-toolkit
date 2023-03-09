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

