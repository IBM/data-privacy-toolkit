/*******************************************************************
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
*******************************************************************/
package com.ibm.research.drl.dpt.schema;

import com.ibm.research.drl.schema.IPVSchemaField;
import com.ibm.research.drl.schema.IPVSchemaFieldType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

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
            assertTrue(expected.contains(itemSet.toString()), "\"" + itemSet.toString() + "\"");

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
