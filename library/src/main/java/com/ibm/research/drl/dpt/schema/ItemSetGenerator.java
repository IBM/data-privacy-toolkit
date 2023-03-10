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

import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

/**
 * The type Item set generator.
 */
public class ItemSetGenerator implements Iterable<ItemSet> {
    /**
     * The Fields.
     */
    protected final List<? extends IPVSchemaField> fields;
    /**
     * The Banned.
     */
    protected final Set<ItemSet> banned;

    /**
     * Instantiates a new Item set generator.
     *
     * @param fields the fields
     */
    public ItemSetGenerator(final List<? extends IPVSchemaField> fields) {
        this.fields = fields;
        banned = new TreeSet<>();
    }

    /**
     * From field list item set generator.
     *
     * @param fieldNames the field names
     * @return the item set generator
     */
    public static ItemSetGenerator fromFieldList(String... fieldNames) {
        return new ItemSetGenerator(toIPVSchemaFieldList(fieldNames));
    }

    private static List<? extends IPVSchemaField> toIPVSchemaFieldList(String... fieldNames) {
        List<IPVSchemaField> fieldList = new ArrayList<>(fieldNames.length);

        for (final String fieldName : fieldNames)
            fieldList.add(new DummyIPVSchemaField(fieldName));

        return fieldList;
    }

    /**
     * To schema fields collection.
     *
     * @param itemSet the item set
     * @return the collection
     */
    public Collection<IPVSchemaField> toSchemaFields(final ItemSet itemSet) {
        Collection<IPVSchemaField> itemSetFields = new ArrayList<>(fields.size());

        for (int i = 0; i < itemSet.size(); ++i) {
            if (itemSet.get(i)) {
                itemSetFields.add(fields.get(i));
            }
        }

        return itemSetFields;
    }

    @Override
    public Iterator<ItemSet> iterator() {
        return new Iterator<ItemSet>() {
            private long counter = 1L;
            private final long max = (long) Math.pow(2, fields.size());

            @Override
            public boolean hasNext() {
                return counter < max;
            }

            @Override
            public ItemSet next() {
                if (!hasNext()) throw new NoSuchElementException();

                do {
                    final ItemSet itemSet = new ItemSet(fields.size());

                    long mask = 0x01;

                    for (int i = 0; i < fields.size(); ++i) {
                        if (mask == (mask & counter)) {
                            itemSet.set(i);
                        }

                        mask <<= 1;
                    }

                    counter += 1;

                    if (!isBanned(itemSet))
                        return itemSet;
                } while (true);
            }

            @Override
            public void remove() {
                throw new RuntimeException("Not implemented");
            }
        };
    }

    private boolean isBanned(ItemSet itemSet) {
        for (ItemSet is : banned) {
            ItemSet is2 = new ItemSet(is.length());

            for (int i = 0; i < is.length(); ++i) {
                if (is.get(i) && itemSet.get(i)) {
                    is2.set(i);
                }
            }

            if (is.equals(is2) || itemSet.equals(is2))
                return true;
        }

        return false;
    }

    /**
     * Gets number of schema attributes.
     *
     * @return the number of schema attributes
     */
    public int getNumberOfSchemaAttributes() {
        return fields.size();
    }

    /**
     * Ban.
     *
     * @param itemSet the item set
     */
    public synchronized void ban(final ItemSet itemSet) {
        this.banned.add(itemSet);
    }

    private static final class DummyIPVSchemaField implements IPVSchemaField {
        private final String fieldName;

        private DummyIPVSchemaField(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String getName() {
            return fieldName;
        }

        @Override
        public IPVSchemaFieldType getType() {
            return IPVSchemaFieldType.STRING;
        }
    }
}
