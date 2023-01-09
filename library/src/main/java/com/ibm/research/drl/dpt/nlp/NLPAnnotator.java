/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;



public interface NLPAnnotator extends Serializable {
    default List<IdentifiedEntity> identify(String text, Language language) throws IOException {
        return identify(text, language, new NLPAnnotator[0]);
    }

    List<IdentifiedEntity> identifyMissing(List<IdentifiedEntity> identifiedEntities, String text, Language language) throws IOException;
    List<IdentifiedEntity> mergeEntities(List<IdentifiedEntity> identifiedEntities, String text);

    String getName();

    default List<IdentifiedEntity> identify(String text, Language language, NLPAnnotator... customIdentifiers) throws IOException {
        return identify(text, language);
    }

    default List<String> getPosIndependentTypes() {
        return Collections.emptyList();
    }
}
