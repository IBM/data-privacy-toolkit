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

/** Represents the type of an identified NLP entity, including subtype and source annotation. */
public final class IdentifiedEntityType implements Serializable {
    /** The entity type label. */
    private final String type;
    /** The entity subtype label. */
    private final String subtype;
    /** The annotation source that produced this identification. */
    private final String source;

    /** Constant representing an unknown annotation source. */
    public final static String UNKNOWN_SOURCE = "__UNKNOWN_SOURCE__";

    /**
     * Returns the type name.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the subtype name.
     *
     * @return the subtype
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * Returns the annotation source identifier.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Constructs an IdentifiedEntityType.
     *
     * @param type    the entity type name (must not be {@code null})
     * @param subtype the entity subtype
     * @param source  the annotation source identifier
     */
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
