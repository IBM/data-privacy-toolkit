/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

public class URLIdentifier extends AbstractIdentifier{
    private static final String[] appropriateNames = {"URL", "Webpage", "Web URL"};

    @Override
    public ProviderType getType() {
        return ProviderType.URL;
    }

    @Override
    public boolean isOfThisType(String value) {
        String data = value.toLowerCase();

        //TODO: to find the correct map of incorrect characters
        for(int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isWhitespace(c) || c == '\r' || c == '\n') {
                return false;
            }
        }

        if (data.startsWith("www.") || data.startsWith("mail.")) {
            return true;
        }

        if (!data.startsWith("http")) {
            return false;
        }
        
        try {
            URL u = new URL(value);
            return true;
        } catch(MalformedURLException ignored) {
            return data.startsWith("www.") || data.startsWith("mail.");
        }
    }

    @Override
    public String getDescription() {
        return "URL identification. Supports HTTP and HTTPS detection";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }
    
    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DOT;
    }

    @Override
    public int getMinimumLength() {
        return 3;
    }

    @Override
    public int getMaximumLength() {
        return 2083;
    }
}
