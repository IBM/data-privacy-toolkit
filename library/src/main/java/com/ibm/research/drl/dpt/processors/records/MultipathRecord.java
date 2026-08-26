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
package com.ibm.research.drl.dpt.processors.records;


/** Abstract base for records that support JSON-pointer-style multi-path field access. */
public abstract class MultipathRecord implements Record {

    /**
     * Generates all concrete field paths matching the given pattern.
     *
     * @param pattern the path pattern
     * @return iterable of concrete field paths
     */
    public abstract Iterable<String> generatePaths(String pattern);

    /**
     * Returns whether the given field name is an absolute path.
     *
     * @param fieldName the field name to check
     * @return true if absolute
     */
    public abstract boolean isAbsolute(String fieldName);

    /**
     * Returns whether the field reference points to a single element (not an array).
     *
     * @param fieldName the field reference
     * @return true if single element
     */
    public abstract boolean isSingleElement(String fieldName);

    /**
     * Returns the base path for the given path (parent path).
     *
     * @param path the full path
     * @return the base path
     */
    public abstract String getBasepath(String path);

    /**
     * Returns field references that may include generalized/wildcard paths.
     *
     * @return iterable of generalized field references
     */
    public Iterable<String> getFieldReferencesWithGeneralization() {
        return getFieldReferences();
    }

    /**
     * Formats this record to its serialized string form.
     *
     * @return the formatted record string
     */
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
