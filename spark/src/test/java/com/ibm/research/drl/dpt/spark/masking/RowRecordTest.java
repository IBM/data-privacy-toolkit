/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.masking;

import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.spark.record.RowRecord;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class RowRecordTest {
    @Test
    public void fromRow() {
        Row row = RowFactory.create("123", 456);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);

        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num"), fieldMap, Arrays.asList("StringType", "IntegerType"));
        assertFalse(record.isHeader());
        assertEquals(2, ((RowRecord)record).getValues().length);
        Object[] values = ((RowRecord)record).getValues();
        assertEquals("123", (String)values[0]);
        assertEquals(456, ((Integer)values[1]).intValue());
    }

    @Test
    public void getValues() {
        Row row = RowFactory.create("123", 456);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);

        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num"), fieldMap, Arrays.asList("StringType", "IntegerType"));
        Object[] values = ((RowRecord)record).getValues();
        
        assertTrue(values[0] instanceof String);
        assertEquals("123", (String)values[0]);
        assertTrue(values[1] instanceof Integer);
        assertEquals(456, ((Integer)values[1]).intValue());
    }

    @Test
    public void getFieldValue() {
        Row row = RowFactory.create("123", 456);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);

        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num"), fieldMap, Arrays.asList("StringType", "IntegerType"));

        assertEquals("123", new String(record.getFieldValue("id")));
        assertEquals("456", new String(record.getFieldValue("num")));
    }

    @Test
    public void getFieldValueNull() {
        Row row = RowFactory.create("123", 456, null);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);
        fieldMap.put("desc", 2);

        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num", "desc"), fieldMap, Arrays.asList("StringType", "IntegerType", "StringType"));
        assertNull(record.getFieldValue("desc"));
    }

    @Test
    public void testSetFieldValueTypeClash() {
        Row row = RowFactory.create("123", 456, null);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);
        fieldMap.put("desc", 2);

        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num", "desc"), fieldMap, Arrays.asList("StringType", "IntegerType", "LongType"));

        record.setFieldValue("num", "abc".getBytes());
    }
    
    @Test
    public void setFieldValue() {
        Row row = RowFactory.create("123", 456, null);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);
        fieldMap.put("desc", 2);

        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num", "desc"), fieldMap, Arrays.asList("StringType", "IntegerType", "LongType"));
        
        record.setFieldValue("id", "foo".getBytes());
        record.setFieldValue("num", "678".getBytes());
        record.setFieldValue("desc", "111".getBytes());
        
        assertEquals("foo", new String(record.getFieldValue("id")));
        assertEquals("678", new String(record.getFieldValue("num")));
        assertEquals("111", new String(record.getFieldValue("desc")));
    }

    @Test
    public void getFieldReferences() {
        Row row = RowFactory.create("123", 123);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);

        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num"), fieldMap, Arrays.asList("StringType", "IntegerType"));
        Iterable<String> fields = record.getFieldReferences();
        Iterator<String> iterator = fields.iterator();
        assertEquals("id", iterator.next());
        assertEquals("num", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void isHeader() {
        Row row = RowFactory.create("123", 123);
        Map<String, Integer> fieldMap = new HashMap<>();
        fieldMap.put("id", 0);
        fieldMap.put("num", 1);
        
        Record record = RowRecord.fromRow(row, Arrays.asList("id", "num"), fieldMap, Arrays.asList("StringType", "IntegerType"));
        assertFalse(record.isHeader());
    }
}