/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.opennlp;

import com.ibm.research.drl.dpt.nlp.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OpenNLPLanguageDetector implements com.ibm.research.drl.dpt.nlp.LanguageDetector {
    private final LanguageDetector myCategorizer;
    private final Map<String, Language> languageMap;
    
    public OpenNLPLanguageDetector() {
        try (InputStream is = this.getClass().getResourceAsStream("/nlp/langdetect-183.bin")) {
            LanguageDetectorModel m = new LanguageDetectorModel(is);
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
    
    public Language detectLanguage(String input) {
        String language = myCategorizer.predictLanguage(input).getLang(); 
        return this.languageMap.getOrDefault(language, Language.UNKNOWN);
    }
    
}