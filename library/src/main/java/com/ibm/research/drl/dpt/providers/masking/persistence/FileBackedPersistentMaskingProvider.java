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
package com.ibm.research.drl.dpt.providers.masking.persistence;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import org.apache.commons.csv.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class FileBackedPersistentMaskingProvider extends AbstractPersistentMaskingProvider {
    private static final Logger log = LogManager.getLogger(FileBackedPersistentMaskingProvider.class);
    private final FileCache cache;

    public FileBackedPersistentMaskingProvider(MaskingProvider maskingProvider, MaskingConfiguration configuration) {
        super(maskingProvider, configuration);
        String directoryName = configuration.getStringValue("persistence.file");
        String nameSpace = configuration.getStringValue("persistence.namespace");

        this.cache = new FileCache(new File(directoryName, nameSpace).getAbsolutePath());

        log.warn("File-backed persistent provider initialized. " + UUID.randomUUID());
    }

    @Override
    protected boolean isCached(String value) {
        return null != cache.getValue(value);
    }

    @Override
    protected String getCachedValue(String value) {
        return cache.getValue(value);
    }

    @Override
    protected void cacheValue(String value, String maskedValue) {
        try {
            cache.setValue(value, maskedValue);
        } catch (IOException e) {
            throw new RuntimeException("Unable to persist masked value", e);
        }
    }

    private class FileCache {
        private final Map<Integer, String> mappings;
        private final CSVPrinter csvPrinter;

        FileCache(String path) {
            mappings = loadFromFile(path);
            try {
                PrintWriter writer = createWriter(path);
                csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(',').withQuoteMode(QuoteMode.MINIMAL));
            } catch (IOException e) {
                throw new RuntimeException("Unable to create file-backed cache", e);
            }
        }

        public void shutDown() {
            try {
                this.csvPrinter.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        private PrintWriter createWriter(String path) throws IOException {
            return new PrintWriter(new FileWriter(path, true));
        }

        private Map<Integer, String> loadFromFile(String path) {
            return loadFromFile(new File(path));
        }

        private Map<Integer, String> loadFromFile(File file) {
            if (!file.exists()) return new HashMap<>();

            try (FileReader reader = new FileReader(file)) {
                return loadFromFile(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null; // TMCH
        }

        private Map<Integer, String> loadFromFile(Reader input) {
            Map<Integer, String> mapping = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(input)) {
                for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                    CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180.withDelimiter(','));
                    CSVRecord csvRecord = parser.getRecords().get(0);

                    int valueCode = Integer.parseInt(csvRecord.get(0));

                    mapping.put(valueCode, csvRecord.get(1));
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to load the data", e);
            }
            return mapping;
        }

        public String getValue(String value) {
            int valueCode = value.hashCode();

            return mappings.get(valueCode);
        }

        private void appendMapping(int valueCode, String mapped) throws IOException {
            csvPrinter.printRecord(valueCode, mapped);
            csvPrinter.flush();
        }

        void setValue(String identifier, String maskedValue) throws IOException {
            synchronized (this) {
                int valueCode = identifier.hashCode();
                String oldValue = mappings.putIfAbsent(valueCode, maskedValue);
                if (!maskedValue.equals(oldValue)) {
                    appendMapping(valueCode, maskedValue);
                }
            }
        }
    }
}
