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
