/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.informationloss;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetricOptions;
import org.apache.spark.api.java.JavaRDD;

import java.util.List;


public interface InformationMetricSpark {

    String getName();
    String getShortName();

    Double getLowerBound();
    Double getUpperBound();

    boolean supportsNumerical();
    boolean supportsCategorical();
    boolean supportsSuppressedDatasets();

    /**
     * Report double.
     *
     * @return the double
     */
    Double report();

    /**
     * Initialize information metric.
     *
     * @param original              the original
     * @param anonymized            the anonymized
     * @param columnInformationList the column information list
     * @param k                     the k
     * @param options               the options
     * @return the information metric
     */
    InformationMetricSpark initialize(JavaRDD<String> original, JavaRDD<String> anonymized, List<ColumnInformation> columnInformationList,
                                      int k, InformationMetricOptions options);
}
