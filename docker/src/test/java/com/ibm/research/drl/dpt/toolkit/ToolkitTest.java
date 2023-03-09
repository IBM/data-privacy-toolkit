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
package com.ibm.research.drl.dpt.toolkit;


import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.AbstractHierarchy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;

public class ToolkitTest {
    @Test
    @Disabled("To be ported to new format")
    public void runningScanOnZipFile() throws Exception {
        String target = ToolkitTest.class.getResource("/test.zip").toURI().getPath();
        String piList = ToolkitTest.class.getResource("/personal_information_types.txt").toURI().getPath();
        String identifiers = ToolkitTest.class.getResource("/identifiersPI.json").toURI().getPath();

        String[] args = Arrays.asList("-d", target, "-C", "SCAN", "--identifiers", identifiers, "--piList", piList, "-n", "4").toArray(new String[]{});

        Toolkit.main(args);
    }

    @Disabled("Because missing licence file")
    @Test
    public void runningMaskingWithConfiguration() throws Exception {
        String testFile = ToolkitTest.class.getResource("/100.csv").toURI().getPath();
        String configuration = ToolkitTest.class.getResource("/testMaskingConfiguration.json").toURI().getPath();

        String[] args = Arrays.asList("-d", testFile, "-C", "MASK", "-M", configuration).toArray(new String[]{});

        Toolkit.main(args);
    }

    @Disabled("Because using old parameters")
    @Test
    public void runningMaskingWithConfigurationAndUserDefinedProviders() throws Exception {
        String testFile = ToolkitTest.class.getResource("/100.csv").toURI().getPath();
        String configuration = ToolkitTest.class.getResource("/testMaskingConfigurationWithUserDefined.json").toURI().getPath();
        String userDefinedProviders = ToolkitTest.class.getResource("/testUserDefinedMaskingProviders.txt").toURI().getPath();

        String[] args = Arrays.asList("-d", testFile, "-C", "MASK", "-M", configuration, "-p", userDefinedProviders).toArray(new String[]{});

        Toolkit.main(args);
    }

    @Disabled("Because missing licence file")
    @Test
    public void runningAnonymizationWithConfiguration() throws Exception {
        String testFile = ToolkitTest.class.getResource("/healthcare-dataset.txt").toURI().getPath();
        String configurationFile = ToolkitTest.class.getResource("/testConfiguration.json").toURI().getPath();
        String licenseFile = ToolkitTest.class.getResource("/license.mac").toURI().getPath();

        String[] args = Arrays.asList(
                "-l", licenseFile,
                "-d", testFile,
                "-C", "ANONYMIZE", "-F", configurationFile, "-A", "OLA", "-o", "healthcare-dataset.anonymized.csv"

        ).toArray(new String[]{});


        Toolkit.main(args);
    }

    public static class UserDefinedHierarchy extends AbstractHierarchy {
        public UserDefinedHierarchy(JsonNode node) {
            super(node);
        }

        @Override
        public int getHeight() {
            return 5;
        }

        @Override
        public long getTotalLeaves() {
            return 5;
        }

        @Override
        public int leavesForNode(String value) {
            return 1;
        }

        @Override
        public Set<String> getNodeLeaves(String value) {
            return null;
        }

        @Override
        public int getNodeLevel(String value) {
            return 0;
        }

        @Override
        public String getTopTerm() {
            return "*";
        }

        @Override
        public String encode(String value, int level, boolean randomizeOnFail) {
            return "TEST";
        }
    }

    @Disabled("Because using old parameters")
    @Test
    public void runningAnonymizationWithConfigurationAndUserDefinedHierarchy() throws Exception {
        String testFile = ToolkitTest.class.getResource("/healthcare-dataset.txt").toURI().getPath();
        String configurationFile = ToolkitTest.class.getResource("/testConfigurationWithUserDefinedHierarchy.json").toURI().getPath();

        String[] args = Arrays.asList(
                "-d", testFile,
                "-C", "ANONYMIZE", "-F", configurationFile, "-A", "OLA", "-o", "healthcare-dataset.anonymized.csv"

        ).toArray(new String[]{});


        Toolkit.main(args);
    }

    @Disabled("Because using old parameters")
    @Test
    public void testIdentification() throws Exception {
        String testFile = ToolkitTest.class.getResource("/healthcare-dataset.txt").toURI().getPath();

        String[] args = {
                "-d", testFile,
                "-C", "IDENTIFY_TYPES"
        };

        Toolkit.main(args);
    }

    @Disabled("Because using old parameters")
    @Test
    public void testWithHighSuppressionAndTrashFile() throws Exception {
        String testFile = ToolkitTest.class.getResource("/healthcare-dataset.txt").toURI().getPath();

        String configuration = ToolkitTest.class.getResource("/configuration_anonymization_healthcare_sup10.json").toURI().getPath();

        String trashFile = Files.createTempFile("__trash", "trash__").toString();

        String[] args = {
                "-d", testFile,
                "-C", "ANONYMIZE",
                "-T", trashFile,
                "-A", "OLA",
                "-F", configuration
        };

        Toolkit.main(args);
    }

    @Test
    public void testMain() throws IOException {
        Toolkit.main(new String[] {
                "-i",
                ToolkitTest.class.getResource("/transactions.csv").getPath(),
                "-c",
                ToolkitTest.class.getResource("/transaction-uniqueness-ok.json").getPath(),
                "-o",
                Files.createTempFile("transaction-uniqueness", "report").toAbsolutePath().toString()});
    }

    @Test
    public void testMainForFlow() throws IOException {
        Toolkit.main(new String[] {
                "-i",
                ToolkitTest.class.getResource("/healthcare-dataset.txt").getPath(),
                "-c",
                ToolkitTest.class.getResource("/test-flow.json").getPath(),
                "-o",
                Files.createTempFile("test-flow", "report").toAbsolutePath().toString()});
    }
}
