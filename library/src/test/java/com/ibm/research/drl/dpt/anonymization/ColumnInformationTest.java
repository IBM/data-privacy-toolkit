/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.GenderHierarchy;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ColumnInformationTest {
    public final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void hierarchyIsDeserializedCorrectlyFromName() throws Exception {
        ColumnInformation columnInformation = mapper.readValue("{" +
                        "\"class\":\"CategoricalInformation\"," +
                        "\"hierarchy\": \"GENDER\"," +
                        "\"columnType\": \"" + ColumnType.QUASI.toString() + "\"," +
                        "\"weight\": 1.0," +
                        "\"maximumLevel\": 2," +
                        "\"forLinking\": false" +
                "}",
                ColumnInformation.class);

        assertThat(columnInformation, notNullValue());
        assertThat(columnInformation, instanceOf(CategoricalInformation.class));

        CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;

        assertThat(categoricalInformation.getHierarchy(), instanceOf(GenderHierarchy.class));
    }

    @Test
    public void hierarchyIsDeserializedCorrectlyFromSpecs() throws Exception {
        ColumnInformation columnInformation = mapper.readValue("{" +
                        "\"class\":\"CategoricalInformation\"," +
                        "\"hierarchy\": {\"terms\":[[\"Male\",\"*\"],[\"Female\",\"*\"]]}," +
                        "\"columnType\": \"" + ColumnType.QUASI.toString() + "\"," +
                        "\"weight\": 1.0," +
                        "\"maximumLevel\": 2," +
                        "\"forLinking\": false" +
                        "}",
                ColumnInformation.class);

        assertThat(columnInformation, notNullValue());
        assertThat(columnInformation, instanceOf(CategoricalInformation.class));

        CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;

        assertThat(categoricalInformation.getHierarchy(), instanceOf(MaterializedHierarchy.class));
    }

    @Test
    public void testSerializationDeSerialization() throws JsonProcessingException {
        List<ColumnInformation> columnInformationList = Arrays.asList(

                new DefaultColumnInformation(false),
                new NumericalRange(Arrays.asList(0.1, 0.2), ColumnType.DIRECT_IDENTIFIER),
                new SensitiveColumnInformation(true),

                new CategoricalInformation(
                        GeneralizationHierarchyFactory.getDefaultHierarchy("GENDER"), ColumnType.DIRECT_IDENTIFIER, 1.0, 4, false
                )
        );

        List<String> serializedColumnInformation = new ArrayList<>();

        for (ColumnInformation columnInformation : columnInformationList) {
            String string = mapper.writeValueAsString(columnInformation);

            serializedColumnInformation.add(
                    string
            );
        }

        for (String serializedConstraint : serializedColumnInformation) {
            ColumnInformation columnInformation = mapper.readValue(serializedConstraint, ColumnInformation.class);
            assertNotNull(columnInformation);
        }
    }

    @Test
    @Disabled
    public void testSimple() throws JsonProcessingException {
        System.out.println(
        mapper.writeValueAsString(new CategoricalInformation(
                GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS.getName()), ColumnType.DIRECT_IDENTIFIER, 1.0, 4, false
        ))
        );
    }
}