/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocsTest {
    
    public static IPVDataset anonymizeDataset(String filename) throws FileNotFoundException, IOException {
        IPVDataset originalDataset = IPVDataset.load(new FileInputStream(new File(filename)), false, ',', '"', false); //we load the file
        
        GeneralizationHierarchy genderHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER);
        GeneralizationHierarchy raceHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE);
        GeneralizationHierarchy zipcodeHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE);
        ColumnInformation heightInformation = ColumnInformationGenerator.generateNumericalRange(originalDataset, 4, ColumnType.QUASI, 1.0, true);
        
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation(false)); 
        columnInformation.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI, 1.0, -1, true));
        columnInformation.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI, 1.0, -1, true));
        columnInformation.add(new CategoricalInformation(raceHierarchy, ColumnType.QUASI, 1.0, -1, true));
        columnInformation.add(new CategoricalInformation(zipcodeHierarchy, ColumnType.QUASI, 1.0, -1, true));
        columnInformation.add(heightInformation);
        
        int k = 10;
       
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k)); // we add the k-anonymity constraint
        
        AnonymizationAlgorithm anonymizationAlgorithm = new OLA(); //we instantiate and initialize the anonymization algorithm
        anonymizationAlgorithm.initialize(originalDataset, columnInformation, privacyConstraints, null);
        
        IPVDataset anonymizedDataset = anonymizationAlgorithm.apply();

        return anonymizedDataset;
    }
}
