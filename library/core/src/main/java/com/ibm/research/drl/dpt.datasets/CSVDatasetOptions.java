package com.ibm.research.drl.prima.datasets;
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class CSVDatasetOptions implements DatasetOptions, Serializable {

    private static final long serialVersionUID = 1302896564898158708L;
    private final Character fieldDelimiter;
    private final Character quoteChar;
    private final boolean hasHeader;
    private final boolean trimFields;

    public Character getFieldDelimiter() {
        return fieldDelimiter;
    }

    public Character getQuoteChar() {
        return quoteChar;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

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
