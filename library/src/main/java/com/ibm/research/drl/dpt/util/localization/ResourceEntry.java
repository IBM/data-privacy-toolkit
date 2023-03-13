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
package com.ibm.research.drl.dpt.util.localization;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceEntry {

    private final String filename;
    private final String countryCode;
    private final ResourceEntryType resourceEntryType;

    /**
     * Instantiates a new Resource entry.
     *
     * @param filename          the filename
     * @param countryCode       the country code
     * @param resourceEntryType the resource entry type
     */
    public ResourceEntry(String filename, String countryCode, ResourceEntryType resourceEntryType) {
        this.filename = filename;
        this.countryCode = countryCode;
        this.resourceEntryType = resourceEntryType;
    }

    /**
     * Create stream input stream.
     *
     * @return the input stream
     */
    public InputStream createStream() {
        switch (resourceEntryType) {
            case INTERNAL_RESOURCE:
                return ResourceEntry.class.getResourceAsStream(filename);
            case EXTERNAL_FILENAME:
                try {
                    return new FileInputStream(filename);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            default:
                throw new RuntimeException("Unknown resource entry type " + resourceEntryType);
        }
    }

    /**
     * Gets country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }
}
