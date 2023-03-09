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
package com.ibm.research.drl.dpt.processors.records;


import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.NoSuchElementException;

public class HL7Record implements Record {
    private final static Logger logger = LogManager.getLogger(HL7Record.class);

    private final Terser terser;
    private final Message message;

    public HL7Record(Message message) {
        this.message = message;
        this.terser = new Terser(message);
    }

    @Override
    public Iterable<String> getFieldReferences() {
        return Collections.emptyList();
    }

    @Override
    public byte[] getFieldValue(String xRef) {
        try {
            String v = terser.get(xRef);
            if (v == null) {
                return null;
            }

            return v.getBytes();
        } catch (NoSuchElementException | HL7Exception e) {
            logger.info("Not existing element", e);
            return null;
        }
    }

    @Override
    public void setFieldValue(String xRef, byte[] value) {
        try {
            terser.set(xRef, (value == null ? "\"\"" : new String(value)));
        } catch (HL7Exception e) {
            throw new RuntimeException("Unable to set value to " + xRef);
        }
    }

    @Override
    public void suppressField(String field) {
        try {
            terser.set(field, "");
        } catch (HL7Exception e) {
            throw new RuntimeException("Unable to suppress value of " + field);
        }
    }

    protected String formatRecord() {
        try {
            return message.encode();
        } catch (HL7Exception e) {
            throw new RuntimeException("Unable to encode the message: " + e.getMessage());
        }
    }

    @Override
    public final String toString() {
        return formatRecord();
    }

    protected byte[] formatRecordBytes() {
        return formatRecord().getBytes();
    }

    @Override
    public final byte[] toBytes() {
        return formatRecordBytes();
    }

    @Override
    public boolean isHeader() {
        return false;
    }

}
