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
package com.ibm.research.drl.dpt.anonymization.hierarchies;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.*;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class GeneralizationHierarchyFactory {
    private static final Logger logger = LogManager.getLogger(GeneralizationHierarchyFactory.class);

    public static GeneralizationHierarchy getDefaultHierarchy(ProviderType type) {
        return getDefaultHierarchy(type.name().toUpperCase());
    }

    public static GeneralizationHierarchy getGenericFromFixedSet(List<String> terms) {
        return getGenericFromFixedSet(terms, "*");
    }

    public static GeneralizationHierarchy getGenericFromFixedSet(List<String> terms, String topTerm) {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();

        for (String v : terms) {
            String[] items = new String[2];
            items[0] = v;
            items[1] = topTerm;
            hierarchy.add(items);
        }

        return hierarchy;
    }

    public static GeneralizationHierarchy getDefaultHierarchy(JsonNode hierarchy) {
        return getDefaultHierarchy(hierarchy.get("type").asText().toUpperCase(), hierarchy);
    }

    public static GeneralizationHierarchy getDefaultHierarchy(String hierarchyType) {
        return getDefaultHierarchy(hierarchyType, null);
    }

    private static GeneralizationHierarchy getDefaultHierarchy(String hierarchyType, JsonNode hierarchyConfig) {
        switch (hierarchyType) {
            case "COUNTRY":
                return CountryHierarchy.getInstance();
            case "CITY":
                return CityHierarchy.getInstance();
            case "GENDER":
                return GenderHierarchy.getInstance();
            case "RACE":
                return RaceHierarchy.getInstance();
            case "MARITAL_STATUS":
                return MaritalStatusHierarchy.getInstance();
            case "YOB":
                return YOBHierarchy.getInstance();
            case "ICDV9":
                return ICDv9Hierarchy.getInstance();
            case "ZIPCODE":
                return ZIPCodeHierarchy.getInstance();
            case "ZIPCODE_MATERIALIZED":
                return ZIPCodeMaterializedHierarchy.getInstance();
            case "ZIPCODE_COMPUTATIONAL":
                return new ZIPCodeCompBasedHierarchy();
            case "HEIGHT":
                return HeightHierarchy.getInstance();
            case "RELIGION":
                return ReligionHierarchy.getInstance();
            case "DATE-YYYY-MM-DD":
                return new DateYYYYMMDDHierarchy();
            case "DATE":
                if (hierarchyConfig != null && hierarchyConfig.has("format") && !hierarchyConfig.get("format").isNull())
                    return new DateHierarchy(hierarchyConfig.get("format").asText());
                return new DateHierarchy();
            case "LAT_LON":
                return new LatitudeLongitudeHierarchy();
            default:
                return constructHierarchy(hierarchyType);
        }
    }

    private static GeneralizationHierarchy constructHierarchy(String hierarchyType) {
        try {
            Class<? extends GeneralizationHierarchy> hierarchyClass = (Class<? extends GeneralizationHierarchy>) Class.forName(hierarchyType);

            return hierarchyClass.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            logger.error("Unable to instantiate hierarchy class {}", hierarchyType);
            throw new RuntimeException(e);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            logger.error("Unable to create instance of hierarchy {}", hierarchyType);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            logger.error("Unable to retrieve empty constructor for hierarchy {}", hierarchyType);
            throw new RuntimeException(e);
        }
    }

    public static GeneralizationHierarchy buildHierarchy(JsonNode hierarchySpec) {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();

        hierarchySpec.get("terms").forEach(hierarchyPath -> {
            List<String> pathTerms = new ArrayList<>();

            hierarchyPath.elements().forEachRemaining(term -> pathTerms.add(term.asText()));

            hierarchy.add(pathTerms);
        });

        return hierarchy;
    }
}
