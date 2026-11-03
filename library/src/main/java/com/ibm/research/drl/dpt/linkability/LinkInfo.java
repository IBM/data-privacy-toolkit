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
package com.ibm.research.drl.dpt.linkability;


/**
 * Describes how a single column pair should be matched during dataset linkage.
 */
public class LinkInfo {
    private final int sourceIndex;
    private final int targetIndex;
    private final String wildcharPattern;
    private final boolean prefixMatch;
    private final int prefixMatchLength;
    private final boolean isNumerical;

    /**
     * Returns whether prefix matching is enabled for this link.
     *
     * @return {@code true} if prefix matching is enabled
     */
    public boolean isPrefixMatch() {
        return prefixMatch;
    }

    /**
     * Returns the prefix match length.
     *
     * @return the prefix length used when prefix matching is enabled
     */
    public int getPrefixMatchLength() {
        return prefixMatchLength;
    }

    /**
     * Returns the source column index.
     *
     * @return the source column index
     */
    public int getSourceIndex() {
        return sourceIndex;
    }

    /**
     * Returns the target column index.
     *
     * @return the target column index
     */
    public int getTargetIndex() {
        return targetIndex;
    }

    /**
     * Returns the wildcard pattern used for value matching.
     *
     * @return the wildcard pattern
     */
    public String getWildcharPattern() {
        return wildcharPattern;
    }

    /**
     * Returns whether the column values are numerical.
     *
     * @return {@code true} if values should be compared numerically
     */
    public boolean isNumerical() {
        return isNumerical;
    }

    /**
     * Constructs a LinkInfo with all options specified.
     *
     * @param sourceIndex       the source column index
     * @param targetIndex       the target column index
     * @param wildcardPattern   the wildcard matching pattern
     * @param prefixMatch       whether to use prefix matching
     * @param prefixMatchLength the prefix length to match
     * @param isNumerical       whether values are numerical
     */
    public LinkInfo(int sourceIndex, int targetIndex, String wildcardPattern, boolean prefixMatch, int prefixMatchLength, boolean isNumerical) {
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
        this.wildcharPattern = wildcardPattern;
        this.prefixMatch = prefixMatch;
        this.prefixMatchLength = prefixMatchLength;
        this.isNumerical = isNumerical;
    }

    /**
     * Constructs a LinkInfo without specifying numerical mode (defaults to {@code false}).
     *
     * @param sourceIndex       the source column index
     * @param targetIndex       the target column index
     * @param wildcardPattern   the wildcard matching pattern
     * @param prefixMatch       whether to use prefix matching
     * @param prefixMatchLength the prefix length to match
     */
    public LinkInfo(int sourceIndex, int targetIndex, String wildcardPattern, boolean prefixMatch, int prefixMatchLength) {
        this(sourceIndex, targetIndex, wildcardPattern, prefixMatch, prefixMatchLength, false);
    }

    /**
     * Constructs a LinkInfo with a wildcard pattern and no prefix match.
     *
     * @param sourceIndex     the source column index
     * @param targetIndex     the target column index
     * @param wildcardPattern the wildcard matching pattern
     */
    public LinkInfo(int sourceIndex, int targetIndex, String wildcardPattern) {
        this(sourceIndex, targetIndex, wildcardPattern, false, 0);
    }

    /**
     * Constructs a LinkInfo that matches all values ({@code "*"} wildcard).
     *
     * @param sourceIndex the source column index
     * @param targetIndex the target column index
     */
    public LinkInfo(int sourceIndex, int targetIndex) {
        this(sourceIndex, targetIndex, "*", false, 0);
    }

    /**
     * Constructs a LinkInfo with numerical mode specified.
     *
     * @param sourceIndex the source column index
     * @param targetIndex the target column index
     * @param isNumerical whether values are numerical
     */
    public LinkInfo(int sourceIndex, int targetIndex, boolean isNumerical) {
        this(sourceIndex, targetIndex, "*", false, 0, isNumerical);
    }

    @Override
    public String toString() {
        return "LinkInfo{" +
                "sourceIndex=" + sourceIndex +
                ", targetIndex=" + targetIndex +
                ", wildcharPattern='" + wildcharPattern + '\'' +
                ", prefixMatch=" + prefixMatch +
                ", prefixMatchLength=" + prefixMatchLength +
                '}';
    }
}

