/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.anonymization.mondrian;

import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class MondrianExecutor {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException, MisconfigurationException {
        String confFile = args[0];
        String input = args[1];
        String output = args[2];

        InputStream confStream;

        if (args.length >=4 && args[3].equals("remoteConf")) {
            confStream = SparkUtils.createHDFSInputStream(confFile);
        }
        else {
            confStream = new FileInputStream(confFile);
        }

        SparkContext sc = SparkUtils.createSparkContext("Mondrian");

        JavaRDD<String> inputRDD = SparkUtils.createTextFileRDD(new JavaSparkContext(sc), input);
        inputRDD.cache();

        System.out.println("rdd partition size: " + inputRDD.partitions().size());

        JavaRDD<String> outputRDD = MondrianSpark.run(confStream, inputRDD);

        if(!output.equals("/dev/null")) {
            outputRDD.saveAsTextFile(output);
        }
        else {
            System.out.println("rdd size after anon: " + outputRDD.count());
        }

        confStream.close();
        sc.stop();
    }
    
}
