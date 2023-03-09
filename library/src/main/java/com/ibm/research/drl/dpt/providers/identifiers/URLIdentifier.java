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
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

public class URLIdentifier extends AbstractIdentifier {
    private static final String[] appropriateNames = {"URL", "Webpage", "Web URL"};

    @Override
    public ProviderType getType() {
        return ProviderType.URL;
    }

    @Override
    public boolean isOfThisType(String value) {
        String data = value.toLowerCase();

        //TODO: to find the correct map of incorrect characters
        for (int i = 0; i < value.length(); i++) {
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
        } catch (MalformedURLException ignored) {
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
