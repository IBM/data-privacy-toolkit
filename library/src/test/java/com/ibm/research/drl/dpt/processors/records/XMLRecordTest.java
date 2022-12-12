/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors.records;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

public class XMLRecordTest {
    @Test
    public void xmlDocumentsAreListedCorrectly() throws Exception {
        String testXML = "<document><name>Name</name><surname>Surname</surname></document>";

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(testXML.getBytes()));

        Record record = new XMLRecord(document);

        Iterable<String> xpathsIterable = record.getFieldReferences();

        assertNotNull(xpathsIterable);

        List<String> xpaths = new ArrayList<>();
        xpathsIterable.iterator().forEachRemaining(xpaths::add);

        assertFalse(xpaths.isEmpty());
        assertThat(xpaths.size(), is(2));
        assertTrue(xpaths.contains("/document/name"));
        assertTrue(xpaths.contains("/document/surname"));
    }

    @Test
    public void fieldValuesAreExtractedCorrectly() throws Exception {
        String name = "Silly Name";
        String surname = "Silly Surname";
        String testXML = "<document><name>" + name + "</name><surname>" + surname + "</surname></document>";

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(testXML.getBytes()));

        Record record = new XMLRecord(document);

        byte[] nameBytes = record.getFieldValue("/document/name");
        assertThat(new String(nameBytes), is(name));

        byte[] surnameBytes = record.getFieldValue("/document/surname");
        assertThat(new String(surnameBytes), is(surname));
    }

    @Test
    public void fieldValuesAreReplacedCorrectly() throws Exception {
        String name = "Silly Name";
        String surname = "Silly Surname";
        String testXML = "<document><name>" + name + "</name><surname>" + surname + "</surname></document>";

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(testXML.getBytes()));

        Record record = new XMLRecord(document);

        assertThat(new String(record.getFieldValue("/document/name")), is(name));

        String fakeName = "FOO_BAR";

        record.setFieldValue("/document/name", fakeName.getBytes());

        assertThat(new String(record.getFieldValue("/document/name")), is(not(name)));
        assertThat(new String(record.getFieldValue("/document/name")), is(fakeName));


        assertThat(new String(record.getFieldValue("/document/surname")), is(surname));

        String fakeSurname = "BAR_FOO";

        record.setFieldValue("/document/surname", fakeSurname.getBytes());
        assertThat(new String(record.getFieldValue("/document/surname")), is(not(surname)));
        assertThat(new String(record.getFieldValue("/document/surname")), is(fakeSurname));
    }

    @Test
    public void serializationIsPerformedCorrectly() throws Exception {
        String name = "Silly Name";
        String surname = "Silly Surname";
        String testXML = "<document><name>" + name + "</name><surname>" + surname + "</surname></document>";

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(testXML.getBytes()));

        Record record = new XMLRecord(document);

        String serializedValue = record.toString();

        assertNotNull(serializedValue);
        assertTrue(serializedValue.contains(testXML));
    }

    @Test
    public void testGeneratesPathsAsterisk() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("/sample.xml"));
        XMLRecord record = new XMLRecord(document);

        String path = "/note/*";

        Iterable<String> pathIterator = record.generatePaths(path);

        List<String> paths = new ArrayList<>();
        CollectionUtils.addAll(paths, pathIterator.iterator());

        assertEquals(6, paths.size());
        assertEquals("/note/to", paths.get(0));
        assertEquals("/note/from", paths.get(1));
        assertEquals("/note/email", paths.get(2));
        assertEquals("/note/heading", paths.get(3));
        assertEquals("/note/numbers", paths.get(4));
        assertEquals("/note/body", paths.get(5));
    }

    @Test
    public void testGeneratesPathsArray() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("/sample.xml"));
        XMLRecord record = new XMLRecord(document);

        String path = "/note/numbers/value";

        Iterable<String> pathIterator = record.generatePaths(path);

        List<String> paths = new ArrayList<>();
        CollectionUtils.addAll(paths, pathIterator.iterator());

        assertEquals(3, paths.size());
        assertEquals("/note/numbers/value[1]", paths.get(0));
        assertEquals("/note/numbers/value[2]", paths.get(1));
        assertEquals("/note/numbers/value[3]", paths.get(2));
    }

    @Test
    public void testGeneratesPathsNoArray() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("/sample.xml"));
        XMLRecord record = new XMLRecord(document);

        String path = "/note/email";

        Iterable<String> pathIterator = record.generatePaths(path);

        List<String> paths = new ArrayList<>();
        CollectionUtils.addAll(paths, pathIterator.iterator());

        assertEquals(1, paths.size());
        assertEquals("/note/email", paths.get(0));
    }

    @Test
    public void testGeneratesPathsArrayWithChildren() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("/books.xml"));
        XMLRecord record = new XMLRecord(document);

        String path = "/bookstore/book/title";

        Iterable<String> pathIterator = record.generatePaths(path);

        List<String> paths = new ArrayList<>();
        CollectionUtils.addAll(paths, pathIterator.iterator());

        assertThat(paths.size(), is(4));
        assertThat(paths.get(0), is("/bookstore/book[1]/title"));
        assertThat(paths.get(1), is("/bookstore/book[2]/title"));
        assertThat(paths.get(2), is("/bookstore/book[3]/title"));
        assertThat(paths.get(3), is("/bookstore/book[4]/title"));

        path = "/bookstore/book/*";

        pathIterator = record.generatePaths(path);

        paths = new ArrayList<>();
        CollectionUtils.addAll(paths, pathIterator.iterator());

        assertThat(paths.size(), is(20));
        assertThat(paths.get(0), is("/bookstore/book[1]/title"));
        assertThat(paths.get(1), is("/bookstore/book[1]/author"));
        assertThat(paths.get(2), is("/bookstore/book[1]/year"));
        assertThat(paths.get(3), is("/bookstore/book[1]/price"));
        assertThat(paths.get(4), is("/bookstore/book[2]/title"));
        assertThat(paths.get(5), is("/bookstore/book[2]/author"));
        assertThat(paths.get(6), is("/bookstore/book[2]/year"));
        assertThat(paths.get(7), is("/bookstore/book[2]/price"));
        assertThat(paths.get(8), is("/bookstore/book[3]/title"));
        assertThat(paths.get(9), is("/bookstore/book[3]/author[1]"));
        assertThat(paths.get(10), is("/bookstore/book[3]/author[2]"));
        assertThat(paths.get(11), is("/bookstore/book[3]/author[3]"));
        assertThat(paths.get(12), is("/bookstore/book[3]/author[4]"));
        assertThat(paths.get(13), is("/bookstore/book[3]/author[5]"));
        assertThat(paths.get(14), is("/bookstore/book[3]/year"));
        assertThat(paths.get(15), is("/bookstore/book[3]/price"));
        assertThat(paths.get(16), is("/bookstore/book[4]/title"));
        assertThat(paths.get(17), is("/bookstore/book[4]/author"));
        assertThat(paths.get(18), is("/bookstore/book[4]/year"));
        assertThat(paths.get(19), is("/bookstore/book[4]/price"));
    }

    @Test
    public void testGeneratesPathsDoesNotExist() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("/sample.xml"));
        XMLRecord record = new XMLRecord(document);

        String path = "/note/email2";

        Iterable<String> pathIterator = record.generatePaths(path);

        List<String> paths = new ArrayList<>();
        CollectionUtils.addAll(paths, pathIterator.iterator());

        assertEquals(0, paths.size());
    }

    @Test
    void testSetNullShouldWork() throws ParserConfigurationException, IOException, SAXException {

        String name = "Silly Name";
        String surname = "Silly Surname";
        String testXML = "<document><name>" + name + "</name><surname>" + surname + "</surname></document>";

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(testXML.getBytes()));

        Record record = new XMLRecord(document);

        assertThat(new String(record.getFieldValue("/document/name")), is(name));

        record.setFieldValue("/document/name", null);

        assertThat(new String(record.getFieldValue("/document/name")), is(""));

        record.setFieldValue("/document", null);

        assertThat(new String(record.getFieldValue("/document")), is(""));

    }

    @Test
    void fieldValuesAreRemovedCorrectly() throws ParserConfigurationException, IOException, SAXException {
        String name = "Silly Name";
        String surname = "Silly Surname";
        String testXML = "<document><name>" + name + "</name><surname>" + surname + "</surname></document>";

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(testXML.getBytes()));

        Record record = new XMLRecord(document);

        assertThat(new String(record.getFieldValue("/document/name")), is(name));

        record.suppressField("/document/name");

        assertThat(new String(record.getFieldValue("/document/name")), is(not(name)));
        assertThat(new String(record.getFieldValue("/document/name")), is(""));


        assertThat(new String(record.getFieldValue("/document/surname")), is(surname));

        record.suppressField("/document/surname");
        assertThat(new String(record.getFieldValue("/document/surname")), is(not(surname)));
        assertThat(new String(record.getFieldValue("/document/surname")), is(""));
    }
}