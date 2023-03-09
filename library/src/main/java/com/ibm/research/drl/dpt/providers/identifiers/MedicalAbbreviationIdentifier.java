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


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MedicalAbbreviationIdentifier extends AbstractIdentifier {
    private final Set<String> terms;
    private final int minimumLength;
    private final int maximumLength;

    public MedicalAbbreviationIdentifier() {
        terms = populateTerms();

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (String key : terms) {
            min = Math.min(min, key.length());
            max = Math.max(max, key.length());
        }

        this.minimumLength = min;
        this.maximumLength = max;
    }

    private Set<String> populateTerms() {
        try (InputStream inputStream = MedicalAbbreviationIdentifier.class.getResourceAsStream("/medical_abbreviations.txt")) {
            CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

            MappingIterator<String[]> termsIterator = mapper.readerFor(
                    String[].class
            ).with(CsvSchema.emptySchema().withoutHeader()).readValues(inputStream);

            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            termsIterator,
                            Spliterator.ORDERED
                    ), true).
                    map(s -> s[0].trim()).
                    filter(((Predicate<String>)String::isEmpty).negate()).
                    collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load medical abbreviations", e);
        }
    }

    @Override
    public ProviderType getType(){
        return ProviderType.valueOf("MEDICAL_ABBREVIATION");
    }

    @Override
    public boolean isOfThisType(String data) {
        return terms.contains(data);
    }

    @Override
    public String getDescription() {
        return "Look up mechanism for medical abbreviations";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public int getMinimumLength() {
        return minimumLength;
    }

    @Override
    public int getMaximumLength() {
        return maximumLength;
    }
}
