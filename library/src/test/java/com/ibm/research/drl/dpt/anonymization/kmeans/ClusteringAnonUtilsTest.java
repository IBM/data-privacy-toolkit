/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmeans;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClusteringAnonUtilsTest {

    @Test
    public void testFindCommonAncestorSingleValue() {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "*");
        hierarchy.add("Manager", "*");
        hierarchy.add("Director", "*");

        Set<String> values = new HashSet<>();
        values.add("Scientist");

        String ancestor = ClusteringAnonUtils.calculateCommonAncestor(values, hierarchy);
        assertEquals("Scientist".toUpperCase(), ancestor.toUpperCase());
    }

    @Test
    public void testFindCommonAncestorDifferentLevels() {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Manager", "*");
        hierarchy.add("Engineer", "Manager", "*");
        hierarchy.add("Salesman", "Sales Leader", "*");
        hierarchy.add("Sales rep", "Sales Leader", "*");

        Set<String> values = new HashSet<>();
        values.add("Scientist");
        values.add("Manager");

        String ancestor = ClusteringAnonUtils.calculateCommonAncestor(values, hierarchy);
        assertEquals("Manager".toUpperCase(), ancestor.toUpperCase());
    }

    @Test
    public void testFindCommonAncestorDifferentBranches() {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Manager", "*");
        hierarchy.add("Engineer", "Manager", "*");
        hierarchy.add("Salesman", "Sales Leader", "*");
        hierarchy.add("Sales rep", "Sales Leader", "*");

        Set<String> values = new HashSet<>();
        values.add("Manager");
        values.add("Sales Leader");

        String ancestor = ClusteringAnonUtils.calculateCommonAncestor(values, hierarchy);
        assertEquals("*".toUpperCase(), ancestor.toUpperCase());
    }

    @Test
    public void testFindCommonAncestorJunkValue() {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Manager", "*");
        hierarchy.add("Engineer", "Manager", "*");
        hierarchy.add("Salesman", "Sales Leader", "*");
        hierarchy.add("Sales rep", "Sales Leader", "*");

        Set<String> values = new HashSet<>();
        values.add("Manager");
        values.add("Foobar");

        String ancestor = ClusteringAnonUtils.calculateCommonAncestor(values, hierarchy);
        assertEquals("*".toUpperCase(), ancestor.toUpperCase());
    }

    @Test
    public void testFindCommonAncestorSameBranch() {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Manager", "*");
        hierarchy.add("Engineer", "Manager", "*");
        hierarchy.add("Salesman", "Sales Leader", "*");
        hierarchy.add("Sales rep", "Sales Leader", "*");

        Set<String> values = new HashSet<>();
        values.add("Scientist");
        values.add("Engineer");

        String ancestor = ClusteringAnonUtils.calculateCommonAncestor(values, hierarchy);
        assertEquals("Manager".toUpperCase(), ancestor.toUpperCase());
    }

    @Test
    public void testFindCommonAncestorMultipleBranches() {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Manager", "*");
        hierarchy.add("Engineer", "Manager", "*");
        hierarchy.add("Salesman", "Sales Leader", "*");
        hierarchy.add("Sales rep", "Sales Leader", "*");

        Set<String> values = new HashSet<>();
        values.add("Scientist");
        values.add("Salesman");

        String ancestor = ClusteringAnonUtils.calculateCommonAncestor(values, hierarchy);
        assertEquals("*", ancestor);
    }

    @Test
    public void testCreateAnonymizedRow() {
        List<String> centroids = new ArrayList<>();
        centroids.add("a+");
        centroids.add("b+");
        centroids.add(null);

        List<String> row = new ArrayList<>();
        row.add("a");
        row.add("b");
        row.add("c");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());

        List<String> anonymizedRow = ClusteringAnonUtils.createAnonymizedRow(centroids, row, columnInformationList);

        assertEquals(3, anonymizedRow.size());
        assertEquals("a+", anonymizedRow.get(0));
        assertEquals("b+", anonymizedRow.get(1));
        assertEquals("c", anonymizedRow.get(2));
    }
}

