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
package com.ibm.research.drl.dpt.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
        return createGenericReader(new InputStreamReader(stream, StandardCharsets.UTF_8), separator, quoteChar);
    }
}
