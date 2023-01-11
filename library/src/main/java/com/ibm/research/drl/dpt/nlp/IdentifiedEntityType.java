package com.ibm.research.drl.dpt.nlp;
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public final class IdentifiedEntityType implements Serializable {
    private final String type;
    private final String subtype;
    private final String source;
    
    public final static String UNKNOWN_SOURCE = "__UNKNOWN_SOURCE__";

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getSource() {
        return source;
    }

    @JsonCreator
    public IdentifiedEntityType(
            @JsonProperty("type") String type, 
            @JsonProperty("subtype") String subtype,
            @JsonProperty("source") String source) {
        if (null == type) throw new NullPointerException();

        this.type = type;
        this.subtype = subtype;
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        IdentifiedEntityType t = (IdentifiedEntityType) o;
        
        return (type.equals(t.type) && subtype.equals(t.subtype));
    }

    @Override
    public int hashCode() {
        return (type + ":" + subtype).hashCode();
    }
    
    @Override
    public String toString() {
        return type;
    }
}
