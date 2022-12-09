/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;


import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DictionaryBasedMaskingProvider extends AbstractMaskingProvider {
    private final static Logger log = LogManager.getLogger(DictionaryBasedMaskingProvider.class);

    private final SecureRandom random;
    private final List<String> terms;
    
    public DictionaryBasedMaskingProvider() {
       this(new SecureRandom(), new DefaultMaskingConfiguration()); 
    }
    
    public DictionaryBasedMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }
    
    public DictionaryBasedMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    private List<String> loadTermsFromFile(String dictionaryFilename) throws IOException  {
        List<String> terms = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(dictionaryFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                terms.add(line.trim());
            }
        }
        
        return terms;
    }
    
    public DictionaryBasedMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.random = random;
        String dictionaryFilename = maskingConfiguration.getStringValue("dictionaryBased.mask.filename");
        try {
            this.terms = loadTermsFromFile(dictionaryFilename);
        }
        catch (IOException e) {
            String msg = "unable to load dictionary file: " + dictionaryFilename;
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }
    
    @Override
    public String mask(String identifier) { 
        int randomIndex = this.random.nextInt(this.terms.size());
        return terms.get(randomIndex);
    }
}
