/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.DistinctLDiversity;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.ZIPCodeCompBasedHierarchy;
import com.ibm.research.drl.dpt.anonymization.informationloss.CategoricalPrecision;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationLossResult;
import com.ibm.research.drl.dpt.anonymization.mondrian.MondrianTest;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class OLATest {
    private MaterializedHierarchy dateHierarchy;
    private MaterializedHierarchy genderHierarchy;
    private MaterializedHierarchy ageHierarchy;

    @BeforeEach
    public void setUp() {
        dateHierarchy = new MaterializedHierarchy();
        genderHierarchy = new MaterializedHierarchy();
        ageHierarchy = new MaterializedHierarchy();
    }

    private List<String> getLeaves(List<List<String>> values) {
        List<String> terms = new ArrayList<>();
        for(List<String> l: values) {
            terms.add(l.get(0));
        }
        return terms;
    }

    private IPVDataset generateRandomDataset(int numberOfRows) {
        IPVDataset IPVDataset = new IPVDataset(3);

        List<String> dates = getLeaves(dateHierarchy.getTerms());
        List<String> genders = getLeaves(genderHierarchy.getTerms());
        List<String> ages = getLeaves(ageHierarchy.getTerms());

        Random random = new Random();

        for(int i = 0; i < numberOfRows; i++) {
            List<String> row = new ArrayList<>();

            int di = random.nextInt(dates.size());
            row.add(dates.get(di));

            int gi = random.nextInt(genders.size());
            row.add(genders.get(gi));

            int ai = random.nextInt(ages.size());
            row.add(ages.get(ai));

            IPVDataset.addRow(row);
        }

        return IPVDataset;
    }

    private void generateAgeHierarchy(){

        for(int age = 0; age < 99; age++) {
            String[] values = new String[5];
            values[0] = "" + age;

            int age5low = age - age%5;
            int age5high = age5low + 5;
            values[1] = age5low + "-" + age5high;

            int age10low = age - age%10;
            int age10high = age10low + 10;
            values[2] = age10low + "-" + age10high;

            values[3] = (age < 50) ? "0-49" : "50-100";
            values[4] = "0-100";

            ageHierarchy.add(values);
        }
    }

    private void generateDateHierarchy() throws Exception {
        String dt = "01/01/2008";  // Start date
        SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat my = new SimpleDateFormat("MM/yyyy");
        SimpleDateFormat y = new SimpleDateFormat("yyyy");

        for(int i = 0; i < 1000; i++) {
            Calendar c = Calendar.getInstance();
            c.setTime(dmy.parse(dt));
            c.add(Calendar.DATE, i);  // number of days to add

            String[] values = new String[4];
            values[0] = dmy.format(c.getTime());
            values[1] = my.format(c.getTime());
            values[2] = y.format(c.getTime());
            values[3] = "2000-2100";

            dateHierarchy.add(values);
        }
    }

    @Test
    @Disabled
    public void testPerformance() throws Exception {
        int[] kValues = new int[]{2, 3, 4};
        int[] nValues = new int[]{1000, 10000, 100000, 1000000}; //, 10000, 100000, 1000000};

        generateDateHierarchy();

        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        generateAgeHierarchy();

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));

        for(int n: nValues) {
            for(int k: kValues) {

                IPVDataset original = generateRandomDataset(n);

                OLAOptions olaOptions = new OLAOptions(1.0d);

                long startMillis = System.currentTimeMillis();

                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                OLA ola = new OLA();
                ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

                IPVDataset anonymized = ola.apply();

                int anonymizedNumberOfRows = anonymized.getNumberOfRows();
                //with 10% suppression rate, we must have at least 10*0.9 rows on the final dataset
                assertTrue(anonymizedNumberOfRows >= (0.9*n));

                long diff = System.currentTimeMillis() - startMillis;

                int nodesChecked = ola.getNodesChecked();
                int totalNodes = ola.getTotalNodes();

                System.out.println(String.format("n=%d: k=%d: OLA took %d milliseconds (%d out of %d) projected worst: %.0f",
                        n, k,  diff, nodesChecked, totalNodes, diff * ((double)totalNodes/(double)nodesChecked)));
            }
        }
    }

    @Test
    public void testOLA() throws Exception {
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        ageHierarchy.add("13", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("18", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("19", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("21", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("22", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("23", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testOLA.csv"), false, ',', '"', false);

        OLAOptions olaOptions = new OLAOptions(20.0d);

        int k = 3;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        int n = anonymized.getNumberOfRows();
        //with 20% suppression rate, we must have at least 10*0.8 rows on the final dataset
        assertTrue(n >= 8);

        assertThat(ola.reportSuppressionRate(), lessThanOrEqualTo(20.0));

        int na = anonymized.getNumberOfColumns();
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);
    }

    @Test
    public void testOLAWithHeader() throws Exception {
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        ageHierarchy.add("13", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("18", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("19", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("21", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("22", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("23", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testOLA_with_header.csv"), true, ',', '"', false);

        OLAOptions olaOptions = new OLAOptions(20.0d);

        int k = 3;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        assertThat(anonymized.hasColumnNames(), is(original.hasColumnNames()));
        assertThat(anonymized.getNumberOfColumns(), is(original.getNumberOfColumns()));

        List<String> headers = anonymized.getSchema().getFields().stream().map(IPVSchemaField::getName).collect(Collectors.toList());

        assertThat(headers.size(), is(3));
        assertThat(headers, hasItems("date", "sex", "age"));

        int n = anonymized.getNumberOfRows();
        //with 20% suppression rate, we must have at least 10*0.8 rows on the final dataset
        assertTrue(n >= 8);

        assertThat(ola.reportSuppressionRate(), lessThanOrEqualTo(20.0));

        int na = anonymized.getNumberOfColumns();
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);
    }

    @Test
    public void testOLAWithExplorationLimits() throws Exception {
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        ageHierarchy.add("13", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("18", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("19", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("21", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("22", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("23", "20-24", "20-29", "0-49", "0-99");

        int maxGenderLevel = 0;
        int maxAgeLevel = 2;
        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI, 1.0, maxGenderLevel));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI, 1.0, maxAgeLevel));

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testOLA.csv"), false, ',', '"', false);

        OLAOptions olaOptions = new OLAOptions(20.0d);

        int k = 3;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);

        int n = anonymized.getNumberOfRows();
        //with 20% suppression rate, we must have at least 10*0.8 rows on the final dataset
        assertTrue(n >= 8);

        LatticeNode bestNode = ola.reportBestNode();

        assertEquals(0, bestNode.getValues()[0]);
        assertThat(bestNode.getValues()[2], lessThanOrEqualTo(maxAgeLevel));
    }

    @Test
    public void testOLAComputationalBased() throws Exception {
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        ageHierarchy.add("13", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("18", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("19", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("21", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("22", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("23", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(new ZIPCodeCompBasedHierarchy(), ColumnType.QUASI));

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testOLACompBased.csv"), false, ',', '"', false);

        OLAOptions olaOptions = new OLAOptions(10.0d);

        int k = 3;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);

        int n = anonymized.getNumberOfRows();
        //with 20% suppression rate, we must have at least 10*0.8 rows on the final dataset
        //assertTrue(n >= 8);

        assertThat(ola.reportSuppressionRate(), lessThanOrEqualTo(20.0));

        int na = anonymized.getNumberOfColumns();
    }

    @Test
    public void testOLAWithLDiversityDistinct() throws Exception {
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        ageHierarchy.add("13", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("18", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("19", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("21", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("22", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("23", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));
        columnInformationList.add(new SensitiveColumnInformation());

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testOLALDiversity.csv"), false, ',', '"', false);

        OLAOptions olaOptions = new OLAOptions(20.0d);

        int k = 3;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));
        privacyConstraints.add(new DistinctLDiversity(2));

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformationList, k);

        assertThat(ola.reportSuppressionRate(), lessThanOrEqualTo(20.0));

        List<Partition> partitions = ola.getAnonymizedPartitions();
        for(Partition partition: partitions) {
            if (!partition.isAnonymous()) {
                continue;
            }
            
            Set<String> uniqueValues = new HashSet<>();

            IPVDataset members = partition.getMember();
            for(int i = 0; i < members.getNumberOfRows(); i++) {
                uniqueValues.add(members.get(i, 3).toUpperCase());
            }

            assertThat(uniqueValues.size() , greaterThanOrEqualTo(2));
        }
    }

    @Test
    @Disabled
    public void testDemoDatasetOLA() throws Exception {
        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ICDv9), ColumnType.QUASI));

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/random1.txt"), false, ',', '"', false);

        OLAOptions olaOptions = new OLAOptions(15.0d);

        int k = 10;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        System.out.println("suppression rate: " + ola.reportSuppressionRate());
        System.out.println("best node: " + ola.reportBestNode());
    }

    @Test
    public void testOLASingleAttribute() throws Exception {
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/olaSingleAttributeData.csv"), false, ',', '"', false);

        OLAOptions olaOptions = new OLAOptions(0.0d);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        assertEquals(0.0, ola.reportSuppressionRate(), 0.01);

        assertEquals(ola.reportBestNode(), new LatticeNode(new int[]{0}));
    }

    @Test
    public void testPartitionAnonymousFlags() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1.txt"), false, ',', '"', false);
        
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ICDv9), ColumnType.QUASI));

        int k = 10;
        
        OLAOptions olaOptions = new OLAOptions(15.0d);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();
        ValidationUtils.validateIsKAnonymous(anonymized, columnInformation, k);

        List<Partition> partitions = ola.getOriginalPartitions();

        for(Partition partition: partitions) {
            if (partition.isAnonymous()) {
                assertThat(partition.size(), greaterThanOrEqualTo(k));
            } else {
                assertThat(partition.size(), lessThan(k));
            }
        }
    }

    @Test
    @Disabled
    public void testDemo() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1.txt"), false, ',', '"', false);

        System.out.println("original: " + original.getNumberOfRows());

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ICDv9), ColumnType.QUASI));

        OLAOptions olaOptions = new OLAOptions(15.0d);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(10));

        OLA ola = new OLA();
        ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        System.out.println("suppression rate: " + ola.reportSuppressionRate());
        System.out.println("best node: " + ola.reportBestNode());

        System.out.println("anonymized: " + anonymized.getNumberOfRows());
    }

    @Test
    @Disabled
    public void testDumpForDecoy() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1.txt"), false, ',', '"', false);

        System.out.println("original: " + original.getNumberOfRows());

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        //columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ICDv9), ColumnType.QUASI));

        int[] kValues = new int[]{2, 5, 10, 20, 50};
        double[] suppressionRates = new double[]{0.0, 2.0, 5.0, 10.0, 15.0};
        
        for(int k: kValues) {
            for(double suppressionRate: suppressionRates) {
                OLAOptions olaOptions = new OLAOptions(suppressionRate);

                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                OLA ola = new OLA();
                ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

                IPVDataset anonymized = ola.apply();

                System.out.println("suppression rate: " + ola.reportSuppressionRate());
                System.out.println("best node: " + ola.reportBestNode());
                System.out.println("anonymized: " + anonymized.getNumberOfRows());

                String filename = "/tmp/random1_4q_" + k + "_" + suppressionRate;
                CSVPrinter writer = new CSVPrinter(new FileWriter(filename), CSVFormat.RFC4180.withDelimiter(',').withQuoteMode(QuoteMode.MINIMAL));

                for(int i = 0; i < anonymized.getNumberOfRows(); i++) {
                    writer.printRecord(anonymized.getRow(i));
                }

                writer.close();
            }
        }
    }

    @Test
    public void verifyNoTransformationIsAppliedIfNoQuasiArePresent() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/random1.txt")) {
            IPVDataset original = IPVDataset.load(is, false, ',', '"', false);
            OLA ola = new OLA();

            List<ColumnInformation> columnInformations = new ArrayList<>();
            for (int i = 0; i < original.getNumberOfColumns(); ++i) {
                columnInformations.add(new DefaultColumnInformation());
            }

            ola.initialize(original, columnInformations, Arrays.asList(new KAnonymity(10)), new OLAOptions(0.0));

            IPVDataset anonymized = ola.apply();

            assertNotNull(anonymized);
            assertEquals(anonymized.getNumberOfColumns(), original.getNumberOfColumns());
            assertEquals(anonymized.getNumberOfRows(), original.getNumberOfRows());
        }
    }
    
    @Test
    @Disabled
    public void testOLAMultipleDimensions() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);

        System.out.println("original: " + original.getNumberOfRows());

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        int[] kValues = {10};
        double[] suppressionValues = {11.0};

        for(int k: kValues) {
            for (double suppression : suppressionValues) {
                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                OLA ola = new OLA();
                OLAOptions olaOptions = new OLAOptions(suppression);
                ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

                IPVDataset anonymized = ola.apply();

                CategoricalPrecision cp = new CategoricalPrecision();
                cp.initialize(original, anonymized, null, null, columnInformation, null);

                List<InformationLossResult> lossResults = cp.reportPerQuasiColumn();

                System.out.printf("k: %d, suppression: max %f eff %f, best node: %s loss-yob: %f loss-gender: %f loss-race: %f loss-marital: %f%n",
                        k, suppression, ola.reportSuppressionRate(),
                        ola.reportBestNode(), lossResults.get(0).getValue(), lossResults.get(1).getValue(), lossResults.get(2).getValue(), lossResults.get(3).getValue());
            }
        }
    }

    @Test
    @Disabled
    public void testOLAMultipleDimensionsWithHeight() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);

        System.out.println("original: " + original.getNumberOfRows());

        GeneralizationHierarchy heightHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.HEIGHT);
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(heightHierarchy, ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());

        int[] kValues = {10};
        double[] suppressionValues = {100.0};

        for(int k: kValues) {
            for (double suppression : suppressionValues) {
                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                OLA ola = new OLA();
                OLAOptions olaOptions = new OLAOptions(suppression);
                ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

                IPVDataset anonymized = ola.apply();

                CategoricalPrecision cp = new CategoricalPrecision();
                cp.initialize(original, anonymized, null, null, columnInformation, null);

                List<InformationLossResult> lossResults = cp.reportPerQuasiColumn();

                System.out.println("tags: " + ola.getTagsPerformed());
                System.out.println("nodes checked: " + ola.getNodesChecked());
                
                System.out.printf("k: %d, suppression: max %f eff %f, best node: %s loss-yob: %f loss-gender: %f loss-race: %f loss-marital: %f%n",
                        k, suppression, ola.reportSuppressionRate(),
                        ola.reportBestNode(), lossResults.get(0).getValue(), lossResults.get(1).getValue(), lossResults.get(2).getValue(), lossResults.get(3).getValue());
            }
        }
    }

    
    
    @Test
    public void testOLAOriginalIsNotMutated() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);

        GeneralizationHierarchy heightHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.HEIGHT);
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(heightHierarchy, ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());

        int[] kValues = {2, 5, 10};
        double[] suppressionValues = {10.0};
        
        IPVDataset reloaded = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);

        for(int k: kValues) {
            for (double suppression : suppressionValues) {
                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                OLA ola = new OLA();
                OLAOptions olaOptions = new OLAOptions(suppression);
                ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

                IPVDataset anonymized = ola.apply();
                assertTrue(anonymized.getNumberOfRows() > 0);
                
                ValidationUtils.mustBeTheSame(original, reloaded);
            }
        }
    }

    

    @Test
    public void testPartitions() throws Exception {
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false);
        Map<String, String> indexMap = MondrianTest.createIndexMap(originalDataset, 0);

        GeneralizationHierarchy raceHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE);
        
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation()); //index, 0
        columnInformation.add(new DefaultColumnInformation()); // 1
        columnInformation.add(new DefaultColumnInformation()); // 2
        columnInformation.add(new DefaultColumnInformation()); // 3
        columnInformation.add(new DefaultColumnInformation()); // 4
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode, 6
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(raceHierarchy, ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); // 9
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        int k = 2;
        double suppression = 0.0;

        int raceIndex = 8;
                
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLA ola = new OLA();
        OLAOptions olaOptions = new OLAOptions(suppression);
        ola.initialize(originalDataset, columnInformation, privacyConstraints, olaOptions);

        IPVDataset anonymizedDataset = ola.apply();
        ValidationUtils.validateIsKAnonymous(anonymizedDataset, columnInformation, k);

        int raceGeneralization = ola.reportBestNode().getValues()[2];
        assertTrue(raceGeneralization > 0); // make sure race is anonymized        
        
        for(int i = 0; i < anonymizedDataset.getNumberOfRows(); i++) {
            String raceValue = anonymizedDataset.get(i, raceIndex);
            int appliedLevel = raceHierarchy.getNodeLevel(raceValue);
            assertEquals(appliedLevel, raceGeneralization);
        }
        
        List<Partition> originalPartitions = ola.getOriginalPartitions();
        List<Partition> anonymizedPartitions = ola.getAnonymizedPartitions();
       
        assertTrue(originalPartitions.size() > 0);
        assertEquals(originalPartitions.size(), anonymizedPartitions.size());
        
        int numPartitions = originalPartitions.size();
        
        for(int i = 0; i < numPartitions; i++) {
            Partition originalPartition = originalPartitions.get(i);
            Partition anonymizedPartition = anonymizedPartitions.get(i);
            
            assertEquals(originalPartition.size(), anonymizedPartition.size());
            assertEquals(originalPartition.isAnonymous(), anonymizedPartition.isAnonymous());
            
            int numRows = originalPartition.size();
            
            for(int j = 0; j < numRows; j++) {
                String originalIndex = originalPartition.getMember().get(j, 0);
                List<String> originalRow = originalPartition.getMember().getRow(j);
                assertEquals(indexMap.get(originalIndex), MondrianTest.createRowRepresentation(originalRow));
                
                String anonIndex = anonymizedPartition.getMember().get(j, 0);
                
                assertEquals(originalIndex, anonIndex);
                
                String originalRace =  originalPartition.getMember().get(j, raceIndex);
                String anonymizedRace =  anonymizedPartition.getMember().get(j, raceIndex);

                assertNotEquals(originalRace, anonymizedRace);
            }
        }
    }
    
    @Test
    @Disabled
    public void testChecker() throws Exception {
        //0:1:1:0:1 

        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);
        
        GeneralizationHierarchy heightHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.HEIGHT);
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(heightHierarchy, ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        
        IPVDataset anonymized = DatasetGeneralizer.generalize(original, columnInformation, new int[] { 0, 1, 1, 0, 1});
        
        int k = 10;
        double suppression = 50;
        
        int suppressed = 0;
        
        Map<String, Integer> anonEQCounters = AnonymizationUtils.generateEQCounters(anonymized, columnInformation);
        
        for(Map.Entry<String, Integer> entry: anonEQCounters.entrySet()) {
            Integer value = entry.getValue();
            
            if (value < k) {
                suppressed += value;
            }
        }

        System.out.println("suppressed: " + suppressed + " = " + ((double) suppressed / (double) original.getNumberOfRows()));
        
    }
    
    @Test
    @Disabled
    public void testPerformanceFlorida() throws Exception {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                ColumnType.QUASI, true));

        double suppression = 10.0;
        int k = 2;
        
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        long start = System.currentTimeMillis();
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/florida_12M.txt"), false, ',', '"', false);
        System.out.println("loading done");
        System.out.println("loading finished in " + (System.currentTimeMillis() - start));
        
        OLA ola = new OLA();
        OLAOptions olaOptions = new OLAOptions(suppression);
        ola.initialize(originalDataset, columnInformation, privacyConstraints, olaOptions);

        start = System.currentTimeMillis();
        
        IPVDataset anonymizedDataset = ola.apply();
        System.out.println(anonymizedDataset.getNumberOfRows());
        
        long end = System.currentTimeMillis();

        System.out.println(ola.getNodesChecked());
        System.out.println(ola.reportBestNode());
        System.out.println("total time in milliseconds: " + (end - start));
    }

    @Test
    @Disabled
    public void testClientILBug20180208Good() throws Exception {
        //this is working
        String goodConfName = "/a71577ba-2eda-47d3-b55b-613e34a3d3e9.json";

        AnonymizationOptions goodOptions = new ObjectMapper().readValue(this.getClass().getResourceAsStream(goodConfName), AnonymizationOptions.class);

        OLA ola = new OLA();
        OLAOptions olaOptions = new OLAOptions(goodOptions.getSuppressionRate());

        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/a71577ba-2eda-47d3-b55b-613e34a3d3e9.csv"), true, ',', '"', false);
        ola.initialize(dataset, goodOptions.getColumnInformation(), goodOptions.getPrivacyConstraints(), olaOptions);

        IPVDataset anonymizedDataset = ola.apply();

        CategoricalPrecision cp = new CategoricalPrecision();
        cp.initialize(dataset, anonymizedDataset, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(),
                goodOptions.getColumnInformation(), null);
    }
    
    @Test
    public void testOLAAgesClientIL() throws Exception {
        
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/olaAges.csv"), true, ',', '"', false);
        
        JsonNode hierarchiesNode = (new ObjectMapper()).readTree(this.getClass().getResourceAsStream("/olaAgesLevels.json")).get("hierarchies");
       
        Map<String, GeneralizationHierarchy> hierarchyMap = AnonymizationOptions.hierarchiesFromJSON(hierarchiesNode);
        
        MaterializedHierarchy hierarchy = (MaterializedHierarchy)hierarchyMap.get("age");
        
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));
        
        List<PrivacyConstraint> privacyConstraints = Arrays.asList(new KAnonymity(6));
        
        OLA ola = new OLA();
        OLAOptions olaOptions = new OLAOptions(2.0);
        
        ola.initialize(dataset, columnInformation, privacyConstraints, olaOptions);
        
        IPVDataset anonymized = ola.apply();
        
        assertEquals(0.0, ola.reportSuppressionRate(), 0.000001);
    }

    @Test
    @Disabled
    public void testClientILBug20180208Bad() throws Exception {
        // this is crashing
        String badConfName = "/2bac2243-fcfc-455d-a096-33b53d795179.json";
        AnonymizationOptions badOptions = new ObjectMapper().readValue(this.getClass().getResourceAsStream(badConfName), AnonymizationOptions.class);

        OLA ola = new OLA();
        OLAOptions olaOptions = new OLAOptions(badOptions.getSuppressionRate());

        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/2bac2243-fcfc-455d-a096-33b53d795179.csv"), true, ',', '"', false);
        ola.initialize(dataset, badOptions.getColumnInformation(), badOptions.getPrivacyConstraints(), olaOptions);

        IPVDataset anonymizedDataset = ola.apply();
        System.out.println(anonymizedDataset.getNumberOfRows());

        CategoricalPrecision cp = new CategoricalPrecision();
        cp.initialize(dataset, anonymizedDataset, ola.getOriginalPartitions(), ola.getAnonymizedPartitions(),
                badOptions.getColumnInformation(), null);
        System.out.println(cp.report());
    }
    
    @Test
    public void testSanityChecks() {
        assertThrows(RuntimeException.class, () -> {

            IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/olaAges.csv"), true, ',', '"', false);

            GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("17", "18", "19", "48", "49", "101"));
            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

            List<PrivacyConstraint> privacyConstraints = Arrays.asList(new KAnonymity(dataset.getNumberOfRows() + 1));

            OLA ola = new OLA();
            OLAOptions olaOptions = new OLAOptions(2.0);

            ola.initialize(dataset, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();

        });
    }

    @Test
    public void testSanityChecksLDiversity() {
        assertThrows(RuntimeException.class, () -> {

            IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/olaAges.csv"), true, ',', '"', false);

            GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("17", "18", "19", "48", "49", "101"));
            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

            assertTrue(dataset.getNumberOfRows() > 5);

            List<PrivacyConstraint> privacyConstraints = Arrays.asList(
                    new KAnonymity(5), new DistinctLDiversity(dataset.getNumberOfRows() + 1));

            OLA ola = new OLA();
            OLAOptions olaOptions = new OLAOptions(2.0);

            ola.initialize(dataset, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();
        });
    }
}



