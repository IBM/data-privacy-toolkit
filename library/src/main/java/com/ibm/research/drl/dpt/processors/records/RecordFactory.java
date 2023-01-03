package com.ibm.research.drl.dpt.processors.records;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import java.io.IOException;
import java.util.Map;

public class RecordFactory {
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
