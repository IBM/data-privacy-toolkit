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

import com.ibm.research.drl.dpt.configuration.*;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class XMLFormatProcessorTest {
    
    @Test
    public void testIdentification() throws IOException {
        try (InputStream inputStream = XMLFormatProcessorTest.class.getResourceAsStream("/sample.xml");) {

            IdentificationReport results =
                    new XMLFormatProcessor().identifyTypesStream(inputStream, DataTypeFormat.XML, null, IdentifierFactory.defaultIdentifiers(), -1);

            assertThat(results.getRawResults().size(), greaterThan(0));
        }
    }
    
    @Test
    public void testMasking() throws IOException {
        try (InputStream inputStream = XMLFormatProcessorTest.class.getResourceAsStream("/sample.xml");

             ByteArrayOutputStream output = new ByteArrayOutputStream();
             PrintStream outputStream = new PrintStream(output);
        ) {

            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "/note/email";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.XML, DataTypeFormat.XML,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new XMLFormatProcessor().maskStream(inputStream, outputStream, factory, dataMaskingOptions, new HashSet<>(), null);

            String maskedXML = output.toString();
            assertFalse(maskedXML.contains("lala@gmail.com"));
        }
    }

    @Test
    public void testMaskingArray() throws Exception {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream inputStream = XMLFormatProcessorTest.class.getResourceAsStream("/sample.xml");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(output);) {

            DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();
            defaultMaskingConfiguration.setValue("shift.mask.value", 1);
            defaultMaskingConfiguration.setValue("shift.mask.digitsToKeep", 0);

            ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

            String path = "/note/numbers/value";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.SHIFT, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.XML, DataTypeFormat.XML,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new XMLFormatProcessor().maskStream(inputStream, outputStream, factory, dataMaskingOptions, new HashSet<>(), null);

            String maskedXML = output.toString();
            Document document = documentBuilder.parse(new ByteArrayInputStream(maskedXML.getBytes()));

            assertEquals("2", xPath.compile("/note/numbers/value[1]").evaluate(document, XPathConstants.STRING));
            assertEquals("3", xPath.compile("/note/numbers/value[2]").evaluate(document, XPathConstants.STRING));
            assertEquals("4", xPath.compile("/note/numbers/value[3]").evaluate(document, XPathConstants.STRING));
        }
    }

    @Test
    public void testMaskingArrayChildren() throws Exception {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream inputStream = XMLFormatProcessorTest.class.getResourceAsStream("/books.xml");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(output);) {

            DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();
            defaultMaskingConfiguration.setValue("shift.mask.value", 1);
            defaultMaskingConfiguration.setValue("shift.mask.digitsToKeep", 2);

            ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

            String path = "/bookstore/book/price";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.SHIFT, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.XML, DataTypeFormat.XML,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new XMLFormatProcessor().maskStream(inputStream, outputStream, factory, dataMaskingOptions, new HashSet<>(), null);

            String maskedXML = output.toString();
            Document document = documentBuilder.parse(new ByteArrayInputStream(maskedXML.getBytes()));

            assertEquals("31.00", xPath.compile("/bookstore/book[1]/price").evaluate(document, XPathConstants.STRING));
            assertEquals("30.99", xPath.compile("/bookstore/book[2]/price").evaluate(document, XPathConstants.STRING));
            assertEquals("50.99", xPath.compile("/bookstore/book[3]/price").evaluate(document, XPathConstants.STRING));
            assertEquals("40.95", xPath.compile("/bookstore/book[4]/price").evaluate(document, XPathConstants.STRING));
        }
    }

    @Test
    public void testMaskingArrayChildrenArray() throws Exception {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream inputStream = XMLFormatProcessorTest.class.getResourceAsStream("/books.xml");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(output);) {

            DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();
            defaultMaskingConfiguration.setValue("redact.preserve.length", false);

            ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

            String path = "/bookstore/book/author";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.REDACT, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.XML, DataTypeFormat.XML,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new XMLFormatProcessor().maskStream(inputStream, outputStream, factory, dataMaskingOptions, new HashSet<>(), null);

            String maskedXML = output.toString();
            Document document = documentBuilder.parse(new ByteArrayInputStream(maskedXML.getBytes()));

            assertEquals("*", xPath.compile("/bookstore/book[1]/author").evaluate(document, XPathConstants.STRING));
            assertEquals("*", xPath.compile("/bookstore/book[2]/author").evaluate(document, XPathConstants.STRING));
            assertEquals("*", xPath.compile("/bookstore/book[3]/author[1]").evaluate(document, XPathConstants.STRING));
            assertEquals("*", xPath.compile("/bookstore/book[3]/author[2]").evaluate(document, XPathConstants.STRING));
            assertEquals("*", xPath.compile("/bookstore/book[3]/author[3]").evaluate(document, XPathConstants.STRING));
            assertEquals("*", xPath.compile("/bookstore/book[3]/author[4]").evaluate(document, XPathConstants.STRING));
            assertEquals("*", xPath.compile("/bookstore/book[3]/author[5]").evaluate(document, XPathConstants.STRING));
            assertEquals("*", xPath.compile("/bookstore/book[4]/author").evaluate(document, XPathConstants.STRING));
        }
    }

    @Test
    public void testSuppressShouldDropField() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream inputStream = this.getClass().getResourceAsStream("/sample.xml");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(output);) {

            DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();

            ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

            String path = "/note/numbers/value";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.XML, DataTypeFormat.XML,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new XMLFormatProcessor().maskStream(inputStream, outputStream, factory, dataMaskingOptions, new HashSet<>(), null);

            String maskedXML = output.toString();
            Document document = documentBuilder.parse(new ByteArrayInputStream(maskedXML.getBytes()));

            assertThat(((NodeList) xPath.compile("/note/numbers").evaluate(document, XPathConstants.NODESET)).getLength(), is(1));
            assertThat(((NodeList) xPath.compile("/note/numbers/value").evaluate(document, XPathConstants.NODESET)).getLength(), is(0));
        }
    }

    @Test
    public void testSuppressShouldDropArrayChildren() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream inputStream = this.getClass().getResourceAsStream("/books.xml");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(output);) {

            DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();

            ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

            String path = "/bookstore/book/title";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.XML, DataTypeFormat.XML,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new XMLFormatProcessor().maskStream(inputStream, outputStream, factory, dataMaskingOptions, new HashSet<>(), null);

            String maskedXML = output.toString();
            Document document = documentBuilder.parse(new ByteArrayInputStream(maskedXML.getBytes()));

            assertThat(((NodeList) xPath.compile("/bookstore/book/author").evaluate(document, XPathConstants.NODESET)).getLength(), is(8));
            assertThat(((NodeList) xPath.compile("/bookstore/book/title").evaluate(document, XPathConstants.NODESET)).getLength(), is(0));
        }
    }

    @Test
    public void testSuppressShouldDropArrayChildrenArray() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream inputStream = this.getClass().getResourceAsStream("/books.xml");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(output);) {

            DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();

            ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

            String path = "/bookstore/book/author";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.XML, DataTypeFormat.XML,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new XMLFormatProcessor().maskStream(inputStream, outputStream, factory, dataMaskingOptions, new HashSet<>(), null);

            String maskedXML = output.toString();
            Document document = documentBuilder.parse(new ByteArrayInputStream(maskedXML.getBytes()));

            assertThat(((NodeList) xPath.compile("/bookstore/book/author").evaluate(document, XPathConstants.NODESET)).getLength(), is(0));
            assertThat(((NodeList) xPath.compile("/bookstore/book/title").evaluate(document, XPathConstants.NODESET)).getLength(), is(4));
        }
    }
}
