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

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Abstract base class for identifiers that use regular expression patterns for matching.
 */
public abstract class AbstractRegexBasedIdentifier extends AbstractIdentifier {
    /**
     * Constructs a new AbstractRegexBasedIdentifier.
     */
    public AbstractRegexBasedIdentifier() {
    }

    /**
     * Gets patterns.
     *
     * @return the patterns
     */
    protected abstract Collection<Pattern> getPatterns();

    /**
     * Performs a quick pre-check before running the full regex match. Subclasses may override
     * to reject obviously invalid inputs early.
     *
     * @param data the input data to check
     * @return true if the input should proceed to full regex matching, false to reject immediately
     */
    protected boolean quickCheck(String data) {
        return true;
    }

    /**
     * Matches boolean.
     *
     * @param data the data
     * @return the boolean
     */
    protected boolean matches(String data) {
        if (!quickCheck(data)) {
            return false;
        }

        for (Pattern p : getPatterns()) {
            if (p.matcher(data).matches()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOfThisType(String identifier) {
        return this.matches(identifier);
    }
}
