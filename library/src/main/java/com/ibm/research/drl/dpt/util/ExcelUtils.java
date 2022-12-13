/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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
