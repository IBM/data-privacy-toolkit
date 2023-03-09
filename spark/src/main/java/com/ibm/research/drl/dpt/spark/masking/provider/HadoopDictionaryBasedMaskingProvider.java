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
package com.ibm.research.drl.dpt.spark.masking.provider;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
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


public class HadoopDictionaryBasedMaskingProvider implements MaskingProvider {
    private final List<String> dictionary;
    private final SecureRandom random;

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
