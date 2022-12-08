/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.ICD;
import com.ibm.research.drl.dpt.models.ICDFormat;
import com.ibm.research.drl.dpt.util.MapWithRandomPick;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

public class ICDv9Manager implements Manager {

    private static final Collection<ResourceEntry> resourceICDList =
            LocalizationManager.getInstance().getResources(Resource.ICDv9);

    private final MapWithRandomPick<String, ICD> icdByCodeMap;
    private final MapWithRandomPick<String, ICD> icdByNameMap;

    private final static ICDv9Manager ICD_V9_MANAGER = new ICDv9Manager();
    public static ICDv9Manager getInstance() { return ICD_V9_MANAGER;}

    public Collection<ICD> getItemList() {
        return icdByCodeMap.getMap().values();
    }

    /**
     * Instantiates a new Ic dv 9 manager.
     */
    private ICDv9Manager() {
        this.icdByCodeMap = new MapWithRandomPick<>(new HashMap<String, ICD>());
        this.icdByNameMap = new MapWithRandomPick<>(new HashMap<String, ICD>());

        readICDList(resourceICDList);

        this.icdByCodeMap.setKeyList();
        this.icdByNameMap.setKeyList();
    }

    private void readICDList(Collection<ResourceEntry> entries) {

        for(ResourceEntry entry: entries) {
            InputStream inputStream = entry.createStream();

            try (CSVParser parser = Readers.createCSVReaderFromStream(inputStream, ';', '"')) {
                for (CSVRecord record : parser) {
                    String code = record.get(0);
                    String shortName = record.get(1);
                    String fullName = record.get(2);
                    String chapterCode = record.get(3);
                    String chapterName = record.get(4);
                    String categoryCode = record.get(5);
                    String categoryName = record.get(6);

                    ICD ICDv9CodeRepr = new ICD(code, shortName, fullName,
                            chapterCode, chapterName, categoryCode, categoryName, ICDFormat.CODE);
                    ICD ICDv9NameRepr = new ICD(code, shortName, fullName,
                            chapterCode, chapterName, categoryCode, categoryName, ICDFormat.NAME);

                    this.icdByCodeMap.getMap().put(code, ICDv9CodeRepr);
                    this.icdByNameMap.getMap().put(shortName.toUpperCase(), ICDv9NameRepr);
                    this.icdByNameMap.getMap().put(fullName.toUpperCase(), ICDv9NameRepr);
                }
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getRandomKey() {
        return this.icdByCodeMap.getRandomKey();
    }

    /**
     * Lookup icd icd.
     *
     * @param codeOrName the code or name
     * @return the icd
     */
    public ICD lookupICD(String codeOrName) {
        String key = codeOrName.toUpperCase();
        ICD ICDv9;

        if ((ICDv9 = this.icdByCodeMap.getMap().get(key)) != null) {
            return ICDv9;
        }
        else if ((ICDv9 = this.icdByNameMap.getMap().get(key)) != null) {
            return ICDv9;
        }

        return null;
    }

    @Override
    public boolean isValidKey(String codeOrName) {
        return lookupICD(codeOrName) != null;
    }
}
