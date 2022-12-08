package com.ibm.research.drl.prima.util;
/*******************************************************************
 * IBM Confidential                                                *
 * *
 * Copyright IBM Corp. 2021                                        *
 * *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static Long longFromHash(String value) {
        return longFromHash(value, "SHA-1");
    }

    public static Long longFromHash(String value, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            if (value == null) {
                value = "";
            }

            md.update(value.getBytes());
            return new BigInteger(md.digest()).longValue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            throw new Error("Impossible to retrieve an instance of " + algorithm);
        }
    }
}
