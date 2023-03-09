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
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.CreditCard;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CreditCardManager {
    private final SecureRandom random;
    private final Map<String, CreditCard> creditCardMap;
    private final Map<String, CreditCard> creditCardMapByPrefix;
    private String[] creditCardNames;

    private final static CreditCardManager CREDIT_CARD_MANAGER = new CreditCardManager();

    public static CreditCardManager getInstance() {
        return CREDIT_CARD_MANAGER;
    }

    /**
     * Instantiates a new Credit card manager.
     */
    private CreditCardManager() {
        this.random = new SecureRandom();

        this.creditCardMapByPrefix = new HashMap<>();
        this.creditCardMap = readResourceList();
        Set<String> keys = creditCardMap.keySet();

        this.creditCardNames = new String[keys.size()];
        creditCardNames = keys.toArray(creditCardNames);
    }

    /**
     * Read resource list map.
     *
     * @return the map
     */
    public Map<String, CreditCard> readResourceList() {
        Collection<ResourceEntry> resources = LocalizationManager.getInstance().getResources(Resource.CREDIT_CARD_TYPE);
        Map<String, CreditCard> ccMap = new HashMap<>();

        for (ResourceEntry resourceEntry : resources) {
            InputStream inputStream = resourceEntry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    String prefixesEncoded = line.get(1);
                    int minimumLength = Integer.parseInt(line.get(2));
                    int maximumLength = Integer.parseInt(line.get(3));
                    String[] prefixes = prefixesEncoded.split(":");

                    CreditCard creditCard = new CreditCard(name, prefixes, minimumLength, maximumLength);
                    ccMap.put(name.toUpperCase(), creditCard);

                    for (String prefix : prefixes) {
                        creditCardMapByPrefix.put(prefix, creditCard);
                    }
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {
            }
        }

        return ccMap;
    }

    /**
     * Lookup info credit card.
     *
     * @param cc the cc
     * @return the credit card
     */
    public CreditCard lookupInfo(String cc) {
        if (cc.length() < 6) {
            return null;
        }

        String issuer = cc.substring(0, 6);

        for (int i = 2; i < 4; i++) {
            String prefix = issuer.substring(0, i);
            CreditCard res = creditCardMapByPrefix.get(prefix);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    /**
     * Random credit card information credit card.
     *
     * @return the credit card
     */
    public CreditCard randomCreditCardInformation() {
        int length = creditCardNames.length;
        int index = random.nextInt(length);
        String randomName = creditCardNames[index];
        return creditCardMap.get(randomName);
    }
}
