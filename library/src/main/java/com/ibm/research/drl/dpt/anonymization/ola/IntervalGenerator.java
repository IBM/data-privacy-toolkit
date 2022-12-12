/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntervalGenerator {
    private final static Logger logger = LogManager.getLogger(IntervalGenerator.class);

    /**
     * Generate hierarchy default hierarchy.
     *
     * @param dataset     the dataset
     * @param columnIndex the column index
     * @return the default hierarchy
     */
    public static MaterializedHierarchy generateHierarchy(IPVDataset dataset, int columnIndex) {
        List<Long> longs = new ArrayList<>();

        int numberOfRows = dataset.getNumberOfRows();

        for(int i = 0; i < numberOfRows; i++) {
            List<String> row = dataset.getRow(i);
            Long v = Long.valueOf(row.get(columnIndex));
            longs.add(v);
        }

        Collections.sort(longs);

        long minimum = longs.get(0);
        long maximum = longs.get(Math.max(0, longs.size() - 1)) + 1;

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();

        long diff = maximum - minimum;
        int levels = (int)(diff/10);
        logger.info("minimum: " + minimum + ", maximum: " + maximum + ", diff: " + diff + ", levels: " + levels);

        for(Long v: longs) {

            int segments = (int)Math.pow(2, levels - 3);
            List<String> terms = new ArrayList<>();
            terms.add(v.toString());

            for (int i = 1; i < (levels - 1); i++) {
                long delta = (maximum - minimum) / segments;

                int group = (int) ((v - minimum)/delta);
                long base = minimum + group*delta;

                terms.add(base + "-" + (base + delta - 1));
                segments /= 2;
            }

            hierarchy.add(terms);
        }

        return hierarchy;
    }

}
