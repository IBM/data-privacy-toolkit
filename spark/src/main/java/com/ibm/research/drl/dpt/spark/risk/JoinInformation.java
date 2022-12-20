/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;

public class JoinInformation {
    private final String rightTable;
    private final String leftColumn;
    private final String rightColumn;
    private final DataTypeFormat rightTableInputFormat;
    private final DatasetOptions rightTableDatasetOptions;
    
    public String getRightTable() {
        return rightTable;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public String getRightColumn() {
        return rightColumn;
    }

    public DataTypeFormat getRightTableInputFormat() {
        return rightTableInputFormat;
    }

    public DatasetOptions getRightTableDatasetOptions() {
        return rightTableDatasetOptions;
    }

    public JoinInformation(String rightTable, String leftColumn, String rightColumn,
                           DataTypeFormat rightTableInputFormat, DatasetOptions rightTableDatasetOptions) {
        
        this.rightTable = rightTable;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
        this.rightTableInputFormat = rightTableInputFormat;
        this.rightTableDatasetOptions = rightTableDatasetOptions;
    }
}
