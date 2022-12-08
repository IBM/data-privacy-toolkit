/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class QATest {
    private final static String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+=-{}[]:;\"'\\?/<>,.~`";

    @Test
    @Disabled
    public void testPerformanceOnLargeFile() throws Exception {
       
        List<Identifier> identifiers = Arrays.asList(
                new AddressForFreeTextIdentifier() 
        );
        
        for(Identifier identifier: identifiers) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/santonat/dev/cedp/unique_values"));
            
            String line;
            int matches = 0;
            int read = 0;
            long start = System.currentTimeMillis();
            while ((line = bufferedReader.readLine()) != null) {
                matches += identifier.isOfThisType(line) ? 1 : 0;
                read++;
            }
            long end = System.currentTimeMillis();

            System.out.println("identifier: " + identifier.getClass().getSimpleName() 
                    + " read: " + read + ", matches : " + matches + " time (secs)" + ((end - start) / 1000));
            
            bufferedReader.close();
        }
        
    }
    
    @Test
    @Disabled
    public void testPhone() { 
        String value = "1990-04-16";
        
        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();
        
        for(Identifier identifier: identifiers) {
            if (identifier.isOfThisType(value)) {
                System.out.println(identifier.getType().getName() + ":" + identifier.getClass().getCanonicalName());
            }
        }
    }
    @Test
    @Disabled
    public void testPopularNouns() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/popular_nouns.csv");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();
        String line;

        while((line = bufferedReader.readLine()) != null) {
            Set<String> results = new HashSet<>();
            for(Identifier identifier: identifiers) {
                if(identifier.isOfThisType(line)) {
                    results.add(identifier.getType().getName());
                }
            }

            if (!results.isEmpty()) {
                System.out.println(line + ":" + results);
            }
        }
    }

    @Test
    @Disabled
    public void testPopularAdjectives() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/popular_adjectives.csv");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();
        String line;

        while((line = bufferedReader.readLine()) != null) {
            Set<String> results = new HashSet<>();
            for(Identifier identifier: identifiers) {
                if(identifier.isOfThisType(line)) {
                    results.add(identifier.getType().getName());
                }
            }

            if (!results.isEmpty()) {
                System.out.println(line + ":" + results);
            }
        }
    }

    @Test
    public void testEmptyStrings() {

        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();

        String value = "";
        for (Identifier identifier : identifiers) {
            assertFalse(identifier.isOfThisType(value));
        }
    }

    @Test
    public void testOneLetters() {

        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();

        for(int i = 0; i < charset.length(); i++) {
            String value = "" + charset.charAt(i);
            for(Identifier identifier: identifiers) {
                boolean match = identifier.isOfThisType(value);

                if (match && Character.isDigit(charset.charAt(i)) && identifier.getType() == ProviderType.NUMERIC) {
                    continue;
                }

                if (match) {
                    System.out.println("value: " + value + " , identifier: " + identifier.getType().name());
                }

                assertEquals(false, match);
            }
        }

    }

    @Test
    public void testTwoLetters() {

        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();

        for(int i = 0; i < charset.length(); i++) {
            for(int j = 0; j < charset.length(); j++) {
                String value = "" + charset.charAt(i) + charset.charAt(j);

                for (Identifier identifier : identifiers) {
                    boolean match = identifier.isOfThisType(value);

                    if (match) {
                        ProviderType providerType = identifier.getType();
                        if (providerType == ProviderType.COUNTRY || providerType == ProviderType.NUMERIC || providerType == ProviderType.STATES_US) {
                            continue;
                        }

                        if (value.equals("::") && providerType == ProviderType.IP_ADDRESS) {
                            continue;
                        }
                    }

                    if (match) {
                        System.out.println("value: " + value + " , identifier: " + identifier.getType().name());
                    }

                    assertFalse(match);
                }
            }
        }

    }

    @Test
    @Disabled
    public void testMicroBenchmarks() {
        String value = "John";
        
        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();
        
        long beginning = System.currentTimeMillis();
        
        for (final Identifier identifier : identifiers) { 
        
            long start = System.currentTimeMillis();
            int matches = 0;
        
            for(int i = 0; i < 10000000; i++) {
                if (identifier.isOfThisType(value)) {
                    matches++;
                }
            }
            
            long end = System.currentTimeMillis();
            System.out.println(identifier.getClass().getSimpleName() + " : " + (end - start) + " : " + matches);
            
        }

        System.out.println("total time : " + (System.currentTimeMillis() - beginning));
    

    }
}

