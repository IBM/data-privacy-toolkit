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
package com.ibm.research.drl.dpt.util;


import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelUtils {

    public static String getValue(byte[] inputBytes, DataTypeFormat inputFormatType, String path) throws IOException {
        try (Workbook wb = readWorkBook(inputFormatType, inputBytes)) {
            String[] parts = path.split("/");
            String workbookName = parts[1];
            String cellReference = parts[2];

            Sheet sheet = wb.getSheet(workbookName);
            if (sheet == null) {
                return null;
            }

            CellReference cellReference1 = new CellReference(cellReference);

            Row row = sheet.getRow(cellReference1.getRow());
            if (row == null) {
                return null;
            }

            Cell toRetrieve = row.getCell(cellReference1.getCol());

            return new DataFormatter().formatCellValue(toRetrieve);
        }
    }

    private static Workbook readWorkBook(DataTypeFormat inputFormatType, byte[] inputBytes) throws IOException {
        InputStream input = new ByteArrayInputStream(inputBytes);

        if (inputFormatType == DataTypeFormat.XLSX) {
            return new XSSFWorkbook(input);
        } else {
            return new HSSFWorkbook(input);
        }
    }
}
