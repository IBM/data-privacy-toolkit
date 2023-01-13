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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PDFFreeTextFormatProcessor implements FreeTextFormatProcessor {
    private static final Logger logger = LogManager.getLogger(PDFFreeTextFormatProcessor.class);

    @Override
    public List<IdentifiedEntity> identifyDocument(InputStream inputStream, NLPAnnotator identifier, DatasetOptions datasetOptions) {
        try {
            PDDocument document = PDDocument.load(inputStream);


            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            return identifier.identify(text, null);
        } catch (IOException e) {
            logger.error("Error identifying entities");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void maskDocument(InputStream inputDocument, OutputStream outputDocument, MaskingProvider maskingProvider) {
        try {
            PDDocument document = PDDocument.load(inputDocument);

            int numOfPages = document.getNumberOfPages();

            PDDocument maskedDocument = new PDDocument();
            PDFont font = loadFont(maskedDocument);

            for (int pageNumber = 0; pageNumber < numOfPages; ++pageNumber) {
                String input = extractPageContent(document, pageNumber + 1);

                String maskedInput = maskingProvider.mask(input);

                PDPage maskedPage = createMaskedPage(maskedDocument, maskedInput, font);

                maskedDocument.addPage(maskedPage);
            }

            maskedDocument.save(outputDocument);
        } catch (IOException e) {
            logger.error("Error identifying entities");
            throw new RuntimeException(e);
        }
    }

    private String normalize(String textToNormalize) {
        return textToNormalize.replace("\n", "")
                .replace("\r", "")
                .replaceAll("\t", " ")
                .trim();
    }

    private PDFont loadFont(PDDocument document) {
        try (InputStream fontStream = this.getClass().getResourceAsStream("/calibri.ttf")) {
            return PDType0Font.load(document, fontStream);
        } catch (IOException e) {
            e.printStackTrace();
            return PDType1Font.TIMES_ROMAN;
        }
    }

    private PDPage createMaskedPage(PDDocument document, String text, PDFont font) {
        PDPage page = new PDPage();
        try {
            PDRectangle mediabox = page.getMediaBox();
            float margin = 72;
            float startX = mediabox.getLowerLeftX() + margin;
            float startY = mediabox.getUpperRightY() - margin;

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();

            int fontSize = 10;
            contentStream.setFont(font, fontSize);
            float leading = 1.5f * fontSize;

            contentStream.newLineAtOffset(startX, startY);
            for (String line : text.split("\n")) {
                contentStream.showText(normalize(line));
                contentStream.newLineAtOffset(0, -leading);
            }

            contentStream.endText();
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }

    private String extractPageContent(PDDocument document, int pageNumber) {
        try {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            pdfTextStripper.setStartPage(pageNumber);
            pdfTextStripper.setEndPage(pageNumber);

            return pdfTextStripper.getText(document);
        } catch (IOException e) {
            logger.error("Unable to extract page {}", pageNumber);
            e.printStackTrace();
        }
        return "";
    }
}
