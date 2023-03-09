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
package com.ibm.research.drl.dpt.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.models.fhir.resources.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public class FHIRValidationTest {
    private static class ElementDefinition {
        private final int min;
        private final int max;
        private final String name;
        private final Collection<String> types;

        public Collection<String> getTypes() {
            return types;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public String getName() {
            return name;
        }

        public ElementDefinition(String name, Collection<String> types, String minValue, String maxValue) {
            this.name = name;
            this.types = types;
            this.min = Integer.parseInt(minValue);

            if (maxValue.equals("*")) {
                max = -1;
            }
            else {
                max = Integer.parseInt(maxValue);
            }

        }
    }

    private Map<String, Class> profileToClass;

    @BeforeEach
    public void setUp() {
        this.profileToClass = new HashMap<>();
        this.profileToClass.put("/fhir/profiles/bodysite.profile.json", FHIRBodySite.class);
        this.profileToClass.put("/fhir/profiles/contract.profile.json", FHIRContract.class);
        this.profileToClass.put("/fhir/profiles/device.profile.json", FHIRDevice.class);
        this.profileToClass.put("/fhir/profiles/devicemetric.profile.json", FHIRDeviceMetric.class);
        this.profileToClass.put("/fhir/profiles/group.profile.json", FHIRGroup.class);
        this.profileToClass.put("/fhir/profiles/location.profile.json", FHIRLocation.class);
        this.profileToClass.put("/fhir/profiles/medication.profile.json", FHIRMedication.class);
        this.profileToClass.put("/fhir/profiles/medicationadministration.profile.json", FHIRMedicationAdministration.class);
        this.profileToClass.put("/fhir/profiles/medicationorder.profile.json", FHIRMedicationOrder.class);
        this.profileToClass.put("/fhir/profiles/observation.profile.json", FHIRObservation.class);
        this.profileToClass.put("/fhir/profiles/patient.profile.json", FHIRPatient.class);
        this.profileToClass.put("/fhir/profiles/practitioner.profile.json", FHIRPractitioner.class);
        this.profileToClass.put("/fhir/profiles/questionnaire.profile.json", FHIRQuestionnaire.class);
        this.profileToClass.put("/fhir/profiles/questionnaireresponse.profile.json", FHIRQuestionnaireResponse.class);
        this.profileToClass.put("/fhir/profiles/goal.profile.json", FHIRGoal.class);
        this.profileToClass.put("/fhir/profiles/devicecomponent.profile.json", FHIRDeviceComponent.class);
        this.profileToClass.put("/fhir/profiles/auditevent.profile.json", FHIRAuditEvent.class);
        this.profileToClass.put("/fhir/profiles/organization.profile.json", FHIROrganization.class);
    }

    private Collection<String> extractTypes(JsonNode typesNode) {
        Collection<String> types = new ArrayList<>();

        if (typesNode == null) {
            return types;
        }

        for (JsonNode aTypesNode : typesNode) {
            types.add(aTypesNode.get("code").asText());
        }

        return types;
    }

    private int countDots(String s, char delimiter) {

        int index = s.indexOf(delimiter);
        int count = 0;

        while (index != -1) {
            count++;

            s = s.substring(index + 1);
            index = s.indexOf(delimiter);
        }

        return count;

    }

    private Collection<ElementDefinition> getListOfFields(InputStream is) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contents = objectMapper.readTree(is);

        JsonNode elements = contents.get("snapshot").get("element");

        Collection<ElementDefinition> fieldNames = new ArrayList<>();

        for (JsonNode node : elements) {
            String fieldName = node.get("path").asText();

            JsonNode typesNode = node.get("type");
            Collection<String> types = extractTypes(typesNode);

            int numberOfDots = countDots(fieldName, '.');

            if (numberOfDots != 1) {
                continue;
            }

            String baseName = fieldName.substring(fieldName.indexOf('.') + 1);

            if (fieldName.contains("[x]")) {
                baseName = baseName.replace("[x]", "");

                for (String t : types) {
                    Collection<String> ntypes = Collections.singletonList(t);
                    ElementDefinition elementDefinition = new ElementDefinition(
                            baseName + capitalizeFirstLetter(t), ntypes, "0", "1");
                    fieldNames.add(elementDefinition);
                }
            } else {
                ElementDefinition elementDefinition = new ElementDefinition(baseName, types, node.get("min").asText(), node.get("max").asText());
                fieldNames.add(elementDefinition);
            }
        }

        return fieldNames;
    }

    private String getPrefixForGetter(Collection<String> types) {
        if (types != null && types.size() > 0) {
            String type = types.iterator().next();
            if (type.equals("boolean")) {
                return "is";
            }
        }
        return "get";
    }

    private String capitalizeFirstLetter(String field) {
        return Character.toUpperCase(field.charAt(0)) + field.substring(1);
    }

    @Test
    public void testValidation() throws Exception {
        for(String key: this.profileToClass.keySet()) {
            try (InputStream is = this.getClass().getResourceAsStream(key)) {
                Collection<ElementDefinition> fields = getListOfFields(is);

                Class objectClass = this.profileToClass.get(key);

                Method[] methods = objectClass.getMethods();
                Map<String, Method> methodMap = new HashMap<>();

                for (Method method : methods) {
                    methodMap.put(method.getName(), method);
                }

                for (ElementDefinition elementDefinition : fields) {
                    String field = elementDefinition.getName();

                    String getterName = getPrefixForGetter(elementDefinition.getTypes()) + capitalizeFirstLetter(field);

                    Method method = methodMap.get(getterName);
                    boolean methodExists = (method != null);

                    if (!methodExists) {
                        fail(objectClass.getName() + " :: Method does not exist:" + getterName);
                    }

                    int max = elementDefinition.getMax();
                    if (max > 1 || max == -1) {
                        if (!method.getReturnType().equals(Collection.class)) {
                            fail(objectClass.getName() + " :: Method does not return a collection: " + getterName);
                        }
                    }

                }
            }
        }

    }
}
