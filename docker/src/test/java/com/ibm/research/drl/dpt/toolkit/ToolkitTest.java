/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit;


import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.AbstractHierarchy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;

public class ToolkitTest {
    @Test
    public void runningScanOnZipFile() throws Exception {
        String target = this.getClass().getResource("/test.zip").toURI().getPath();
        String piList = this.getClass().getResource("/personal_information_types.txt").toURI().getPath();
        String identifiers = this.getClass().getResource("/identifiersPI.json").toURI().getPath();

        String[] args = Arrays.asList("-d", target, "-C", "SCAN", "--identifiers", identifiers, "--piList", piList, "-n", "4").toArray(new String[]{});

        Toolkit.main(args);
    }

    @Disabled("Because missing licence file")
    @Test
    public void runningMaskingWithConfiguration() throws Exception {
        String testFile = this.getClass().getResource("/100.csv").toURI().getPath();
        String configuration = this.getClass().getResource("/testMaskingConfiguration.json").toURI().getPath();
        String license = this.getClass().getResource("/license.mac").toURI().getPath();

        String[] args = Arrays.asList("-l", license, "-d", testFile, "-C", "MASK", "-M", configuration).toArray(new String[]{});

        Toolkit.main(args);
    }

    @Disabled("Because missing licence file")
    @Test
    public void runningMaskingWithConfigurationAndUserDefinedProviders() throws Exception {
        String testFile = this.getClass().getResource("/100.csv").toURI().getPath();
        String configuration = this.getClass().getResource("/testMaskingConfigurationWithUserDefined.json").toURI().getPath();
        String userDefinedProviders = this.getClass().getResource("/testUserDefinedMaskingProviders.txt").toURI().getPath();

        String[] args = Arrays.asList("-d", testFile, "-C", "MASK", "-M", configuration, "-p", userDefinedProviders).toArray(new String[]{});

        Toolkit.main(args);
    }

    @Disabled("Because missing licence file")
    @Test
    public void runningAnonymizationWithConfiguration() throws Exception {
        String testFile = this.getClass().getResource("/healthcare-dataset.txt").toURI().getPath();
        String configurationFile = this.getClass().getResource("/testConfiguration.json").toURI().getPath();
        String licenseFile = this.getClass().getResource("/license.mac").toURI().getPath();

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

    @Disabled("Because missing licence file")
    @Test
    public void runningAnonymizationWithConfigurationAndUserDefinedHierarchy() throws Exception {
        String testFile = this.getClass().getResource("/healthcare-dataset.txt").toURI().getPath();
        String configurationFile = this.getClass().getResource("/testConfigurationWithUserDefinedHierarchy.json").toURI().getPath();
        String licenseFile = this.getClass().getResource("/license.mac").toURI().getPath();

        String[] args = Arrays.asList(
                "-l", licenseFile,
                "-d", testFile,
                "-C", "ANONYMIZE", "-F", configurationFile, "-A", "OLA", "-o", "healthcare-dataset.anonymized.csv"

        ).toArray(new String[]{});


        Toolkit.main(args);
    }

    @Disabled("Because missing licence file")
    @Test
    public void testIdentification() throws Exception {
        String testFile = this.getClass().getResource("/healthcare-dataset.txt").toURI().getPath();
        String licenseFile = this.getClass().getResource("/license.mac").toURI().getPath();

        //-C IDENTIFY_TYPES -d input_identification.csv -l license.mac -h
        String[] args = {
                "-l", licenseFile,
                "-d", testFile,
                "-C", "IDENTIFY_TYPES"

        };

        Toolkit.main(args);
    }

    @Disabled("Because missing licence file")
    @Test
    public void testWithHighSuppressionAndTrashFile() throws Exception {
        String testFile = this.getClass().getResource("/healthcare-dataset.txt").toURI().getPath();
        String licenseFile = this.getClass().getResource("/license.mac").toURI().getPath();

        String configuration = this.getClass().getResource("/configuration_anonymization_healthcare_sup10.json").toURI().getPath();

        String trashFile = Files.createTempFile("__trash", "trash__").toAbsolutePath().toString();

        String[] args = {
                "-l", licenseFile,
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
                getClass().getResource("/transactions.csv").getPath(),
                "-c",
                getClass().getResource("/transaction-uniqueness-ok.json").getPath(),
                "-o",
                File.createTempFile("transaction-uniqueness", "report").getPath()});
    }

    @Test
    public void testMainForFlow() throws IOException {
        Toolkit.main(new String[] {
                "-i",
                getClass().getResource("/healthcare-dataset.txt").getPath(),
                "-c",
                getClass().getResource("/test-flow.json").getPath(),
                "-o",
                File.createTempFile("test-flow", "report").getPath()});
    }
}
