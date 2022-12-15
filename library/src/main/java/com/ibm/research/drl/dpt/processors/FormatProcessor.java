/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.IPVAlgorithm;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FormatProcessor extends Serializable {
    boolean supportsStreams();

    void maskStream(InputStream dataset, OutputStream output,
                    MaskingProviderFactory factory,
                    DataMaskingOptions dataMaskingOptions, Set<String> alreadyMaskedFields,
                    Map<ProviderType, Class<? extends MaskingProvider>> registerTypes) throws IOException;

    //one level is the raw results, next is the best types
    IdentificationReport identifyTypesStream(InputStream input,
                                             DataTypeFormat inputFormatType, DatasetOptions datasetOptions,
                                             Collection<Identifier> identifiers, int firstN) throws IOException;

    default Map<IPVVulnerability, List<Integer>> identifyVulnerabilitiesStream(InputStream input, IPVAlgorithm algorithm,
                                                                               DataTypeFormat inputFormatType, DatasetOptions datasetOptions,
                                                                               boolean isFullReport, int kValue) throws IOException {
        throw new UnsupportedOperationException("Identification of vulnerabilities is limited");
    }
}
