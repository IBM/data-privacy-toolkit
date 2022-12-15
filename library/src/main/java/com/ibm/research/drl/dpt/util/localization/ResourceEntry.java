/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util.localization;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceEntry {
    private static final Logger logger = LogManager.getLogger(ResourceEntry.class);

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
        //logger.debug("Creating stream for {} {}", filename, countryCode);

        switch (resourceEntryType) {
            case INTERNAL_RESOURCE:
                //logger.debug("Returning internal resource");
                return this.getClass().getResourceAsStream(filename);
            case EXTERNAL_FILENAME:
//                logger.debug("External resource");
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
