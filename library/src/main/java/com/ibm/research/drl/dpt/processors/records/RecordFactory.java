package com.ibm.research.drl.dpt.processors.records;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import java.io.IOException;
import java.util.Map;

/** Factory that parses input strings into {@link Record} instances based on the data format. */
public class RecordFactory {

    /** Not instantiable. */
    private RecordFactory() {}

    /**
     * Parses a string into a {@link Record}.
     *
     * @param input         the raw input string
     * @param inputFormat   the data format of the string
     * @param datasetOptions dataset-level options
     * @param fieldNames    mapping of field names to column indices
     * @param isHeader      whether the string represents a header row
     * @return the parsed record
     * @throws IOException if parsing fails
     */
    public static Record parseString(String input, DataTypeFormat inputFormat, DatasetOptions datasetOptions, Map<String, Integer> fieldNames, boolean isHeader) throws IOException {
        switch (inputFormat) {
            case CSV:
                return CSVRecord.fromString(input, datasetOptions, fieldNames, isHeader);
            case JSON:
                return JSONRecord.fromString(input);
            default:
                throw new IllegalArgumentException("Unsupported format " + inputFormat);
        }
    }
}
