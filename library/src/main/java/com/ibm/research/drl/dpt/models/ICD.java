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
package com.ibm.research.drl.dpt.models;

public class ICD {

    private final String code;
    private final String shortName;
    private final String fullName;
    private final String chapterCode;
    private final String chapterName;
    private final String categoryCode;
    private final String categoryName;
    private final ICDFormat format;

    /**
     * Instantiates a new Icd.
     *
     * @param code         the code
     * @param shortName    the short name
     * @param fullName     the full name
     * @param chapterCode  the chapter code
     * @param chapterName  the chapter name
     * @param categoryCode the category code
     * @param categoryName the category name
     * @param format       the format
     */
    public ICD(String code, String shortName, String fullName,
               String chapterCode, String chapterName,
               String categoryCode, String categoryName, ICDFormat format) {

        this.code = code;
        this.shortName = shortName;
        this.fullName = fullName;
        this.chapterCode = chapterCode;
        this.chapterName = chapterName;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.format = format;
    }

    /**
     * Gets category name.
     *
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Gets category code.
     *
     * @return the category code
     */
    public String getCategoryCode() {
        return categoryCode;
    }


    /**
     * Gets chapter code.
     *
     * @return the chapter code
     */
    public String getChapterCode() {
        return chapterCode;
    }

    /**
     * Gets chapter name.
     *
     * @return the chapter name
     */
    public String getChapterName() {
        return chapterName;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets short name.
     *
     * @return the short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets format.
     *
     * @return the format
     */
    public ICDFormat getFormat() {
        return format;
    }


    @Override
    public String toString() {
        if (this.format == ICDFormat.CODE) {
            return this.code;
        }

        return this.fullName;
    }

}
