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

package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithm;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.ZIPCodeMaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.mondrian.Mondrian;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DifferentialPrivacyTest {
    private static String quasiSelection[] = {
            "", // Patient Id
            "", // First Name
            "", // Surname
            "", // Email
            "YOB",
            "", //"ZIPCODE",
            "GENDER",
            "RACE",
            "", //"RELIGION",
            "MARITAL_STATUS",
            "", // ICD-9
            "HEIGHT",
            "", // Weight
            "", // Height for error calculation
    };

    @Disabled
    @Test
    public void testNewDifferentialPrivacyClass() throws Exception {
        double[] epsilonValues = {.01, .05, .1, .5, 1.0, 2.0, 4.0, 8.0, 16.0};
        int[] kValues = {10};

        // Adult
//        final int heightIndex = 11;
        final String filename = "/random1_height_weight_v2.txt";

////      Florida
//        final String filename = "/florida_sample_height_weight_with_id.txt";
//        final int heightIndex = 6;
//        String quasiSelection[] = {
//                "", // First Name
//                "", // Surname
//                "ZIPCODE",
//                "GENDER_Flor",
//                "YOB",
//                "RACE_Flor",
//                "", //"HEIGHT",
//                "", // Weight
//                "", // Height for error
//                "", // ID
//        };

////      Michigan
//        final String filename = "/michigan_sample_height_weight_with_id.csv";
//        final int heightIndex = 5;
//        String quasiSelection[] = {
//                "", // First Name
//                "", // Surname
//                "YOB",
//                "GENDER_Mich",
//                "ZIPCODE",
//                "", //"HEIGHT", // Weight
//                "", // Height
//                "", // Weight for error
//                "", // ID
//        };

        final int runs = 30;
        final boolean runOLA = false;

        List<ColumnInformation> columnInformation = new ArrayList<>();

        for(String quasi: quasiSelection) {
            switch (quasi) {
                case "":
                    columnInformation.add(new DefaultColumnInformation());
                    break;
                case "GENDER_Mich":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U", "1", "2")), ColumnType.QUASI));
                    break;
                case "GENDER_Flor":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI));
                    break;
                case "GENDER_Colo":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("Male", "Female", "Unknown")), ColumnType.QUASI));
                    break;
                case "RACE_Flor":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")), ColumnType.QUASI));
                    break;
                case "ZIPCODE":
                    if (!runOLA) {
                        columnInformation.add(new CategoricalInformation(ZIPCodeMaterializedHierarchy.getInstance(), ColumnType.QUASI));
                        break;
                    }
                case "HEIGHT":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf(quasi)), ColumnType.E_QUASI));
                    break;
                default:
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf(quasi)), ColumnType.QUASI));
                    break;
            }
        }

        OLAOptions olaOptions = new OLAOptions(5.0d);

        List<DPMechanism> mechanisms = Arrays.asList(new Laplace(), new BoundedLaplace(), new TruncatedLaplace());
        DPError errorFunction = new DPErrorAverageRelative();
//        long startTime = System.currentTimeMillis();

        for (DPMechanism mechanism: mechanisms) {
            System.out.println("\n" + mechanism.getName());

            for (int k : kValues) {
                System.out.printf("k = %d", k);

                IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream(filename), false, ',', '"', false);
                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                AnonymizationAlgorithm algorithm  = runOLA ?
                        new OLA().initialize(original, columnInformation, privacyConstraints, olaOptions) :
                        new Mondrian().initialize(original, columnInformation, privacyConstraints, null);

                final IPVDataset anonymizedDataset = algorithm.apply();

                System.out.println("\nEps\tAbs\tRel");

                DifferentialPrivacyMechanismOptions dpOptions = new DifferentialPrivacyMechanismOptions(mechanism);
                dpOptions.getBoundsFromData();
                dpOptions.DPPerEquivalenceClass(true);

                for (double e : epsilonValues) {
                    double averageRelativeError = 0.0;

                    for (int i=0; i<runs; i++) {
                        dpOptions.setEpsilon(e);

                        DifferentialPrivacy differentialPrivacy = new DifferentialPrivacy();
                        differentialPrivacy.initialize(anonymizedDataset, columnInformation, null, dpOptions);
                        differentialPrivacy.apply();

                        averageRelativeError += errorFunction.reportError(differentialPrivacy);
//                        System.out.printf("Run %2d, post-error, %8d\n", i, System.currentTimeMillis()  - startTime);
                    }

                    System.out.printf("%f\t%f", e, averageRelativeError/runs);
                    System.out.println("");
                }
            }
        }
    }



    @Disabled
    @Test
    public void testCategoricalMechanism() throws Exception {
        double[] epsilonValues = {.01, .05, .1, .5, 1.0, 2.0, 4.0, 8.0, 16.0};
        int[] kValues = {10};
        final String filename;

        // Adult
        filename = "/random1_height_weight_v2.txt";

////      Florida
//        filename = "/florida_sample_height_weight_with_id.txt";
//        final int heightIndex = 6;
//        String quasiSelection[] = {
//                "", // First Name
//                "", // Surname
//                "ZIPCODE",
//                "GENDER_Flor",
//                "YOB",
//                "RACE_Flor",
//                "", //"HEIGHT",
//                "", // Weight
//                "", // Height for error
//                "", // ID
//        };

////      Michigan
//        filename = "/michigan_sample_height_weight_with_id.csv";
//        final int heightIndex = 5;
//        String quasiSelection[] = {
//                "", // First Name
//                "", // Surname
//                "YOB",
//                "GENDER_Mich",
//                "ZIPCODE",
//                "", //"HEIGHT", // Weight
//                "", // Height
//                "", // Weight for error
//                "", // ID
//        };

        final int runs = 30;
        final boolean runOLA = false;

        List<ColumnInformation> columnInformation = new ArrayList<>();

        for(String quasi: quasiSelection) {
            switch (quasi) {
                case "":
                    columnInformation.add(new DefaultColumnInformation());
                    break;
                case "GENDER_Mich":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U", "1", "2")), ColumnType.QUASI));
                    break;
                case "GENDER_Flor":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI));
                    break;
                case "GENDER_Colo":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("Male", "Female", "Unknown")), ColumnType.QUASI));
                    break;
                case "RACE_Flor":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")), ColumnType.QUASI));
                    break;
                case "MARITAL_STATUS":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf(quasi)), ColumnType.E_QUASI));
                    break;
                case "ZIPCODE":
                    if (!runOLA) {
                        columnInformation.add(new CategoricalInformation(ZIPCodeMaterializedHierarchy.getInstance(), ColumnType.QUASI));
                        break;
                    }
                case "HEIGHT":
//                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf(quasi)), ColumnType.QUASI));
                    columnInformation.add(new DefaultColumnInformation());
                    break;
                default:
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf(quasi)), ColumnType.QUASI));
                    break;
            }
        }

        OLAOptions olaOptions = new OLAOptions(5.0d);

        DPMechanism mechanism = new Categorical();
        System.out.println(mechanism.getName());

        DifferentialPrivacyMechanismOptions dpOptions = new DifferentialPrivacyMechanismOptions(mechanism);
        dpOptions.getBoundsFromData();
        dpOptions.DPPerEquivalenceClass(true);
        dpOptions.setHierarchy(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf("MARITAL_STATUS")));
        DPError errorFunction = new DPErrorDiscrete();

        for (int k : kValues) {
            System.out.printf("k = %d", k);

            IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream(filename), false, ',', '"', false);
            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            AnonymizationAlgorithm algorithm  = runOLA ?
                    new OLA().initialize(original, columnInformation, privacyConstraints, olaOptions) :
                    new Mondrian().initialize(original, columnInformation, privacyConstraints, null);


            final IPVDataset anonymizedDataset = algorithm.apply();

            System.out.println("\nEps\tAbs\tRel");

            for (double e : epsilonValues) {
                double error = 0.0;
                for (int i=0; i<runs; i++) {
                    dpOptions.setEpsilon(e);

                    DifferentialPrivacy differentialPrivacy = new DifferentialPrivacy();
                    differentialPrivacy.initialize(anonymizedDataset, columnInformation, null, dpOptions);
                    differentialPrivacy.apply();

                    error += errorFunction.reportError(differentialPrivacy);
                }

                System.out.printf("%f\t%f\n", e, error / runs);
            }
        }

    }

    @Test
    @Disabled
    public void testDiffPrivacyMean() throws Exception {
        double[] epsilonValues = {0.5, 1.0, 2.0, 4.0, 8.0, 16.0};
        int[] kValues = {2, 5, 10, 20, 50, 100};
        final String filename;

//        filename = "/random1_height_weight_v2.txt";

////      Florida
//        filename = "/florida_sample_height_weight.txt";
//        String quasiSelection[] = {
//                "", // First Name
//                "", // Surname
//                "ZIPCODE",
//                "GENDER_Flor",
//                "YOB",
//                "RACE_Flor",
//                "", //"HEIGHT",
//                "", // Weight
//                "", // Height for error
//        };

//      Colorado
        filename = "/colorado_sample_height_weight.txt";
        String quasiSelection[] = {
                "", // First Name
                "", // Surname
                "ZIPCODE",
                "GENDER_Colo",
                "YOB",
                "HEIGHT", //"HEIGHT",
                "", // Weight
                "", // Height for error
        };

////      Michigan
//        filename = "/michigan_sample_height_weight.csv";
//        String quasiSelection[] = {
//                "", // First Name
//                "", // Surname
//                "YOB",
//                "GENDER_Mich",
//                "ZIPCODE",
//                "", //"HEIGHT",
//                "HEIGHT", // Weight
//                "", // Height for error
//        };

        final boolean runOLA = false;
        int runs = 30;
        double suppressionOLA = 5.0;

        List<ColumnInformation> columnInformation = new ArrayList<>();

        for(String quasi: quasiSelection) {
            switch (quasi) {
                case "": columnInformation.add(new DefaultColumnInformation()); break;
                case "GENDER_Mich": columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U", "1", "2")), ColumnType.QUASI)); break;
                case "GENDER_Flor": columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI)); break;
                case "GENDER_Colo": columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("Male", "Female", "Unknown")), ColumnType.QUASI)); break;
                case "RACE_Flor": columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")), ColumnType.QUASI)); break;
                case "HEIGHT":
                    columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf(quasi)), ColumnType.E_QUASI));
                    break;
                case "ZIPCODE":
                    if (!runOLA) {
                        columnInformation.add(new CategoricalInformation(ZIPCodeMaterializedHierarchy.getInstance(), ColumnType.QUASI));
                        break;
                    }
                default: columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.valueOf(quasi)), ColumnType.QUASI)); break;
            }
        }

        OLAOptions olaOptions = new OLAOptions(suppressionOLA);

        System.out.printf("Runs: %d\nOLA Suppression: %f%%\n", runs, (runOLA) ? suppressionOLA : Double.NaN);

//        System.out.print((runOLA) ? "\nk/e\tSupp" : "\nk/e");
        System.out.print("\nk/e");
        for (double e : epsilonValues) {
            System.out.print("\t" + Double.toString(e));
//            System.out.print("\tϵ=" + Double.toString(e));
        }
        System.out.println("");

        DPMechanism mechanism = new Laplace();
        DPError errorFunction = new DPErrorECMean();
        DifferentialPrivacyMechanismOptions dpOptions = new DifferentialPrivacyMechanismOptions(mechanism);
        dpOptions.getBoundsFromData();
        dpOptions.DPPerEquivalenceClass(true);

        for (int k : kValues) {
            IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream(filename), false, ',', '"', false);

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            AnonymizationAlgorithm algorithm  = runOLA ?
                    new OLA().initialize(original, columnInformation, privacyConstraints, olaOptions) :
                    new Mondrian().initialize(original, columnInformation, privacyConstraints, null);
            final IPVDataset anonymizedDataset = algorithm.apply();

            System.out.printf("%d", k);

            for (double e : epsilonValues) {
                double totalRelMeanError = 0.0;

                dpOptions.setEpsilon(e);

                for (int r=0;r<runs;r++) {
                    DifferentialPrivacy differentialPrivacy = new DifferentialPrivacy();
                    differentialPrivacy.initialize(anonymizedDataset, columnInformation, null, dpOptions);
                    differentialPrivacy.apply();

                    totalRelMeanError += errorFunction.reportError(differentialPrivacy);
                }

                System.out.printf("\t%f", totalRelMeanError / runs);
            }

            System.out.println("");
        }
    }
}
