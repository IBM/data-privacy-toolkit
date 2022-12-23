/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;

import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;

public class DatasetUtils {

    public static boolean checkForHeader(DatasetOptions datasetOptions) {
        if (datasetOptions instanceof CSVDatasetOptions) {
            CSVDatasetOptions csvDatasetOptions = (CSVDatasetOptions) datasetOptions;
            return csvDatasetOptions.isHasHeader();
        }

        return false;
    }
}
