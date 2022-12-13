/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.managers.*;
import com.ibm.research.drl.dpt.models.CreditCard;
import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.models.LatitudeLongitudeFormat;
import com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier;
import com.ibm.research.drl.dpt.providers.masking.IPAddressMaskingProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

import static org.apache.commons.lang3.ArrayUtils.contains;

/**
 * The type Random generators.
 */
public class RandomGenerators {
    private static final Logger logger = LogManager.getLogger(RandomGenerators.class);

    private static final SecureRandom random = new SecureRandom();
    private static final IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();
    private static final IPAddressMaskingProvider ipAddressMaskingProvider = new IPAddressMaskingProvider();
    private static final TLDManager tldManager = TLDManager.instance();
    private static final CreditCardManager creditCardManager = CreditCardManager.getInstance();
    private static final LuhnCheckDigit luhnCheckDigit = new LuhnCheckDigit();
    private static final IMSIManager imsiManager = IMSIManager.getInstance();
    private static final NamesManager.Names namesManager = NamesManager.instance();

    private static final char[] alphaDigitSubset = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final SSNUKManager SSNUK_MANAGER = SSNUKManager.getInstance();

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Generate random imsi string.
     *
     * @return the string
     */
    public static String generateRandomIMSI() {
        String mccmnc = imsiManager.getRandomKey();
        StringBuilder builder = new StringBuilder(mccmnc);

        for (int i = mccmnc.length(); i < 15; i++) {
            builder.append(randomDigit());
        }

        return builder.toString();
    }

    public static String generateRandomSSNUK() {
        String prefix = SSNUK_MANAGER.getRandomPrefix();
        StringBuilder builder = new StringBuilder(prefix);

        for (int i = 0; i < 6; i++) {
            builder.append(randomDigit());
        }

        builder.append(SSNUK_MANAGER.getRandomSuffix());

        return builder.toString();
    }

    public static String buildNameBasedUsername() {
        String firstName = namesManager.getRandomFirstName();
        String lastName = namesManager.getRandomLastName();

        int prefixForSurname = random.nextInt(2);

        String lastNamePortion;

        if (prefixForSurname <= lastName.length()) {
            lastNamePortion = lastName.substring(0, prefixForSurname);
        } else {
            lastNamePortion = lastName;
        }

        return firstName + lastNamePortion;

    }

    /**
     * Luhn check digit int.
     *
     * @param body the body
     * @return the int
     */
    public static int luhnCheckDigit(String body) {
        boolean evenPosition = true;
        int sum = 0;

        for (int i = body.length() - 1; i >= 0; i--) {
            int n = body.charAt(i) - '0';
            if (evenPosition) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            evenPosition = !evenPosition;
        }

        int s = 10 - sum % 10;
        return (s % 10 == 0) ? 0 : s;
    }

    /**
     * Generate random credit card string.
     *
     * @return the string
     */
    public static String generateRandomCreditCard() {
        CreditCard creditCard = creditCardManager.randomCreditCardInformation();

        String[] prefixes = creditCard.getPrefixes();
        StringBuilder randomCC = new StringBuilder(prefixes[random.nextInt(prefixes.length)]);
        for (int i = randomCC.length(); i < 6; i++) {
            randomCC.append(randomDigit());
        }

        int length = creditCard.getMinimumLength();

        for (int i = 6; i < (length - 1); i++) {
            randomCC.append(randomDigit());
        }

        randomCC.append((char) ('0' + luhnCheckDigit(randomCC.toString())));

        return randomCC.toString();
    }

    /**
     * Random digit char.
     *
     * @return the char
     */
    public static char randomDigit() {
        return (char) ('0' + random.nextInt(10));
    }

    /**
     * Random within range int.
     *
     * @param base      the base
     * @param rangeDown the range down
     * @param rangeUp   the range up
     * @return the int
     */
    public static int randomWithinRange(int base, int rangeDown, int rangeUp) {
        return (base - rangeDown) + random.nextInt(rangeUp + rangeDown);
    }

    public static double randomWithinRange(double base, double rangeDown, double rangeUp) {
        return (base - rangeDown) + random.nextDouble() * (rangeUp + rangeDown);
    }

    /**
     * Gets random tld.
     *
     * @return the random tld
     */
    public static String getRandomTLD() {
        return tldManager.getRandomTLD();
    }

    /**
     * Random username and domain string.
     *
     * @return the string
     */
    public static String randomUsernameAndDomain() {
        int length = 5 + random.nextInt(3);
        final int subsetLength = alphaDigitSubset.length;
        char[] rnd = new char[length];

        rnd[0] = (char) ('a' + random.nextInt(26));
        for (int i = 1; i < length; i++) {
            int idx = random.nextInt(subsetLength);
            rnd[i] = alphaDigitSubset[idx];
        }

        return new String(rnd);
    }

    /**
     * Random replacement string.
     *
     * @param identifier the identifier
     * @return the string
     */
    public static String randomReplacement(String identifier) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < identifier.length(); i++) {
            char c = identifier.charAt(i);

            if (Character.isDigit(c)) {
                builder.append(RandomGenerators.randomDigit());
            } else if (Character.isUpperCase(c)) {
                builder.append((char) ('A' + random.nextInt(25)));
            } else if (Character.isLowerCase(c)) {
                builder.append((char) ('a' + random.nextInt(25)));
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    /**
     * Deterministic replacement string.
     *
     * @param identifier the identifier
     * @return the string
     */
    public static String deterministicReplacement(String identifier) {

        try {
            MessageDigest salt = MessageDigest.getInstance("SHA-256");
            salt.update(identifier.getBytes(StandardCharsets.UTF_8));
            String digest = bytesToHex(salt.digest());

            if (digest.length() >= identifier.length()) {
                StringBuilder replacement = new StringBuilder();

                while (replacement.length() < identifier.length()) {
                    replacement.append(digest);
                }

                digest = replacement.toString();
            }

            return digest.substring(0, identifier.length());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate random url string.
     *
     * @return the string
     */
    public static String generateRandomURL() {
        String host = RandomGenerators.randomUIDGenerator(10);
        String tld = getRandomTLD();

        String builder = "http://" + host +
                '.' +
                tld;
        return builder;
    }

    /**
     * Random date milliseconds long.
     *
     * @return the long
     */
    public static long randomDateMilliseconds() {
        long currentMillis = System.currentTimeMillis();
        return currentMillis - (long) random.nextInt(100) * 365 * 24 * 60 * 60 * 1000;
    }

    /**
     * Generate random date string.
     *
     * @return the string
     */
    public static String generateRandomDate() {
        Date date = new Date(randomDateMilliseconds());
        return date.toString();
    }

    /**
     * Generate random date string.
     *
     * @param dateFormat the date format
     * @return the string
     */

    public static String generateRandomDate(DateTimeFormatter dateFormat) {
        LocalDateTime dt = Instant.ofEpochMilli(randomDateMilliseconds()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dt.format(dateFormat);
    }

    /**
     * Generate random digit sequence string.
     *
     * @param length the length
     * @return the string
     */
    public static String generateRandomDigitSequence(int length) {
        /* TODO missing tests */
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(RandomGenerators.randomDigit());
        }

        return builder.toString();
    }

    public static LatitudeLongitude generateRandomCoordinateRandomDirection(
            LatitudeLongitude latitudeLongitude, int distance) {

        double radian = Math.random() * 2.0 * Math.PI;
        double latitude = latitudeLongitude.getLatitude();
        double longitude = latitudeLongitude.getLongitude();

        return generateRandomCoordinateFromBearing(latitude, longitude, radian, distance);

    }

    public static LatitudeLongitude generateRandomCoordinateFromBearing(
            double latitude, double longitude, double radian, int distance) {

        double delta = (double) distance / GeoUtils.getR();
        double theta = Math.toRadians(radian);

        double f1 = Math.toRadians(latitude);
        double l1 = Math.toRadians(longitude);

        double f2 = Math.asin(Math.sin(f1) * Math.cos(delta) +
                Math.cos(f1) * Math.sin(delta) * Math.cos(theta));

        double l2 = l1 + Math.atan2(Math.sin(theta) * Math.sin(delta) * Math.cos(f1),
                Math.cos(delta) - Math.sin(f1) * Math.sin(f2));
        // normalise to -180..+180
        l2 = (l2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

        return new LatitudeLongitude(Math.toDegrees(f2), Math.toDegrees(l2));

    }

    public static LatitudeLongitude generateRandomCoordinateRandomDirection(
            double latitude, double longitude, int distance) {

        double radian = Math.random() * 2.0 * Math.PI;
        return generateRandomCoordinateFromBearing(latitude, longitude, radian, distance);
    }

    public static LatitudeLongitude generateRandomCoordinate(
            LatitudeLongitude latitudeLongitude, int minimumOffsetRadius, int maximumOffsetRadius) {
        return generateRandomCoordinate(latitudeLongitude.getLatitude(), latitudeLongitude.getLongitude(), minimumOffsetRadius, maximumOffsetRadius);

    }

    public static LatitudeLongitude generateRandomCoordinate(
            Double latitude, Double longitude, int minimumOffsetRadius, int maximumOffsetRadius) {

        while (true) {
            LatitudeLongitude latitudeLongitude = generateRandomCoordinate(latitude, longitude, maximumOffsetRadius);
            Double distance = GeoUtils.latitudeLongitudeDistance(latitude, longitude, latitudeLongitude.getLatitude(),
                    latitudeLongitude.getLongitude());

            if (distance >= minimumOffsetRadius) {
                return latitudeLongitude;
            }

        }

    }

    /**
     * Generate random coordinate latitude longitude.
     *
     * @param latitude     the latitude
     * @param longitude    the longitude
     * @param offsetRadius the offset radius
     * @return the latitude longitude
     */
    public static LatitudeLongitude generateRandomCoordinate(
            Double latitude, Double longitude, int offsetRadius) {

        double radiusInDegrees = offsetRadius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);

        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(longitude);

        double foundLatitude = y + latitude;
        if (foundLatitude > 90.0) {
            double diff = foundLatitude - 90.0;
            foundLatitude = Math.abs(-90 + diff);
        }

        double foundLongitude = new_x + longitude;
        if (foundLongitude > 180) {
            double diff = foundLongitude - 180.0;
            foundLongitude = Math.abs(-180 + diff);
        }

        return new LatitudeLongitude(foundLatitude, foundLongitude, LatitudeLongitudeFormat.DECIMAL);
    }

    /**
     * Generate random coordinate latitude longitude.
     *
     * @param latitudeLongitude the latitude longitude
     * @param offsetRadius      the offset radius
     * @return the latitude longitude
     */
    public static LatitudeLongitude generateRandomCoordinate(
            LatitudeLongitude latitudeLongitude, int offsetRadius
    ) {
        LatitudeLongitude randomLatitudeLongitude = generateRandomCoordinate(latitudeLongitude.getLatitude(), latitudeLongitude.getLongitude(),
                offsetRadius);
        randomLatitudeLongitude.setFormat(latitudeLongitude.getFormat());
        return randomLatitudeLongitude;
    }

    /**
     * Generate random coordinate latitude longitude.
     *
     * @return the latitude longitude
     */
    public static LatitudeLongitude generateRandomCoordinate() {
        Double latitude = (double) random.nextInt(90);
        Double longitude = (double) random.nextInt(180);

        if (random.nextBoolean()) {
            latitude = -latitude;
        }

        if (random.nextBoolean()) {
            longitude = -longitude;
        }

        return new LatitudeLongitude(latitude, longitude, LatitudeLongitudeFormat.DECIMAL);
    }

    private static String generateRandomHost(String hostTemplate, int preserveSubdomains) {
        String[] domains = hostTemplate.split("\\.");
        int domainsLength = domains.length;

        // we preserve everything
        if (preserveSubdomains >= domainsLength) {
            return hostTemplate;
        }

        for (int i = 0; i < (domainsLength - preserveSubdomains); i++) {
            domains[i] = RandomGenerators.randomUIDGenerator(domains[i].length());
        }

        return StringUtils.join(domains, ".");
    }

    /**
     * Random hostname generator string.
     *
     * @param hostname        the hostname
     * @param preserveDomains the preserve domains
     * @return the string
     */
    public static String randomHostnameGenerator(String hostname, int preserveDomains) {

        if (preserveDomains == -1) {
            return hostname;
        }

        int idx;
        int preserveSubdomains = Math.max(preserveDomains - 1, 0);

        if (ipAddressIdentifier.isIPv4(hostname)) {
            return ipAddressMaskingProvider.directMask(hostname, true);
        } else if (ipAddressIdentifier.isIPv6(hostname)) {
            return ipAddressMaskingProvider.directMask(hostname, false);
        }


        if (preserveDomains == 0) {
            String builder = generateRandomHost(hostname, preserveSubdomains) + '.' +
                    tldManager.getRandomTLD();
            return builder;
        }

        String tld = tldManager.getTLD(hostname);
        if (tld == null || (idx = hostname.indexOf(tld)) == 0) {
            return generateRandomHost(hostname, preserveDomains);
        }

        hostname = hostname.substring(0, idx - 1);

        String builder = generateRandomHost(hostname, preserveSubdomains) + '.' +
                tld;
        return builder;
    }

    /**
     * Random uid generator with inclusions string.
     *
     * @param length the length
     * @param subset the subset
     * @return the string
     */
    public static String randomUIDGeneratorWithInclusions(int length, char[] subset) {
        int subsetLength = subset.length;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(subsetLength);
            char nextRandom = subset[idx];
            builder.append(nextRandom);
        }
        return builder.toString();
    }

    /**
     * Random uid generator string.
     *
     * @param length             the length
     * @param excludedCharacters the excluded characters
     * @return the string
     */
    public static String randomUIDGenerator(int length, char[] excludedCharacters) {
        StringBuilder builder = new StringBuilder();
        char nextRandom;

        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                nextRandom = (char) ('a' + random.nextInt(26));
            } else {
                nextRandom = (char) ('0' + random.nextInt(10));
            }

            if (excludedCharacters != null) {
                if (contains(excludedCharacters, nextRandom)) {
                    i--;
                    continue;
                }
            }

            builder.append(nextRandom);
        }

        return builder.toString();

    }

    /**
     * Random uid generator string.
     *
     * @param length the length
     * @return the string
     */
    public static String randomUIDGenerator(int length) {
        return randomUIDGenerator(length, null);
    }

    /* Creates a random sequence of hex pairs, each pair is a value from 0 to 255 */
    public static String randomHexSequence(int length) {
        if (length == 0) {
            return "";
        }

        Random random = new SecureRandom();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(String.format("%02x", random.nextInt(256)));
        }

        return builder.toString();
    }
}
