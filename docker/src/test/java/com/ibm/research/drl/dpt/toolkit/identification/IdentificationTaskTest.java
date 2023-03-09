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
package com.ibm.research.drl.dpt.toolkit.identification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class IdentificationTaskTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testFromFileShouldDeserializeCorrectly() throws IOException {
        final TaskToExecute taskToExecute;
        try (InputStream inputStream = IdentificationTaskTest.class.getResourceAsStream("/configuration_identification_with_headers.json")) {
            taskToExecute = mapper.readValue(inputStream, TaskToExecute.class);
        }

        assertThat(taskToExecute.getInputOptions(), instanceOf(CSVDatasetOptions.class));
        assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).getFieldDelimiter(), is(','));
        assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).getQuoteChar(), is('"'));
        assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).isHasHeader(), is(true));
        assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).isTrimFields(), is(true));

        assertThat(taskToExecute, instanceOf(IdentificationTask.class));
        assertThat(taskToExecute.getExtension(), is("csv"));
        assertThat(taskToExecute.getInputFormat(), is(DataTypeFormat.CSV));

        IdentificationTask identificationTask = (IdentificationTask) taskToExecute;

        assertThat(identificationTask.getTaskOptions(), instanceOf(IdentificationOptions.class));
        assertThat(identificationTask.getTaskOptions().getLocalization(), is("en-US"));
        assertThat(identificationTask.getTaskOptions().getFirstN(), is(-1));
    }

    @Test
    public void deserializesJsonDatasetOptions() throws Exception {
        IdentificationTask identificationTask = mapper.readValue(
                "{" +
                        "\"task\":\"Identification\"," +
                        "\"extension\": \"json\"," +
                        "\"inputFormat\": \"JSON\"," +
                        "\"inputOptions\": {},\n" +
                        "\"taskOptions\": {" +
                        "\"localization\": \"en-US\"" +
                        "}\n" +
                        "}",
                IdentificationTask.class
        );

        assertThat(identificationTask, notNullValue());

        DatasetOptions datasetOptions = identificationTask.getInputOptions();

        assertThat(datasetOptions, notNullValue());

        assertThat(datasetOptions, instanceOf(JSONDatasetOptions.class));
    }

    @Test
    public void alternativeConfiguration() throws Exception {
        TaskToExecute task = mapper.readValue("{\n" +
                "  \"task\":\"Identification\",\n" +
                "  \"extension\":\"json\",\n" +
                "  \"inputFormat\":\"JSON\",\n" +
                "  \"inputOptions\":{},\n" +
                "  \"taskOptions\":{\n" +
                "    \"localization\":\"en-US\",\n" +
                "    \"firstN\":-1" +
                "  }\n" +
                "}", TaskToExecute.class);

        assertThat(task, notNullValue());
        assertThat(task, instanceOf(IdentificationTask.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void worksWithCustomDictionaryIdentifiers() throws Exception {
        TaskToExecute task = mapper.readValue("{" +
                "\"task\":\"Identification\"," +
                "  \"inputFormat\":\"CSV\"," +
                "  \"inputOptions\":{" +
                "    \"fieldDelimiter\":\",\"," +
                "    \"quoteChar\":\"\\\"\"," +
                "    \"hasHeader\":true," +
                "    \"trimFields\":true" +
                "  }," +
                "  \"taskOptions\":{" +
                "\"identifiers\":[" +
                //"\"" + EmailIdentifier.class.getName() +"\"," +
                "{\"type\":\"DICTIONARY\",\"providerType\":\"MY_TYPE\",\"terms\":[\"123123123\"]}" +
                "]} }", TaskToExecute.class);

        assertThat(task, notNullValue());
        assertThat(task, instanceOf(IdentificationTask.class));

        try (
                ByteArrayInputStream input = new ByteArrayInputStream((
                        "email,number,name\n" +
                                "foo@gmail.com,123123123,John"
                ).getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            task.processFile(input, output);

            Map<String, Object> report = mapper.readValue(output.toString(), new TypeReference<>() {});

            assertThat(report, notNullValue());
            assertThat(report.size(), is(3));
            Map<String, Map<String, Object>> bestTypes = (Map<String, Map<String, Object>>) report.get("bestTypes");
            assertThat(bestTypes.size(), is(3));

            assertThat(bestTypes.get("email").get("typeName"), is("UNKNOWN"));
            assertThat(bestTypes.get("email").get("count"), is(-1));

            assertThat(bestTypes.get("number").get("typeName"), is("MY_TYPE"));

            assertThat(bestTypes.get("name").get("typeName"), is("UNKNOWN"));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void worksWithCustomRegexIdentifiers() throws Exception {
        TaskToExecute task = mapper.readValue("{" +
                "\"task\":\"Identification\"," +
                "  \"inputFormat\":\"CSV\"," +
                "  \"inputOptions\":{" +
                "    \"fieldDelimiter\":\",\"," +
                "    \"quoteChar\":\"\\\"\"," +
                "    \"hasHeader\":true," +
                "    \"trimFields\":true" +
                "  }," +
                "  \"taskOptions\":{" +
                "\"identifiers\":[" +
                //"\"" + EmailIdentifier.class.getName() +"\"," +
                "{\"type\":\"REGEX\",\"providerType\":\"MY_TYPE\",\"regex\":[\"123123123\"]}" +
                "]} }", TaskToExecute.class);

        assertThat(task, notNullValue());
        assertThat(task, instanceOf(IdentificationTask.class));

        try (
                ByteArrayInputStream input = new ByteArrayInputStream((
                        "email,number,name\n" +
                                "foo@gmail.com,123123123,John"
                ).getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            task.processFile(input, output);

            Map<String, Object> report = mapper.readValue(output.toString(), new TypeReference<>() {
            });

            assertThat(report, notNullValue());
            assertThat(report.size(), is(3));
            Map<String, Map<String, Object>> bestTypes = (Map<String, Map<String, Object>>) report.get("bestTypes");
            assertThat(bestTypes.size(), is(3));

            assertThat(bestTypes.get("email").get("typeName"), is("UNKNOWN"));
            assertThat(bestTypes.get("email").get("count"), is(-1));

            assertThat(bestTypes.get("number").get("typeName"), is("MY_TYPE"));

            assertThat(bestTypes.get("name").get("typeName"), is("UNKNOWN"));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void worksWithUserDefinedIdentifiers() throws Exception {
        TaskToExecute task = mapper.readValue("{" +
                "\"task\":\"Identification\"," +
                        "  \"inputFormat\":\"CSV\"," +
                        "  \"inputOptions\":{" +
                        "    \"fieldDelimiter\":\",\"," +
                        "    \"quoteChar\":\"\\\"\"," +
                        "    \"hasHeader\":true," +
                        "    \"trimFields\":true" +
                        "  }," +
                        "  \"taskOptions\":{" +
                "\"identifiers\":[" +
                "\"" + EmailIdentifier.class.getName() +"\"" +
                        "]} }", TaskToExecute.class);

        assertThat(task, notNullValue());
        assertThat(task, instanceOf(IdentificationTask.class));

        try (
                ByteArrayInputStream input = new ByteArrayInputStream((
                        "email,number,name\n" +
                                "foo@gmail.com,123123123,John"
                ).getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            task.processFile(input, output);

            Map<String, Object> report = mapper.readValue(output.toString(), new TypeReference<>() {});

            assertThat(report, notNullValue());
            assertThat(report.size(), is(3));
            Map<String, Map<String, Object>> bestTypes = (Map<String, Map<String, Object>>) report.get("bestTypes");
            assertThat(bestTypes.size(), is(3));

            assertThat(bestTypes.get("email").get("typeName"), is("EMAIL"));
            assertThat(bestTypes.get("email").get("count"), is(1));

            assertThat(bestTypes.get("number").get("typeName"), is("UNKNOWN"));

            assertThat(bestTypes.get("name").get("typeName"), is("UNKNOWN"));
        }
    }

    @Test
    public void malformedJSON() throws Exception {
        TaskToExecute task = mapper.readValue("{" +
                "\"task\":\"Identification\"," +
                "  \"inputFormat\":\"JSON\"," +
                "  \"inputOptions\":{ " +
                "  }," +
                "  \"taskOptions\":{" +
                "\"identifiers\":[" +
                "\"" + EmailIdentifier.class.getName() +"\"" +
                "]} }", TaskToExecute.class);

        try (
                ByteArrayInputStream input = new ByteArrayInputStream((
                        "{" +
                            "  \"id\": \"1\",\n" +
                            "  \"email\": \"bar@gmail.com\"\n" +
                        "}" +
                        "{" +
                            "\"id\": \"2\",\n" +
                            "  \"email\": \"bar@gmail.com\"\n" +
                        "}" +
                        "{" +
                            "\"id\": \"3\"," +
                            "  \"email\": \"bar@gmail.com\"\n" +
                        "}"
                ).getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            task.processFile(input, output);

            Map<String, Object> report = mapper.readValue(output.toString(), new TypeReference<>() {});

            assertThat(report.get("recordCount"), is(3));
        }
    }
}
