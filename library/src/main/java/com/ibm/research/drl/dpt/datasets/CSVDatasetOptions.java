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
package com.ibm.research.drl.dpt.datasets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Dataset options for CSV-formatted inputs, specifying delimiter, quote character, header presence and trimming.
 */
public class CSVDatasetOptions implements DatasetOptions, Serializable {

    private static final long serialVersionUID = 1302896564898158708L;
    private final Character fieldDelimiter;
    private final Character quoteChar;
    private final boolean hasHeader;
    private final boolean trimFields;

    /**
     * Returns the field delimiter character.
     *
     * @return the field delimiter
     */
    public Character getFieldDelimiter() {
        return fieldDelimiter;
    }

    /**
     * Returns the quote character.
     *
     * @return the quote character
     */
    public Character getQuoteChar() {
        return quoteChar;
    }

    /**
     * Returns whether the CSV has a header row.
     *
     * @return true if a header row is present
     */
    public boolean isHasHeader() {
        return hasHeader;
    }

    /**
     * Constructs a CSVDatasetOptions.
     *
     * @param hasHeader      whether the CSV has a header row
     * @param fieldDelimiter the field delimiter character
     * @param quoteChar      the quote character
     * @param trimFields     whether to trim whitespace from field values
     */
    @JsonCreator
    public CSVDatasetOptions(
            @JsonProperty("hasHeader") boolean hasHeader,
            @JsonProperty("fieldDelimiter") Character fieldDelimiter,
            @JsonProperty("quoteChar") Character quoteChar,
            @JsonProperty("trimFields") boolean trimFields) {
        this.fieldDelimiter = fieldDelimiter;
        this.quoteChar = quoteChar;
        this.hasHeader = hasHeader;
        this.trimFields = trimFields;
    }

    /**
     * Returns whether field values should be trimmed.
     *
     * @return true if trim is enabled
     */
    public boolean isTrimFields() {
        return trimFields;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fieldDelimiter", fieldDelimiter)
                .append("hasHeader", hasHeader)
                .append("quoteChar", quoteChar)
                .append("trimFields", trimFields)
                .toString();
    }
}
