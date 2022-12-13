/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors.records;


public abstract class MultipathRecord implements Record {

    public abstract Iterable<String> generatePaths(String pattern);

    public abstract boolean isAbsolute(String fieldName);

    public abstract boolean isSingleElement(String fieldName);

    public abstract String getBasepath(String path);

    public Iterable<String> getFieldReferencesWithGeneralization() {
        return getFieldReferences();
    }

    protected abstract String formatRecord();

    @Override
    public final String toString() {
        return formatRecord();
    }

    protected byte[] formatRecordBytes() {
        return formatRecord().getBytes();
    }

    @Override
    public final byte[] toBytes() {
        return formatRecordBytes();
    }

    @Override
    public final boolean isHeader() {
        return false;
    }

    public boolean isMatching(String pattern, String fieldIdentifier) {
        if (pattern.contains("*")) {
            String[] patternParts = pattern.split("/");
            String[] fIParts = fieldIdentifier.split("/");

            if (patternParts.length == fIParts.length) {
                for (int i = 0; i < patternParts.length; ++i) {
                    String patternPart = patternParts[i];

                    if (patternPart.equals("*")) continue;
                    if (!patternPart.equals(fIParts[i])) return false;
                }
            }

            return true;
        } else {
            return pattern.equals(fieldIdentifier);
        }
    }

    public abstract boolean isPrimitiveType(String fieldIdentifier);

    public abstract Object getFieldObject(String fieldIdentifier);
}
