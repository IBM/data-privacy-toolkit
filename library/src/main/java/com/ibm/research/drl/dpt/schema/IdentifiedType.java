/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.schema;

import java.io.Serializable;
import java.util.Objects;

/**
 * The type Identified type.
 */
public final class IdentifiedType implements Comparable<IdentifiedType>, Serializable {
    private final String typeName;
    private final long count;

    /**
     * Instantiates a new Identified type.
     *
     * @param typeName the type name
     * @param count    the count
     */
    public IdentifiedType(final String typeName, final long count) {
        this.typeName = typeName;
        this.count = count;
    }

    /**
     * Gets type name.
     *
     * @return the type name
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public long getCount() {
        return count;
    }

    @Override
    public int compareTo(IdentifiedType o) {
        if (null == o) {
            return 1;
        }

        return Long.compare(this.count, o.count);
    }

    public int hashCode() {
        return this.typeName.hashCode();
    }

    @Override
    public String toString() {
        return "IdentifiedType(" + getCount() + ", " + getTypeName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiedType that = (IdentifiedType) o;
        return count == that.count && Objects.equals(typeName, that.typeName);
    }
}
