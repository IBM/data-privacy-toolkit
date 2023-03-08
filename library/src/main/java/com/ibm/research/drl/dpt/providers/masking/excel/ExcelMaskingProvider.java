/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.excel;


import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExcelMaskingProvider implements MaskingProvider {
    private static final Logger logger = LogManager.getLogger(ExcelMaskingProvider.class);
    private final DataTypeFormat inputFormatType;
    private final Map<String, DataMaskingTarget> toBeMasked;
    private final MaskingProviderFactory maskingProviderFactory;
    private final boolean ignoreNonExistent;

    public ExcelMaskingProvider(MaskingConfiguration maskingConfiguration, DataTypeFormat inputFormatType,
                                Map<String, DataMaskingTarget> toBeMasked, MaskingProviderFactory maskingProviderFactory) {
        this.inputFormatType = inputFormatType;
        this.ignoreNonExistent = maskingConfiguration.getBooleanValue("excel.mask.ignoreNonExistent");
        this.toBeMasked = toBeMasked;
        this.maskingProviderFactory = maskingProviderFactory;
    }

    @Override
    public String mask(String identifier) {
        return null;
    }

    private Workbook copyXSSFWorkbook(Workbook source) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        source.write(outputStream);

        byte[] bytes = outputStream.toByteArray();
        return new XSSFWorkbook(new ByteArrayInputStream(bytes));
    }

    private Workbook copyHSSFWorkbook(Workbook source) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        source.write(outputStream);

        byte[] bytes = outputStream.toByteArray();
        return new HSSFWorkbook(new ByteArrayInputStream(bytes));
    }

    @Override
    public byte[] mask(byte[] data) {
        try {
            InputStream input = new ByteArrayInputStream(data);

            Workbook wb;
            Workbook maskedWb;

            if (inputFormatType == DataTypeFormat.XLSX) {
                wb = new XSSFWorkbook(input);
                maskedWb = copyXSSFWorkbook(wb);
            } else {
                wb = new HSSFWorkbook(input);
                maskedWb = copyHSSFWorkbook(wb);
            }

            for (Map.Entry<String, DataMaskingTarget> entry : toBeMasked.entrySet()) {
                String path = entry.getKey();
                DataMaskingTarget dataMaskingTarget = entry.getValue();

                ProviderType providerType = dataMaskingTarget.getProviderType();
                String targetPath = dataMaskingTarget.getTargetPath();

                MaskingProvider maskingProvider = this.maskingProviderFactory.get(path, providerType);

                String[] parts = path.trim().split("/");

                if (parts.length != 3) {
                    throw new RuntimeException("wrongly formatted path: " + path.trim());
                }

                String workbookName = parts[1];
                String cellReference = parts[2];

                Sheet sheet = wb.getSheet(workbookName);

                if (sheet == null && this.ignoreNonExistent) {
                    continue;
                }

                boolean isRange = cellReference.contains(":");

                List<Cell> cellsToMask = getCells(sheet, cellReference);

                DataFormatter formatter = new DataFormatter();

                for (Cell toMask : cellsToMask) {
                    String value = formatter.formatCellValue(toMask);
                    String maskedValue = maskingProvider.mask(value);

                    String[] targetParts = targetPath.split("/");
                    String targetWb = targetParts[1];
                    String targetCellReferenceS = targetParts[2];

                    int targetRow;
                    int targetColumn;

                    if (isRange) {
                        targetRow = toMask.getRowIndex();
                        targetColumn = toMask.getColumnIndex();
                    } else {
                        CellReference targetCellReference = new CellReference(targetCellReferenceS);
                        targetRow = targetCellReference.getRow();
                        targetColumn = targetCellReference.getCol();
                    }

                    Cell targetCell = getOrCreateCell(maskedWb, targetWb, targetRow, targetColumn);
                    targetCell.setCellValue(maskedValue);
                }
            }

            wb.close();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            maskedWb.write(outputStream);

            maskedWb.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Cell> getCellRange(Sheet sheet, String cellReference) {
        List<Cell> cells = new ArrayList<>();

        String[] references = cellReference.split(":");

        CellAddress firstAddress = new CellAddress(new CellReference(references[0]));
        CellAddress lastAddress = new CellAddress(new CellReference(references[1]));

        for (int i = firstAddress.getRow(); i <= lastAddress.getRow(); i++) {
            for (int j = firstAddress.getColumn(); j <= lastAddress.getColumn(); j++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Cell cell = row.getCell(j);

                if (cell == null) {
                    continue;
                }

                cells.add(cell);
            }
        }

        return cells;
    }

    private List<Cell> getCells(Sheet sheet, String cellReferenceSpecification) {

        if (cellReferenceSpecification.contains(":")) {
            return getCellRange(sheet, cellReferenceSpecification);
        }

        CellReference cellReference = new CellReference(cellReferenceSpecification);

        Row cellRow = sheet.getRow(cellReference.getRow());

        if (cellRow != null) {
            Cell cell = cellRow.getCell(cellReference.getCol());

            if (cell != null) {
                return Collections.singletonList(cell);
            } else {
                logger.trace("Cell is null: {}", cellReferenceSpecification);
            }
        } else {
            logger.trace("Row is null: {}", cellReferenceSpecification);
        }

        if (this.ignoreNonExistent) {
            return Collections.emptyList();
        }

        throw new IllegalArgumentException("Invalid/non existing cell reference: " + cellReferenceSpecification);
    }


    private Cell getOrCreateCell(Workbook maskedWb, String targetWb, int rowIndex, int columnIndex) {

        if (maskedWb.getSheet(targetWb) == null) {
            maskedWb.createSheet(targetWb);
        }

        Row targetRow = maskedWb.getSheet(targetWb).getRow(rowIndex);
        if (targetRow == null) {
            targetRow = maskedWb.getSheet(targetWb).createRow(rowIndex);
        }

        Cell targetCell = targetRow.getCell(columnIndex);
        if (targetCell == null) {
            targetCell = targetRow.createCell(columnIndex);
        }

        return targetCell;
    }

}
