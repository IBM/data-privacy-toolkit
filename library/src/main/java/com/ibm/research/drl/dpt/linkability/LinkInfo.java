/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.linkability;


public class LinkInfo {
    private final int sourceIndex;
    private final int targetIndex;
    private final String wildcharPattern;
    private final boolean prefixMatch;
    private final int prefixMatchLength;
    private final boolean isNumerical;
    
    public boolean isPrefixMatch() {
        return prefixMatch;
    }

    public int getPrefixMatchLength() {
        return prefixMatchLength;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public String getWildcharPattern() {
        return wildcharPattern;
    }

    public boolean isNumerical() {
        return isNumerical;
    }

    public LinkInfo(int sourceIndex, int targetIndex, String wildcardPattern, boolean prefixMatch, int prefixMatchLength, boolean isNumerical) {
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
        this.wildcharPattern = wildcardPattern;
        this.prefixMatch = prefixMatch;
        this.prefixMatchLength = prefixMatchLength;
        this.isNumerical = isNumerical;
    }

    public LinkInfo(int sourceIndex, int targetIndex, String wildcardPattern, boolean prefixMatch, int prefixMatchLength) {
        this(sourceIndex, targetIndex, wildcardPattern, prefixMatch, prefixMatchLength, false);
    }

    public LinkInfo(int sourceIndex, int targetIndex, String wildcardPattern) {
        this(sourceIndex, targetIndex, wildcardPattern, false, 0);
    }

    public LinkInfo(int sourceIndex, int targetIndex) {
        this(sourceIndex, targetIndex, "*", false, 0);
    }

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

