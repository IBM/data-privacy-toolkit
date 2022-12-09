/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.io.Serializable;

public class FHIRResourceMaskingAction implements Serializable{

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


