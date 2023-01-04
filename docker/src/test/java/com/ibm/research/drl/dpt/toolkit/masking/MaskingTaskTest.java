/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.masking;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class MaskingTaskTest {

    @Test
    public void testFromFileShouldDeserializeCorrectly() throws IOException {
        try (InputStream inputStream = MaskingTaskTest.class.getResourceAsStream("/configuration_masking_shift.json")) {
            final TaskToExecute taskToExecute = JsonUtils.MAPPER.readValue(inputStream, TaskToExecute.class);

            assertThat(taskToExecute, instanceOf(MaskingTask.class));
            assertThat(taskToExecute.getExtension(), is("csv"));
            assertThat(taskToExecute.getInputFormat(), is(DataTypeFormat.CSV));

            assertThat(taskToExecute.getInputOptions(), instanceOf(CSVDatasetOptions.class));
            assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).getFieldDelimiter(), is(','));
            assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).getQuoteChar(), is('"'));
            assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).isHasHeader(), is(false));
            assertThat(((CSVDatasetOptions) taskToExecute.getInputOptions()).isTrimFields(), is(false));

            final MaskingTask maskingTask = (MaskingTask) taskToExecute;

            assertThat(maskingTask.getTaskOptions(), instanceOf(MaskingOptions.class));
            assertThat(maskingTask.getTaskOptions().getToBeMasked(), instanceOf(HashMap.class));
            assertThat(maskingTask.getTaskOptions().getToBeMasked().get("Column 1").getProviderType().getName(), is("SHIFT"));
            assertThat(maskingTask.getTaskOptions().getPredefinedRelationships(), is(nullValue()));
            assertThat(maskingTask.getTaskOptions().getMaskingProviders(), is(nullValue()));
        }
    }

    @Test
    public void testExecuteShouldGenerateOutputCorrectly() throws IOException {
        try (InputStream inputStream = MaskingOptionsTest.class.getResourceAsStream("/configuration_masking_shift.json");
             InputStream input = MaskingTaskTest.class.getResourceAsStream("/input_masking_shift.csv");
             OutputStream output = new ByteArrayOutputStream()) {
            final TaskToExecute maskingTask = JsonUtils.MAPPER.readValue(inputStream, TaskToExecute.class);
            maskingTask.processFile(input, output);

            CsvMapper mapper = new CsvMapper();

            MappingIterator<List<String>> iterator = mapper.readerForListOf(String.class).with(CsvParser.Feature.WRAP_AS_ARRAY).readValues(output.toString());

            List<List<String>> records = new ArrayList<>();

            iterator.forEachRemaining(records::add);

            assertThat(records.get(0).get(0), is("M"));
            assertThat(records.get(0).get(1), is("23.000000"));
            assertThat(records.get(1).get(0), is("F"));
            assertThat(records.get(1).get(1), is("24.000000"));
            assertThat(records.get(2).get(0), is("M"));
            assertThat(records.get(2).get(1), is("28.000000"));
        }
    }

    @Test
    @Disabled("DICOM not fully supported in this version")
    public void testWithDICOM() throws Exception {
        try (InputStream inputStream = MaskingTaskTest.class.getResourceAsStream("/config_demo_dicom.json")) {
            final TaskToExecute maskingTask = JsonUtils.MAPPER.readValue(inputStream, TaskToExecute.class);
            try (
                    InputStream input = MaskingTaskTest.class.getResourceAsStream("/test.dcm");
                    OutputStream output = new FileOutputStream("/tmp/output.dcm")
            ) {
                maskingTask.processFile(input, output);
            }
        }
    }
}