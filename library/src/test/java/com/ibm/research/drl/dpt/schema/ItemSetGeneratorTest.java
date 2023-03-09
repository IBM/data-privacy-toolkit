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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemSetGeneratorTest {
    private List<IPVSchemaField> fields;
    private ItemSetGenerator isg;

    @BeforeEach
    public void setUp() {
        fields = Arrays.asList(new IPVSchemaField() {
            @Override
            public String getName() {
                return "f1";
            }

            @Override
            public IPVSchemaFieldType getType() {
                return IPVSchemaFieldType.INT;
            }
        },  new IPVSchemaField() {
            @Override
            public String getName() {
                return "f2";
            }

            @Override
            public IPVSchemaFieldType getType() {
                return IPVSchemaFieldType.BOOLEAN;
            }
        }, new IPVSchemaField() {
            @Override
            public String getName() {
                return "f3";
            }

            @Override
            public IPVSchemaFieldType getType() {
                return IPVSchemaFieldType.ATTACHMENT_BLOB;
            }
        });

        isg = new ItemSetGenerator(fields);
    }

    @Test
    public void testGeneration() throws Exception {
        Set<ItemSet> itemSets = new HashSet<>();

        Collection<String> expected = Arrays.asList("{0}", "{1}", "{0, 1}", "{2}", "{0, 2}", "{1, 2}", "{0, 1, 2}");

        for (ItemSet itemSet : isg) {
            assertTrue(expected.contains(itemSet.toString()), "\"" + itemSet + "\"");

            itemSets.add(itemSet);
        }

        assertThat(itemSets.size(), is((int) Math.pow(2, fields.size()) - 1));
    }

    @Test
    public void testFieldConsistency() throws Exception {
        assertThat(isg.getNumberOfSchemaAttributes(), is(fields.size()));
    }

    @Test
    public void testConversion() throws Exception {
        Collection<IPVSchemaField> v = isg.toSchemaFields(new ItemSet("{0}"));

        assertTrue(v.contains(fields.get(0)));

        v = isg.toSchemaFields(new ItemSet("{0, 2}"));

        assertTrue(v.contains(fields.get(0)));
        assertTrue(v.contains(fields.get(2)));
        assertFalse(v.contains(fields.get(1)));
    }
}
