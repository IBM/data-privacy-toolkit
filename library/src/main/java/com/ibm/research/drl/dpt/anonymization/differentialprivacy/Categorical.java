/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.SecureRandom;
import java.util.*;

public class Categorical implements DPMechanism {
    private static final Logger log = LogManager.getLogger(Categorical.class);
    private final Random rnd = new SecureRandom();
    private double epsilon;
    private GeneralizationHierarchy hierarchy;
    private Map<Integer, Double> utilityFunction = new HashMap<>();
    private Map<String, Double> normalisingConstant = new HashMap<>();
    private Set<String> hierarchyLeaves = new HashSet<>();

    @Override
    public void setOptions(AnonymizationAlgorithmOptions options) {
        if (!(options instanceof DifferentialPrivacyMechanismOptions)) throw new IllegalArgumentException("Expecting instance of DifferentialPrivacyMechanismOptions");

        this.epsilon = ((DifferentialPrivacyMechanismOptions) options).getEpsilon();

        if (this.epsilon < 0.0) {
            log.error("Epsilon parameter must be positive");
            throw new RuntimeException("Epsilon parameter must be positive");
        }

        this.hierarchy = ((DifferentialPrivacyMechanismOptions) options).getHierarchy();

        if (this.hierarchy == null) {
            log.error("Hierarchy must be specified");
            throw new RuntimeException("Hierarchy must be specified");
        }

        Set<String> tempLeaves = this.hierarchy.getNodeLeaves(this.hierarchy.getTopTerm());

        for (String leaf : tempLeaves) {
            this.hierarchyLeaves.add(leaf.toUpperCase());
        }

        this.clearUtilityFunction();
    }

    @Override
    public void analyseForParams(List<Partition> equivalenceClasses, int columnIndex) {
        this.hierarchyLeaves = new HashSet<>();

        for (Partition ec : equivalenceClasses) {
            IPVDataset dataset = ec.getMember();

            for (List<String> row : dataset) {
                String value = row.get(columnIndex).toUpperCase();
                this.hierarchyLeaves.add(value);
            }
        }

        this.clearUtilityFunction();
    }

    @Override
    public String randomise(String value) {
        if (this.utilityFunction.size() == 0) {
            initUtilityFunction();
        }

        value = value.toUpperCase();
        double u = this.rnd.nextDouble() * this.normalisingConstant.get(value);
        double sum = 0.0;

        for (String target : this.hierarchyLeaves) {
            sum += getProbOfValues(value, target);

            if (u <= sum) {
                return target;
            }
        }

        return value;
    }

    @Override
    public double randomise(double value) {
        throw new UnsupportedOperationException("Not a numerical mechanism");
    }

    private void clearUtilityFunction() {
        this.utilityFunction = new HashMap<>();
    }

    @Override
    public String getName() {
        return "Hierarchical Exponential mechanism";
    }

    private int getHashValue(String value1, String value2) {
        return Objects.hash(value1, value2);
    }

    private void initUtilityFunction() {
        this.utilityFunction = new HashMap<>();

        int hierarchyHeight = this.hierarchy.getHeight();

        for (String base : this.hierarchyLeaves) {
            List<String> baseGeneralizations = new ArrayList<>();

            if (this.hierarchy.getNodeLevel(base) >= 0) {
                for (int i = 0; i < hierarchyHeight; i++) {
                    baseGeneralizations.add(this.hierarchy.encode(base, i, false));
                }
            }

            for (String target : this.hierarchyLeaves) {
                int hash = getHashValue(base, target);

                if (this.utilityFunction.containsKey(hash)) {
                    continue;
                }

                if (base.equals(target)) {
                    this.utilityFunction.put(hash, 0.0);
                    continue;
                }

                if (this.hierarchy.getNodeLevel(base) < 0 || this.hierarchy.getNodeLevel(target) < 0) {
                    this.utilityFunction.put(hash, (double) hierarchyHeight);
                    continue;
                }

                for (int i = 1; i < hierarchyHeight; i++) {
                    String targetGeneralised = this.hierarchy.encode(target, i, false);

                    if (targetGeneralised.equals(baseGeneralizations.get(i))) {
                        this.utilityFunction.put(hash, (double) i);
                        break;
                    }
                }
            }
        }

        initNormalisingConstant();
    }

    private void initNormalisingConstant() {
        this.normalisingConstant = new HashMap<>();

        for (String base : this.hierarchyLeaves) {
            double tempNormalisingConstant = 0.0;

            for (String target : this.hierarchyLeaves) {
                tempNormalisingConstant += getProbOfValues(base, target);
            }

            this.normalisingConstant.put(base, tempNormalisingConstant);
        }
    }

    private double getProbOfValues(String value1, String value2) {
        return Math.exp(- this.epsilon * getUtility(value1, value2) / 2);
    }

    public double getUtility(String value1, String value2) {
        return this.utilityFunction.get(getHashValue(value1, value2));
    }
}

