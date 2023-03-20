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
package com.ibm.research.drl.dpt.rest.models.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ibm.research.drl.dpt.rest.models.CompleteAlgorithmConfiguration;

import java.io.IOException;

public class CompleteAlgorithmConfigurationSerializer extends JsonSerializer<CompleteAlgorithmConfiguration> {
    @Override
    public void serialize(CompleteAlgorithmConfiguration completeAlgorithmConfiguration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        for (CompleteAlgorithmConfiguration.SUPPORTED_CONFIGURATION name : completeAlgorithmConfiguration.getOptionNames()) {
            Object value = completeAlgorithmConfiguration.getOption(name);

            if (value instanceof Number) {
                if (value instanceof Integer) jsonGenerator.writeNumberField(String.valueOf(name), (Integer) value);
                else if (value instanceof Short) jsonGenerator.writeNumberField(String.valueOf(name), (Short) value);
                else if (value instanceof Long) jsonGenerator.writeNumberField(String.valueOf(name), (Long) value);
                else if (value instanceof Float) jsonGenerator.writeNumberField(String.valueOf(name), (Float) value);
                else if (value instanceof Double) jsonGenerator.writeNumberField(String.valueOf(name), (Double) value);
                else if (value instanceof Byte)
                    jsonGenerator.writeBinaryField(String.valueOf(name), new byte[]{(byte) value});
                else throw new RuntimeException("Unknown type: " + value.getClass().getCanonicalName());

            } else if (value instanceof String) jsonGenerator.writeStringField(String.valueOf(name), (String) value);
            else if (value instanceof Character)
                jsonGenerator.writeStringField(String.valueOf(name), Character.toString((Character) value));
            else throw new RuntimeException("Unknown type: " + value.getClass().getCanonicalName());
        }

        jsonGenerator.writeEndObject();
    }
}

