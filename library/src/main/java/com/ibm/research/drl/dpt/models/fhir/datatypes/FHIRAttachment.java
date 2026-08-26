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
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.FHIRExtension;

import java.util.Collection;

/** FHIRAttachment FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRAttachment {
    /** Constructs a FHIRAttachment. */
    public FHIRAttachment() {}

    private Collection<FHIRExtension> extension;
    private String contentType;
    private String language;
    private String data;
    private String url;
    private String size;
    private String hash;
    private String title;
    private String creation;

    /**
     * Returns the extension.
     * @return the extension
     */
    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    /**
     * Sets the extension.
     * @param extension the extension
     */
    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    /**
     * Returns the contentType.
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the contentType.
     * @param contentType the contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns the language.
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Returns the data.
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data.
     * @param data the data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Returns the url.
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the size.
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the size.
     * @param size the size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Returns the hash.
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the hash.
     * @param hash the hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Returns the title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the creation.
     * @return the creation
     */
    public String getCreation() {
        return creation;
    }

    /**
     * Sets the creation.
     * @param creation the creation
     */
    public void setCreation(String creation) {
        this.creation = creation;
    }

}
