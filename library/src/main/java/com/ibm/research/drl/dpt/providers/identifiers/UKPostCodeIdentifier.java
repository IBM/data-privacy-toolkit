/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class UKPostCodeIdentifier extends AbstractRegexBasedIdentifier {
    private static final String AREA = "(?:" +
            "(?:A[BL])|" +
            "(?:B[ABDHLNRST]?)|" +
            "(?:C[ABFHMORTVW])|" +
            "(?:D[ADEGHLNTY])|" +
            "(?:E[CHNX]?)|" +
            "(?:F[KY])|" +
            "(?:G[LUY]?)|" +
            "(?:H[ADGPRSUX])|" +
            "(?:I[GPVM])|" +
            "(?:JE)|" +
            "(?:K[ATWY])|" +
            "(?:L[ADELNSU]?)|" +
            "(?:M[EKL]?)|" +
            "(?:N[EGNPRW]?)|" +
            "(?:O[LX])|" +
            "(?:P[AEHLOR])|" +
            "(?:R[GHM])|" +
            "(?:S[AEGKLMNOPRSTWY]?)|" +
            "(?:T[ADFNQRSW])|" +
            "(?:UB)|" +
            "(?:W[ACDFNRSV]?)|" +
            "(?:YO)|" +
            "(?:ZE)" +
            ")";
    private static final String DISTRICT = "(?:" +
            "(?:\\d{1,2})|" +
            "(?:\\d\\w)" +
            ")";
    private static final String SECTOR = "(?:\\d)";
    private static final String UNIT = "(?:\\w{2})";

    private static final String OUTWARD = "(?:" +
            AREA + "\\s?" + DISTRICT +
            ")";

    private static final String INWARD = "(?:" +
            SECTOR + "\\s?" + UNIT +
            ")";

    private final Collection<Pattern> patterns = Collections.singletonList(
            Pattern.compile(OUTWARD + "\\s?" + INWARD, Pattern.CASE_INSENSITIVE)
    );

    @Override
    protected Collection<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.POSTCODE;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT + CharacterRequirements.ALPHA;
    }

    @Override
    public int getMinimumLength() {
        return 7;
    }

    @Override
    public int getMaximumLength() {
        return 8;
    }
}
