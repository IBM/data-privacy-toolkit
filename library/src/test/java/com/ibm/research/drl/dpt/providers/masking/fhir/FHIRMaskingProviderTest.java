/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FHIRMaskingProviderTest {
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testLoadsCorrectRules() throws Exception {
        try (InputStream inputStream = FHIRMaskingProviderTest.class.getResourceAsStream("/fhir/masking-full.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(inputStream));
            MaskingConfiguration maskingConfiguration = configurationManager.getDefaultConfiguration();

            FHIRMaskingProvider ignored = new FHIRMaskingProvider(configurationManager.getDefaultConfiguration(), this.factory);
            assertEquals(47, FHIRMaskingProvider.loadRulesForResource("Device", maskingConfiguration).size());
        }
    }

    @Test
    public void testDevices() throws Exception {
        String[] filenames = new String[]{
                "/fhir/examples/device-example-f001-feedingtube.json",
                "/fhir/examples/device-example-ihe-pcd.json",
                "/fhir/examples/device-example-pacemaker.json",
                "/fhir/examples/device-example-software.json",
                "/fhir/examples/device-example-udi1.json",
                "/fhir/examples/device-example.json",
                "/fhir/examples/patient-example-a.json",
        };

        FHIRMaskingProvider fhirMaskingProvider = new FHIRMaskingProvider(new DefaultMaskingConfiguration(), this.factory);

        for(String filename: filenames) {
            try (InputStream is = FHIRMaskingProviderTest.class.getResourceAsStream(filename)) {
                JsonNode node = JsonUtils.MAPPER.readTree(is);

                String result = fhirMaskingProvider.mask(node);

                assertNotNull(result);
            }
        }
    }

    @Test
    public void testMaintainsDataType() throws Exception {
        try (InputStream inputStream = FHIRMaskingProviderTest.class.getResourceAsStream("/fhir/masking-full.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(inputStream));
            MaskingConfiguration maskingConfiguration = configurationManager.getDefaultConfiguration();

            FHIRMaskingProvider fhirMaskingProvider = new FHIRMaskingProvider(maskingConfiguration, this.factory);

            try (InputStream is = FHIRMaskingProviderTest.class.getResourceAsStream("/fhir/MedicationOrder-230986.json");) {
                JsonNode node = JsonUtils.MAPPER.readTree(is);

                assertTrue(node.get("dispenseRequest").get("numberOfRepeatsAllowed").isInt());
                assertEquals(2, node.get("dispenseRequest").get("numberOfRepeatsAllowed").intValue());

                String maskedString = fhirMaskingProvider.mask(node);
                JsonNode maskedNode = JsonUtils.MAPPER.readTree(maskedString);

                assertTrue(maskedNode.get("dispenseRequest").get("numberOfRepeatsAllowed").isInt());
            }
        }
    }

    @Test
    public void testMaintainsDataTypeArrays() throws Exception {
        try (InputStream inputStream = FHIRMaskingProviderTest.class.getResourceAsStream("/fhir/masking-full.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(inputStream));
            MaskingConfiguration maskingConfiguration = configurationManager.getDefaultConfiguration();

            FHIRMaskingProvider fhirMaskingProvider = new FHIRMaskingProvider(maskingConfiguration, this.factory);

            try (InputStream is = FHIRMaskingProviderTest.class.getResourceAsStream("/fhir/MedicationOrder-arrays-230986.json");) {
                JsonNode node = JsonUtils.MAPPER.readTree(is);

                assertTrue(node.get("dispenseRequest").get("numberOfRepeatsAllowed").isArray());
                assertEquals(2, node.get("dispenseRequest").get("numberOfRepeatsAllowed").get(0).intValue());

                String maskedString = fhirMaskingProvider.mask(node);
                JsonNode maskedNode = JsonUtils.MAPPER.readTree(maskedString);

                System.out.println(maskedNode.get("dispenseRequest").get("numberOfRepeatsAllowed"));
                assertTrue(maskedNode.get("dispenseRequest").get("numberOfRepeatsAllowed").get(0).isInt());
            }
        }
    }

    @Test
    public void testPatientE2E() throws Exception {
        try (InputStream inputStream = FHIRMaskingProviderTest.class.getResourceAsStream("/fhir/maskingConfiguration.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(inputStream));

            FHIRMaskingProvider fhirMaskingProvider = new FHIRMaskingProvider(configurationManager.getDefaultConfiguration(), this.factory);
            ObjectMapper mapper = JsonUtils.MAPPER;

            String filename = "/fhir/patientExample.json";

            try (InputStream is = FHIRMaskingProviderTest.class.getResourceAsStream(filename)) {
                JsonNode node = mapper.readTree(is);
                JsonNode identifier = node.get("identifier").iterator().next();
                String originalPeriodStart = identifier.get("period").get("start").asText();
                assertEquals("2001-05-06", originalPeriodStart);
                assertEquals("12345", identifier.get("value").asText());
                assertEquals("Acme Healthcare", identifier.get("assigner").get("display").asText());

                String result = fhirMaskingProvider.mask(node);
                JsonNode resultNode = mapper.readTree(result);
                JsonNode maskedIdentifier = resultNode.get("identifier").iterator().next();
                String maskedPeriodStart = maskedIdentifier.get("period").get("start").asText();

                assertNotEquals("2001-05-06", maskedPeriodStart);
                assertEquals("Acme Healthcare", maskedIdentifier.get("assigner").get("display").asText());
                assertNull(maskedIdentifier.get("value"));
            }
        }
    }

    @Test
    public void testExamples() throws Exception {
        String[] filenames = new String[]{
                "/fhir/examples/bodysite-example.json",
                "/fhir/examples/careplan-example-GPVisit.json",
                "/fhir/examples/careplan-example-f001-heart.json",
                "/fhir/examples/careplan-example-f002-lung.json",
                "/fhir/examples/careplan-example-f003-pharynx.json",
                "/fhir/examples/careplan-example-f201-renal.json",
                "/fhir/examples/careplan-example-f202-malignancy.json",
                "/fhir/examples/careplan-example-f203-sepsis.json",
                "/fhir/examples/careplan-example-integrated.json",
                "/fhir/examples/careplan-example-pregnancy.json",
                "/fhir/examples/careplan-example.json",
                "/fhir/examples/contract-example.json",
                "/fhir/examples/device-example-f001-feedingtube.json",
                "/fhir/examples/device-example-ihe-pcd.json",
                "/fhir/examples/device-example-pacemaker.json",
                "/fhir/examples/device-example-software.json",
                "/fhir/examples/device-example-udi1.json",
                "/fhir/examples/device-example.json",
                "/fhir/examples/devicecomponent-example-prodspec.json",
                "/fhir/examples/devicecomponent-example.json",
                "/fhir/examples/devicemetric-example.json",
                "/fhir/examples/goal-example.json",
                "/fhir/examples/group-example-member.json",
                "/fhir/examples/group-example.json",
                "/fhir/examples/location-example-ambulance.json",
                "/fhir/examples/location-example-hl7hq.json",
                "/fhir/examples/location-example-patients-home.json",
                "/fhir/examples/location-example-room.json",
                "/fhir/examples/location-example-ukpharmacy.json",
                "/fhir/examples/location-example.json",
                "/fhir/examples/location-extensions-Location-alias.canonical.json",
                "/fhir/examples/location-extensions-Location-alias.json",
                "/fhir/examples/medication-example-f001-combivent.json",
                "/fhir/examples/medication-example-f002-crestor.json",
                "/fhir/examples/medication-example-f003-tolbutamide.json",
                "/fhir/examples/medication-example-f004-metoprolol.json",
                "/fhir/examples/medication-example-f005-enalapril.json",
                "/fhir/examples/medication-example-f201-salmeterol.json",
                "/fhir/examples/medication-example-f202-flucloxacilline.json",
                "/fhir/examples/medication-example-f203-paracetamol.json",
                "/fhir/examples/medicationadministrationexample1.json",
                "/fhir/examples/medicationadministrationexample2.json",
                "/fhir/examples/medicationadministrationexample3.json",
                "/fhir/examples/medicationorder-example-f001-combivent.json",
                "/fhir/examples/medicationorder-example-f002-crestor.json",
                "/fhir/examples/medicationorder-example-f003-tolbutamide.json",
                "/fhir/examples/medicationorder-example-f004-metoprolol.json",
                "/fhir/examples/medicationorder-example-f005-enalapril.json",
                "/fhir/examples/medicationorder-example-f201-salmeterol.json",
                "/fhir/examples/medicationorder-example-f202-flucloxacilline.json",
                "/fhir/examples/medicationorder-example-f203-paracetamol.json",
                "/fhir/examples/observation-example-bloodpressure-cancel.json",
                "/fhir/examples/observation-example-bloodpressure.json",
                "/fhir/examples/observation-example-f001-glucose.json",
                "/fhir/examples/observation-example-f002-excess.json",
                "/fhir/examples/observation-example-f003-co2.json",
                "/fhir/examples/observation-example-f004-erythrocyte.json",
                "/fhir/examples/observation-example-f005-hemoglobin.json",
                "/fhir/examples/observation-example-f202-temperature.json",
                "/fhir/examples/observation-example-f203-bicarbonate.json",
                "/fhir/examples/observation-example-f204-creatinine.json",
                "/fhir/examples/observation-example-f205-egfr.json",
                "/fhir/examples/observation-example-f206-staphylococcus.json",
                "/fhir/examples/observation-example-genetics-1.json",
                "/fhir/examples/observation-example-genetics-2.json",
                "/fhir/examples/observation-example-genetics-3.json",
                "/fhir/examples/observation-example-genetics-4.json",
                "/fhir/examples/observation-example-genetics-5.json",
                "/fhir/examples/observation-example-glasgow-qa.json",
                "/fhir/examples/observation-example-glasgow.json",
                "/fhir/examples/observation-example-sample-data.json",
                "/fhir/examples/observation-example-satO2.json",
                "/fhir/examples/observation-example-unsat.json",
                "/fhir/examples/observation-example.json",
                "/fhir/examples/patient-example-a.json",
                "/fhir/examples/patient-example-animal.json",
                "/fhir/examples/patient-example-b.json",
                "/fhir/examples/patient-example-c.json",
                "/fhir/examples/patient-example-d.json",
                "/fhir/examples/patient-example-dicom.json",
                "/fhir/examples/patient-example-f001-pieter.json",
                "/fhir/examples/patient-example-f201-roel.json",
                "/fhir/examples/patient-example-ihe-pcd.json",
                "/fhir/examples/patient-example-proband.json",
                "/fhir/examples/patient-example-us-extensions.json",
                "/fhir/examples/patient-example-xcda.json",
                "/fhir/examples/patient-example-xds.json",
                "/fhir/examples/patient-example.json",
                "/fhir/examples/patient-examples-cypress-template.json",
                "/fhir/examples/patient-examples-general.json",
                "/fhir/examples/practitioner-example-f001-evdb.json",
                "/fhir/examples/practitioner-example-f002-pv.json",
                "/fhir/examples/practitioner-example-f003-mv.json",
                "/fhir/examples/practitioner-example-f004-rb.json",
                "/fhir/examples/practitioner-example-f005-al.json",
                "/fhir/examples/practitioner-example-f006-rvdb.json",
                "/fhir/examples/practitioner-example-f007-sh.json",
                "/fhir/examples/practitioner-example-f201-ab.json",
                "/fhir/examples/practitioner-example-f202-lm.json",
                "/fhir/examples/practitioner-example-f203-jvg.json",
                "/fhir/examples/practitioner-example-f204-ce.json",
                "/fhir/examples/practitioner-example-xcda-author.json",
                "/fhir/examples/practitioner-example-xcda1.json",
                "/fhir/examples/practitioner-example.json",
                "/fhir/examples/practitioner-examples-general.json",
                "/fhir/examples/questionnaire-example-bluebook.json",
                "/fhir/examples/questionnaire-example-f201-lifelines.json",
                "/fhir/examples/questionnaire-example-gcs.json",
                "/fhir/examples/questionnaire-example.json",
                "/fhir/examples/questionnaireresponse-example-bluebook.json",
                "/fhir/examples/questionnaireresponse-example-f201-lifelines.json",
                "/fhir/examples/questionnaireresponse-example-gcs.json",
                "/fhir/examples/questionnaireresponse-example.json",
                "/fhir/examples/auditevent-example-disclosure.json",
                "/fhir/examples/auditevent-example.json",
                "/fhir/examples/organization-example-f001-burgers.json",
                "/fhir/examples/organization-example-f002-burgers-card.json",
                "/fhir/examples/organization-example-f003-burgers-ENT.json",
                "/fhir/examples/organization-example-f201-aumc.json",
                "/fhir/examples/organization-example-f203-bumc.json",
                "/fhir/examples/organization-example-gastro.json",
                "/fhir/examples/organization-example-good-health-care.json",
                "/fhir/examples/organization-example-insurer.json",
                "/fhir/examples/organization-example-lab.json",
                "/fhir/examples/organization-example.json"
        };

        FHIRMaskingProvider fhirMaskingProvider = new FHIRMaskingProvider(new DefaultMaskingConfiguration(), this.factory);

        for(String filename: filenames) {
            try(InputStream is = FHIRMaskingProviderTest.class.getResourceAsStream(filename)) {
                JsonNode node = JsonUtils.MAPPER.readTree(is);

                String result = fhirMaskingProvider.mask(node);

                assertNotNull(result);
            }
        }
    }
}
