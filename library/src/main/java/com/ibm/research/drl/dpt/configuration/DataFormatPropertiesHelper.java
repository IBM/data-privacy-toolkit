/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;


import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataFormatPropertiesHelper {

    public static Map<DataTypeFormat, DataFormatProperties> buildProperties(InputStream inputStream) throws IOException {
        Map<DataTypeFormat, DataFormatProperties> results = new HashMap<>();

        JsonNode contents = JsonUtils.MAPPER.readTree(inputStream);

        contents.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode jsonNode = entry.getValue();

            Set<DataTypeFormat> validOutputs = new HashSet<>();

            JsonNode outputNode = jsonNode.get("output");
            outputNode.forEach(o -> {
                validOutputs.add(DataTypeFormat.valueOf(o.asText()));
            });

            DataFormatProperties properties = new DataFormatProperties(
                    jsonNode.get("identification").asBoolean(),
                    jsonNode.get("vulnerabilityAssessment").asBoolean(),
                    jsonNode.get("masking").asBoolean(),
                    jsonNode.get("anonymization").asBoolean(),
                    jsonNode.get("freeText").asBoolean(),
                    validOutputs
            );

            results.put(DataTypeFormat.valueOf(key), properties);
        });

        return results;
    }

}
