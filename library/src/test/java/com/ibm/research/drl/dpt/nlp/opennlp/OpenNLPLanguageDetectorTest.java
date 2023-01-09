/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.opennlp;

import com.ibm.research.drl.dpt.nlp.Language;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenNLPLanguageDetectorTest {
    
    @Test
    public void testLanguageDetection() {
        String input = "This is the input in English";
        
        OpenNLPLanguageDetector openNLPLanguageDetector = new OpenNLPLanguageDetector();
        assertEquals(Language.ENGLISH, openNLPLanguageDetector.detectLanguage(input));
        
        input = "αυτό είναι ένα κείμενο στα ελληνικά";
        assertEquals(Language.GREEK, openNLPLanguageDetector.detectLanguage(input));
    }
    
    @Test
    @Disabled
    public void testUnknownLanguage() {
        String input = "";

        OpenNLPLanguageDetector openNLPLanguageDetector = new OpenNLPLanguageDetector();
        System.out.println(openNLPLanguageDetector.detectLanguage(input));
    }
}

