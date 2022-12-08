/*******************************************************************
* IBM Confidential                                                *
*                                                                 *
* Copyright IBM Corp. 2021                                        *
*                                                                 *
* The source code for this program is not published or otherwise  *
* divested of its trade secrets, irrespective of what has         *
* been deposited with the U.S. Copyright Office.                  *
*******************************************************************/
package com.ibm.research.drl.prima.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.*;

public class Readers {
    private final static Readers instance = new Readers();

    private static CSVParser createGenericReader(Reader reader, char separator, char quoteChar) throws IOException {
        return CSVFormat.DEFAULT.withDelimiter(separator).withQuote(quoteChar).parse(reader);
    }

    /**
     * Create csv reader from resource csv reader.
     *
     * @param filename the filename
     * @return the csv reader
     */
    public static CSVParser createCSVReaderFromResource(String filename) throws IOException {
        return createCSVReaderFromStream(instance.getClass().getResourceAsStream(filename), ',', '"');
    }

    /**
     * Create csv reader from file csv reader.
     *
     * @param filename the filename
     * @return the csv reader
     * @throws FileNotFoundException the file not found exception
     */
    public static CSVParser createCSVReaderFromFile(String filename) throws IOException {
        return createGenericReader(new FileReader(filename), ',', '"');
    }

    /**
     * Create csv reader from stream csv reader.
     *
     * @param stream the stream
     * @return the csv reader
     */
    public static CSVParser createCSVReaderFromStream(InputStream stream) throws IOException {
        return createCSVReaderFromStream(stream, ',', '"');
    }

    /**
     * Create csv reader from stream csv reader.
     *
     * @param stream    the stream
     * @param separator the separator
     * @param quoteChar the quote char
     * @return the csv reader
     */
    public static CSVParser createCSVReaderFromStream(InputStream stream, char separator, char quoteChar) throws IOException {
        return createGenericReader(new InputStreamReader(stream, "UTF-8"), separator, quoteChar);
    }
}
