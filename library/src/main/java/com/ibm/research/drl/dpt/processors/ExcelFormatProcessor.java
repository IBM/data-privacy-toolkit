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
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.excel.ExcelMaskingProvider;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import com.ibm.research.drl.dpt.util.IdentifierUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class ExcelFormatProcessor extends FormatProcessor {

    @Override
    public boolean supportsStreams() {
        return true;
    }

    @Override
    public void maskStream(InputStream dataset, OutputStream output, MaskingProviderFactory factory,
                           DataMaskingOptions dataMaskingOptions, Set<String> alreadyMaskedFields,
                           Map<ProviderType, Class<? extends MaskingProvider>> registerTypes) throws IOException {

        if (registerTypes != null) {
            for (final Map.Entry<ProviderType, Class<? extends MaskingProvider>> typeProviderPair : registerTypes.entrySet()) {
                factory.registerMaskingProviderClass(typeProviderPair.getValue(), typeProviderPair.getKey());
            }
        }

        final ExcelMaskingProvider maskingProvider = new ExcelMaskingProvider(
                factory.getConfigurationForField(null), dataMaskingOptions.getInputFormat(),
                dataMaskingOptions.getToBeMasked(), factory);

        byte[] inputBytes = IOUtils.toByteArray(dataset);
        byte[] outputBytes = maskingProvider.mask(inputBytes);
        output.write(outputBytes);
    }

    @Override
    protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions dataOptions, int firstN) {
        return null;
    }

    private static void inspectSheet(Sheet sheet, Map<String, List<IdentifiedType>> allTypes, Collection<Identifier> identifiers) {

        DataFormatter formatter = new DataFormatter();
        String sheetName = sheet.getSheetName();

        for (Row row : sheet) {
            for (Cell cell : row) {
                String value = formatter.formatCellValue(cell);

                if (value == null || value.isEmpty()) {
                    continue;
                }

                CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
                String cellName = "/" + sheetName + "/" + cellRef.formatAsString();

                for (Identifier identifier : identifiers) {
                    if (identifier.isOfThisType(value)) {
                        String type = identifier.getType().getName();

                        Collection<IdentifiedType> columnTypes = allTypes.get(cellName);
                        if (columnTypes == null) {
                            allTypes.put(cellName, new ArrayList<>());
                            columnTypes = allTypes.get(cellName);
                        }

                        columnTypes.add(new IdentifiedType(type, 1L));

                    }
                }
            }
        }
    }

    @Override
    public IdentificationReport identifyTypesStream(InputStream input, DataTypeFormat inputFormatType,
                                                    DatasetOptions datasetOptions, Collection<Identifier> identifiers,
                                                    int firstN) throws IOException {

        final Map<String, List<IdentifiedType>> allTypes = new HashMap<>();

        Workbook wb;

        if (inputFormatType == DataTypeFormat.XLSX) {
            wb = new XSSFWorkbook(input);
        } else {
            wb = new HSSFWorkbook(input);
        }

        int numberOfSheets = wb.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = wb.getSheetAt(i);
            inspectSheet(sheet, allTypes, identifiers);
        }

        wb.close();


        Map<String, IdentifiedType> bestTypes = IdentifierUtils.getIdentifiedType(allTypes, 1L, IdentificationConfiguration.DEFAULT);

        return new IdentificationReport(
                allTypes,
                bestTypes,
                1L
        );
    }
}
