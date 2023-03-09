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
package com.ibm.research.drl.dpt.processors;


import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.text.PDFTextStripperByArea;;

import java.awt.Rectangle;
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

            PDFont font = loadFont(document, maskedDocument);

            for (int pageNumber = 0; pageNumber < numOfPages; ++pageNumber) {
                PDPage page = document.getPage(pageNumber);
                String input = extractPageContent(page, pageNumber);

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

    private PDFont loadFont(PDDocument original, PDDocument masked) {
        // TODO: add loading fonts from original document
        return PDType1Font.HELVETICA;
    }

    private PDPage createMaskedPage(PDDocument document, String text, PDFont font) {
        PDPage page = new PDPage();
        try {
            PDRectangle mediaBox = page.getMediaBox();
            float margin = 72;
            float startX = mediaBox.getLowerLeftX() + margin;
            float startY = mediaBox.getUpperRightY() - margin;

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();

            float fontSize = 10.0f;
            contentStream.setFont(
                    font,
                    fontSize
            );
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

    String extractPageContent(PDPage page, int pageNumber) throws IOException {
        logger.trace("Extracting text from page " + page);
        try {
            PDFTextStripperByArea textStripper = new PDFTextStripperByArea();

            PDRectangle boundingBox = page.getBBox();

            Rectangle region = new Rectangle(
                    (int) boundingBox.getLowerLeftX(), (int) boundingBox.getLowerLeftY(), (int) boundingBox.getWidth(), (int) boundingBox.getHeight()
            );

            textStripper.addRegion("all_page", region);
            textStripper.extractRegions(page);

            return textStripper.getTextForRegion("all_page");
        } catch (IOException e) {
            logger.error("Unable to extract page {}", pageNumber);
            throw e;
        }
    }
}
