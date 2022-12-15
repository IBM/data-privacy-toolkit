/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static Long longFromHash(String value) {
        return longFromHash(value, "SHA-512");
    }

    private static Long longFromHash(String value, String algorithm) {
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
