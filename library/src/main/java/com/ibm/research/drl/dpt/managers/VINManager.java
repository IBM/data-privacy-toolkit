/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VINManager implements Manager, Serializable {

    private static final Collection<ResourceEntry> resourceWMIList =
            LocalizationManager.getInstance().getResources(Resource.WMI);
    private final Map<String, String> wmiMap;
    private final String[] wmiList;
    private final SecureRandom random;
    private final char[] excludedCharacters = {'I', 'O', 'Q', 'i', 'o', 'q'};

    /**
     * Instantiates a new Vin manager.
     */
    public VINManager() {
        this.wmiMap = new HashMap<String, String>();
        this.wmiMap.putAll(readWMIList(resourceWMIList));
        this.random = new SecureRandom();

        Set<String> keyset = wmiMap.keySet();
        this.wmiList = keyset.toArray(new String[keyset.size()]);

        for(String wmi: wmiList) {
            if (!isValidWMI(wmi)) {
                throw new RuntimeException("invalid WMI loaded:" + wmi);
            }
        }
    }

    private Map<? extends String, ? extends String> readWMIList(Collection<ResourceEntry> entries) {
        Map<String, String> names = new HashMap<>();

        for(ResourceEntry entry: entries) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    names.put(line.get(0).toUpperCase(), line.get(1));
                }
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        return names;
    }

    @Override
    public boolean isValidKey(String data) {

        /* All standards for VIN are 17-digit format
        First 3 digits is WMI - World manufacturer identifier
        Digits 4-9 are the vehicle description section
        Digits 10-17 is the vehicle identifier section
         */
        if (data.length() != 17) {
            return false;
        }

        /* In 1981, the National Highway Traffic Safety Administration of the United States
        standardized the format.[1] It required all over-the-road-vehicles sold to contain a 17-character VIN,
        which does not include the letters I (i), O (o), or Q (q) (to avoid confusion with numerals 1 and 0).
         */
        //data = data.toUpperCase();
        for(int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);

            /* VIN is composed only of digits and letters */
            if (!Character.isDigit(c) && !Character.isLetter(c)) {
                return false;
            }
            if (c == 'I' || c == 'i' || c == 'O' || c == 'o' || c == 'Q' || c == 'q') {
                return false;
            }
        }

        /* check if the WMI is one of the known */
        String wmi = data.substring(0, 3);
        if (!isValidWMI(wmi)) {
            return false;
        }

        return true;
    }

    @Override
    public String getRandomKey() {
        return getRandomWMI() + randomUIDGenerator(14, excludedCharacters).toUpperCase();
    }

    private String randomUIDGenerator(int length, char[] excludedCharacters) {
        StringBuilder builder = new StringBuilder();
        char nextRandom;

        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                nextRandom = (char) ( 'a' + random.nextInt(26));
            } else {
                nextRandom = (char) ( '0' + random.nextInt(10));
            }

            if (excludedCharacters != null) {
                if(ArrayUtils.contains(excludedCharacters, nextRandom)) {
                    i--;
                    continue;
                }
            }

            builder.append(nextRandom);
        }

        return builder.toString();

    }

    /**
     * Gets random wmi.
     *
     * @return the random wmi
     */
    public String getRandomWMI() {
        int index = random.nextInt(wmiList.length);
        return this.wmiList[index];
    }

    /**
     * Gets random wmi.
     *
     * @param exceptionWMI the exception wmi
     * @return the random wmi
     */
    public String getRandomWMI(String exceptionWMI) {
        String randomWmi = getRandomWMI();
        if (randomWmi.equals(exceptionWMI)) {
            return getRandomWMI(exceptionWMI);
        }
        return randomWmi;
    }

    /**
     * Is valid wmi boolean.
     *
     * @param wmi the wmi
     * @return the boolean
     */
    public boolean isValidWMI(String wmi) {
        if (wmi.length() != 3) {
            return false;
        }

        return wmiMap.containsKey(wmi.toUpperCase());
    }
}
