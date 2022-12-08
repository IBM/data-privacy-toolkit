/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.CreditCardType;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreditCardTypeManager extends ResourceBasedManager<CreditCardType> {
    private static final long serialVersionUID = 2610710155976479839L;
    private static final CreditCardTypeManager CREDIT_CARD_TYPE_MANAGER = new CreditCardTypeManager();

    public static CreditCardTypeManager getInstance() {return CREDIT_CARD_TYPE_MANAGER;}

    private CreditCardTypeManager() {super();}

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.CREDIT_CARD_TYPE);
    }

    @Override
    protected List<Tuple<String, CreditCardType>> parseResourceRecord(CSVRecord line, String countryCode) {
        String cctype = line.get(0);
        String key = cctype.toUpperCase();

        CreditCardType creditCardType = new CreditCardType(cctype);
        return Arrays.asList(new Tuple<>(key, creditCardType));
    }

    @Override
    public Collection<CreditCardType> getItemList() {
        return getValues();
    }
}
