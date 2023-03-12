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
package com.ibm.research.drl.dpt.anonymization.ola;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.SensitiveColumnInformation;
import com.ibm.research.drl.dpt.anonymization.constraints.DistinctLDiversity;
import com.ibm.research.drl.dpt.anonymization.constraints.EntropyLDiversity;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.constraints.RecursiveCLDiversity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DemoGenTest {

    public static Map<String, MaterializedHierarchy> hierarchiesFromJSON(JsonNode contents) {
        JsonNode hierarchies = contents.findPath("hierarchies");

        if (hierarchies == null || hierarchies.isNull() || !hierarchies.isObject()) {
            throw new RuntimeException("hierarchies key is either missing or null or not an object");
        }

        /*
          "hierarchies": {
             "age": [
                ["01/01/2008", "Jan_2008", "2008"],
                ["02/01/2008", "Jan_2008", "2008"]
             ]
          }
        */

        Map<String, MaterializedHierarchy> hierarchyMap = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> iterator = hierarchies.fields();

        while(iterator.hasNext()) {
            Map.Entry<String, JsonNode> field = iterator.next();
            String hierarchyName = field.getKey();

            MaterializedHierarchy materializedHierarchy = new MaterializedHierarchy();

            JsonNode hierarchyValues = field.getValue();

            for (JsonNode hierarchyEntry : hierarchyValues) {
                Iterator<JsonNode> entryIterator = hierarchyEntry.iterator();
                List<String> entries = new ArrayList<>();

                while (entryIterator.hasNext()) {
                    String v = entryIterator.next().asText();
                    entries.add(v);
                }

                if (entries.size() == 0) {
                    throw new RuntimeException("Empty entries for hierarchy");
                }

                materializedHierarchy.add(entries);
            }

            hierarchyMap.put(hierarchyName, materializedHierarchy);
        }

        return hierarchyMap;
    }

    private OLA initOLA(List<PrivacyConstraint> privacyConstraints, double suppression, IPVDataset original, List<ColumnInformation> columnInformationList) {
        OLAOptions olaOptions = new OLAOptions(suppression);

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        return ola;
    }

    public static List<ColumnInformation> columnInformationFromJSON(JsonNode configurationJSON) {
        /*
            "columnInformation": [
                {"type": "QUASI", "hierarchy": ""},
                {"type": "SENSITIVE"},
                {"type": "NORMAL"},
            ]
         */

        Map<String, MaterializedHierarchy> defaultHierarchyMap = hierarchiesFromJSON(configurationJSON);

        List<ColumnInformation> columnInformationList = new ArrayList<>();

        JsonNode contents = configurationJSON.get("columnInformation");

        for (JsonNode entry : contents) {
            ColumnType columnType = ColumnType.valueOf(entry.get("type").asText());

            if (columnType == ColumnType.QUASI) {
                String hierarchyName = entry.get("hierarchy").asText();
                MaterializedHierarchy hierarchy = defaultHierarchyMap.get(hierarchyName);
                columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

            } else if (columnType == ColumnType.SENSITIVE) {
                columnInformationList.add(new SensitiveColumnInformation());
            } else {
                columnInformationList.add(new DefaultColumnInformation());
            }
        }


        return columnInformationList;
    }

    public static List<PrivacyConstraint> privacyConstraintsFromJSON(JsonNode contents) {
        if (contents == null) {
            throw new RuntimeException("Missing privacy constraints");
        }

        JsonNode node = contents.findPath("privacyConstraints");

        if (node.isNull() || !node.isArray()) {
            throw new RuntimeException("Privacy constraints is not an array");
        }

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();

        for (JsonNode element : node) {
            if (element.isNull() || !element.isObject()) {
                throw new RuntimeException("Element in privacy constraints array is null or not an object");
            }

            JsonNode nameNode = element.get("name");
            if (nameNode == null || nameNode.isNull() || !nameNode.isTextual()) {
                throw new RuntimeException("Name of privacy constraint missing or null or not a text");
            }

            String name = nameNode.asText();

            switch (name) {
                case "k":
                    JsonNode kNode = element.get("k");
                    if (kNode == null || kNode.isNull() || !kNode.isInt()) {
                        throw new RuntimeException("parameter for k is either missing, null or not an integer");
                    }
                    privacyConstraints.add(new KAnonymity(kNode.intValue()));
                    break;
                case "distinctL":
                    JsonNode lNode = element.get("l");
                    if (lNode == null || lNode.isNull() || !lNode.isInt()) {
                        throw new RuntimeException("parameter for distinctL is either missing, null or not an integer");
                    }
                    privacyConstraints.add(new DistinctLDiversity(lNode.intValue()));
                    break;
                case "entropyL":
                    JsonNode leNode = element.get("l");
                    if (leNode == null || leNode.isNull() || !leNode.isInt()) {
                        throw new RuntimeException("parameter for entropyL is either missing, null or not an integer");
                    }
                    privacyConstraints.add(new EntropyLDiversity(leNode.intValue()));
                    break;
                case "recursiveCL":
                    JsonNode lcNode = element.get("l");
                    if (lcNode == null || lcNode.isNull() || !lcNode.isInt()) {
                        throw new RuntimeException("parameter for recursiveCL is either missing, null or not an integer");
                    }

                    JsonNode cNode = element.get("c");
                    if (cNode == null || cNode.isNull() || !cNode.isInt()) {
                        throw new RuntimeException("parameter for recursiveCL is either missing, null or not an integer");
                    }
                    privacyConstraints.add(new RecursiveCLDiversity(lcNode.intValue(), cNode.asDouble()));
                    break;
                default:
                    throw new RuntimeException("unknown privacy constraint name");
            }
        }

        return privacyConstraints;
    }

    @Test
    @Disabled
    public void testGenData() throws Exception {
        try (InputStream configuration = DemoGenTest.class.getResourceAsStream("/olaDemogen.json");) {
            JsonNode confContents = JsonUtils.MAPPER.readTree(configuration);

            List<ColumnInformation> columnInformationList = columnInformationFromJSON(confContents);
            List<PrivacyConstraint> privacyConstraints = privacyConstraintsFromJSON(confContents);
            double suppression = confContents.get("options").get("suppression").asDouble();

            try (InputStream data = DemoGenTest.class.getResourceAsStream("/random1.txt")) {
                IPVDataset original = IPVDataset.load(data, false, ',', '"', false);

                OLA ola = initOLA(privacyConstraints, suppression, original, columnInformationList);
                IPVDataset anonymized = ola.apply();

                assertNotNull(anonymized);

                System.out.println("OLA best node: " + ola.reportBestNode());
            }
        }
    }
}

