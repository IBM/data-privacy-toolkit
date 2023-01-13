/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class PlainFreeTextFormatProcessor implements FreeTextFormatProcessor {
    private final static Logger logger = LogManager.getLogger(PlainFreeTextFormatProcessor.class);

    @Override
    public List<IdentifiedEntity> identifyDocument(InputStream inputStream, NLPAnnotator identifier, DatasetOptions datasetOptions) {
        try {
            String input = IOUtils.toString(new InputStreamReader(inputStream));
            return identifier.identify(input, null);
        } catch (IOException e) {
            logger.error("Error identifying entities");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void maskDocument(InputStream inputStream, OutputStream output, MaskingProvider maskingProvider) {
        try {
            String input = IOUtils.toString(new InputStreamReader(inputStream));

            String masked = maskingProvider.mask(input);
            output.write(masked.getBytes());
        } catch (IOException e) {
            logger.error("Error identifying entities");
            throw new RuntimeException(e);
        }
    }
}
