/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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

public class HolidayIdentifier extends AbstractIdentifier {
    private final Set<String> terms;
    private final int minimumLength;
    private final int maximumLength;
    
    public HolidayIdentifier() {
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
        try (InputStream inputStream = HolidayIdentifier.class.getResourceAsStream("/holidays.csv")) {
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
            throw new RuntimeException("Unable to load the processed terms", e);
        }
    }

    @Override
    public ProviderType getType(){
        return ProviderType.valueOf("HOLIDAY");
    }

    @Override
    public boolean isOfThisType(String data) {
        return terms.contains(data);
    }

    @Override
    public String getDescription() {
        return "Look up mechanism for holidays";
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

