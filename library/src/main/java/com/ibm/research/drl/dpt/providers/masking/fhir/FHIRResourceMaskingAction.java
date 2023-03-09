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

import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.io.Serializable;

public class FHIRResourceMaskingAction implements Serializable {

    private final String fullPath;
    private final String path;
    private final String[] paths;
    private final boolean delete;

    private final MaskingProvider maskingProvider;
    private final AbstractComplexMaskingProvider abstractComplexMaskingProvider;

    public boolean isDelete() {
        return delete;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String[] getPaths() {
        return paths;
    }

    public MaskingProvider getMaskingProvider() {
        return maskingProvider;
    }

    public AbstractComplexMaskingProvider getAbstractComplexMaskingProvider() {
        return abstractComplexMaskingProvider;
    }

    public FHIRResourceMaskingAction(String fullPath, String path,
                                     MaskingProvider maskingProvider, AbstractComplexMaskingProvider abstractComplexMaskingProvider,
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


