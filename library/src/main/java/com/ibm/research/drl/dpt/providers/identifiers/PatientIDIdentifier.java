/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * The type Patient id identifier.
 */
public class PatientIDIdentifier extends AbstractRegexBasedIdentifier {
    private static final String[] appropriateNames = {"Patient ID", "PatientID"};
    private final Collection<Pattern> patientIDPatterns = new ArrayList<>(Arrays.asList(
        Pattern.compile("^\\d{3}-\\d{3}-\\d{3}-\\d{3}$")
    ));

    @Override
    public ProviderType getType() {
        return ProviderType.EMAIL;
    }

    @Override
    public String getDescription() {
        return "Patient ID identification";
    }

    @Override
    protected Collection<Pattern> getPatterns() {
        return patientIDPatterns;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }
    
    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 15;
    }

    @Override
    public int getMaximumLength() {
        return getMinimumLength();
    }
}
