/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.providers.masking.ReplaceMaskingProvider;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * The type Default masking configuration.
 *
 * @author santonat
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"optionsByCategory","name"})
public final class DefaultMaskingConfiguration implements MaskingConfiguration, Serializable {
    private final Map<String, ConfigurationOption> optionMap;
    private final String name;
    private ConfigurationManager configurationManager;
    
    /**
     * Instantiates a new Default masking configuration.
     *
     * @param name the name
     */
    public DefaultMaskingConfiguration(String name) {
        this(null, name);
    }

    public DefaultMaskingConfiguration() {
        this(null, null);
    }

    /**
     * Instantiates a new Default masking configuration.
     */
    public DefaultMaskingConfiguration(ConfigurationManager configurationManager, String name) {
        this.configurationManager = configurationManager;

        this.optionMap = new HashMap<>();

        this.name = name;

        this.optionMap.put("default.masking.provider", new ConfigurationOption("RANDOM", "Default masking provider", "Defaults"));
        this.optionMap.put("fail.mode", new ConfigurationOption(FailMode.RETURN_EMPTY, "Fail mode", "Error handling"));
        
        this.optionMap.put("export.sampling", new ConfigurationOption(100, "Sampling percentage", "Export"));
        this.optionMap.put("export.relationships.perRecord", new ConfigurationOption(false, "Per record relationship extraction", "Export"));

        this.optionMap.put("generic.lookupTokens", new ConfigurationOption(false, "Lookup tokens to find them in other fields", "Generic"));
        this.optionMap.put("generic.lookupTokensIgnoreCase", new ConfigurationOption(false, "Lookup tokens to find them in other fields", "Generic"));
        this.optionMap.put("generic.lookupTokensFindAnywhere", new ConfigurationOption(false, "Lookup tokens to find them in other fields", "Generic"));
        this.optionMap.put("generic.lookupTokensSeparator", new ConfigurationOption("^", "Lookup tokens to find them in other fields", "Generic"));
        this.optionMap.put("generic.lookupTokensSources", new ConfigurationOption("", "Lookup tokens to find them in other fields", "Generic"));
        this.optionMap.put("generic.lookupTokensType", new ConfigurationOption("", "Type of the tokens", "Generic"));

        this.optionMap.put("ratiobased.mask.ratio", new ConfigurationOption(1.0, "Ratio to be used", "Ratio-based"));
        this.optionMap.put("ratiobased.mask.precisionDigits", new ConfigurationOption(-1, "Precision digits", "Ratio-based"));

        this.optionMap.put("persistence.export", new ConfigurationOption(false, "Persistence per export", "Persistence"));
        this.optionMap.put("persistence.schema", new ConfigurationOption(false, "Persistence per schema", "Persistence"));
        this.optionMap.put("persistence.namespace", new ConfigurationOption(null, "Persistence global namespace", "Persistence"));
        this.optionMap.put("persistence.type", new ConfigurationOption("memory", "Persistence type (memory or file)", "Persistence"));
        this.optionMap.put("persistence.file", new ConfigurationOption("null", "Persistence file storage", "Persistence"));
        this.optionMap.put("persistence.normalize.toLower", new ConfigurationOption(false, "Normalize persisted values to lower case before masking", "Persistence"));

        this.optionMap.put("persistence.database.connectionString", new ConfigurationOption("", "Persistence database connection host", "Persistence"));
        this.optionMap.put("persistence.database.username", new ConfigurationOption("", "Persistence database connection username", "Persistence"));
        this.optionMap.put("persistence.database.password", new ConfigurationOption("", "Persistence database connection password", "Persistence"));
        this.optionMap.put("persistence.database.driverName", new ConfigurationOption("", "Persistence database connection driver name", "Persistence"));
        this.optionMap.put("persistence.database.cacheLimit", new ConfigurationOption(0, "Persistence database cache limit", "Persistence"));

        this.optionMap.put("persistence.causal.backend", new ConfigurationOption("db", "Persistence causal backend", "Persistence"));

        this.optionMap.put("age.mask.redactNumbers", new ConfigurationOption(true, "Redact age", "Age"));
        this.optionMap.put("age.mask.randomNumbers", new ConfigurationOption(false, "Randomize age", "Age"));

        this.optionMap.put("excel.mask.ignoreNonExistent", new ConfigurationOption(true, "Ignore non-existent cells", "Excel"));
       
        this.optionMap.put("decimalrounding.mask.rules", new ConfigurationOption("", "Decimal rounding masking rules", "Decimal Rounding"));
        
        this.optionMap.put("dictionaryBased.mask.filename", new ConfigurationOption("", "Dictionary filename", "Dictionary-based masking provider"));
        
        this.optionMap.put("hl7.prefixGUID", new ConfigurationOption("", "hl7.prefixGUID", "HL7"));

        /* DICOM options */
        this.optionMap.put("dicom.prefixGUID", new ConfigurationOption("", "dicom.prefixGUID", "DICOM"));
        this.optionMap.put("dicom.cs.entityType", new ConfigurationOption("GENDER", "dicom.cs.entityType", "DICOM"));
        this.optionMap.put("dicom.lo.entityType", new ConfigurationOption("NAME", "dicom.cs.entityType", "DICOM"));
        this.optionMap.put("dicom.image.mask", new ConfigurationOption(false, "Mask image part", "DICOM"));
        this.optionMap.put("dicom.image.sensitiveTags", new ConfigurationOption("(0010,0010):(0008,0080):(0010,0020):(0008,1070):(0008,1060)", "Mask image tags with sensitive info", "DICOM"));
        this.optionMap.put("dicom.image.sensitiveTypes", new ConfigurationOption("", "Mask image tags based on types", "DICOM"));
        
        /* FHIR options */

        this.optionMap.put("fhir.prefixGUID", new ConfigurationOption("--", "fhir.prefixGUID", "FHIR"));

        this.optionMap.put("fhir.resources.enabled", new ConfigurationOption("Device,DeviceMetric,DeviceComponent,Patient,Practitioner,Location,Organization,Observation,Medication,MedicationOrder,MedicationAdministration,Contract,QuestionnaireResponse,BodySite,Group,CarePlan,AuditEvent", "fhir.resources.enabled", "FHIR"));

        this.optionMap.put("fhir.basePath.Patient", new ConfigurationOption("/fhir/Patient","fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.CarePlan", new ConfigurationOption("/fhir/CarePlan","fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.Device", new ConfigurationOption("/fhir/Device","fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.DeviceComponent", new ConfigurationOption("/fhir/DeviceComponent", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.DeviceMetric", new ConfigurationOption("/fhir/DeviceMetric", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.Observation", new ConfigurationOption("/fhir/Observation", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.MedicationOrder", new ConfigurationOption("/fhir/MedicationOrder", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.MedicationAdministration", new ConfigurationOption("/fhir/MedicationAdministration", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.Organization", new ConfigurationOption("/fhir/Organization", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.Practitioner", new ConfigurationOption("/fhir/Practitioner", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.Medication", new ConfigurationOption("/fhir/Medication", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.Group", new ConfigurationOption("/fhir/Group", "fhir.resources.enabled", "FHIR"));
        this.optionMap.put("fhir.basePath.QuestionnaireResponse", new ConfigurationOption("/fhir/QuestionnaireResponse", "fhir.resources.enabled", "FHIR"));

        this.optionMap.put("fhir.resource.maskId", new ConfigurationOption(true, "fhir.resource.maskId", "FHIR"));
        this.optionMap.put("fhir.resource.preserveIdPrefix", new ConfigurationOption(true, "fhir.resource.maskId", "FHIR"));
        this.optionMap.put("fhir.resource.removeMeta", new ConfigurationOption(false, "fhir.resource.removeMeta", "FHIR"));

        this.optionMap.put("fhir.domainresource.removeContained", new ConfigurationOption(false, "fhir.resource.removeMeta", "FHIR"));
        this.optionMap.put("fhir.domainresource.removeText", new ConfigurationOption(false, "fhir.resource.removeText", "FHIR"));
        this.optionMap.put("fhir.domainresource.removeExtension", new ConfigurationOption(false, "fhir.resource.removeExtension", "FHIR"));

        this.optionMap.put("fhir.contactPoint.randomizeUse", new ConfigurationOption(false, "fhir.contactPoint.randomizeUse", "FHIR"));
        this.optionMap.put("fhir.contactPoint.maskValue", new ConfigurationOption(true, "fhir.contactPoint.maskValue", "FHIR"));
        this.optionMap.put("fhir.contactPoint.maskPeriod", new ConfigurationOption(true, "fhir.contactPoint.maskPeriod", "FHIR"));
        this.optionMap.put("fhir.contactPoint.removeExtensions", new ConfigurationOption(false, "fhir.contactPoint.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.contactPoint.removeSystem", new ConfigurationOption(false, "fhir.contactPoint.removeExtensions", "FHIR"));

        this.optionMap.put("fhir.address.removeExtensions", new ConfigurationOption(false, "fhir.contactPoint.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.address.preserveStateOnly", new ConfigurationOption(true, "fhir.address.preserveStateOnly", "FHIR"));

        this.optionMap.put("fhir.humanName.removeExtensions", new ConfigurationOption(false, "fhir.humanName.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.humanName.removeSuffix", new ConfigurationOption(true, "fhir.humanName.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.humanName.removePrefix", new ConfigurationOption(true, "fhir.humanName.removeExtensions", "FHIR"));

        this.optionMap.put("fhir.identifier.removeExtensions", new ConfigurationOption(false, "fhir.identifier.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.identifier.maskValue", new ConfigurationOption(true, "fhir.identifier.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.identifier.maskPeriod", new ConfigurationOption(true, "fhir.identifier.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.identifier.maskSystem", new ConfigurationOption(true, "fhir.identifier.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.identifier.maskType", new ConfigurationOption(true, "fhir.identifier.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.identifier.maskAssigner", new ConfigurationOption(true, "fhir.identifier.removeExtensions", "FHIR"));

        this.optionMap.put("fhir.contact.maskName", new ConfigurationOption(true, "fhir.contact.maskName", "FHIR"));
        this.optionMap.put("fhir.contact.maskTelecom", new ConfigurationOption(true, "fhir.contact.maskTelecom", "FHIR"));
        this.optionMap.put("fhir.contact.maskAddress", new ConfigurationOption(true, "fhir.contact.maskAddress", "FHIR"));

        this.optionMap.put("fhir.attachment.removeExtensions", new ConfigurationOption(true, "fhir.attachment.removeExtensions", "FHIR"));
        this.optionMap.put("fhir.attachment.removeData", new ConfigurationOption(true, "fhir.attachment.removeData", "FHIR"));
        this.optionMap.put("fhir.attachment.removeURI", new ConfigurationOption(true, "fhir.attachment.removeURI", "FHIR"));
        this.optionMap.put("fhir.attachment.removeTitle", new ConfigurationOption(true, "fhir.attachment.removeTitle", "FHIR"));

        this.optionMap.put("fhir.reference.removeDisplay", new ConfigurationOption(false, "fhir.reference.removeDisplay", "FHIR"));
        this.optionMap.put("fhir.reference.maskDisplay", new ConfigurationOption(true, "fhir.reference.removeDisplay", "FHIR"));
        this.optionMap.put("fhir.reference.removeExtension", new ConfigurationOption(false, "fhir.reference.removeExtension", "FHIR"));
        this.optionMap.put("fhir.reference.maskReference", new ConfigurationOption(true, "fhir.reference.maskReference", "FHIR"));
        this.optionMap.put("fhir.reference.removeReference", new ConfigurationOption(false, "fhir.reference.maskReference", "FHIR"));
        this.optionMap.put("fhir.reference.preserveReferencePrefix", new ConfigurationOption(true, "fhir.reference.maskReference", "FHIR"));
        this.optionMap.put("fhir.reference.maskReferenceExcludePrefixList", new ConfigurationOption("", "fhir.reference.maskReferenceExcludePrefixList", "FHIR"));

        this.optionMap.put("fhir.annotation.removeExtensions", new ConfigurationOption(false, "fhir.annotation.removeExtension", "FHIR"));
        this.optionMap.put("fhir.annotation.removeText", new ConfigurationOption(true, "fhir.annotation.removeText", "FHIR"));
        this.optionMap.put("fhir.annotation.removeAuthorString", new ConfigurationOption(true, "fhir.annotation.removeAuthorString", "FHIR"));
        this.optionMap.put("fhir.annotation.maskTime", new ConfigurationOption(true, "fhir.annotation.maskTime", "FHIR"));
        this.optionMap.put("fhir.annotation.maskAuthorReference", new ConfigurationOption(true, "fhir.annotation.maskAuthorReference", "FHIR"));

        this.optionMap.put("fhir.codeableconcept.maskText", new ConfigurationOption(false, "fhir.codeableconcept.removeText", "FHIR"));
        this.optionMap.put("fhir.codeableconcept.maskCoding", new ConfigurationOption(true, "fhir.codeableconcept.maskCoding", "FHIR"));

        this.optionMap.put("fhir.coding.maskVersion", new ConfigurationOption(true, "fhir.coding.maskVersion", "FHIR"));
        this.optionMap.put("fhir.coding.maskDisplay", new ConfigurationOption(false, "fhir.coding.removeDisplay", "FHIR"));
        this.optionMap.put("fhir.coding.maskSystem", new ConfigurationOption(false, "fhir.coding.removeDisplay", "FHIR"));
        this.optionMap.put("fhir.coding.maskCode", new ConfigurationOption(false, "fhir.coding.removeDisplay", "FHIR"));

        this.optionMap.put("fhir.narrative.removeDiv", new ConfigurationOption(true, "fhir.narrative.removeDiv", "FHIR"));
        this.optionMap.put("fhir.narrative.removeExtensions", new ConfigurationOption(true, "fhir.narrative.removeExtensions", "FHIR"));

        this.optionMap.put("fhir.quantity.maskValue", new ConfigurationOption(true, "fhir.quantity.maskValue", "FHIR"));
        this.optionMap.put("fhir.quantity.maskSystem", new ConfigurationOption(true, "fhir.quantity.maskValue", "FHIR"));
        this.optionMap.put("fhir.quantity.maskCode", new ConfigurationOption(true, "fhir.quantity.maskValue", "FHIR"));

        this.optionMap.put("fhir.period.maskStart", new ConfigurationOption(true, "fhir.period.maskStart", "FHIR"));
        this.optionMap.put("fhir.period.maskEnd", new ConfigurationOption(true, "fhir.period.maskEnd", "FHIR"));
        this.optionMap.put("fhir.period.removeStart", new ConfigurationOption(false, "fhir.period.removeStart", "FHIR"));
        this.optionMap.put("fhir.period.removeEnd", new ConfigurationOption(false, "fhir.period.removeEnd", "FHIR"));

        this.optionMap.put("fhir.ratio.deleteDenominator", new ConfigurationOption(false, "fhir.ratio.deleteDenominator", "FHIR"));
        this.optionMap.put("fhir.ratio.maskDenominator", new ConfigurationOption(false, "fhir.ratio.maskDenominator", "FHIR"));
        this.optionMap.put("fhir.ratio.deleteNumerator", new ConfigurationOption(false, "fhir.ratio.deleteNumerator", "FHIR"));
        this.optionMap.put("fhir.ratio.maskNumerator", new ConfigurationOption(false, "fhir.ratio.maskNumerator", "FHIR"));

        this.optionMap.put("fhir.range.deleteHigh", new ConfigurationOption(false, "fhir.range.deleteDenominator", "FHIR"));
        this.optionMap.put("fhir.range.maskHigh", new ConfigurationOption(false, "fhir.range.maskDenominator", "FHIR"));
        this.optionMap.put("fhir.range.deleteLow", new ConfigurationOption(false, "fhir.range.deleteNumerator", "FHIR"));
        this.optionMap.put("fhir.range.maskLow", new ConfigurationOption(false, "fhir.range.maskNumerator", "FHIR"));

        this.optionMap.put("fhir.sampleddata.maskOrigin", new ConfigurationOption(true, "fhir.sampleddata.maskOrigin", "FHIR"));
        this.optionMap.put("fhir.sampleddata.maskPeriod", new ConfigurationOption(true, "fhir.sampleddata.maskPeriod", "FHIR"));
        this.optionMap.put("fhir.sampleddata.maskFactor", new ConfigurationOption(true, "fhir.sampleddata.maskFactor", "FHIR"));
        this.optionMap.put("fhir.sampleddata.maskLowerLimit", new ConfigurationOption(true, "fhir.sampleddata.maskLowerLimit", "FHIR"));
        this.optionMap.put("fhir.sampleddata.maskUpperLimit", new ConfigurationOption(true, "fhir.sampleddata.maskUpperLimit", "FHIR"));
        this.optionMap.put("fhir.sampleddata.maskDimensions", new ConfigurationOption(true, "fhir.sampleddata.maskDimensions", "FHIR"));

        this.optionMap.put("fhir.timing.maskEvent", new ConfigurationOption(true, "fhir.timing.maskEvent", "FHIR"));
        this.optionMap.put("fhir.timing.maskCode", new ConfigurationOption(true, "fhir.timing.maskCode", "FHIR"));

        /* end of FHIR options */

        this.optionMap.put("null.mask.returnNull", new ConfigurationOption(true, "When true, will replace the value with null, otherwise with empty string.", "Null"));

        this.optionMap.put("name.prefixGUID", new ConfigurationOption("", "name.prefixGUID", "Names"));
        this.optionMap.put("names.masking.allowAnyGender", new ConfigurationOption(false, "Allow masking to produce any gender", "Names"));
        this.optionMap.put("names.masking.separator", new ConfigurationOption(" ", "Separator for the name tokens", "Names"));
        this.optionMap.put("names.masking.whitespace", new ConfigurationOption(" ", "Whitespace for the name tokens", "Names"));
        this.optionMap.put("names.token.consistence", new ConfigurationOption(false, "Provide consistence per token", "Names"));
        this.optionMap.put("names.mask.pseudorandom", new ConfigurationOption(false, "Provide pseudodandom consistence", "Names"));

        this.optionMap.put("hospital.mask.preserveCountry", new ConfigurationOption(true, "Select a hospital from the same country", "Hospitals"));

        this.optionMap.put("freetext.prefixGUID", new ConfigurationOption("", "freetext.prefixGUID", "Free text"));
        this.optionMap.put("freetext.mask.maskingConfigurationFilename", new ConfigurationOption("/freetextMaskingDefaults.json", "Path to configuration file", "Free text"));
        this.optionMap.put("freetext.mask.maskingConfigurationResourceType", new ConfigurationOption("INTERNAL_RESOURCE", "Path type of the conf file", "Free text"));
        this.optionMap.put("freetext.mask.complexConfigurationFilename", new ConfigurationOption("/complexWithIdentifiers.json", "Path to configuration file", "Free text"));
        this.optionMap.put("freetext.mask.complexConfigurationResourceType", new ConfigurationOption("INTERNAL_RESOURCE", "Path type of the conf file", "Free text"));

        this.optionMap.put("binning.mask.binSize", new ConfigurationOption(5, "The bin size", "Binning"));
        this.optionMap.put("binning.mask.format", new ConfigurationOption("%s-%s", "The format of the binned value", "Binning"));
        this.optionMap.put("binning.mask.returnBinMean", new ConfigurationOption(false, "Return the bin mean", "Binning"));

        this.optionMap.put("mac.masking.preserveVendor", new ConfigurationOption(true, "Preserve vendor information", "MAC Address"));

        this.optionMap.put("occupation.mask.generalize", new ConfigurationOption(false, "Generalize to occupation category", "Occupation"));

        this.optionMap.put("icd.randomize.category", new ConfigurationOption(true, "Randomize by 3-digit code", "ICD"));
        this.optionMap.put("icd.randomize.chapter", new ConfigurationOption(false, "Randomize by chapter", "ICD"));

        this.optionMap.put("url.mask.usernamePassword", new ConfigurationOption(true, "Mask username and password", "URL"));
        this.optionMap.put("url.mask.port", new ConfigurationOption(false, "Mask port", "URL"));
        this.optionMap.put("url.mask.removeQuery", new ConfigurationOption(false, "Remove query part", "URL"));
        this.optionMap.put("url.mask.maskQuery", new ConfigurationOption(false, "Mask query part", "URL"));
        this.optionMap.put("url.preserve.domains", new ConfigurationOption(1, "Number of domains to preserve", "URL"));

        this.optionMap.put("email.preserve.domains", new ConfigurationOption(1, "Number of domains to preserve", "E-mail"));
        this.optionMap.put("email.nameBasedUsername", new ConfigurationOption(false, "Name-based usernames", "E-mail"));
        this.optionMap.put("email.usernameVirtualField", new ConfigurationOption(null, "Virtual field name for username", "E-mail"));
        this.optionMap.put("email.domainVirtualField", new ConfigurationOption(null, "Virtual field name for domain", "E-mail"));

        this.optionMap.put("imsi.mask.preserveMCC", new ConfigurationOption(true, "Preserve MCC", "IMSI"));
        this.optionMap.put("imsi.mask.preserveMNC", new ConfigurationOption(true, "Preserve MNC", "IMSI"));

        this.optionMap.put("iban.mask.preserveCountry", new ConfigurationOption(true, "Preserve country code", "IBAN"));
        this.optionMap.put("imei.mask.preserveTAC", new ConfigurationOption(true, "Preserve TAC prefix", "IMEI"));

        this.optionMap.put("replace.mask.mode", new ConfigurationOption("WITH_PARTIAL", "Replace mode (one of " + String.join(", ", Stream.of(ReplaceMaskingProvider.ReplaceMode.values()).map(Enum::name).collect(Collectors.toList())) + ")", "Replace"));
        this.optionMap.put("replace.mask.prefix", new ConfigurationOption("", "Prefix to be added to the replaced value", "Replace"));
        this.optionMap.put("replace.mask.offset", new ConfigurationOption(0, "Starting offset for preserving", "Replace"));
        this.optionMap.put("replace.mask.preserve", new ConfigurationOption(0, "Number of characters to preserve", "Replace"));
        this.optionMap.put("replace.mask.preserveLength", new ConfigurationOption(true, "When true, replace with a value having the same length as the original", "Replace"));
        this.optionMap.put("replace.mask.replaceOnValueInSet", new ConfigurationOption(false, "When true, replace only if the value is in a set", "Replace"));
        this.optionMap.put("replace.mask.replaceOnValueNotInSet", new ConfigurationOption(false, "When true, replace only if the value is not in a set", "Replace"));
        this.optionMap.put("replace.mask.testValues", new ConfigurationOption(null, "Set of values to be used for the test", "Replace"));
        this.optionMap.put("replace.mask.asteriskValue", new ConfigurationOption("*", "Asterisk character to be used (WITH_ASTERISKS mode)", "Replace"));
        this.optionMap.put("replace.mask.replacementValueSet", new ConfigurationOption(null, "Set of values to be used for replacement (WITH_SET mode)", "Replace"));

        this.optionMap.put("numvariance.mask.limitDown", new ConfigurationOption(10.0, "Down percentage limit", "Number Variance"));
        this.optionMap.put("numvariance.mask.limitUp", new ConfigurationOption(10.0, "Up percentage limit", "Number Variance"));
        this.optionMap.put("numvariance.mask.precisionDigits", new ConfigurationOption(-1, "Precision digits", "Number Variance"));

        this.optionMap.put("ssnuk.mask.preservePrefix", new ConfigurationOption(true, "Preserve prefix", "SSN UK"));

        this.optionMap.put("ssnus.mask.preserveAreaNumber", new ConfigurationOption(true, "Preserve area number", "SSN US"));
        this.optionMap.put("ssnus.mask.preserveGroup", new ConfigurationOption(true, "Preserve group", "SSN US"));

        this.optionMap.put("shift.mask.value", new ConfigurationOption(1.0d, "Shift value", "SHIFT"));
        this.optionMap.put("shift.mask.digitsToKeep", new ConfigurationOption(-1, "Shift digits to keep", "SHIFT"));

        this.optionMap.put("address.number.mask", new ConfigurationOption(true, "Mask number", "Address"));
        this.optionMap.put("address.city.mask", new ConfigurationOption(true, "Mask city", "Address"));
        this.optionMap.put("address.streetName.mask", new ConfigurationOption(true, "Mask street name", "Address"));
        this.optionMap.put("address.postalCode.mask", new ConfigurationOption(true, "Mask postal code", "Address"));
        this.optionMap.put("address.postalCode.nearest", new ConfigurationOption(false, "Select nearest postal code", "Address"));
        this.optionMap.put("address.postalCode.nearestK", new ConfigurationOption(10, "Number of closest postal codes to select from", "Address"));
        this.optionMap.put("address.roadType.mask", new ConfigurationOption(true, "Mask road type (street, avenue, etc)", "Address"));
        this.optionMap.put("address.country.mask", new ConfigurationOption(true, "Mask country", "Address"));
        this.optionMap.put("address.mask.pseudorandom", new ConfigurationOption(false, "Mask based on pseudorandom function", "Address"));

        this.optionMap.put("patientID.groups.preserve", new ConfigurationOption(0, "Number of groups to preserve", "Patient ID"));

        this.optionMap.put("atc.mask.levelsToKeep", new ConfigurationOption(4, "Number of levels to keep", "ATC"));

        this.optionMap.put("hashing.algorithm.default", new ConfigurationOption("SHA-256", "Default algorithm"));
        this.optionMap.put("hashing.salt", new ConfigurationOption("", "Default salt"));
        this.optionMap.put("hashing.normalize", new ConfigurationOption(false, "Normalize input", "Hashing"));

        this.optionMap.put("city.mask.closest", new ConfigurationOption(false, "Select one of the near cities", "City"));
        this.optionMap.put("city.mask.closestK", new ConfigurationOption(10, "Number of closest cities to select from", "City"));
        this.optionMap.put("city.mask.pseudorandom", new ConfigurationOption(false, "Mask based on pseudorandom function", "City"));

        this.optionMap.put("country.mask.closest", new ConfigurationOption(false, "Select one of the near countries", "Country"));
        this.optionMap.put("country.mask.closestK", new ConfigurationOption(10, "Number of nearest countries to select from", "Country"));
        this.optionMap.put("country.mask.pseudorandom", new ConfigurationOption(false, "Mask based on pseudorandom function", "Country"));

        this.optionMap.put("county.mask.pseudorandom", new ConfigurationOption(false, "Mask based on pseudorandom function", "County"));

        this.optionMap.put("phone.countryCode.preserve", new ConfigurationOption(true, "Preserve country code", "Phone numbers"));
        this.optionMap.put("phone.areaCode.preserve", new ConfigurationOption(true, "Preserve area code", "Phone numbers"));

        this.optionMap.put("continent.mask.closest", new ConfigurationOption(false, "Select one of the nearest continents", "Continent"));
        this.optionMap.put("continent.mask.closestK", new ConfigurationOption(5, "Number of neighbors for nearest continents", "Continent"));

        this.optionMap.put("creditCard.issuer.preserve", new ConfigurationOption(true, "Preserve issuer", "Credit card"));

        this.optionMap.put("vin.wmi.preserve", new ConfigurationOption(true, "Preserve manufacturer information (WMI)", "Vehicle Identifier"));
        this.optionMap.put("vin.vds.preserve", new ConfigurationOption(false, "Preserve vehicle description information (VDS)", "Vehicle Identifier"));

        this.optionMap.put("ipaddress.subnets.preserve", new ConfigurationOption(0, "Number of prefixes to preserve", "IP Address"));

        this.optionMap.put("swift.mask.preserveCountry", new ConfigurationOption(false, "Preserve Country", "SWIFT code"));

        this.optionMap.put("latlon.mask.randomWithinCircle", new ConfigurationOption(true, "Randomize a point in a circle", "Latitude longitude"));
        this.optionMap.put("latlon.mask.donutMasking", new ConfigurationOption(false, "Randomize a point in a donut-shaped", "Latitude longitude"));
        this.optionMap.put("latlon.mask.fixedRadiusRandomDirection", new ConfigurationOption(false, "Random a point with a fixed radium but random direction", "Latitude longitude"));
        this.optionMap.put("latlon.offset.maximumRadius", new ConfigurationOption(100, "Maximum offset radius (in meters)", "Latitude longitude"));
        this.optionMap.put("latlon.offset.minimumRadius", new ConfigurationOption(50, "Minimum Offset radius (in meters)", "Latitude longitude"));

        this.optionMap.put("datetime.generalize.weekyear", new ConfigurationOption(false, "Generalize to week/year", "Date/Time"));
        this.optionMap.put("datetime.generalize.monthyear", new ConfigurationOption(false, "Generalize to mm/year", "Date/Time"));
        this.optionMap.put("datetime.generalize.quarteryear", new ConfigurationOption(false, "Generalize to quarter/year", "Date/Time"));
        this.optionMap.put("datetime.generalize.year", new ConfigurationOption(false, "Generalize to year", "Date/Time"));
        this.optionMap.put("datetime.generalize.nyearinterval", new ConfigurationOption(false, "Generalize to N-year interval", "Date/Time"));
        this.optionMap.put("datetime.generalize.nyearintervalvalue", new ConfigurationOption(0, "Value of for N-year interval generalization", "Date/Time"));

        this.optionMap.put("datetime.format.fixed", new ConfigurationOption(null, "Datetime format", "Date/Time"));
        this.optionMap.put("datetime.format.timezone", new ConfigurationOption(null, "Datetime timezone", "Date/Time"));
        this.optionMap.put("datetime.year.mask", new ConfigurationOption(true, "Mask year", "Date/Time"));
        this.optionMap.put("datetime.year.rangeUp", new ConfigurationOption(0, "Mask year range upwards", "Date/Time"));
        this.optionMap.put("datetime.year.rangeDown", new ConfigurationOption(10, "Mask year range downwards", "Date/Time"));
        this.optionMap.put("datetime.month.mask", new ConfigurationOption(true, "Mask month", "Date/Time"));
        this.optionMap.put("datetime.month.rangeUp", new ConfigurationOption(0, "Mask month range upwards", "Date/Time"));
        this.optionMap.put("datetime.month.rangeDown", new ConfigurationOption(12, "Mask month range downwards", "Date/Time"));
        this.optionMap.put("datetime.day.mask", new ConfigurationOption(true, "Mask day", "Date/Time"));
        this.optionMap.put("datetime.day.rangeUp", new ConfigurationOption(0, "Mask day range upwards", "Date/Time"));
        this.optionMap.put("datetime.day.rangeDown", new ConfigurationOption(7, "Mask day range downwards", "Date/Time"));
        this.optionMap.put("datetime.hour.mask", new ConfigurationOption(true, "Mask hour", "Date/Time"));
        this.optionMap.put("datetime.hour.rangeUp", new ConfigurationOption(0, "Mask hour range upwards", "Date/Time"));
        this.optionMap.put("datetime.hour.rangeDown", new ConfigurationOption(100, "Mask hour range downwards", "Date/Time"));
        this.optionMap.put("datetime.minutes.mask", new ConfigurationOption(true, "Mask minutes", "Date/Time"));
        this.optionMap.put("datetime.minutes.rangeUp", new ConfigurationOption(0, "Mask minutes range upwards", "Date/Time"));
        this.optionMap.put("datetime.minutes.rangeDown", new ConfigurationOption(100, "Mask minutes range downwards", "Date/Time"));
        this.optionMap.put("datetime.seconds.mask", new ConfigurationOption(true, "Mask seconds", "Date/Time"));
        this.optionMap.put("datetime.seconds.rangeUp", new ConfigurationOption(0, "Mask seconds range upwards", "Date/Time"));
        this.optionMap.put("datetime.seconds.rangeDown", new ConfigurationOption(100, "Mask seconds range downwards", "Date/Time"));
        this.optionMap.put("datetime.mask.shiftDate", new ConfigurationOption(false, "Shift date by a constant amount", "Date/Time"));
        this.optionMap.put("datetime.mask.shiftSeconds", new ConfigurationOption(0, "Seconds to shift date by", "Date/Time"));
        this.optionMap.put("datetime.mask.returnOriginalOnUnknownFormat", new ConfigurationOption(false, "Return original value on unknown format", "Date/Time"));
        this.optionMap.put("datetime.mask.keyBasedMaxDays", new ConfigurationOption(100, "Max days to shift when using compound masking", "Date/Time"));
        this.optionMap.put("datetime.mask.keyBasedMinDays", new ConfigurationOption(0, "Min days to shift when using compound masking", "Date/Time"));
        this.optionMap.put("datetime.mask.replaceDaySameClass", new ConfigurationOption(false, "Replace weekday with weekday, weekend with weekend", "Date/Time"));
        this.optionMap.put("datetime.mask.replaceDayWithDiffPriv", new ConfigurationOption(false, "Replace day using DP", "Date/Time"));
        this.optionMap.put("datetime.mask.replaceDayWithDiffPrivEpsilon", new ConfigurationOption(3.0, "Replace day using DP", "Date/Time"));
        this.optionMap.put("datetime.mask.trimTimeToHourInterval", new ConfigurationOption(false, "Trim time to hourly interval", "Date/Time"));
        this.optionMap.put("datetime.mask.numberOfIntervals", new ConfigurationOption(4, "Inteval in which trim 24 hours", "Date/Time"));

        this.optionMap.put("hashint.algorithm.default", new ConfigurationOption("SHA-256", "Default algorithm", "HashInt"));
        this.optionMap.put("hashint.budget.use", new ConfigurationOption(false, "Limit the randomization within a budget", "HashInt"));
        this.optionMap.put("hashint.budget.amount", new ConfigurationOption(10, "Amount of randomization to be added", "HashInt"));
        this.optionMap.put("hashint.sign.coherent", new ConfigurationOption(true, "The masked value has to be coherente with respect to the sign", "HashInt"));

        this.optionMap.put("ope.in.start", new ConfigurationOption(0, "Default IN-range start", "OrderPreservingEncryption"));
        this.optionMap.put("ope.in.end", new ConfigurationOption(((int) Math.pow(2, 15)) - 1, "Default IN-range end", "OrderPreservingEncryption"));
        this.optionMap.put("ope.out.start", new ConfigurationOption(0, "Default OUT-range start", "OrderPreservingEncryption"));
        this.optionMap.put("ope.out.end", new ConfigurationOption(((int) Math.pow(2, 31)) - 1, "Default OUT-range end", "OrderPreservingEncryption"));
        this.optionMap.put("ope.default.key", new ConfigurationOption(UUID.randomUUID().toString(), "Default encryption key", "OrderPreservingEncryption"));

        this.optionMap.put("redact.replace.character", new ConfigurationOption("*", "Default replacement character", "Redact"));
        this.optionMap.put("redact.preserve.length", new ConfigurationOption(true, "Preserve token length", "Redact"));
        this.optionMap.put("redact.replace.length", new ConfigurationOption(1, "Replacement length  ", "Redact"));

        this.optionMap.put("monetary.replacing.character", new ConfigurationOption("X", "Replacement character for digits", "Monetary"));
        this.optionMap.put("monetary.preserve.size", new ConfigurationOption(true, "Preserve the number of digits", "Monetary"));

        this.optionMap.put("zipcode.mask.countryCode", new ConfigurationOption("US", "Country for the zip codes (2-digit ISO code)", "ZIP code"));
        this.optionMap.put("zipcode.mask.requireMinimumPopulation", new ConfigurationOption(true, "Require minimum population", "ZIP code"));
        this.optionMap.put("zipcode.mask.minimumPopulationUsePrefix", new ConfigurationOption(true, "Use prefix for minimum population", "ZIP code"));
        this.optionMap.put("zipcode.mask.minimumPopulationPrefixDigits", new ConfigurationOption(3, "Prefix for minimum population", "ZIP code"));
        this.optionMap.put("zipcode.mask.minimumPopulation", new ConfigurationOption(20000, "Minimum Population", "ZIP code"));
    
        this.optionMap.put("image.mask.words", new ConfigurationOption("", "Words to mask on the image", "Imaging"));
        this.optionMap.put("image.mask.types", new ConfigurationOption("", "Types to mask on the image", "Imaging"));

        this.optionMap.put("religion.mask.probabilityBased", new ConfigurationOption(false, "Probabilistic masking of religions", "Religion"));
        
        this.optionMap.put("race.mask.probabilityBased", new ConfigurationOption(false, "Probabilistic masking of races", "Race"));
        
        this.optionMap.put("generalization.masking.hierarchyName", new ConfigurationOption("", "Probabilistic masking of races", "Generalization"));
        this.optionMap.put("generalization.masking.hierarchyMap", new ConfigurationOption(null, "Probabilistic masking of races", "Generalization"));
        this.optionMap.put("generalization.masking.hierarchyLevel", new ConfigurationOption(-1, "Probabilistic masking of races", "Generalization"));
        this.optionMap.put("generalization.masking.randomizeOnFail", new ConfigurationOption(true, "Probabilistic masking of races", "Generalization"));
        
        this.optionMap.put("differentialPrivacy.parameter.epsilon", new ConfigurationOption(8.0, "Noise parameter", "Differential Privacy"));
        //this.optionMap.put("differentialPrivacy.parameter.delta", new ConfigurationOption(0.0, "Relaxation parameter", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.parameter.diameter", new ConfigurationOption(-1, "Base for the admissible displacement", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.behavior.round", new ConfigurationOption(true, "Apply rounding to the decimals", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.behavior.ceil", new ConfigurationOption(false, "Apply ceiling to the decimals", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.behavior.floor", new ConfigurationOption(false, "Apply flooring to the decimals", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.behavior.numberOfDecimals", new ConfigurationOption(3, "Number of desired decimal digits", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.behavior.limitToRange", new ConfigurationOption(false, "Limit values within a range", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.behavior.extractRangeFromData", new ConfigurationOption(true, "Extract the range value from data", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.behavior.extractDiameterFromData", new ConfigurationOption(true, "Extract diameter value from data", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.range.min", new ConfigurationOption(0, "", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.range.max", new ConfigurationOption(0, "", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.mechanism", new ConfigurationOption("LAPLACE_NATIVE", "", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.binary.value1", new ConfigurationOption("TRUE", "", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.binary.value2", new ConfigurationOption("FALSE", "", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.categorical.set", new ConfigurationOption("", "", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.categorical.hierarchyName", new ConfigurationOption("", "hierarchy name", "Differential Privacy"));
        this.optionMap.put("differentialPrivacy.categorical.hierarchyMap", new ConfigurationOption(null, "", "Differential Privacy"));

        this.optionMap.put("timestamp.mask.format", new ConfigurationOption("yyyy-MM-dd HH:mm:ss", "Timestamp format", "Timestamp"));
        this.optionMap.put("timestamp.mask.year", new ConfigurationOption(false, "Remove year component from the timestamp", "Timestamp"));
        this.optionMap.put("timestamp.mask.month", new ConfigurationOption(false, "Remove month component from the timestamp", "Timestamp"));
        this.optionMap.put("timestamp.mask.day", new ConfigurationOption(false, "Remove day component from the timestamp", "Timestamp"));
        this.optionMap.put("timestamp.mask.hour", new ConfigurationOption(false, "Remove hour component from the timestamp", "Timestamp"));
        this.optionMap.put("timestamp.mask.minute", new ConfigurationOption(true, "Remove minute component from the timestamp", "Timestamp"));
        this.optionMap.put("timestamp.mask.second", new ConfigurationOption(true, "Remove second component from the timestamp", "Timestamp"));
        this.optionMap.put("timestamp.mask.millisecond", new ConfigurationOption(true, "Remove millisecond component from the timestamp", "Timestamp"));
        this.optionMap.put("timestamp.mask.nanosecond", new ConfigurationOption(true, "Remove nanosecond component from the timestamp", "Timestamp"));
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    @Override
    @Deprecated
    public Object getValue(String key) {
        if (optionMap.containsKey(key))
            return optionMap.get(key).getValue();
        return null;
    }

    public Collection<String> getStringValueWithPrefixMatch(String prefix) {
        Collection<String> values = new ArrayList<>();

        for(String key: this.optionMap.keySet()) {
            if (key.startsWith(prefix)) {
                values.add(getStringValue(key));
            }
        }

        return values;
    }

    @Override
    public int getIntValue(String key) {
        Object value = getValue(key);

        if (value instanceof Number) {
            Number v = (Number) value;

            return v.intValue();
        }

        return (int) value;
    }

    @Override
    public double getDoubleValue(String key) {
        Object value = getValue(key);

        if (value instanceof Number) {
            Number v = (Number) value;

            return v.doubleValue();
        }

        return (double) value;
    }

    @Override
    public boolean getBooleanValue(String key) {
        return (boolean) getValue(key);
    }

    @Override
    public String getStringValue(String key) {
        return (String) getValue(key);
    }

    @Override
    public JsonNode getJsonNodeValue(String key) {
        return (JsonNode) getValue(key);
    }

    @Override
    public void setValue(String key, Object value) {
        ConfigurationOption option = optionMap.get(key);

        if (option == null) {
            this.optionMap.put(key, new ConfigurationOption(value, "unknown"));
        } else {
            option.setValue(value);
        }
    }

    @Override
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * Gets options.
     *
     * @return the options
     */
    public Map<String, ConfigurationOption> getOptions() {
        return this.optionMap;
    }


    /**
     * Gets options by category.
     *
     * @return the options by category
     */
    public Map<String, List<Map.Entry<String, ConfigurationOption>>> getOptionsByCategory() {
        Map<String, List<Map.Entry<String, ConfigurationOption>>> resultMap = new HashMap<>();

        for (Map.Entry<String, ConfigurationOption> entry : optionMap.entrySet()) {
            String category = entry.getValue().getCategory();

            if (!resultMap.containsKey(category)) {
                resultMap.put(category, new ArrayList<>());
            }

            resultMap.get(category).add(entry);
        }

        return resultMap;
    }

    public void setConfigurationManager(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }
}
