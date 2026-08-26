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
package com.ibm.research.drl.dpt.providers.identifiers;

/**
 * Mixin interface for identifiers that validate a value using the Luhn algorithm.
 */
public interface LuhnBasedIdentifier {
    /**
     * Returns whether the last digit of the given numeric string is a valid Luhn check digit.
     *
     * @param value the numeric string to validate
     * @return {@code true} if the Luhn check passes
     */
    default boolean checkLastDigit(String value) {
        int sum = 0;
        boolean doubleValue = false;

        for (int i = value.length() - 1; i >= 0; --i) {
            int currentDigit = value.charAt(i) - '0';

            if (doubleValue) {
                currentDigit *= 2;

                if (currentDigit > 9) {
                    currentDigit -= 9;
                }
            }

            sum += currentDigit;

            doubleValue = !doubleValue;
        }

        return 0 == sum % 10;
    }
}
