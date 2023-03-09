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

