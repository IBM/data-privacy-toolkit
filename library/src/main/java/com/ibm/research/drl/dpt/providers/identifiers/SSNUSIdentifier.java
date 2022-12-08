/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.SSNUS;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SSNUSIdentifier extends AbstractIdentifier implements IdentifierWithOffset {
    private static final Set<String> prefixes = new HashSet<>(Arrays.asList("SS #", "SS# ", "SS # OF", "SS# OF", "SS:", "SSN:", "SS"));
    
    private final static String[] appropriateNames = new String[]{
            "SSN"
    };

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SSN_US;
    }

    /**
     * Parse ssnus ssnus.
     *
     * @param data the data
     * @return the ssnus
     */
    public SSNUS parseSSNUS(String data) {
        String[] toks = data.split("-");
        String[] ssnParts;

        if (toks.length == 3) {
            if (toks[0].length() != 3 || toks[1].length() != 2 || toks[2].length() != 4) {
                return null;
            }

            ssnParts = toks;
        }
        else {
            return null;
        }

        for (int i = 0; i < 3; i++)  {
            for (int j = 0; j < ssnParts[i].length(); j++) {
                if (!Character.isDigit(ssnParts[i].charAt(j))) {
                    return null;
                }
            }
        }

        if (ssnParts[0].equals("000") || ssnParts[0].equals("666")) {
            return null;
        }


        return new SSNUS(ssnParts[0], ssnParts[1], ssnParts[2]);
    }

    @Override
    public boolean isOfThisType(String data) {
        return (parseSSNUS(data) != null || prefixMatch(data) > 0);
    }

            
    private int prefixMatch(String data) {
        data = data.toUpperCase();
        
        for(String prefix: prefixes) {
            if (data.startsWith(prefix)) {
                String ssn = data.substring(prefix.length()).trim();
                if (parseSSNUS(ssn) != null) {
                    return firstOccurenceOfNumber(data, prefix.length());
                }
            }
        }
        
        return -1; 
    }

    private int firstOccurenceOfNumber(String data, int start) {
        for(int i = start; i < data.length(); i++) {
            if (Character.isDigit(data.charAt(i))) {
                return i;
            }
        }
        
        return -1;
    }

    @Override
    public String getDescription() {
        return "SSN identification for US";
    }

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String data) {
        SSNUS ssn = parseSSNUS(data);
        
        if (ssn != null) {
            return new Tuple<>(true, new Tuple<>(0, data.length()));
        }
        
        int offset = prefixMatch(data);
       
        if (offset >= 0) {
            return new Tuple<>(true, new Tuple<>(offset, data.length() - offset));
        }
        
        return new Tuple<>(false, null);
    }
    
    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 9;
    }

    @Override
    public int getMaximumLength() {
        return 11;
    }
}
