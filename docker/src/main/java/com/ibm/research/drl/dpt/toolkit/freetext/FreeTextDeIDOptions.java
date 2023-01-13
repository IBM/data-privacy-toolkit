/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.freetext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.toolkit.masking.MaskingOptions;
import com.ibm.research.drl.dpt.toolkit.task.TaskOptions;

import java.util.Optional;

public class FreeTextDeIDOptions extends TaskOptions {
    private final MaskingOptions maskingConfiguration;
    private final boolean annotate;
    private final JsonNode nlpAnnotator;

    @JsonCreator
    public FreeTextDeIDOptions(
            @JsonProperty("nlpAnnotator") JsonNode nlpAnnotator,
            @JsonProperty("maskingConfiguration") MaskingOptions maskingConfiguration,
            @JsonProperty("annotate") Boolean annotate) {
        this.nlpAnnotator = nlpAnnotator;
        this.maskingConfiguration = maskingConfiguration;
        this.annotate = Optional.ofNullable(annotate).orElse(false);
    }

    public MaskingOptions getMaskingConfiguration() {
        return maskingConfiguration;
    }

    public JsonNode getNlpAnnotator() {
        return nlpAnnotator;
    }

    public boolean getAnnotate() {
        return annotate;
    }
}
