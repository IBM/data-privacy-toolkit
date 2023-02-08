/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

/**
 * The type Provider type.
 */
public final class ProviderType implements Serializable {
    private static final Map<String, ProviderType> registeredTypes = new HashMap<>();

    /**
     * The constant UNKNOWN.
     */
    public static final ProviderType UNKNOWN = new ProviderType("UNKNOWN", "Unknown", "for internal puposes", TypeClass.CATEGORICAL, true);
    /**
     * The constant EMPTY.
     */
    public static final ProviderType EMPTY = new ProviderType("EMPTY", "Empty", "Empty", TypeClass.CATEGORICAL, true);

    public static final ProviderType SORT_CODE = new ProviderType("SORT_CODE", "SORT CODE", "");

    public static final ProviderType AGE = new ProviderType("AGE", "Age", "Masks age values", TypeClass.NUMERICAL);
    /**
     * The constant NUMERIC.
     */
    public static final ProviderType NUMERIC = new ProviderType("NUMERIC", "Numeric", "Masks numerical values", TypeClass.NUMERICAL);
    /**
     * The constant NAME.
     */
    public static final ProviderType NAME = new ProviderType("NAME", "Name", "Masks names (with gender preservation) and surnames");

    public static final ProviderType ANIMAL = new ProviderType("ANIMAL", "Animal", "Masks animal species");
    /**
     * The constant EMAIL.
     */
    public static final ProviderType EMAIL = new ProviderType("EMAIL", "E-mail", "Masks e-mail addresses, supports preservation of domain names");
    /**
     * The constant CREDIT_CARD.
     */
    public static final ProviderType CREDIT_CARD = new ProviderType("CREDIT_CARD", "Credit Card", "Masks credit cards, support vendor preservation");
    /**
     * The constant ADDRESS.
     */
    public static final ProviderType ADDRESS = new ProviderType("ADDRESS", "Addresses", "Masks addresses, supports free text and PO BOX formats");
    /**
     * The constant NATIONAL_ID.
     */
    public static final ProviderType NATIONAL_ID = new ProviderType("NATIONAL_ID", "National ID", "Masks national ID");
    /**
     * The constant IP_ADDRESS.
     */
    public static final ProviderType IP_ADDRESS = new ProviderType("IP_ADDRESS", "IP address", "Masks IP addresses, supports prefix preservation");
    /**
     * The constant URL.
     */
    public static final ProviderType URL = new ProviderType("URL", "URLs", "Masks URLs, supports preservation of domain names and usernames");
    /**
     * The constant VIN.
     */
    public static final ProviderType VIN = new ProviderType("VIN", "Vehicle Identification Number", "Masks VINs, support vendor preservation");
    /**
     * The constant LATITUDE_LONGITUDE.
     */
    public static final ProviderType LATITUDE_LONGITUDE = new ProviderType("LATITUDE_LONGITUDE", "Latitude/Longitude", "Masks latitude/longitude pairs, supports multiple coordinate formats", TypeClass.NUMERICAL);
    /**
     * The constant COUNTRY.
     */
    public static final ProviderType COUNTRY = new ProviderType("COUNTRY", "Country", "Masks country names, supports masking based on closest countries");
    /**
     * The constant DATETIME.
     */
    public static final ProviderType DATETIME = new ProviderType("DATETIME", "Date/Time", "Masks date/time, supports multiple datetime formats or user-specified ones", TypeClass.NUMERICAL);
    /**
     * The constant CITY.
     */
    public static final ProviderType CITY = new ProviderType("CITY", "City", "Masks city names, support masking based on closest cities");
    /**
     * The constant CONTINENT.
     */
    public static final ProviderType CONTINENT = new ProviderType("CONTINENT", "Continent", "Masks continent names");
    /**
     * The constant DUMMY.
     */
    public static final ProviderType DUMMY = new ProviderType("DUMMY", "Dummy", "Reflects original value back");

    public static final ProviderType BINNING = new ProviderType("BINNING", "Binning", "Bins numerical values");
    /**
     * The constant ICDv9.
     */
    public static final ProviderType ICDv9 = new ProviderType("ICDv9", "ICDv9", "Masks ICD v9 codes, supports preservation of chapter and section");
    /**
     * The constant ICDv10.
     */
    public static final ProviderType ICDv10 = new ProviderType("ICDv10", "ICDv10", "Masks ICD v10 codes, supports preservation of chapter and section");
    /**
     * The constant PHONE.
     */
    public static final ProviderType PHONE = new ProviderType("PHONE", "Telephone numbers", "Masks phone numbers, supports preservation of country codes and areas");
    /**
     * The constant HOSPITAL.
     */
    public static final ProviderType HOSPITAL = new ProviderType("HOSPITAL", "Hospitals and medical centers", "Masks names of hospitals and medical centers");
    /**
     * The constant RANDOM.
     */
    public static final ProviderType RANDOM = new ProviderType("RANDOM", "Random", "Changes characters of the value randomly");
    /**
     * The constant RELIGION.
     */
    public static final ProviderType RELIGION = new ProviderType("RELIGION", "Religion", "Masks religion names");
    /**
     * The constant MARITAL_STATUS.
     */
    public static final ProviderType MARITAL_STATUS = new ProviderType("MARITAL_STATUS", "Marital Status", "Masks marital status");
    /**
     * The constant RACE
     */
    public static final ProviderType RACE = new ProviderType("RACE", "Race/Ethnicity", "Masks races and ethnicities");
    /**
     * The constant MAC_ADDRESS.
     */
    public static final ProviderType MAC_ADDRESS = new ProviderType("MAC_ADDRESS", "MAC Address", "Masks MAC addresses, supports vendor preservation");
    /**
     * The constant CREDIT_CARD_TYPE.
     */
    public static final ProviderType CREDIT_CARD_TYPE = new ProviderType("CREDIT_CARD_TYPE", "Credit Card type", "Mask credit card vendor names");
    /**
     * The constant IBAN.
     */
    public static final ProviderType IBAN = new ProviderType("IBAN", "IBAN", "Masks IBAN values");
    /**
     * The constant IMEI.
     */
    public static final ProviderType IMEI = new ProviderType("IMEI", "IMEI", "Masks IMEI values, supports vendor preservation");
    /**
     * The constant SSN_UK.
     */
    public static final ProviderType SSN_UK = new ProviderType("SSN_UK", "Social Security Number UK", "Masks social security numbers");
    /**
     * The constant SSN_US.
     */
    public static final ProviderType SSN_US = new ProviderType("SSN_US", "Social Security Number US", "Masks social security numbers");
    /**
     * The constant OCCUPATION.
     */
    public static final ProviderType OCCUPATION = new ProviderType("OCCUPATION", "Occupation", "Masks occupations");
    /**
     * The constant SWIFT.
     */
    public static final ProviderType SWIFT = new ProviderType("SWIFT", "SWIFT code", "Masks SWIFT codes, support country preservation");
    /**
     * The constant GUID.
     */
    public static final ProviderType GUID = new ProviderType("GUID", "GUID", "Replaces values with a GUID");
    /**
     * The constant UNSUPPORTED.
     */
    public static final ProviderType UNSUPPORTED = new ProviderType("UNSUPPORTED", "dummy", "for internal purposes", TypeClass.CATEGORICAL, true);
    /**
     * The constant ATC.
     */
    public static final ProviderType ATC = new ProviderType("ATC", "ATC codes", "Masks ATC codes");
    /**
     * The constant MEDICINE.
     */
    public static final ProviderType MEDICINE = new ProviderType("MEDICINE", "Drug list", "Masks name of drugs");
    /**
     * The constant IMSI.
     */
    public static final ProviderType IMSI = new ProviderType("IMSI", "IMSI", "Masks IMSI values");
    /**
     * The constant REPLACE.
     */
    public static final ProviderType REPLACE = new ProviderType("REPLACE", "Replace", "Replaces and preserves parts of the value", TypeClass.CATEGORICAL, false);

    /**
     * The constant NULL.
     */
    public static final ProviderType NULL = new ProviderType("NULL", "Null", "Replaces the value with an empty one", TypeClass.CATEGORICAL, false);

    public static final ProviderType HASH = new ProviderType("HASH", "Hash", "Hashes the value", TypeClass.CATEGORICAL, false);

    public static final ProviderType SHIFT = new ProviderType("SHIFT", "Shift", "Shifts the value", TypeClass.NUMERICAL, false);

    /**
     * The constant STATES_US
     */
    public static final ProviderType STATES_US = new ProviderType("STATES_US", "US States", "Replaces the US States value", TypeClass.CATEGORICAL, false);

    public static final ProviderType GENDER = new ProviderType("GENDER", "Gender", "Replaces the genders");

    public static final ProviderType COUNTY = new ProviderType("COUNTY", "Counties", "Replaces the county names");

    public static final ProviderType FHIR = new ProviderType("FHIR", "FHIR objects", "Masks FHIR objects");

    public static final ProviderType ORGANIZATION = new ProviderType("ORGANIZATION", "Organizations", "Masks organizations");
    public static final ProviderType GENERIC_LOCATION = new ProviderType("GENERIC_LOCATION", "Locations", "Masks generic locations");
    public static final ProviderType LOCATION = new ProviderType("LOCATION", "Locations", "Masks generic locations");

    public static final ProviderType HASHINT = new ProviderType("HASHINT", "Hash and convert to int", "Hashes the incoming (integer) value and returns an integer (as a string)");

    public static final ProviderType OPE = new ProviderType("OPE", "Order Preserving Encryption", "Encrypts numerical, positive, integer value in an order preserving manner");

    public static final ProviderType REDACT = new ProviderType("REDACT", "Redact provider type", "Remove the value replacing it with an appropriate number of X");

    public static final ProviderType MONETARY = new ProviderType("MONETARY", "Monetary value provider type", "Redact the monetary value replacing the digits");

    public static final ProviderType ZIPCODE = new ProviderType("ZIPCODE", "ZIP codes", "Replaces the ZIP codes");
    public static final ProviderType POSTCODE = new ProviderType("POSTCODE", "UK post codes", "Replaces the UK Post codes");

    public static final ProviderType YOB = new ProviderType("YOB", "Year of birth", "Replaces the year of birth");

    public static final ProviderType NUMBERVARIANCE = new ProviderType("NUMBERVARIANCE", "Number variance", "Replaces numerical date with a random variance");

    public static final ProviderType BOOLEAN = new ProviderType("BOOLEAN", "Boolean", "Replaces boolean values with another random boolean value");


    public static final ProviderType STREET_TYPES = new ProviderType("STREET_TYPES", "Street types provider type", "Randomizes street types");

    public static final ProviderType DEPENDENT = new ProviderType("DEPENDENT", "Dependent provider type", "Randomizes dependents");

    public static final ProviderType FREE_TEXT = new ProviderType("FREE_TEXT", "Free text provider type", "Mask provider type");

    public static final ProviderType DAY = new ProviderType("DAY", "Day masking provider type", "Mask day provider type");
    public static final ProviderType MONTH = new ProviderType("MONTH", "Month masking provider type", "Mask month provider type");

    public static final ProviderType SYMPTOM = new ProviderType("SYMPTOM", "Symptom", "Mask symptoms");
    public static final ProviderType PROCEDURE = new ProviderType("PROCEDURE", "Procedure", "Mask procedures");

    public static final ProviderType HEIGHT = new ProviderType("HEIGHT", "Height", "Masks height values", TypeClass.NUMERICAL);

    public static final ProviderType MRN = new ProviderType("MRN", "Medical Record Number", "Medical record number");

    public static final ProviderType TEMPORAL = new ProviderType("TEMPORAL", "Temporal pattern", "Temporal pattern");

    public static final ProviderType TIME = new ProviderType("TIME", "Time pattern", "Time pattern");

    public static final ProviderType REPLACE_FIXED = new ProviderType("REPLACE_FIXED", "Replace fixed", "Replace fixed");

    public static final ProviderType CHAMELEON = new ProviderType("CHAMELEON", "Chameleon Pseudonyms", "Chameleon Pseudonyms");
    public static final ProviderType DIFFERENTIAL_PRIVACY = new ProviderType("DIFFERENTIAL_PRIVACY", "Differential Privacy", "e-differential privacy");

    public static final ProviderType SUPPRESS_FIELD = new ProviderType("SUPPRESS_FIELD", "Suppress field", "Suppress the entire field");

    public static final ProviderType DICTIONARY_BASED = new ProviderType("DICTIONARY_BASED", "Dictionary based",
            "Replaces values based on dictionaries");

    public static final ProviderType DECIMAL_ROUNDING = new ProviderType("DECIMAL_ROUNDING", "Decimal rounding",
            "Rounds decimal points");

    public static final ProviderType RATIO_BASED = new ProviderType("RATIO_BASED", "Ratio-based", "Ratio based");

    public static final ProviderType GENERALIZATION = new ProviderType("GENERALIZATION", "Generalization-based masking", "Generalization-based");

    public static final ProviderType PERSON = new ProviderType("PERSON", "Generic person", "Person");

    private final String name;
    private final String description;
    private final String friendlyName;
    private final int id;
    private final boolean forInternalPurposes;
    private final TypeClass typeClass;

    /**
     * Gets id.
     *
     * @return the id
     */
    @JsonIgnore
    public int getId() {
        return this.id;
    }

    /**
     * Name string.
     *
     * @return the string
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name();
    }

    /**
     * Gets friendly name.
     *
     * @return the friendly name
     */
    public String getFriendlyName() {
        return this.friendlyName;
    }

    private boolean isForInternalPurposes() {
        return forInternalPurposes;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets type class.
     *
     * @return the type class
     */
    public TypeClass getTypeClass() {
        return typeClass;
    }

    private ProviderType(String name, String friendlyName, String description, TypeClass typeClass, boolean forInternalPurposes) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.description = description;
        this.forInternalPurposes = forInternalPurposes;
        this.typeClass = typeClass;

        this.id = insertType(this);
    }

    private ProviderType(String name, String friendlyName, String description, TypeClass typeClass) {
        this(name, friendlyName, description, typeClass, false);
    }

    private ProviderType(String name, String friendlyName, String description) {
        this(name, friendlyName, description, TypeClass.CATEGORICAL, false);
    }

    private ProviderType(String name) {
        this(name, name, "");
    }


    @Override
    public boolean equals(Object o) {
        return !(null == o || !(o instanceof ProviderType)) && this.name.equals(((ProviderType) o).name());
    }

    @Override
    public String toString() {
        return name();
    }

    /**
     * Value of provider type.
     *
     * @param name the name
     * @return the provider type
     */
    public static synchronized ProviderType valueOf(String name) {
        if (!registeredTypes.containsKey(name)) {
            return new ProviderType(name);
        }

        return registeredTypes.get(name);
    }

    /**
     * Public values collection.
     *
     * @return the collection
     */
    public static synchronized Collection<ProviderType> publicValues() {
        Collection<ProviderType> providerTypes = new ArrayList<>();

        for (ProviderType p : registeredTypes.values()) {
            if (!p.isForInternalPurposes()) {
                providerTypes.add(p);
            }
        }

        return providerTypes;
    }

    /**
     * Values provider type [ ].
     *
     * @return the provider type [ ]
     */
    public static synchronized ProviderType[] values() {
        ProviderType[] values = new ProviderType[registeredTypes.size()];
        return registeredTypes.values().toArray(values);
    }

    /**
     * Register type provider type.
     *
     * @param typeName the type name
     * @return the provider type
     */
    public static ProviderType registerType(String typeName) {
        ProviderType providerType = valueOf(typeName);
        if (providerType != null) {
            return providerType;
        }

        return new ProviderType(typeName);
    }

    private static synchronized int insertType(ProviderType type) {
        if (!registeredTypes.containsKey(type.name())) {
            registeredTypes.put(type.name(), type);
            return registeredTypes.size();
        }

        return registeredTypes.get(type.name()).getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, friendlyName, id, forInternalPurposes, typeClass);
    }
}
