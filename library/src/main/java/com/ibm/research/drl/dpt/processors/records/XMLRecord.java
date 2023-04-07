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
package com.ibm.research.drl.dpt.processors.records;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.*;

public class XMLRecord extends MultipathRecord {
    private final Document document;

    private final static String transformation =
            "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" +
                    "<xsl:output method=\"text\" indent=\"no\" />" +
                    "<xsl:template match=\"*[not(*)]\">" +
                    "<xsl:for-each select=\"ancestor-or-self::*\">" +
                    "<xsl:value-of select=\"concat('/', name())\" />" +
                    "<xsl:if test=\"count(preceding-sibling::*[name() = name(current())]) != 0\">" +
                    "<xsl:value-of select=\"concat('[', count(preceding-sibling::*[name() = name(current())]) + 1, ']')\" />" +
                    "</xsl:if>" +
                    "</xsl:for-each>" +
                    "<xsl:text>&#xA;</xsl:text>" +
                    "<xsl:apply-templates select=\"*\" />" +
                    "</xsl:template>" +
                    "<xsl:template match=\"*\">" +
                    "<xsl:apply-templates select=\"*\" />" +
                    "</xsl:template>" +
                    "</xsl:stylesheet>";
    private final static XPath xPath = XPathFactory.newInstance().newXPath();

    public XMLRecord(Document document) {
        this.document = document;
    }

    @Override
    public byte[] getFieldValue(String path) {
        try {
            String fieldValue = (String) xPath.compile(path).evaluate(document, XPathConstants.STRING);
            return fieldValue.getBytes();
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Unable to extract xpath from record", e);
        }
    }

    @Override
    public void setFieldValue(String path, byte[] value) {
        try {
            Node field = (Node) xPath.compile(path).evaluate(document, XPathConstants.NODE);
            field.setTextContent(value == null ? "" : new String(value));
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Unable to extract xpath from record", e);
        } catch (DOMException e) {
            throw new RuntimeException("Unable to set value correctly", e);
        }
    }

    @Override
    public Iterable<String> getFieldReferences() {
        try (
                StringWriter stringWriter = new StringWriter();
                Writer writer = new BufferedWriter(stringWriter);
                Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(transformation.getBytes())))
        ) {
            DOMSource source = new DOMSource(document);
            StreamSource styleSource = new StreamSource(reader);
            StreamResult result = new StreamResult(writer);

            Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);

            transformer.transform(source, result);

            String paths = stringWriter.toString();

            return Arrays.asList(paths.split("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to list the xPaths", e);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Unable to instantiate the transformer", e);
        } catch (TransformerException e) {
            throw new RuntimeException("Unable to transform the document", e);
        }
    }

    @Override
    protected String formatRecord() {
        try (
                StringWriter stringWriter = new StringWriter();
                Writer writer = new BufferedWriter(stringWriter)
        ) {
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(writer);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);

            return stringWriter.toString();
        } catch (IOException | TransformerException e) {
            throw new RuntimeException("Unable to format the record ", e);
        }
    }

    @Override
    public void suppressField(String path) {

        try {
            Node field = (Node) xPath.compile(path).evaluate(document, XPathConstants.NODE);
            Node parentNode = field.getParentNode();
            parentNode.removeChild(field);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Unable to extract xpath from record", e);
        }

    }

    @Override
    public Iterable<String> generatePaths(String pattern) {
        List<String> pointers = Arrays.asList(pattern.split("/"));

        return generatePaths(document, pointers.subList(1, pointers.size()), "");
    }

    private Map<String, Integer> getNodeFrequencies(NodeList nl) {
        HashMap<String, Integer> nodesFrequencies = new HashMap<>();
        for (int i = 0; i < nl.getLength(); i++) {
            nodesFrequencies.merge(nl.item(i).getNodeName(), 1, Integer::sum);
        }
        return nodesFrequencies;
    }

    private Boolean isArray(String node, Map<String, Integer> nodesFrequencies) {
        return nodesFrequencies.getOrDefault(node, 1) > 1;
    }

    private List<String> generatePaths(Document document, List<String> parts, String head) {
        if (parts.isEmpty()) {
            try {
                NodeList nl = (NodeList) xPath.compile(head).evaluate(document, XPathConstants.NODESET);
                if (nl.getLength() == 1) {
                    return Collections.singletonList(head);
                } else {
                    List<String> paths = new ArrayList<>();
                    for (int i = 1; i <= nl.getLength(); i++) {
                        paths.add(head + "[" + i + "]");
                    }
                    return paths;
                }
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        String part = parts.get(0);

        List<String> paths = new ArrayList<>();

        try {
            NodeList nl = (NodeList) xPath.compile(head + "/" + part).evaluate(document, XPathConstants.NODESET);

            Map<String, Integer> nodesFrequencies = getNodeFrequencies(nl);

            HashMap<String, Integer> arraysCounters = new HashMap<>();
            for (int i = 0; i < nl.getLength(); i++) {
                String node = nl.item(i).getNodeName();
                if (!isArray(node, nodesFrequencies)) {
                    paths.addAll(
                            generatePaths(
                                    document,
                                    parts.subList(1, parts.size()),
                                    head + "/" + node
                            )
                    );
                } else {
                    arraysCounters.merge(node, 1, (a, b) -> a + b);
                    paths.addAll(
                            generatePaths(
                                    document,
                                    parts.subList(1, parts.size()),
                                    head + "/" + nl.item(i).getNodeName() + "[" + arraysCounters.get(node) + "]"
                            )
                    );
                }
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e.getMessage());
        }

        return paths;
    }

    @Override
    public String getBasepath(String path) {
        int idx = path.lastIndexOf('/');
        return path.substring(0, idx + 1);
    }

    @Override
    public boolean isPrimitiveType(String fieldIdentifier) {
        try {
            Node node = (Node) xPath.compile(fieldIdentifier).evaluate(document, XPathConstants.NODE);

            NodeList childNodes = node.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); ++i) {
                if (childNodes.item(i).getNodeType() != Node.TEXT_NODE) {
                    return false;
                }
            }
            return true;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Object getFieldObject(String fieldIdentifier) {
        throw new RuntimeException("Not implemented yet"); // TODO: -TO COMPLETE
    }

    @Override
    public boolean isAbsolute(String fieldName) {
        return fieldName.startsWith("/");
    }

    @Override
    public boolean isSingleElement(String fieldName) {
        try {
            NodeList nl = (NodeList) xPath.compile(fieldName).evaluate(document, XPathConstants.NODESET);
            return (nl.getLength() <= 1);
        } catch (XPathExpressionException e) {
            return true;
        }
    }
}
