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
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.io.Serializable;

/** FHIRResourceMaskingAction FHIR datatype. */
public class FHIRResourceMaskingAction implements Serializable {

    /** The full JSON path of the field to mask or delete. */
    private final String fullPath;
    /** The relative path segment of the field. */
    private final String path;
    /** Path segments split for traversal. */
    private final String[] paths;
    /** Whether this action deletes the field rather than masking it. */
    private final boolean delete;

    /** The simple masking provider to apply (may be {@code null}). */
    private final MaskingProvider maskingProvider;
    /** The complex masking provider to apply (may be {@code null}). */
    private final AbstractComplexMaskingProvider<JsonNode> abstractComplexMaskingProvider;

    /**
     * Returns whether delete.
     * @return true if delete
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * Returns the path.
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the fullPath.
     * @return the fullPath
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * Returns the paths.
     * @return the paths
     */
    public String[] getPaths() {
        return paths;
    }

    /**
     * Returns the maskingProvider.
     * @return the maskingProvider
     */
    public MaskingProvider getMaskingProvider() {
        return maskingProvider;
    }

    /**
     * Returns the abstractComplexMaskingProvider.
     * @return the abstractComplexMaskingProvider
     */
    public AbstractComplexMaskingProvider<JsonNode> getAbstractComplexMaskingProvider() {
        return abstractComplexMaskingProvider;
    }

    /**
     * Constructs a FHIRResourceMaskingAction.
     * @param fullPath the fullPath
     * @param path the path
     * @param maskingProvider the maskingProvider
     * @param abstractComplexMaskingProvider the abstractComplexMaskingProvider
     * @param delete the delete
     */
    public FHIRResourceMaskingAction(String fullPath, String path,
                                     MaskingProvider maskingProvider, AbstractComplexMaskingProvider<JsonNode> abstractComplexMaskingProvider,
                                     boolean delete) {
        this.fullPath = fullPath;
        this.path = path;

        if (this.path.startsWith("/")) {
            this.paths = path.substring(1).split("/");
        } else {
            this.paths = path.split("/");
        }

        this.delete = delete;

        this.maskingProvider = maskingProvider;
        this.abstractComplexMaskingProvider = abstractComplexMaskingProvider;
    }

}


