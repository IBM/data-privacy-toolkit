/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.masking.provider;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractMaskingProvider;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class HadoopDictionaryBasedMaskingProvider extends AbstractMaskingProvider {
    private final List<String> dictionary;

    public HadoopDictionaryBasedMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) throws IOException {
        this.dictionary = loadDictionary(maskingConfiguration.getStringValue("hadoop.dictionary.path"));
        this.random = random;
    }

    private List<String> loadDictionary(String path) throws IOException {
        return new ArrayList<>(new HashSet<>(loadTerms(path)));
    }

    private Collection<? extends String> loadTerms(String path) throws IOException {
        try (
                InputStream inputStream = SparkUtils.createHDFSInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                ) {
            List<String> terms = new ArrayList<>();
            for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                terms.add(line.trim());
            }

            return terms;
        }
    }

    @Override
    public String mask(String value) {
        return dictionary.get(
                random.nextInt(dictionary.size())
        );
    }
}
