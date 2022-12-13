/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.IMSI;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.security.SecureRandom;
import java.util.*;

public class IMSIManager extends ResourceBasedManager<IMSI> {
    private final static IMSIManager IMSI_MANAGER_INSTANCE = new IMSIManager();

    public static IMSIManager getInstance() {
        return IMSI_MANAGER_INSTANCE;
    }

    private IMSIManager() {
        super();
    }

    private final SecureRandom random = new SecureRandom();
    private Map<String, List<String>> mccMap;

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.IMSI);
    }

    @Override
    protected boolean appliesToAllCountriesOnly() {
        return true;
    }

    @Override
    protected List<Tuple<String, IMSI>> parseResourceRecord(CSVRecord line, String countryCode) {
        String mcc = line.get(0);
        String mnc = line.get(1);
        String mccmnc = mcc + mnc;

        if (!mccMap.containsKey(mcc)) {
            mccMap.put(mcc, new ArrayList<>());
        }

        mccMap.get(mcc).add(mnc);

        IMSI imsi = new IMSI(mcc, mnc);
        return List.of(new Tuple<>(mccmnc.toUpperCase(), imsi));
    }

    @Override
    public void init() {
        mccMap = new HashMap<>();
    }

    /**
     * Gets random mnc.
     *
     * @param mcc the mcc
     * @return the random mnc
     */
    public String getRandomMNC(String mcc) {
        List<String> mncs = mccMap.get(mcc);
        int index = random.nextInt(mncs.size());
        return mncs.get(index);
    }

    /**
     * Gets random mcc.
     *
     * @return the random mcc
     */
    public String getRandomMCC() {
        String mccmnc = getRandomKey();
        return mccmnc.substring(0, 3);
    }

    /**
     * Is valid mcc boolean.
     *
     * @param mcc the mcc
     * @return the boolean
     */
    public boolean isValidMCC(String mcc) {
        return mccMap.containsKey(mcc);
    }

    /**
     * Is valid imsi boolean.
     *
     * @param imsi the imsi
     * @return the boolean
     */
    public boolean isValidIMSI(String imsi) {
        if (imsi == null || imsi.length() != 15) {
            return false;
        }

        for (int i = 0; i < imsi.length(); i++) {
            if (!Character.isDigit(imsi.charAt(i))) {
                return false;
            }
        }

        String mccmnc1 = imsi.substring(0, 5);
        if (isValidKey(mccmnc1)) {
            return true;
        }

        String mccmnc2 = imsi.substring(0, 6);
        return isValidKey(mccmnc2);
    }

    @Override
    public Collection<IMSI> getItemList() {
        return getValues();
    }
}
