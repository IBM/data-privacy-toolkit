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
package com.ibm.research.drl.dpt.nlp.opennlp;


import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.Language;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.nlp.PartOfSpeechType;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenNLPPOSTaggerTest {

    private final String note2 = "Called pt home phone number listed at 555-000-1111.  I asked for Ms. Ball and the phone was handed off to what sound ed like an elderly women from someone who appeared to be a caregiver.  I asked if it was Ms. Ball and she said yes and  I told her who I was and that I was calling with Dr. Hammonds office where then she told me that she doesnt know who that is.  I then asked if her first name was Chloe and she responded in a way that I couldnt understand what she was saying.  But that is not her first name and she didnt know who I was asking for.  She thanked me and hung up the phone.   Because this isnt my doctor and I have never spoken with pt before I did not want to try the cell phone that was listed.  This message will be written so that Lilian the primary RN CC can read it and decide what she would like to do from here";

    @Test
    public void testIdentification() throws Exception {
        try (
        InputStream openNLPPOSTaggerConfig = OpenNLPAnnotatorTest.class.getResourceAsStream("/opennlp.json")
        ) {
            final NLPAnnotator identifier = new OpenNLPPOSTagger(JsonUtils.MAPPER.readTree(openNLPPOSTaggerConfig));

            //we invoke the NLPAnnotator. It will return a list of identified tokens per sentence.
            final List<IdentifiedEntity> entities = identifier.identify(note2, Language.UNKNOWN);

            for (IdentifiedEntity entity : entities) {
                if (entity.getText().equals("home")) {
                    assertEquals(PartOfSpeechType.valueOf("NN"), entity.getPos().iterator().next());
                }
            }
        }
    }

    @Test
    public void testTaggingItalian() throws Exception {
        String configuration = "{" +
                "\"defaultLanguage\": \"ITALIAN\"," +
                "\"configuration\":{" +
                    "\"ITALIAN\": {" +
                        "\"tagger\" : \"/nlp/it/it-pos-maxent.bin\",\n" +
                        "\"tokenizer\" : \"/nlp/it/it-token.bin\"\n" +
                    "}" +
                "}" +
                "}";

        OpenNLPPOSTagger tagger = new OpenNLPPOSTagger(JsonUtils.MAPPER.readTree(configuration));

        List<IdentifiedEntity> entities = tagger.identify("Il mio nome e' stefano, vivo a Roma", Language.ITALIAN);

        System.out.println(entities);
    }

    @Test
    @Disabled("To be expanded")
    public void testInitializationWithSpanishModels() throws Exception {
        String configuration = "{" +
                "\"defaultLanguage\": \"SPANISH\"," +
                "\"configuration\" : {" +
                    "\"SPANISH\" : {" +
                        "\"sentenceFinder\" : \"/nlp/en-sent.bin\"," +
                        "\"tagger\" : \"/nlp/es/SpanishPOS.bin\"," +
                        "\"tokenizer\" : \"/nlp/es/SpanishTok.bin\"" +
                    "}" +
                    "}" +
                "}";

        final NLPAnnotator annotator = new OpenNLPPOSTagger(JsonUtils.MAPPER.readTree(configuration));

        List<IdentifiedEntity> annotations = annotator.identify("hola, muy bueno tu deck, mi idioma nativo es español latino, solo tengo que corregir la frase, Acaba de empezar a llover, la frase correcta es\n" +
                "EMPEZO A LLOVER, descargue tu deck hize 20 frases y solo frase una estaba mal hecha, hicistes un buen trabajo haciendo ese deck, tendras uno en ingles me interesa aprender frases en ese idioma, Soy nueva en anki.", Language.SPANISH);

        assertNotNull(annotations);
        assertFalse(annotations.isEmpty());
    }
}
