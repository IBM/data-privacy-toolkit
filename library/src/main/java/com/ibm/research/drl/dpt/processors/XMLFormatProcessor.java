/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.processors.records.XMLRecord;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class XMLFormatProcessor extends AbstractMultipathFormatProcessor {
    private final DocumentBuilder documentBuilder;

    public XMLFormatProcessor() {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to instantiate the document builder " + e.getMessage());
        }
    }

    @Override
    protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions dataOptions, int firstN) throws IOException {
        try {
            Document document = documentBuilder.parse(dataset);

            return Collections.singletonList(
                    new XMLRecord(document)
            );
        } catch ( SAXException e) {
            throw new RuntimeException("Error parsing the document " + e.getMessage());
        }
    }

    @Override
    public boolean supportsStreams() {
        return true;
    }


}
