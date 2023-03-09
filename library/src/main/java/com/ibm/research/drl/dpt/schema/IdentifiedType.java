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
