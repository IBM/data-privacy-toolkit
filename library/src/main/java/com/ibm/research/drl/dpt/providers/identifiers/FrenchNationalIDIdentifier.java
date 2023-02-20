package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrenchNationalIDIdentifier extends AbstractIdentifier {
    private static final Pattern pattern = Pattern.compile("([12])\\d{2}(0[1-9]|1[0-2]|2[0-9])(\\d{2}|\\d[a-zA-Z]|\\d{3})(\\d{3}|\\d{2})(\\d{3})\\s?(0[1-9]|[1-8]\\d|9[0-7])");

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("FRANCE_INSEE");
    }

    @Override
    public boolean isOfThisType(String data) {
        data = data.strip();

        if (data.length() < getMinimumLength() || data.length() > getMaximumLength())
            return false;

        Matcher matcher = pattern.matcher(data);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "France National identification number (INSEE)";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return 0;
    }

    @Override
    public int getMinimumLength() {
        return 15;
    }

    @Override
    public int getMaximumLength() {
        return 15;
    }
}
