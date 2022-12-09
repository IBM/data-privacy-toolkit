/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence.causal;

public class DictionaryEntry {

    private final DictionaryEntryType type;
    private final String value;

    public DictionaryEntryType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public DictionaryEntry(String value, DictionaryEntryType type) {
        this.type = type;
        this.value = value;
    }

}

