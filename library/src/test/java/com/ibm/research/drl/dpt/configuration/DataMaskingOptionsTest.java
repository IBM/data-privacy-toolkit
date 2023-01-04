/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DataMaskingOptionsTest {
    private final static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    @Test
    public void testValidMaskingOptionsYaml() throws IOException {
        try (InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/validMaskingOptions.yaml")){
            DataMaskingOptions dataMaskingOptions = YAML_MAPPER.readValue(in, DataMaskingOptions.class);
        }
    }

    @Test
    public void testValidMaskingOptionsBackwardsCompatibleToBeMasked() throws IOException {
        try (InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/validMaskingOptionsToBeMaskedString.json")) {
            DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);
        }
    }

    @Test
    public void testInvalidMaskingOptionsUnsupportedOutput() throws IOException {
        assertThrows(Exception.class, () -> {
            try (InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/invalidMaskingOptionsUnsupportedOutput.json")) {
                DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);
            }
        });
    }

    @Test
    public void testInvalidMaskingOptionsWrongInput() throws IOException {
        assertThrows(Exception.class, () -> {
            try (InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/invalidMaskingOptionsWrongInput.json")) {
                DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);
            }
        });
    }

    
    @Test
    public void testParsesPredefinedRelationships() throws IOException {
        try (InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/masking_key_rel.json")) {
            DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);

            Map<String, FieldRelationship> relationshipMap = dataMaskingOptions.getPredefinedRelationships();

            assertEquals(1, relationshipMap.size());

            FieldRelationship relationship = relationshipMap.get("date");
            assertEquals("date", relationship.getFieldName());
            assertEquals(RelationshipType.KEY, relationship.getRelationshipType());
            assertEquals(1, relationship.getOperands().length);
            assertEquals("id", relationship.getOperands()[0].getName());
        }
    }
    
    @Test
    public void testValidatesCyclicDependencies() {
        Map<String, FieldRelationship> relationships = new HashMap<>();
        relationships.put("date", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "date", List.of(new RelationshipOperand("userid"))));
        relationships.put("userid", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "userid", List.of(new RelationshipOperand("date"))));

        assertFalse(DataMaskingOptions.validateRelationships(relationships));
    }

    @Test
    public void testValidatesNoCyclicDependencies() {
        Map<String, FieldRelationship> relationships = new HashMap<>();
        relationships.put("date", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "date", List.of(new RelationshipOperand("userid"))));

        assertTrue(DataMaskingOptions.validateRelationships(relationships));
    }

    @Test
    public void testValidatesNoCyclicDependenciesChain() {
        Map<String, FieldRelationship> relationships = new HashMap<>();
        relationships.put("date", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "date", List.of(new RelationshipOperand("userid"))));
        relationships.put("userid", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "userid", List.of(new RelationshipOperand("bankid"))));
        relationships.put("bankid", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "bankid", List.of(new RelationshipOperand("iban"))));

        assertTrue(DataMaskingOptions.validateRelationships(relationships));
    }

    @Test
    public void testValidatesCyclicDependenciesChain() {
        Map<String, FieldRelationship> relationships = new HashMap<>();
        relationships.put("date", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "date", List.of(new RelationshipOperand("userid"))));
        relationships.put("userid", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "userid", List.of(new RelationshipOperand("bankid"))));
        relationships.put("bankid", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "bankid", List.of(new RelationshipOperand("iban"))));
        relationships.put("iban", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "iban", List.of(new RelationshipOperand("date"))));

        assertFalse(DataMaskingOptions.validateRelationships(relationships));
    }

    @Test
    public void testValidatesNoCyclicDependenciesChain2() {
        Map<String, FieldRelationship> relationships = new HashMap<>();
        relationships.put("date", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "date", List.of(new RelationshipOperand("userid"))));
        relationships.put("userid", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "userid", List.of(new RelationshipOperand("bankid"))));
        relationships.put("bankid", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "bankid", List.of(new RelationshipOperand("iban"))));
        relationships.put("iban", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "iban", List.of(new RelationshipOperand("date2"))));

        assertTrue(DataMaskingOptions.validateRelationships(relationships));
    }

    @Test
    public void testValidMaskingOptionsWithMapper() throws IOException {
        try (InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/validMaskingOptions.json")) {
            DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);
        }
    }

    @Test
    public void testValidMaskingOptionsBackwardsCompatibleToBeMaskedWithMapper() throws IOException {
        try (InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/validMaskingOptionsToBeMaskedString.json")) {
            DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);
        }
    }

    @Test
    public void testInvalidMaskingOptionsUnsupportedOutputWithMapper() throws IOException {
        assertThrows(Exception.class, () -> {
            InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/invalidMaskingOptionsUnsupportedOutput.json");
            DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);
        });
    }

    @Test
    public void testInvalidMaskingOptionsWrongInputWithMapper() throws IOException {
        assertThrows(Exception.class, () -> {
            InputStream in = DataMaskingOptionsTest.class.getResourceAsStream("/invalidMaskingOptionsWrongInput.json");
            DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(in, DataMaskingOptions.class);
        });
    }
}
