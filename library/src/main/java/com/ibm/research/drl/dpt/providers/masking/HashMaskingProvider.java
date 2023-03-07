/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The type Hash masking provider.
 */
public class HashMaskingProvider extends AbstractMaskingProvider {
    private static final Logger log = LogManager.getLogger(HashMaskingProvider.class);

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final String algorithm;
    private final String salt;
    private final Boolean normalize;

    /**
     * Instantiates a new Hash masking provider.
     */
    public HashMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Hash masking provider.
     *
     * @param configuration the configuration
     */
    public HashMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Hash masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public HashMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.algorithm = configuration.getStringValue("hashing.algorithm.default");
        this.salt = configuration.getStringValue("hashing.salt");
        this.normalize = configuration.getBooleanValue("hashing.normalize");
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    @Override
    public String mask(String identifier) {
        return maskWithKey(identifier, this.salt);
    }

    @Override
    public String maskWithKey(String identifier, String key) {
        try {
            MessageDigest md = MessageDigest.getInstance(this.algorithm);

            if (this.normalize) {
                identifier = identifier.toLowerCase();
            }

            md.update((identifier + key).getBytes());
            byte[] shaDig = md.digest();

            return bytesToHex(shaDig);
        } catch (NoSuchAlgorithmException e) {
            String msg = "Unable to retrieve an instance of " + this.algorithm;
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }
}
