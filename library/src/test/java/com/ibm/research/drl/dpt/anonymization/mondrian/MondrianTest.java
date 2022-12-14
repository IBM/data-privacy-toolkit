/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.mondrian;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.ZIPCodeMaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.informationloss.CategoricalPrecision;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationLossResult;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetric;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.generators.ItemSet;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.Histogram;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MondrianTest {

    private List<String> toString(Long[] svs) {
        List<String> values = new ArrayList<>();
        for (Long v : svs) {
            values.add(Long.toString(v));
        }

        return values;
    }

    private List<String> toString(String ... svs) {
        return new ArrayList<>(Arrays.asList(svs));
    }

    private int countQuasiColumns(List<ColumnInformation> columnInformationList) {
        int counter = 0;

        for (ColumnInformation columnInformation : columnInformationList) {
            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                counter++;
            }
        }
        return counter;
    }


    @Test
    @Disabled
    public void testMondrianColumnInformationAutoBuild() throws Exception {
        Collection<String> sensitiveFields = new ArrayList<>();
        MondrianOptions options = new MondrianOptions();

        IPVDataset dataset = IPVDataset.load(getClass().getResourceAsStream("/testNumericOriginal.csv"), false, ',', '"', false);

        List<? extends IPVSchemaField> fields = dataset.getSchema().getFields();
        for (IPVSchemaField field : fields) {
            System.out.println("field: " + field.getName());
        }

        ItemSet itemSet = new ItemSet();
        itemSet.addItem(0);
        itemSet.addItem(1);
        IPVVulnerability vulnerability = new IPVVulnerability(itemSet);
        Collection<IPVVulnerability> vulnerabilities = new ArrayList<>();
        vulnerabilities.add(vulnerability);

        Map<String, ProviderType> fieldTypes = new HashMap<>();
        fieldTypes.put("Column 0", ProviderType.NUMERIC);
        fieldTypes.put("Column 1", ProviderType.NUMERIC);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, vulnerabilities, sensitiveFields, fieldTypes, privacyConstraints, options);

        IPVDataset anonymizedDataset = mondrian.apply();

        System.out.println("====== Anonymized Dataset ========");

        for (int k = 0; k < anonymizedDataset.getNumberOfRows(); k++) {
            for (int j = 0; j < anonymizedDataset.getNumberOfColumns(); j++) {
                System.out.print(anonymizedDataset.get(k, j) + ",");
            }

            System.out.println();
        }

    }

    @Test
    public void testMondrianBoth() {
        List<List<String>> values = new ArrayList<>();
        values.add(Arrays.asList("1", "Greece"));
        values.add(Arrays.asList("6", "Italy"));
        values.add(Arrays.asList("6", "Italy"));
        values.add(Arrays.asList("5", "Egypt"));
        values.add(Arrays.asList("10", "China"));
        values.add(Arrays.asList("10", "Singapore"));
        values.add(Arrays.asList("7", "China"));

        IPVDataset dataset = new IPVDataset(values, null, false);

        MaterializedHierarchy terms = new MaterializedHierarchy();
        terms.add(Arrays.asList("Greece", "Europe", "*"));
        terms.add(Arrays.asList("Italy", "Europe", "*"));
        terms.add(Arrays.asList("Egypt", "Africa", "*"));
        terms.add(Arrays.asList("Kenya", "Africa", "*"));
        terms.add(Arrays.asList("Singapore", "Asia", "*"));
        terms.add(Arrays.asList("China", "Asia", "*"));

        List<ColumnInformation> columnInformationList = new ArrayList<>(2);
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(terms, ColumnType.QUASI));

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian(); //dataset, 2, 1, columnInformationList); // Stefano, I hope 1 is a valid value for L
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions());

        IPVDataset anonymizedDataset = mondrian.apply();

        assertEquals(dataset.getNumberOfRows(), anonymizedDataset.getNumberOfRows());
    }

    @Test
    public void testMondrianNumerical() {
        List<List<String>> values = new ArrayList<>();
        values.add(toString(new Long[]{1L, 7L, 2L, 4L}));
        values.add(toString(new Long[]{6L, 6L, 12L, 4L}));
        values.add(toString(new Long[]{5L, 7L, 11L, 4L}));
        values.add(toString(new Long[]{10L, 7L, 5L, 4L}));
        values.add(toString(new Long[]{10L, 7L, 11L, 4L}));

        IPVDataset dataset = new IPVDataset(values, null, false);

        List<ColumnInformation> columnInformationList = new ArrayList<>(4);
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 1, ColumnType.QUASI));
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 2, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        assertEquals(dataset.getNumberOfRows(), anonymizedDataset.getNumberOfRows());
    }

    @Test
    public void testMondrianNumericalHole() {
        List<List<String>> values = new ArrayList<>();
        values.add(toString(new Long[]{1L, 7L, 2L, 4L}));
        values.add(toString(new Long[]{6L, 6L, 12L, 4L}));
        values.add(toString(new Long[]{5L, 7L, 11L, 4L}));
        values.add(toString(new Long[]{10L, 7L, 5L, 4L}));
        values.add(toString(new Long[]{10L, 7L, 11L, 4L}));

        IPVDataset dataset = new IPVDataset(values, null, false);

        List<ColumnInformation> columnInformationList = new ArrayList<>(4);
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 1, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 3, ColumnType.QUASI));

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        assertEquals(dataset.getNumberOfRows(), anonymizedDataset.getNumberOfRows());
    }

    @Test
    public void testMondrianNumerical100() throws Exception {
        int K = 3;
        Collection<Integer> quasiColumns = Arrays.asList(1,
                2,
                4);

        IPVDataset dataset = IPVDataset.load(getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);

        Map<String, ProviderType> fieldTypes = new HashMap<>();
        fieldTypes.put("Column 0", ProviderType.NUMERIC);
        fieldTypes.put("Column 1", ProviderType.NUMERIC);
        fieldTypes.put("Column 2", ProviderType.NUMERIC);
        fieldTypes.put("Column 3", ProviderType.NUMERIC);
        fieldTypes.put("Column 4", ProviderType.NUMERIC);

        ItemSet itemSet = new ItemSet();
        for (Integer q : quasiColumns) {
            itemSet.addItem(q);
        }
        IPVVulnerability vulnerability = new IPVVulnerability(itemSet);
        Collection<IPVVulnerability> vulnerabilities = new ArrayList<>();
        vulnerabilities.add(vulnerability);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(K));

        Mondrian mondrian = new Mondrian();
        MondrianOptions mondrianOptions = new MondrianOptions();
        mondrian.initialize(dataset, vulnerabilities, new ArrayList<String>(), fieldTypes, privacyConstraints, mondrianOptions);

        IPVDataset anonymizedDataset = mondrian.apply();

        assertEquals(dataset.getNumberOfRows(), anonymizedDataset.getNumberOfRows());

        System.out.println("====== Anonymized Dataset ========");

        for (int k = 0; k < anonymizedDataset.getNumberOfRows(); k++) {
            for (int j = 0; j < anonymizedDataset.getNumberOfColumns(); j++) {
                System.out.print(anonymizedDataset.get(k, j) + ",");
            }

            System.out.println();
        }

        assertEquals(100, anonymizedDataset.getNumberOfRows());
        Map<String, Integer> counters = new HashMap<>();

        for (int k = 0; k < anonymizedDataset.getNumberOfRows(); k++) {
            List<String> qidValues = new ArrayList<>();
            for (Integer q : quasiColumns) {
                qidValues.add(anonymizedDataset.get(k, q));

            }
            String key = StringUtils.join(qidValues, ",");

            counters.merge(key, 1, Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : counters.entrySet()) {
            assertTrue(entry.getValue() >= K);
        }

    }

    @Test
    public void testMondrianEmptyValueCategorical() {
        List<List<String>> values = new ArrayList<>();
        values.add(toString("Greece"));
        values.add(toString("Italy"));
        values.add(toString("Egypt"));
        values.add(toString("Singapore"));
        values.add(toString("China"));
        values.add(toString(""));
        values.add(toString(""));

        IPVDataset dataset = new IPVDataset(values, null, false);

        MaterializedHierarchy terms = new MaterializedHierarchy();
        terms.add(Arrays.asList("Greece", "Europe", "*"));
        terms.add(Arrays.asList("Italy", "Europe", "*"));
        terms.add(Arrays.asList("Egypt", "Africa", "*"));
        terms.add(Arrays.asList("Kenya", "Africa", "*"));
        terms.add(Arrays.asList("Singapore", "Asia", "*"));
        terms.add(Arrays.asList("China", "Asia", "*"));

        List<ColumnInformation> columnInformationList = new ArrayList<>(1);
        columnInformationList.add(new CategoricalInformation(terms, ColumnType.QUASI));

        int k = 2;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(anonymizedDataset.getNumberOfRows(), dataset.getNumberOfRows());
        ValidationUtils.validateIsKAnonymous(anonymizedDataset, columnInformationList, k);
    }

    @Test
    public void testMondrianEmptyValueNumerical() {
        assertThrows(NumberFormatException.class, () -> {
            List<List<String>> values = new ArrayList<>();
            values.add(toString("1"));
            values.add(toString("2"));
            values.add(toString("2"));
            values.add(toString("3"));
            values.add(toString("4"));
            values.add(toString("5"));
            values.add(toString(""));

            IPVDataset dataset = new IPVDataset(values, null, false);

            List<ColumnInformation> columnInformationList = new ArrayList<>(1);
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(2));

            Mondrian mondrian = new Mondrian();
            mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

            IPVDataset anonymizedDataset = mondrian.apply();
        });
    }

    @Test
    public void testMondrianCategorical() {
        List<List<String>> values = new ArrayList<>();
        values.add(toString("Greece"));
        values.add(toString("Italy"));
        values.add(toString("Egypt"));
        values.add(toString("Singapore"));
        values.add(toString("China"));

        IPVDataset dataset = new IPVDataset(values, null, false);

        MaterializedHierarchy terms = new MaterializedHierarchy();
        terms.add(Arrays.asList("Greece", "Europe", "*"));
        terms.add(Arrays.asList("Italy", "Europe", "*"));
        terms.add(Arrays.asList("Egypt", "Africa", "*"));
        terms.add(Arrays.asList("Kenya", "Africa", "*"));
        terms.add(Arrays.asList("Singapore", "Asia", "*"));
        terms.add(Arrays.asList("China", "Asia", "*"));

        List<ColumnInformation> columnInformationList = new ArrayList<>(1);
        columnInformationList.add(new CategoricalInformation(terms, ColumnType.QUASI));

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        System.out.println("====== Anonymized Dataset ========");
        for (int k = 0; k < anonymizedDataset.getNumberOfRows(); k++) {
            for (int j = 0; j < anonymizedDataset.getNumberOfColumns(); j++) {
                System.out.print(anonymizedDataset.get(k, j) + ",");
            }

            System.out.println();
        }

    }

    @Test
    public void testMondrianCategoricalShouldGeneralizeToMiddleLevelHierarchySplit() {
        List<List<String>> values = new ArrayList<>();
        values.add(toString("Greece"));
        values.add(toString("Italy"));
        values.add(toString("Egypt"));
        values.add(toString("Singapore"));
        values.add(toString("China"));
        values.add(Arrays.asList("Kenya"));

        IPVDataset dataset = new IPVDataset(values, null, false);

        MaterializedHierarchy terms = new MaterializedHierarchy();
        terms.add(Arrays.asList("Greece", "Europe", "*"));
        terms.add(Arrays.asList("Italy", "Europe", "*"));
        terms.add(Arrays.asList("Egypt", "Africa", "*"));
        terms.add(Arrays.asList("Kenya", "Africa", "*"));
        terms.add(Arrays.asList("Singapore", "Asia", "*"));
        terms.add(Arrays.asList("China", "Asia", "*"));

        List<ColumnInformation> columnInformationList = new ArrayList<>(1);
        columnInformationList.add(new CategoricalInformation(terms, ColumnType.QUASI));

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions(CategoricalSplitStrategy.HIERARCHY_BASED)); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(dataset.getNumberOfRows(), anonymizedDataset.getNumberOfRows());

        Map<String, Integer> counters = new HashMap<>();
        for (int i = 0; i < anonymizedDataset.getNumberOfRows(); i++) {
            String value = anonymizedDataset.get(i, 0);
            counters.merge(value, 1, (a, b) -> a + b);
        }

        System.out.println("====== Anonymized Dataset ========");
        for (int k = 0; k < anonymizedDataset.getNumberOfRows(); k++) {
            for (int j = 0; j < anonymizedDataset.getNumberOfColumns(); j++) {
                System.out.print(anonymizedDataset.get(k, j) + ",");
            }

            System.out.println();
        }
        System.out.println("==================================");

        assertEquals(2, counters.get("Europe".toUpperCase()).intValue());
        assertEquals(2, counters.get("Asia".toUpperCase()).intValue());
        assertEquals(2, counters.get("Africa".toUpperCase()).intValue());
    }

    @Test
    @Disabled
    public void testMondrianPerformanceNumerical() {
        int[] Ns = new int[]{10000, 100000, 1000000};
        int K = 2;
        Random random = new Random();

        for (int N : Ns) {
            List<List<String>> values = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                values.add(toString(new Long[]{(long) random.nextInt(N), (long) random.nextInt(N),
                        (long) random.nextInt(N), (long) random.nextInt(N)}));
            }

            IPVDataset dataset = new IPVDataset(values, null, false);

            long startMillis = System.currentTimeMillis();

            List<ColumnInformation> columnInformationList = new ArrayList<>(4);
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 1, ColumnType.QUASI));
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 2, ColumnType.QUASI));
            columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 3, ColumnType.QUASI));

            long diff = System.currentTimeMillis() - startMillis;
            System.out.printf("N=%d, k=%d, QI=%d column information took %d milliseconds%n",
                    N, K, countQuasiColumns(columnInformationList), diff);

            startMillis = System.currentTimeMillis();

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(K));

            Mondrian mondrian = new Mondrian();
            mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, K, 1, columnInformationList);
            IPVDataset anonymizedDataset = mondrian.apply();

            diff = System.currentTimeMillis() - startMillis;
            System.out.printf("N=%d, k=%d, QI=%d Mondrian took %d milliseconds%n",
                    N, K, countQuasiColumns(columnInformationList), diff);

            assertEquals(dataset.getNumberOfRows(), anonymizedDataset.getNumberOfRows());
        }
    }

    @Test
    public void testMondrianCategoricalCorrectness() throws Exception {
        IPVDataset dataset = IPVDataset.load(getClass().getResourceAsStream("/testCategoricalOriginal.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Worker", "*");
        hierarchy.add("Manager", "Employed", "*");
        hierarchy.add("Director", "Employed", "*");

        CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
        columnInformationList.add(categoricalInformation);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        Map<String, Integer> counters = new HashMap<>();
        for (int i = 0; i < anonymizedDataset.getNumberOfRows(); i++) {
            String value = anonymizedDataset.get(i, 0);
            counters.merge(value, 1, Integer::sum);
        }

        for (String s : Arrays.asList("Scientist", "Employed")) {
            assertEquals(2, counters.get(s.toUpperCase()).intValue());
        }
    }

    @Test
    @Disabled("Missing data file")
    public void testMondrianCategoricalCorrectness2() throws Exception {
        IPVDataset dataset = IPVDataset.load(getClass().getResourceAsStream("/testCategoricalOriginal3plus1.csv"), false, ',', '"', false);


        List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Worker");
        hierarchy.add("Manager", "Worker");
        hierarchy.add("Director", "Worker");

        CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
        columnInformationList.add(categoricalInformation);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        Map<String, Integer> counters = new HashMap<>();
        for (int i = 0; i < anonymizedDataset.getNumberOfRows(); i++) {
            String value = anonymizedDataset.get(i, 0);
            counters.merge(value, 1, Integer::sum);
        }

        assertEquals(4, counters.get("Worker".toUpperCase()).intValue());
    }

    @Test
    public void testMondrianOriginalIsNotMutated() throws Exception {

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
        columnInformation.add(ColumnInformationGenerator.generateNumericalRange(IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false),
                11, ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());

        int[] kValues = {2, 5, 10};

        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);
        int originalRows = original.getNumberOfRows();

        IPVDataset reloaded = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight.txt"), false, ',', '"', false);

        for (int k : kValues) {
            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            Mondrian mondrian = new Mondrian();
            mondrian.initialize(original, columnInformation, privacyConstraints, null);

            IPVDataset anonymized = mondrian.apply();

            ValidationUtils.mustBeTheSame(original, reloaded);

            assertEquals(originalRows, anonymized.getNumberOfRows());
        }
    }

    public static String createRowRepresentation(List<String> row) {
        return StringUtils.join(row, ":");
    }

    public static Map<String, String> createIndexMap(IPVDataset dataset, int columnIndex) {
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < dataset.getNumberOfRows(); i++) {
            String indexValue = dataset.get(i, columnIndex);
            map.put(indexValue, createRowRepresentation(dataset.getRow(i)));
        }

        return map;
    }

    public static Map<String, List<String>> createIndex(IPVDataset dataset, int columnIndex) {
        Map<String, List<String>> map = new HashMap<>();

        for (int i = 0; i < dataset.getNumberOfRows(); i++) {
            String indexValue = dataset.get(i, columnIndex);
            map.put(indexValue, dataset.getRow(i));
        }

        return map;
    }

    @Test
    @Disabled("Missing data file")
    public void testNumericOnlySimple() throws Exception {
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/mondrianNumeric4.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(ColumnInformationGenerator.generateNumericalRange(originalDataset, 0, ColumnType.QUASI)); //height

        int k = 2;

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(originalDataset, columnInformation, privacyConstraints, null);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(anonymizedDataset.getNumberOfRows(), originalDataset.getNumberOfRows());

        //input is 2, 2, 3, 3, 4 so we expect anon to be 2, 2, 3-4, 3-4, 3-4
        Histogram<String> histogram = Histogram.createHistogram(anonymizedDataset, 0);

        assertEquals(2, histogram.size());
        assertEquals(2, histogram.get("2.0").longValue());
        assertEquals(3, histogram.get("3.0-4.0").longValue());
    }

    @Test
    public void testNumericOnly() throws Exception {
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false);
        Map<String, String> indexMap = createIndexMap(originalDataset, 0);

        System.out.println("original: " + originalDataset.getNumberOfRows());

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation()); //index, 0
        columnInformation.add(new DefaultColumnInformation()); // 1
        columnInformation.add(new DefaultColumnInformation()); // 2
        columnInformation.add(new DefaultColumnInformation()); // 3
        columnInformation.add(new DefaultColumnInformation()); // 4
        columnInformation.add(new DefaultColumnInformation()); // 5
        columnInformation.add(new DefaultColumnInformation()); // 6
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(ColumnInformationGenerator.generateNumericalRange(IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false),
                12, ColumnType.QUASI)); //height
        columnInformation.add(new DefaultColumnInformation());

        int k = 2;

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(originalDataset, columnInformation, privacyConstraints, null);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(anonymizedDataset.getNumberOfRows(), originalDataset.getNumberOfRows());

        ValidationUtils.validateIsKAnonymous(anonymizedDataset, columnInformation, k);
    }

    @Test
    public void testCategoricalOnly() throws Exception {
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false);
        Map<String, String> indexMap = createIndexMap(originalDataset, 0);

        System.out.println("original: " + originalDataset.getNumberOfRows());

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation()); //index, 0
        columnInformation.add(new DefaultColumnInformation()); // 1
        columnInformation.add(new DefaultColumnInformation()); // 2
        columnInformation.add(new DefaultColumnInformation()); // 3
        columnInformation.add(new DefaultColumnInformation()); // 4
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI)); //5, yob
        columnInformation.add(new DefaultColumnInformation()); // zipcode, 6
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        int k = 2;

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(originalDataset, columnInformation, privacyConstraints, null);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(anonymizedDataset.getNumberOfRows(), originalDataset.getNumberOfRows());
        ValidationUtils.validateIsKAnonymous(anonymizedDataset, columnInformation, k);
    }

    @Test
    public void testMixNumericalCategorical() throws Exception {
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false);
        Map<String, String> indexMap = createIndexMap(originalDataset, 0);

        System.out.println("original: " + originalDataset.getNumberOfRows());

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation()); //index, 0
        columnInformation.add(new DefaultColumnInformation()); // 1
        columnInformation.add(new DefaultColumnInformation()); // 2
        columnInformation.add(new DefaultColumnInformation()); // 3
        columnInformation.add(new DefaultColumnInformation()); // 4
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI)); //5, yob
        columnInformation.add(new DefaultColumnInformation()); // 6
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(ColumnInformationGenerator.generateNumericalRange(IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false),
                12, ColumnType.QUASI)); //height
        columnInformation.add(new DefaultColumnInformation());

        int k = 2;

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(originalDataset, columnInformation, privacyConstraints, null);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(anonymizedDataset.getNumberOfRows(), originalDataset.getNumberOfRows());
        ValidationUtils.validateIsKAnonymous(anonymizedDataset, columnInformation, k);
    }

    @Test
    public void testPartitions() throws Exception {
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false);
        Map<String, String> indexMap = createIndexMap(originalDataset, 0);

        System.out.println("original: " + originalDataset.getNumberOfRows());

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

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(originalDataset, columnInformation, privacyConstraints, null);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(anonymizedDataset.getNumberOfRows(), originalDataset.getNumberOfRows());

        List<Partition> originalPartitions = mondrian.getOriginalPartitions();
        List<Partition> anonymizedPartitions = mondrian.getAnonymizedPartitions();

        assertTrue(originalPartitions.size() > 0);
        assertEquals(originalPartitions.size(), anonymizedPartitions.size());

        int numPartitions = originalPartitions.size();

        for (int i = 0; i < numPartitions; i++) {
            Partition originalPartition = originalPartitions.get(i);
            Partition anonymizedPartition = anonymizedPartitions.get(i);

            assertEquals(originalPartition.size(), anonymizedPartition.size());
            assertEquals(originalPartition.isAnonymous(), anonymizedPartition.isAnonymous());

            assertTrue(originalPartition.isAnonymous());
            assertTrue(anonymizedPartition.isAnonymous());

            int numRows = originalPartition.size();

            for (int j = 0; j < numRows; j++) {
                String originalIndex = originalPartition.getMember().get(j, 0);
                List<String> originalRow = originalPartition.getMember().getRow(j);
                assertEquals(indexMap.get(originalIndex), createRowRepresentation(originalRow));

                String anonIndex = anonymizedPartition.getMember().get(j, 0);

                assertEquals(originalIndex, anonIndex);
            }
        }
    }

    @Test
    public void testFindCommonAncestor() {
        MaterializedHierarchy terms = new MaterializedHierarchy();
        terms.add(Arrays.asList("Greece", "Europe", "*"));
        terms.add(Arrays.asList("Italy", "Europe", "*"));
        terms.add(Arrays.asList("Egypt", "Africa", "*"));
        terms.add(Arrays.asList("Kenya", "Africa", "*"));
        terms.add(Arrays.asList("Singapore", "Asia", "*"));
        terms.add(Arrays.asList("China", "Asia", "*"));

        List<List<String>> values = new ArrayList<>();
        values.add(Arrays.asList("Greece"));
        values.add(Arrays.asList("Greece"));
        values.add(Arrays.asList("Greece"));
        values.add(Arrays.asList("Greece"));

        String commonAncestor = Mondrian.findCommonAncestor(values, 0, terms);
        assertEquals("Greece".toUpperCase(), commonAncestor.toUpperCase());

        values = new ArrayList<>();
        values.add(Arrays.asList("Greece"));
        values.add(Arrays.asList("Italy"));
        values.add(Arrays.asList("Greece"));
        values.add(Arrays.asList("Greece"));

        assertEquals("Europe".toUpperCase(), Mondrian.findCommonAncestor(values, 0, terms).toUpperCase());

        values = new ArrayList<>();
        values.add(Arrays.asList("Greece"));
        values.add(Arrays.asList("Italy"));
        values.add(Arrays.asList("Greece"));
        values.add(Arrays.asList("Egypt"));

        assertEquals("*".toUpperCase(), Mondrian.findCommonAncestor(values, 0, terms).toUpperCase());
    }

    @Test
    public void testCorrectMiddleValues() throws Exception {
        List<List<String>> values = new ArrayList<>();
        values.add(toString(new Long[]{1L, 0L, 2L, 4L}));
        values.add(toString(new Long[]{5L, 1L, 11L, 4L}));
        values.add(toString(new Long[]{6L, 2L, 12L, 4L}));
        values.add(toString(new Long[]{10L, 3L, 5L, 4L}));
        values.add(toString(new Long[]{10L, 4L, 11L, 4L}));

        IPVDataset dataset = new IPVDataset(values, null, false);

        List<ColumnInformation> columnInformationList = new ArrayList<>(4);
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 2, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());

        Mondrian mondrian = new Mondrian();

        int k = 2;
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();
        assertEquals(dataset.getNumberOfRows(), anonymizedDataset.getNumberOfRows());
        ValidationUtils.validateIsKAnonymous(anonymizedDataset, columnInformationList, k);
        
        List<Partition> anonymizedPartitions = mondrian.getAnonymizedPartitions();
        List<Partition> originalPartitions = mondrian.getOriginalPartitions();
        
        List<Integer> quasiColums = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);
        
        for(int i = 0; i < anonymizedPartitions.size(); i++) {
            Partition anonymized = anonymizedPartitions.get(i);
            Partition original = originalPartitions.get(i);
            
            for(Integer q: quasiColums) {
                List<Double> originalValues = extractValues(original.getMember(), q);
                Collections.sort(originalValues);
                double min = originalValues.get(0);
                double max = originalValues.get(originalValues.size() - 1);
                
                String expectedMiddleKey = MondrianPartition.generateMiddleKey(min, max);
                String anonMiddleKey = anonymized.getMember().get(0, q); //the one from the first row should do
                
                assertEquals(expectedMiddleKey, anonMiddleKey);
            }
        }
        
    }
    
    private List<Double> extractValues(IPVDataset member, Integer q) {
        List<Double> values = new ArrayList<>();
        
        for(int i = 0; i < member.getNumberOfRows(); i++) {
            values.add(Double.parseDouble(member.get(i, q)));
        }
        
        return values;
    }

    public static List<ColumnInformation> getFloridaColumnInformation() {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(ZIPCodeMaterializedHierarchy.getInstance(), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")), ColumnType.QUASI, true));

        return columnInformation;
    }
    
    @Test
    @Disabled
    public void testCategoricalStrategies() throws Exception {

        List<ColumnInformation> columnInformation = getFloridaColumnInformation();

        int[] kValues = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        //int kValues[] = {20};
        CategoricalSplitStrategy[] categoricalSplitStrategies = {CategoricalSplitStrategy.ORDER_BASED, CategoricalSplitStrategy.HIERARCHY_BASED};

        for(CategoricalSplitStrategy categoricalSplitStrategy: categoricalSplitStrategies) {
            for(int k: kValues) {
                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                IPVDataset sampleDataset = IPVDataset.load(this.getClass().getResourceAsStream("/riskPCSample.csv"), false, ',', '"', false);

                Mondrian mondrian = new Mondrian();
                mondrian.initialize(sampleDataset, columnInformation, privacyConstraints, new MondrianOptions(categoricalSplitStrategy));

                IPVDataset anonymized = mondrian.apply();

                InformationMetric lossMetric = new CategoricalPrecision();
                lossMetric.initialize(sampleDataset, anonymized, mondrian.getOriginalPartitions(), mondrian.getAnonymizedPartitions(), columnInformation, null);
                double iloss = lossMetric.report();

                System.out.println(String.format("%s\t%d\t%f", categoricalSplitStrategy.name(), k, iloss));
                for(InformationLossResult lossResult: lossMetric.reportPerQuasiColumn()) {
                    System.out.println("\t" + lossResult.getValue());
                }
                
            }
        }

    }

    @Test
    @Disabled
    public void testPerformanceFlorida() throws Exception {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy("ZIPCODE_MATERIALIZED"), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                ColumnType.QUASI, true));

        int k = 2;

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        long start = System.currentTimeMillis();
        IPVDataset originalDataset = IPVDataset.load(this.getClass().getResourceAsStream("/florida_12M.txt"), false, ',', '"', false);
        System.out.println("loading done");
        System.out.println("loading finished in " + (System.currentTimeMillis() - start));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(originalDataset, columnInformation, privacyConstraints, null);

        start = System.currentTimeMillis();

        IPVDataset anonymizedDataset = mondrian.apply();
        System.out.println(anonymizedDataset.getNumberOfRows());

        long end = System.currentTimeMillis();

        System.out.println("total time in milliseconds: " + (end - start));
    }
}
