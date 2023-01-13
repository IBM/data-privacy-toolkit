/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.Language;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class DOCXFreeTextFormatProcessor implements FreeTextFormatProcessor {
    private static final Logger logger = LogManager.getLogger(DOCXFreeTextFormatProcessor.class);

    @Override
    public List<IdentifiedEntity> identifyDocument(InputStream inputStream, NLPAnnotator identifier, DatasetOptions datasetOptions) throws IOException {
        XWPFDocument document = new XWPFDocument(inputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);

        return identifier.identify(extractor.getText(), Language.ENGLISH);
    }

    private void processRun(XWPFRun run, MaskingProvider maskingProvider) {
        String text = run.text();

        if (null != text) {
            run.setText(
                    maskingProvider.mask(text)
            );
        }
    }

    @Override
    public void maskDocument(InputStream inputStream, OutputStream output, MaskingProvider maskingProvider) {
        try {
            XWPFDocument document = new XWPFDocument(inputStream);

            document.getParagraphs().forEach(
                    paragraph -> {
                        processRuns(paragraph.getRuns(), maskingProvider);
                    }
            );

            document.getTables().forEach(
                    table -> table.getRows().forEach(
                            row -> row.getTableCells().forEach(
                                    cell -> cell.getParagraphs().forEach(
                                            paragraph -> {
                                                processRuns(paragraph.getRuns(), maskingProvider);
                                            }
                                    )
                            )
                    )
            );

            document.write(output);
        } catch (IOException e) {
            logger.error("Error identifying entities");
            throw new RuntimeException(e);
        }
    }

    private void processRuns(List<XWPFRun> runs, MaskingProvider maskingProvider) {
        if (runs != null) {
            for (XWPFRun run : runs) {
                processRun(run, maskingProvider);
            }
        }
    }
}