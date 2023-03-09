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

import com.ibm.research.drl.dpt.models.Address;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Address identifier.
 */
public class AddressIdentifier extends AbstractIdentifier {
    private static final Pattern[] poBoxPatterns = {
            Pattern.compile("(PO|P.O.) BOX (?<poboxnumber>\\d+)")
    };
    private final static String[] appropriateNames = {"Address"};
    /**
     * The Road type pattern.
     */
    private final static Pattern roadTypePattern = Pattern.compile(
            "\\b(?<roadtype>ST|ST.|STREET|DR|DR.|DRIVE|BOULEVARD|BLVD|BLVD.|COURT|CT|CT.|ROUTE|ROAD|RD.|RD|AVE|AVENUE|AVE.|LANE|LN.)\\b");

    /**
     * The First part pattern.
     */
    private final static Pattern firstPartPattern = Pattern.compile("^(?<number>\\d+)?\\s*(?<street>(([\\w|]+)\\s*)+)");
    /**
     * The Second part pattern.
     */
    private final static Pattern secondPartPattern = Pattern.compile(",\\s+(?<cityorstate>(([a-zA-Z.’]+)\\s+)+)(?<postal>([A-Z]*\\d+[A-Z]*\\s*)+)?(,\\s+(?<country>(\\w+\\s*)+))?");

    /**
     * Remove diacritical marks string.
     *
     * @param string the string
     * @return the string
     */
    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    public ProviderType getType() {
        return ProviderType.ADDRESS;
    }

    private Address tryParsePOBOX(String key) {
        if (key.startsWith("PO ") || key.startsWith("P.O. ")) {
            for (Pattern p : poBoxPatterns) {
                Matcher m = p.matcher(key);
                if (m.matches()) {
                    String poboxnumber = m.group("poboxnumber");
                    Address address = new Address();
                    address.setPoBox(true);
                    address.setPoBoxNumber(poboxnumber);
                    return address;
                }
            }
        }

        return null;
    }

    /**
     * Parse address address.
     *
     * @param data the data
     * @return the address
     */
    public Address parseAddress(String data) {
        //String key = removeDiacriticalMarks(data.trim()).toUpperCase();
        String key = data.toUpperCase();

        Address address = tryParsePOBOX(key);
        if (address != null) {
            return address;
        }

        Matcher roadtypeMatch = roadTypePattern.matcher(key);

        int roadtypeMatchOffset = -1;
        int roadtypeMatchEnd = -1;
        String roadType = null;

        while (roadtypeMatch.find()) {
            roadtypeMatchOffset = roadtypeMatch.start();
            roadtypeMatchEnd = roadtypeMatch.end();
            roadType = roadtypeMatch.group("roadtype").trim();
        }

        if (roadtypeMatchOffset < 5) {
            return null;
        }


        Matcher firstPartMatch = firstPartPattern.matcher(key.substring(0, roadtypeMatchOffset));
        if (!firstPartMatch.find()) {
            return null;
        }

        String number = firstPartMatch.group("number");
        if (number == null) {
            number = "";
        }

        String street = firstPartMatch.group("street").trim();

        String cityOrState;
        String postal;
        String country;

        Matcher secondPartMatch = secondPartPattern.matcher(key.substring(roadtypeMatchEnd));
        if (!secondPartMatch.matches()) {
            cityOrState = "";
            postal = "";
            country = "";
        } else {
            cityOrState = secondPartMatch.group("cityorstate").trim();

            postal = secondPartMatch.group("postal");
            if (postal == null) {
                postal = "";
            }

            country = secondPartMatch.group("country");
            if (country == null) {
                country = "";
            }
        }

        address = new Address();

        address.setRoadType(roadType);
        address.setNumber(number);
        address.setName(street);
        address.setCityOrState(cityOrState);
        address.setPostalCode(postal.trim());
        address.setCountry(country.trim());

        return address;
    }

    @Override
    public boolean isOfThisType(String data) {
        return (parseAddress(data) != null);
    }

    @Override
    public String getDescription() {
        return "Address identification of the most common formats like \"200 Main Street, NY, USA\", \"PO BOX 123\" etc.";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }

    @Override
    public int getMinimumLength() {
        return 0;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }
}
