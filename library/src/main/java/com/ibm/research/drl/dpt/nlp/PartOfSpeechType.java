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
package com.ibm.research.drl.dpt.nlp;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public final class PartOfSpeechType implements Serializable {
    private static final Logger logger = LogManager.getLogger(PartOfSpeechType.class);
    public static final PartOfSpeechType UNKNOWN = new PartOfSpeechType("UNKNOWN");
    private final static Map<String, PartOfSpeechType> knownTypes;

    static {
        knownTypes = new HashMap<>();
        knownTypes.put("``", new PartOfSpeechType("``"));
        knownTypes.put(".", new PartOfSpeechType("."));
        knownTypes.put("#", new PartOfSpeechType("#"));
        knownTypes.put("$", new PartOfSpeechType("$"));
        knownTypes.put("''", new PartOfSpeechType("''"));
        knownTypes.put(",", new PartOfSpeechType(","));
        knownTypes.put("-LRB-", new PartOfSpeechType("-LRB-"));
        knownTypes.put("-RRB-", new PartOfSpeechType("-RRB-"));
        knownTypes.put("(", new PartOfSpeechType("("));
        knownTypes.put(")", new PartOfSpeechType(")"));
        knownTypes.put("", new PartOfSpeechType(""));
        knownTypes.put(":", new PartOfSpeechType(":"));
        knownTypes.put("CC", new PartOfSpeechType("CC"));
        knownTypes.put("CD", new PartOfSpeechType("CD"));
        knownTypes.put("DT", new PartOfSpeechType("DT"));
        knownTypes.put("EX", new PartOfSpeechType("EX"));
        knownTypes.put("FW", new PartOfSpeechType("FW"));
        knownTypes.put("IN", new PartOfSpeechType("IN"));
        knownTypes.put("JJ", new PartOfSpeechType("JJ"));
        knownTypes.put("JJR", new PartOfSpeechType("JJR"));
        knownTypes.put("JJS", new PartOfSpeechType("JJS"));
        knownTypes.put("LS", new PartOfSpeechType("LS"));
        knownTypes.put("MD", new PartOfSpeechType("MD"));
        knownTypes.put("NN", new PartOfSpeechType("NN"));
        knownTypes.put("NNP", new PartOfSpeechType("NNP"));
        knownTypes.put("NNPS", new PartOfSpeechType("NNPS"));
        knownTypes.put("NNS", new PartOfSpeechType("NNS"));
        knownTypes.put("NPP", new PartOfSpeechType("NPP"));
        knownTypes.put("PDT", new PartOfSpeechType("PDT"));
        knownTypes.put("POS", new PartOfSpeechType("POS"));
        knownTypes.put("PRP", new PartOfSpeechType("PRP"));
        knownTypes.put("PRP$", new PartOfSpeechType("PRP$"));
        knownTypes.put("RB", new PartOfSpeechType("RB"));
        knownTypes.put("RBR", new PartOfSpeechType("RBR"));
        knownTypes.put("RBS", new PartOfSpeechType("RBS"));
        knownTypes.put("RP", new PartOfSpeechType("RP"));
        knownTypes.put("QT", new PartOfSpeechType("QT"));
        knownTypes.put("TO", new PartOfSpeechType("TO"));
        knownTypes.put("UH", new PartOfSpeechType("UH"));
        knownTypes.put("VB", new PartOfSpeechType("VB"));
        knownTypes.put("VBD", new PartOfSpeechType("VBD"));
        knownTypes.put("VBG", new PartOfSpeechType("VBG"));
        knownTypes.put("VBN", new PartOfSpeechType("VBN"));
        knownTypes.put("VBP", new PartOfSpeechType("VBP"));
        knownTypes.put("VBZ", new PartOfSpeechType("VBZ"));
        knownTypes.put("VRB", new PartOfSpeechType("VRB"));
        knownTypes.put("WDT", new PartOfSpeechType("WDT"));
        knownTypes.put("WP", new PartOfSpeechType("WP"));
        knownTypes.put("WP$", new PartOfSpeechType("WP$"));
        knownTypes.put("WRB", new PartOfSpeechType("WRB"));

        knownTypes.put("noun", new PartOfSpeechType("NOUN"));
        knownTypes.put("verb", new PartOfSpeechType("VERB"));
        knownTypes.put("det", new PartOfSpeechType("DET"));
        knownTypes.put("prep", new PartOfSpeechType("IN"));
        knownTypes.put("adj", new PartOfSpeechType("JJ"));
        knownTypes.put("pron", new PartOfSpeechType("PRON"));
        knownTypes.put("cord", new PartOfSpeechType("CC"));
        knownTypes.put("adv", new PartOfSpeechType("ADV"));

        // from UD
        knownTypes.put("ADJ", new PartOfSpeechType("ADJ"));
        knownTypes.put("ADP", new PartOfSpeechType("ADP"));
        knownTypes.put("ADV", new PartOfSpeechType("ADV"));
        knownTypes.put("AUX", new PartOfSpeechType("AUX"));
        knownTypes.put("CCONJ", new PartOfSpeechType("CCONJ"));
        knownTypes.put("DET", new PartOfSpeechType("DET"));
        knownTypes.put("INTJ", new PartOfSpeechType("INTJ"));
        knownTypes.put("NOUN", new PartOfSpeechType("NOUN"));
        knownTypes.put("NUM", new PartOfSpeechType("NUM"));
        knownTypes.put("PART", new PartOfSpeechType("PART"));
        knownTypes.put("PRON", new PartOfSpeechType("PRON"));
        knownTypes.put("PROPN", new PartOfSpeechType("PROPN"));
        knownTypes.put("PUNCT", new PartOfSpeechType("PUNCT"));
        knownTypes.put("SCONJ", new PartOfSpeechType("SCONJ"));
        knownTypes.put("SYM", new PartOfSpeechType("SYM"));
        knownTypes.put("VERB", new PartOfSpeechType("VERB"));
        knownTypes.put("X", new PartOfSpeechType("X"));

        /*
        ADJ: adjective
        ADP: adposition
        ADV: adverb
        AUX: auxiliary
        CCONJ: coordinating conjunction
        DET: determiner
        INTJ: interjection
        NOUN: noun
        NUM: numeral
        PART: particle
        PRON: pronoun
        PROPN: proper noun
        PUNCT: punctuation
        SCONJ: subordinating conjunction
        SYM: symbol
        VERB: verb
        X: other
        */

    }

    private final String type;

    private PartOfSpeechType(String type) {
        this.type = type;
    }

    public static PartOfSpeechType valueOf(String type) {
        PartOfSpeechType pos = knownTypes.get(type);

        if (null != pos) return pos;

        {
            logger.warn("Unknown type {}", type);
            return PartOfSpeechType.UNKNOWN;
            //throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private static final Collection<String> conjunctions = new HashSet<>(Arrays.asList("CC", "IN", "conj", "CCONJ", "SCONJ"));
    private static final Collection<String> nouns = new HashSet<>(Arrays.asList("NN", "NNS", "NNP", "NT", "NNPS", "NOUN", "PROPN"));
    private static final Collection<String> adjectives = new HashSet<>(Arrays.asList("JJ", "JJR", "JJS", "ADJ"));
    private static final Collection<String> localizer = new HashSet<>(Collections.singletonList("LC"));
    private static final Collection<String> pronouns = new HashSet<>(Arrays.asList("PN", "PRP", "PRP", "WP", "WP"));
    private static final Collection<String> verbs = new HashSet<>(Arrays.asList("VV", "MD",  "VA",  "VC",  "VE",  "VB",  "VBD",  "VBG",  "VBN",  "VBP",  "VBZ", "VERB"));
    private static final Collection<String> adverbs = new HashSet<>(Arrays.asList("RB", "RBR", "RBS", "WRB"));
    private static final Collection<String> modals = new HashSet<>(Arrays.asList("MD"));
    private static final Collection<String> numericals = new HashSet<>(Arrays.asList("CD"));
    private static final Collection<String> punctuation = new HashSet<>(Arrays.asList("PUNCT", ":", ",", "``", ".", "#", "''", "$", "(", ")"));

    public boolean isNumerical() {return numericals.contains(this.type);}
    
    public boolean isUnknown() {
        return !knownTypes.containsKey(this.type);
    }
    
    public boolean isNoun() {
        return nouns.contains(this.type);
    }
    
    public boolean isAdjective() {
        return adjectives.contains(this.type);
    }

    public boolean isLocalizer() {
        return localizer.contains(this.type);
    }

    public boolean isPronoun() {
        return pronouns.contains(this.type);
    }

    public boolean isVerb() {
        return verbs.contains(this.type);
    }
    
    public boolean isModal() {
        return modals.contains(this.type);
    }

    @Override
    public String toString() {
        return "PartOfSpeechType." + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartOfSpeechType that = (PartOfSpeechType) o;

        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    public boolean isOther() {
        return false;
    }

    public boolean isAdverb() {
        return adverbs.contains(this.type);
    }

    public boolean isConjunction() {
        return conjunctions.contains(this.type);
    }

    public boolean isPuntuation() {
        return punctuation.contains(this.type);
    }
}
