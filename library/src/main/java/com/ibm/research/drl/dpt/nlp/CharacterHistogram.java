/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

public class CharacterHistogram {
    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz";

    public static int[] generateHistogram(String input) {
        int[] counters = new int[ALPHANUMERIC.length()];

        for (int i = 0; i < ALPHANUMERIC.length(); i++) {
            counters[i] = 0;
        }

        int inputSize = input.length();

        for(int i = 0; i < inputSize; i++) {
            char c = Character.toLowerCase(input.charAt(i));
            if (c >= 'a' && c <= 'z') {
                int index = (int)(c - 'a');
                counters[index]++;
            }
        }

        return counters;
    }


}

