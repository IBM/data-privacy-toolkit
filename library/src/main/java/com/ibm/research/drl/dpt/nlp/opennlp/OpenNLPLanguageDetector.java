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

import com.ibm.research.drl.dpt.nlp.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OpenNLPLanguageDetector implements com.ibm.research.drl.dpt.nlp.LanguageDetector {
    private final LanguageDetector myCategorizer;
    private final Map<String, Language> languageMap;
    
    public OpenNLPLanguageDetector() {
        try (InputStream is = OpenNLPLanguageDetector.class.getResourceAsStream("/nlp/langdetect-183.bin")) {
            LanguageDetectorModel m = new LanguageDetectorModel(Objects.requireNonNull(is));
            this.myCategorizer = new LanguageDetectorME(m);
        } catch (IOException e) {
            throw new RuntimeException("unable to initialize OpenNLPLanguageDetector");
        }
        
        this.languageMap = new HashMap<>();
        this.languageMap.put("eng", Language.ENGLISH);
        this.languageMap.put("ell", Language.GREEK);
        this.languageMap.put("ita", Language.ITALIAN);
        this.languageMap.put("gle", Language.IRISH);
        this.languageMap.put("heb", Language.HEBREW);
    }

    @Override
    public Language detectLanguage(String input) {
        String language = myCategorizer.predictLanguage(input).getLang();
        return this.languageMap.getOrDefault(language, Language.UNKNOWN);
    }
    
}