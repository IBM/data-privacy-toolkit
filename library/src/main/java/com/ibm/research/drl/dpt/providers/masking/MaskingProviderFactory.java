/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.dicom.CSMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.DAMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.DTMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.LOMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.PNMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.SHMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.SQMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.TMMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.dicom.UIMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.persistence.AbstractPersistentMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.persistence.DBPersistentMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.persistence.FileBackedPersistentMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.persistence.LocallyPersistentMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.persistence.causal.CausalOrderingConsistentMaskingProvider;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MaskingProviderFactory implements Serializable {
    private static final Logger logger = LogManager.getLogger(MaskingProviderFactory.class);
    private static final SecureRandom random = new SecureRandom();

    private static final DummyMaskingProvider dummy = new DummyMaskingProvider();
    private final ConfigurationManager configurationManager;

    private final Map<String, DataMaskingTarget> identifiedTypes;
    private final Map<String, MaskingProvider> cachedProviders;
    private final Map<ProviderType, Class<? extends MaskingProvider>> registeredMaskingProviders;
    private final HashMap<String, MaskingProvider> globalPersistent;

    private static final String PERSISTENCE_TYPE_CONF_NAME = "persistence.type";

    @Deprecated(forRemoval = true)
    public MaskingProviderFactory() {
        this(new ConfigurationManager(), Collections.emptyMap());
    }

    /**
     * Instantiates a new Masking provider factory.
     *
     * @param configurationManager the configuration manager
     * @param identifiedTypes      the identified types
     */
    public MaskingProviderFactory(ConfigurationManager configurationManager, Map<String, DataMaskingTarget> identifiedTypes) {
        this.configurationManager = configurationManager;
        this.identifiedTypes = identifiedTypes;

        cachedProviders = new HashMap<>();
        registeredMaskingProviders = new HashMap<>();
        globalPersistent = new HashMap<>();
    }

    /**
     * Get masking provider.
     *
     * @param fieldName the field name
     * @return the masking provider
     */
    @Deprecated
    public MaskingProvider get(final String fieldName) {
        final DataMaskingTarget dataMaskingTarget = identifiedTypes.get(fieldName);
        final ProviderType providerType = (null == dataMaskingTarget) ? null : dataMaskingTarget.getProviderType();

        return get(fieldName, providerType);
    }

    public synchronized MaskingProvider get(final String fieldName, ProviderType providerType) {
        if (!cachedProviders.containsKey(fieldName)) {
            MaskingConfiguration maskingConfiguration = configurationManager.getFieldConfiguration(fieldName);
            cachedProviders.put(fieldName, get(providerType, maskingConfiguration));
        }

        return cachedProviders.get(fieldName);
    }

    /**
     * Gets configuration for field.
     *
     * @param field the field
     * @return the configuration for field
     */
    public MaskingConfiguration getConfigurationForField(String field) {
        return configurationManager.getFieldConfiguration(field);
    }

    /**
     * Register masking provider class.
     *
     * @param maskingProviderClass the masking provider class
     * @param providerType         the provider type
     */
    public synchronized void registerMaskingProviderClass(Class<? extends MaskingProvider> maskingProviderClass, ProviderType providerType) {
        logger.info("Registering masking provider {} using provider class {}", providerType.toString(), maskingProviderClass.getCanonicalName());

        registeredMaskingProviders.put(providerType, maskingProviderClass);
    }

    /**
     * Get masking provider.
     *
     * @param providerType  the provider type
     * @param configuration the configuration
     * @return the masking provider
     */
    public synchronized MaskingProvider get(ProviderType providerType, MaskingConfiguration configuration) {
        if (null == providerType)
            providerType = ProviderType.valueOf(configuration.getStringValue("default.masking.provider"));

        final MaskingProvider provider;

        if (configuration.getBooleanValue("persistence.export")) {
            provider = getPersistentMaskingProvider(configuration, providerType);
        } else {
            provider = getProviderFromType(providerType, configuration);
        }

        return provider;
    }


    private String createNamespaceID(MaskingConfiguration configuration) {
        String namespace = configuration.getStringValue("persistence.namespace");
        String type = configuration.getStringValue(PERSISTENCE_TYPE_CONF_NAME);

        if (!type.equals("file")) {
            return namespace;
        }

        return new File(configuration.getStringValue("persistence.file"), namespace).getAbsolutePath();
    }

    private MaskingProvider getPersistentMaskingProvider(MaskingConfiguration configuration, ProviderType providerType) {
        String namespace = configuration.getStringValue("persistence.namespace");

        if (namespace == null) {
            throw new IllegalArgumentException("persistence.namespace is not defined");
        }

        String namespaceID = createNamespaceID(configuration);

        if (!globalPersistent.containsKey(namespaceID)) {
            logger.info("Persistent provider for " + namespaceID + " does not exist. Creating..");
            globalPersistent.put(namespaceID, createPersistentWrapper(getProviderFromType(providerType, configuration), configuration));
        }

        return globalPersistent.get(namespaceID);
    }

    private MaskingProvider createPersistentWrapper(MaskingProvider providerFromType, MaskingConfiguration configuration) {
        final AbstractPersistentMaskingProvider persistentMaskingProvider;
        switch (AbstractPersistentMaskingProvider.PersistencyType.valueOf(configuration.getStringValue(PERSISTENCE_TYPE_CONF_NAME).toUpperCase())) {
            case MEMORY:
                persistentMaskingProvider = new LocallyPersistentMaskingProvider(providerFromType, configuration);
                break;
            case FILE:
                persistentMaskingProvider = new FileBackedPersistentMaskingProvider(providerFromType, configuration);
                break;
            case DATABASE:
                persistentMaskingProvider = new DBPersistentMaskingProvider(providerFromType, configuration);
                break;
            case CAUSAL:
                persistentMaskingProvider = new CausalOrderingConsistentMaskingProvider(providerFromType, configuration);
                break;
            default:
                throw new MisconfigurationException("Unknown persistence type: " + configuration.getStringValue(PERSISTENCE_TYPE_CONF_NAME));
        }

        return persistentMaskingProvider;
    }

    private MaskingProvider getProviderFromType(ProviderType providerType, MaskingConfiguration configuration) {
        switch (providerType.name()) {
            case "CONDITIONAL":
                return new ConditionalMaskingProvider(configuration, this);
            case "NAME":
                return new NameMaskingProvider(configuration, this);

            case "CREDIT_CARD":
                return new CreditCardMaskingProvider(random, configuration);

            case "NATIONAL_ID":
                return new NationalIdentifierMaskingProvider(random, configuration);

            case "REPLACE":
                return new ReplaceMaskingProvider(random, configuration);

            case "NULL":
                return new NullMaskingProvider(random, configuration);

            case "SHIFT":
                return new ShiftMaskingProvider(random, configuration);

            case "HASH":
                return new HashMaskingProvider(random, configuration);

            case "PHONE":
                return new PhoneMaskingProvider(random, configuration);

            case "EMAIL":
                return new EmailMaskingProvider(configuration, this);

            case "URL":
                return new URLMaskingProvider(configuration, this);

            case "IP_ADDRESS":
                return new IPAddressMaskingProvider(random, configuration);

            case "VIN":
                return new VINMaskingProvider(random, configuration);

            case "COUNTRY":
                return new CountryMaskingProvider(random, configuration);

            case "COUNTY":
                return new CountyMaskingProvider(random, configuration);

            case "STATES_US":
                return new StatesUSMaskingProvider(random, configuration);

            case "GENDER":
                return new GenderMaskingProvider(random, configuration);

            case "DATETIME":
                return new DateTimeMaskingProvider(random, configuration);

            case "ADDRESS":
                return new AddressMaskingProvider(random, configuration);

            case "RANDOM":
                return new RandomMaskingProvider(random, configuration);

            case "GUID":
                return new GUIDMaskingProvider(random, configuration);

            case "IBAN":
                return new IBANMaskingProvider(random, configuration);

            case "OCCUPATION":
                return new OccupationMaskingProvider(random, configuration);

            case "IMEI":
                return new IMEIMaskingProvider(random, configuration);

            case "BINNING":
                return new BinningMaskingProvider(configuration);

            case "SSN_UK":
                return new SSNUKMaskingProvider(random, configuration);

            case "SSN_US":
                return new SSNUSMaskingProvider(random, configuration);

            case "CREDIT_CARD_TYPE":
                return new CreditCardTypeMaskingProvider(random, configuration);

            case "CITY":
                return new CityMaskingProvider(random, configuration);

            case "CONTINENT":
                return new ContinentMaskingProvider(random, configuration);

            case "ICD":
            case "ICDv9":
                return new ICDv9MaskingProvider(random, configuration);

            case "RELIGION":
                return new ReligionMaskingProvider(random, configuration);

            case "MARITAL_STATUS":
                return new MaritalStatusMaskingProvider(random, configuration);

            case "RACE":
                return new RaceEthnicityMaskingProvider(random, configuration);

            case "MAC_ADDRESS":
                return new MACAddressMaskingProvider(random, configuration);

            case "ATC":
                return new ATCMaskingProvider(random, configuration);

            case "MEDICINE":
                return new MedicineMaskingProvider(random, configuration);

            case "LATITUDE_LONGITUDE":
                return new LatitudeLongitudeMaskingProvider(random, configuration);

            case "IMSI":
                return new IMSIMaskingProvider(random, configuration);

            case "HOSPITAL":
                return new HospitalMaskingProvider(random, configuration);

            case "SWIFT":
                return new SWIFTCodeMaskingProvider(random, configuration);

            case "FHIR":
                return new FHIRMaskingProvider(random, configuration, this);

            case "HASHINT":
                return new HashIntMaskingProvider(random, configuration);

            case "REDACT":
                return new RedactMaskingProvider(random, configuration);

            case "MONETARY":
                return new MonetaryMaskingProvider(configuration);

            case "ZIPCODE":
                return new ZIPCodeMaskingProvider(configuration);

            case "NUMBERVARIANCE":
                return new NumberVarianceMaskingProvider(random, configuration);

            case "BOOLEAN":
                return new BooleanMaskingProvider(random, configuration);

            case "DICOM_PN":
                return new PNMaskingProvider(random, configuration, this);

            case "DICOM_DA":
                return new DAMaskingProvider(random, configuration);

            case "DICOM_CS":
                return new CSMaskingProvider(random, configuration);

            case "DICOM_DT":
                return new DTMaskingProvider(random, configuration);

            case "DICOM_SH":
                return new SHMaskingProvider(random, configuration);

            case "DICOM_LO":
                return new LOMaskingProvider(random, configuration, this);

            case "DICOM_SQ":
                return new SQMaskingProvider(random, configuration);

            case "DICOM_TM":
                return new TMMaskingProvider(random, configuration);

            case "DICOM_UI":
                return new UIMaskingProvider(random, configuration);

            case "TEMPORAL":
                return new TemporalAnnotationMaskingProvider(random, configuration);

            case "DIFFERENTIAL_PRIVACY":
                try {
                    Constructor<? extends MaskingProvider> constructor =
                            (Constructor<? extends MaskingProvider>) Class.forName("com.ibm.research.drl.dpt.providers.masking.DifferentialPrivacyMaskingProvider")
                                    .getConstructor(MaskingProviderFactory.class, MaskingConfiguration.class, Map.class);
                    return constructor.newInstance(this, configuration, identifiedTypes);
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                         InstantiationException | InvocationTargetException e) {
                    logger.error("Unable to instantiate masking provider for FREE_TEXT");

                    throw new RuntimeException(e);
                }

            case "SUPPRESS_FIELD":
                return new SuppressFieldMaskingProvider(random, configuration);

            case "DICTIONARY_BASED":
                return new DictionaryBasedMaskingProvider(random, configuration);

            case "DECIMAL_ROUNDING":
                return new DecimalTrimmingMaskingProvider(random, configuration);

            case "RATIO_BASED":
                return new RatioBasedMaskingProvider(random, configuration);

            case "GENERALIZATION":
                try {
                    Constructor<? extends MaskingProvider> constructor =
                            (Constructor<? extends MaskingProvider>) Class.forName("com.ibm.research.drl.dpt.providers.masking.GeneralizationMaskingProvider")
                                    .getConstructor(MaskingProviderFactory.class, MaskingConfiguration.class, Map.class);
                    return constructor.newInstance(this, configuration, identifiedTypes);
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                         InstantiationException | InvocationTargetException e) {
                    logger.error("Unable to instantiate masking provider for FREE_TEXT");

                    throw new RuntimeException(e);
                }

            case "TIMESTAMP":
                return new TimeStampMaskingProvider(random, configuration);

            case "FREE_TEXT":
                try {
                    Constructor<? extends MaskingProvider> constructor =
                            (Constructor<? extends MaskingProvider>) Class.forName("com.ibm.research.drl.dpt.providers.masking.FreeTextMaskingProvider")
                                    .getConstructor(MaskingProviderFactory.class, MaskingConfiguration.class, Map.class);
                    return constructor.newInstance(this, configuration, identifiedTypes);
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                         InstantiationException | InvocationTargetException e) {
                    logger.error("Unable to instantiate masking provider for FREE_TEXT");

                    throw new RuntimeException(e);
                }

            case "TAG":
                return new TagMaskingProvider(random, configuration);

            case "ANNOTATE":
                try {
                    Constructor<? extends MaskingProvider> constructor =
                            (Constructor<? extends MaskingProvider>) Class.forName("com.ibm.research.drl.dpt.providers.masking.AnnotateMaskingProvider").getConstructor(SecureRandom.class, MaskingConfiguration.class);
                    return constructor.newInstance(random, configuration);
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                         InstantiationException | InvocationTargetException e) {
                    logger.error("Unable to load AnnotateMaskingProvider", e);
                    throw new RuntimeException(e);
                }
            case "ICDv10":
                try {
                    Constructor<? extends MaskingProvider> constructor =
                            (Constructor<? extends MaskingProvider>) Class.forName("com.ibm.research.drl.dpt.providers.masking.ICDv10MaskingProvider").getConstructor(MaskingConfiguration.class, MaskingProviderFactory.class);
                    return constructor.newInstance(configuration, this);
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                         InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            case "HADOOP_DICTIONARY_BASED":
                try {
                    Constructor<? extends MaskingProvider> constructor =
                            (Constructor<? extends MaskingProvider>) Class.forName("com.ibm.research.drl.dpt.spark.masking.provider.HadoopDictionaryBasedMaskingProvider").getConstructor(SecureRandom.class, MaskingConfiguration.class);
                    return constructor.newInstance(random, configuration);
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                         InstantiationException | InvocationTargetException e) {
                    logger.error("Unable to load HadoopDictionaryBasedMaskingProvider", e);
                    throw new RuntimeException(e);
                }

            case "DUMMY":
            case "NO_OP":
                return dummy;

            case "UNSUPPORTED":
            default:
                if (registeredMaskingProviders.containsKey(providerType)) {
                    final Class<? extends MaskingProvider> maskingProviderClass = registeredMaskingProviders.get(providerType);

                    try {
                        return maskingProviderClass.
                                getDeclaredConstructor(SecureRandom.class, MaskingConfiguration.class).
                                newInstance(random, configuration);
                    } catch (Exception e) {
                        logger.error("Unable to load registered masking provider with type " + providerType);
                    }
                }

                try {
                    logger.warn("Falling back to default.masking.provider");
                    return getProviderFromType(ProviderType.valueOf(configuration.getStringValue("default.masking.provider")), configuration);
                } catch (Exception e) {
                    logger.error("Error getting default masking provider", e);
                    throw new RuntimeException(e);
                }
        }
    }
}
