/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PluggableRegexIdentifier extends  AbstractRegexBasedIdentifier implements IdentifierWithOffset {
    private final Collection<String> appropriateNames;
    private Collection<Pattern> patterns;
    private final ProviderType providerType;
    private final ValueClass valueClass;
    private final boolean isPOSIndependent;

    /**
     * Instantiates a new Pluggable regex identifier.
     *
     * @param providerTypeName the provider type name
     * @param appropriateNames the appropriate names
     * @param patternStrings   the pattern strings
     * @param valueClass       the value class
     */
    public PluggableRegexIdentifier(String providerTypeName,
                                    Collection<String> appropriateNames,
                                    Collection<String> patternStrings,
                                    ValueClass valueClass) {
        this(providerTypeName, appropriateNames, patternStrings, valueClass, true);
    }

    public PluggableRegexIdentifier(String providerTypeName,
                                    Collection<String> appropriateNames,
                                    Collection<String> patternStrings,
                                    ValueClass valueClass,
                                    boolean isPOSIndependent) {

        this.appropriateNames = appropriateNames;
        this.providerType = ProviderType.valueOf(providerTypeName);
        this.valueClass = valueClass;
        this.patterns = patternStrings.stream().map(Pattern::compile).collect(Collectors.toList());
        this.isPOSIndependent = isPOSIndependent;
    }

    @Override
    protected Collection<Pattern> getPatterns() {
        return this.patterns;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return appropriateNames;
    }

    @Override
    public ProviderType getType() {
        return this.providerType;
    }

    @Override
    public String getDescription() {
        return "Pluggable identifier";
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

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String data) {
        if (!quickCheck(data)) {
            return new Tuple<>(false, null);
        }

        for(Pattern p: this.patterns) {
            Matcher matcher = p.matcher(data);

            if (!matcher.matches()) {
                continue;
            }

            if (matcher.groupCount() == 0) {
                return new Tuple<>(true, new Tuple<>(0, data.length()));
            }

            int begin = matcher.start(1);
            int end = matcher.end(1);

            return new Tuple<>(true, new Tuple<>(begin, end - begin));
        }

        return new Tuple<>(false, null);
    }

    public boolean isPOSIndependent() {return this.isPOSIndependent;}
}
