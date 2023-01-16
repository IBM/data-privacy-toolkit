/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.nlp.ComplexFreeTextAnnotator;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.FreeTextMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class PDFFreeTextFormatProcessorTest {
    private final ObjectMapper mapper = new ObjectMapper();

    
    @Test
    public void testIdentifyStream() throws IOException {
        try (
                InputStream configuration = PDFFreeTextFormatProcessorTest.class.getResourceAsStream("/complexWithIdentifiers.json");
                InputStream inputStream = PDFFreeTextFormatProcessorTest.class.getResourceAsStream("/testPDF.pdf");
                ) {
            PDFFreeTextFormatProcessor processor = new PDFFreeTextFormatProcessor();

            List<IdentifiedEntity> identifiedEntityList = processor.identifyDocument(inputStream, new ComplexFreeTextAnnotator(mapper.readTree(configuration)), null);

            assertNotNull(identifiedEntityList);
            assertFalse(identifiedEntityList.isEmpty());
            assertEquals(2, identifiedEntityList.size());

            Set<String> stringRepresentation = identifiedEntityList.stream().map(IdentifiedEntity::toString).collect(Collectors.toSet());

            assertThat("IdentifiedEntity{text='John', start=9, end=13, type=NAME, sources=PRIMA, pos=[PartOfSpeechType.UNKNOWN]}", is(in(stringRepresentation)));
            assertThat("IdentifiedEntity{text='Ireland', start=28, end=35, type=ADDRESS, sources=OpenNLP, pos=[PartOfSpeechType.UNKNOWN]}", is(in(stringRepresentation)));
        }
    }

    @Test
    public void testAnnotateStream() throws IOException {
        PDFFreeTextFormatProcessor processor = new PDFFreeTextFormatProcessor();

        Path outputFile = Files.createTempFile("temp", "pdf");
        try (
            InputStream configuration = PDFFreeTextFormatProcessorTest.class.getResourceAsStream("/complexWithIdentifiers.json");
            InputStream inputStream = PDFFreeTextFormatProcessorTest.class.getResourceAsStream("/testPDF.pdf");
            FileOutputStream fos = new FileOutputStream(outputFile.toString());
            PrintStream outputStream = new PrintStream(fos);
        ) {
            HashMap<String, DataMaskingTarget> targets = new HashMap<>() {{
                put("NAME", new DataMaskingTarget(ProviderType.REDACT, "NAME"));
            }};
            MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(),
                    targets
            );

            processor.maskDocument(
                    inputStream, outputStream,
                    new FreeTextMaskingProvider(factory, new ComplexFreeTextAnnotator(mapper.readTree(configuration)), targets)
            );
        }
    }

    @Test
    public void testNewPageBasedTextExtraction() throws IOException {
        PDFFreeTextFormatProcessor processor = new PDFFreeTextFormatProcessor();

        try (
                InputStream documentStream = PDFFreeTextFormatProcessorTest.class.getResourceAsStream("/multi-page.pdf");
                PDDocument document = PDDocument.load(documentStream);
                ) {
            PDPage firstPage = document.getPage(0);

            assertEquals("FIRST PAGE \n" +
                    " \n" +
                    "SIMPLE TEXT \n" +
                    " \n" +
                    "With multiple case type", processor.extractPageContent(firstPage, 0).strip());

            PDPage secondPage = document.getPage(1);

            assertEquals("PAGE 2 \n" +
                    " \n" +
                    "MORE SIMPLE TEXT", processor.extractPageContent(secondPage, 1).strip());
        }
    }
}
