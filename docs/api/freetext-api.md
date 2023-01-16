# Prerequisites

Project was developed and tested using Java 11


# Free-text de-identification pipeline

The class `com.ibm.research.drl.dpt.nlp.ComplexFreeTextAnnotator` contains the implementation of the annotation pipeline.
It does the pre-processing, invokes the individual annotators and performs the post-processing phase.

In order to initialize the ComplexFreeTextAnnotator one of the following constructors needs to be invoked:

```
	public ComplexFreeTextAnnotator()
```

This constructor will initialize the pipeline with the system defaults (see default configuration section below)

```
	public ComplexFreeTextAnnotator(InputStream configuration)
```

This constructor will initialize based on the configuration contents of the input stream. The configuration is a JSON object.

Once the identifier is initialized, then we can get the list of identified tokens from the following function:

```
    List<IdentifiedEntity> identify(String text, Language language) throws IOException;
```

The `text` parameter contains the input text. The `language` parameter declares the input language. If the value is `Language.UNKNOWN`, the identifier will use the default language based on the value of `defaultLanguage` in the configuration if the `performLanguageDetection` is set to `false` or will detect the language when `performLanguageDetection` is set to `true`.


# Free-text de-identification pipeline configuration


## Structure of the configuration

The configuration is a JSON object that contains the following objects:

1) **identifiers**: it is a JSON object that contains the full class names of the annotators to load as the keys. The value to each key is the annotator-specific configuration. For example, the following configuration will load the PRIMA annotator with its configuration:

```json
"identifiers": {
    "com.ibm.research.drl.dpt.nlp.PRIMAAnnotator": {
      "sentenceDetectorModel": "/nlp/en/en-sent.bin",
      "mapping": {
        "ADDRESS": "LOCATION",
        "STATES_US": "LOCATION",
        "CITY": "LOCATION",
        "DAY": "DATETIME",
        "MONTH": "DATETIME",
        "SSN_US": "NATIONAL_ID",
        "SSN_UK": "NATIONAL_ID"
      },
      "MIN_SHINGLE_SIZE": 2,
      "MAX_SHINGLE_SIZE": 10
    }
}
```

See the next section for the available annotators and their respective configuration

2) **blacklist**: a JSON object that includes the values we want to blacklist from the annotator results. The keys are the entity types and the value of each key is a JSON array that contains the values to blacklist for that entity type. For example, the following configuration:

```json
"blacklist": {
	"ORGANIZATION": [
      "INP",
      "LTC"
    ]
}
```

denotes that the values `INP` and `LTC` will be ignored for the `ORGANIZATION` entity type

3) **unreliable**: this part of the configuration controls which entities types are considered unreliable, per annotator, and thus need to be excluded from the results. It is a JSON object where each key is the annotator name and the value for each key is a JSON array that contains the unreliable entities for that annotator. For example, the following configuration:

```json
"unreliable": {
	"ACI": [
      "NUMBER"
   ]
}
```

denotes that if we see a `NUMBER` entity from the `ACI` annotator we need to ignore it.

4) **connected**: There are cases where an entity is split into separate results by an annotators. For example, let us consider that the input is "Health Center of Washington". An annotator can detect two entities: "Health Center" as an organisation and "Washington" as a location. However, we want to detect only one entity and that is the entire token as an organization. The pipeline enables the merging of two results into a single entity.
   Users can specify through the configuration the left and right-hand side of the connected entities along with the connecting particles.

For example, the following configuration:

```json
"connected": [
    {
      "first": "ORGANIZATION",
      "second": "LOCATION",
      "endType": "ORGANIZATION",
      "endSubtype": "ORGANIZATION",
      "particles": ["in", "on", "of", "the", "to"]
    }
  ]
```

declares that if the first term is an ORGANIZATION and the second term is LOCATION and in-between one of the `["in", "on", "of", "the", "to"]` is found, then these entities will be merged to one with the primary and secondary type being ORGANIZATION.

5) **weights**:

Since we invoke multiple annotators, we might end up with conflicting results. In order to handle the potential conflicts, we introduced a priority system per annotator per entity type. For each annotator, we assign a weight to each of the entity types it supports. The weight is a value from 0 to 100 and practically reflects the accuracy of the annotator for the specific type. The initial weight assignment was performed based on the evaluation results of each individual annotator. If no weight is assigned for an entity, then the default value of 1 is assumed. A weight value of 0 for an entity will force the pipeline to ignore the results for that entity for that annotator.

The following configuration:

```json
...
"weights": {
    "Stanford": {
      "DATETIME": 50,
      "NAME": 95,
      "LOCATION": 90
    },
    "PRIMA": {
      "DATETIME": 50,
      "EMAIL": 90,
      "NAME": 50
    }
}
...
```

includes the weights for two annotators. If a term is recognized as `NAME` from the PRIMA annotator and as a `LOCATION` from the Stanford one, then the Stanford result wins since it has a higher weight value for that entity (90 over 50).

6) **not_POS**: contains the entity types that are POS independent. It is a JSON array. Its values can either be a string or an object. The string represents the entity type name. The object has the entity types as keys and arrays as values. The values in the arrays contain the exceptions for that POS independent type. For example, consider the following configuration:

```json
"not_POS": [
    "DATETIME",
    {"MONTH": ["may"]},
    "DAY",
    "PHONE",
    "NUMERIC",
    "NATIONAL_ID",
    "EMAIL"
]
```

instructs that the entity types `DATETIME`, `DAY`, `PHONE`, `NUMERIC`, `NATIONAL_ID`, `EMAIL` are POS-independent but also `MONTH` is POS-independent except the case that its value is `may`.


#### Default configuration

```json
{
  "defaultLanguage": "ENGLISH",
  "performLanguageDetection": false,
  "identifiers": {
    "com.ibm.research.drl.dpt.nlp.PRIMAAnnotator": {
      "sentenceDetectorModel": "/nlp/en/en-sent.bin",
      "mapping": {
        "ADDRESS": "LOCATION",
        "STATES_US": "LOCATION",
        "CITY": "LOCATION",
        "DAY": "DATETIME",
        "MONTH": "DATETIME",
        "SSN_US": "NATIONAL_ID",
        "SSN_UK": "NATIONAL_ID"
      },
      "MIN_SHINGLE_SIZE": 2,
      "MAX_SHINGLE_SIZE": 10
    },
    "com.ibm.research.drl.dpt.nlp.stanford.StanfordNLPAnnotator": {
      "defaultLanguage" : "ENGLISH",
      "configuration": {
        "ENGLISH" :  [
          "annotators", "tokenize,ssplit,pos,lemma,ner",
          "tokenize.language", "en"
        ]
      },
      "mapping": {
        "PERSON" : "NAME",
        "DATE" : "DATETIME",
        "TIME" : "DATETIME",
        "SET" : "DATETIME",
        "PERCENT" : "NUMERIC",
        "NUMBER" : "NUMERIC",
        "ORDINAL" : "O"
      },
      "subtypeMapping": {
        "SET": "TEMPORAL",
        "TIME": "TIME",
        "PERSON": "NAME"
      }
    }
  },
  "blacklist": {
    "DATETIME": [
      "day",
      "past",
      "days",
      "currently",
      "date",
      "present",
      "current",
      "hours",
      "weeks",
      "months",
      "years",
      "once"
    ],
    "ORGANIZATION": [
      "INP",
      "LTC"
    ]
  },
  "connected": [
    {
      "first": "ORGANIZATION",
      "second": "LOCATION",
      "endType": "ORGANIZATION",
      "endSubtype": "ORGANIZATION",
      "particles": ["in", "on", "of", "the", "to"]
    }
  ],
  "unreliable": {
    "Stanford": [
      "O",
      "DURATION",
      "MISC",
      "NUMERIC"
    ]
  },
  "weights": {
    "Stanford": {
      "DATETIME": 50,
      "NAME": 95,
      "LOCATION": 90
    },
    "PRIMA": {
      "DATETIME": 50,
      "EMAIL": 90,
      "NAME": 50
    }
  },
  "not_POS": [
    "DATETIME",
    {"MONTH": ["may"]},
    "DAY",
    "PHONE",
    "NUMERIC",
    "NATIONAL_ID",
    "EMAIL"
  ]
}
```


### Annotators

The library comes with a list of annotators:

| Annotator name | Class                                                     |
|----------------|-----------------------------------------------------------|
| PRIMA          | com.ibm.research.drl.dpt.nlp.PRIMAAnnotator               |
| OpenNLP        | com.ibm.research.drl.dpt.nlp.opennlp.OpenNLPAnnotator     |
| SystemT        | com.ibm.research.drl.dpt.nlp.systemT.SystemTAnnotator     |
| ACI            | com.ibm.research.drl.dpt.nlp.aci.ACIAnnotator             |
| EMRA           | com.ibm.research.drl.dpt.nlp.medius.MediusAnnotator       |
| WatsonNLU      | com.ibm.research.drl.dpt.nlp.watsonNLU.WatsonNLUAnnotator |

#### Advanced Care Insights (ACI)

Entities covered are names, phone numbers, email, organization, location, datetime entities of the medical domain (symptoms, diseases, ICD codes etc.). ACI is an online REST service.
It does not expose any POS tagging capabilities.

The configuration for ACI:

```
{
  "ACIEndpoint": "ACI",
  "mapping": {
    "com.ibm.ha.en.PersonInd": "NAME",
    "com.ibm.ha.en.LocationInd": "LOCATION",
    "com.ibm.ha.en.SymptomDiseaseInd": "SYMPTOM",
    "com.ibm.ha.en.ProcedureInd": "PROCEDURE",
    "com.ibm.ha.US_PhoneNumberInd": "PHONE",
    "com.ibm.ha.AllergyMedicationInd": "MEDICINE",
    "com.ibm.ha.AllergyInd": "SYMPTOM",
    "com.ibm.ha.dla.BathingAssistanceInd": "PROCEDURE",
    "com.ibm.ha.IcaCancerDiagnosisInd": "ICD",
    "com.ibm.ha.dla.DressingAssistanceInd": "PROCEDURE",
    "com.ibm.ha.dla.EatingAssistanceInd": "PROCEDURE",
    "com.ibm.ha.EjectionFractionInd": "NUMBER",
    "com.ibm.ha.LabValueInd": "NUMBER",
    "com.ibm.ha.MedicationInd": "MEDICINE",
    "com.ibm.ha.EmailAddressInd": "EMAIL",
    "com.ibm.ha.LocationInd": "LOCATION",
    "com.ibm.ha.MedicalInstitutionInd": "HOSPITAL",
    "com.ibm.ha.OrganizationInd": "ORGANIZATION",
    "com.ibm.ha.dla.SeeingAssistanceInd": "PROCEDURE",
    "com.ibm.ha.SmokingInd": "BOOLEAN",
    "com.ibm.ha.dla.ToiletingAssistanceInd": "PROCEDURE",
    "com.ibm.ha.dla.WalkingAssistanceInd": "PROCEDURE",
    "com.ibm.ha.en.US_PhoneNumberInd": "PHONE",
    "com.ibm.ha.en.OrganizationInd": "ORGANIZATION",
    "PersonInd": "NAME",
    "LocationInd": "LOCATION",
    "SymptomDiseaseInd": "SYMPTOM",
    "ProcedureInd": "PROCEDURE",
    "PhoneNumberInd": "PHONE",
    "OrganizationInd": "ORGANIZATION",
    "US_PhoneNumberInd": "PHONE",
    "MedicalInstitutionInd": "ORGANIZATION",
    "EmailAddressInd": "EMAIL",
    "MedicationInd": "MEDICINE",
    "EatingAssistanceInd": "PROCEDURE",
    "SeeingAssistanceInd": "PROCEDURE",
    "LabValueInd": "NUMBER",
    "IcaCancerDiagnosisInd": "SYMPTOM"
  }
}
```

The `ACIEndpoint` contains the REST service base URL. The `mapping` configuration should not be changed.

#### Medius (EMRA)

Entities covered are names, phone numbers, email, organization, location, datetime entities of the medical domain (symptoms, diseases, ICD codes etc.). Medius is an online REST service. It does not expose any POS tagging capabilities.

```json
{
  "MEDIUSEndpoint": "URL",
  "minimumConfidence": 0.8,
  "mapping": {
    "PERSON": "NAME",
    "TimeOfYear": "DATETIME",
    "Date": "DATETIME",
    "Medicine": "MEDICINE",
    "umls_PharmacologicSubstance": "MEDICINE",
    "Symptom": "SYMPTOM",
    "Occupation": "OCCUPATION",
    "umls_ProfessionalOrOccupationalGroup": "OCCUPATION",
    "GovernmentOrganization": "ORGANIZATION",
    "Organization": "ORGANIZATION",
    "UsState": "LOCATION",
    "umls_DiagnosticProcedure": "PROCEDURE"
  }
}
```

The `MEDIUSEndpoint` contains the REST service base URL. The `mapping` and `minimumConfidence` configuration should not be changed.

#### Apache OpenNLP

NLP-based annotator that detects names, organizations, locations and datetime
It is an offline library and also provides POS tagging capabilities.

```json
{
  "defaultLanguage": "ENGLISH",
  "configuration" : {
    "ENGLISH" : {
      "nameFinder": "/nlp/en/en-ner-person.bin",
      "locationFinder" : "/nlp/en/en-ner-location.bin",
      "organizationFinder" : "/nlp/en/en-ner-organization.bin",
      "timeFinder" : "/nlp/en/en-ner-time.bin",
      "dateFinder" : "/nlp/en/en-ner-date.bin",
      "sentenceFinder" : "/nlp/en/en-sent.bin",
      "tagger" : "/nlp/en/en-pos-maxent.bin",
      "tokenizer" : "/nlp/en/en-token.bin"
    }
  }
}
```

The configuration needs to include the location of the models.

#### Stanford CoreNLP

NLP-based annotator that detects names, organizations, locations and datetime.
It is an offline library and also provides POS tagging capabilities.

```json
{
  "defaultLanguage" : "ENGLISH",
  "configuration": {
    "ENGLISH" :  [
      "annotators", "tokenize,ssplit,pos,lemma,ner",
      "tokenize.language", "en"
    ]
  },
  "mapping": {
    "PERSON" : "NAME",
    "DATE" : "DATETIME",
    "TIME" : "DATETIME",
    "SET" : "DATETIME",
    "PERCENT" : "NUMERIC",
    "NUMBER" : "NUMERIC",
    "ORDINAL" : "O"
  },

  "subtypeMapping": {
    "SET": "TEMPORAL",
    "TIME": "TIME"
  }

}
```

The `configuration` element specifies which modules to load from CoreNLP package and in what order. `tokenize.language` defines the language of the models. Other options include parse.model, ner.model and pos.model. See https://stanfordnlp.github.io/CoreNLP/annotators.html for more details on the available options. The `mapping` and `subtypeMapping` should not be changed.


#### SystemT

It is an offline Java library that compiles and executes AQL scripts. It expose any POS tagging capabilities. Entities covered are address, datetime, email, location, organization, person, phone number and URL.

```json
{
  "COMPILED_MODULES_PATH": "/systemt/tam",
  "sourceModulePath": "/systemt/src",
  "internalTypeMapping": {
    "Person" : "Person_Name",
    "Email" : "Email_Address",
    "Organization" : "Organization",
    "DateTime" : "Date_Time",
    "Location" : "Location",
    "Address" : "Address",
    "URL" : "URL",
    "PhoneNumber" : "Phone_Number"
  },
  "mapping": {
    "Person" : "NAME",
    "Email" : "EMAIL",
    "Organization" : "ORGANIZATION",
    "DateTime" : "DATETIME",
    "Location" : "LOCATION",
    "Address" : "LOCATION",
    "URL" : "URL",
    "PhoneNumber" : "PHONE"
  },
  "sourceModules": [
    "demo"
  ],
  "compiledModules": [],
  "posModules": [
    "POSOutput"
  ],
  "posMapping": {
    "Adverb": "adv",
    "Adjective": "adj",
    "Verb": "verb",
    "Noun": "noun"
  }
}
```

This configuration should not be modified.

#### PRIMA

PRIMA contains a set identifiers based on either dictionaries, regex or code
It is an offline library that does not provide any POS tagging capabilities.
Identifiers are called for a sliding window of N tokens. External identifiers can be plugged in.

```json
{
  "sentenceDetectorModel": "/nlp/en/en-sent.bin",
  "mapping": {
    "ADDRESS" : "LOCATION",
    "STATES_US" : "LOCATION",
    "CITY" : "LOCATION",
    "DAY" : "DATETIME",
    "MONTH" : "DATETIME",
    "SSN_US" : "NATIONAL_ID",
    "SSN_UK" : "NATIONAL_ID",
    "HOSPITAL": "ORGANIZATION"
  },
  "splitSentences" : true,
  "MIN_SHINGLE_SIZE" : 2,
  "MAX_SHINGLE_SIZE" : 10
}
```

Sentence splitting is based on Apache OpenNLP. The sentenceDetectorModel defines the location of the model to load

The `mapping` and shingle configuration should not be changed.

The user can also specify the list of identifiers to load from the PRIMA core through the `identifiers` option.
If the option is missing, then the PRIMAAnnotator will use the system defaults. The user can specify the list of identifiers to load through two alternative ways.
The first one is make the value of the `identifiers` option an array of string, where each string represent the fully qualified class name of the identifier to load.
For example, the following configuration will load only the email identifier:

```json
{
  "sentenceDetectorModel": "/nlp/en/en-sent.bin",
  "mapping": {
    "ADDRESS" : "LOCATION",
    "STATES_US" : "LOCATION",
    "CITY" : "LOCATION",
    "DAY" : "DATETIME",
    "MONTH" : "DATETIME",
    "SSN_US" : "NATIONAL_ID",
    "SSN_UK" : "NATIONAL_ID",
    "HOSPITAL": "ORGANIZATION"
  },

	"identifiers": [
		"com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier"
	],

  "MIN_SHINGLE_SIZE" : 2,
  "MAX_SHINGLE_SIZE" : 10
}
```

When specifying the identifiers list as an array, there are two options for its values:

* string, specifying fully qualified class name of a class implementing the `Annotator` interface;
* an object of the form:

```
  {
      "type": "REGEX",
      "providerType": "SENSITIVE_LISTS",
      "regex": [
        "^true$"
      ]
    }
```
where `type` must be `REGEX` or `DICTIONARY_BASED`, `providerType` defines the provider type name of this custom identifer. When type is `REGEX` then a field `regex` is expected that is an array of string representing regular expressions patterns (Java/POSIX standard).
If the type is `DICTIONARY_BASED` then a field `terms` with the dictionary terms is expected.

The second way is to make the `identifiers` option an object that requires two keys: the `path` and the `resourceType`. The `resourceType` can be either `EXTERNAL_FILENAME`
if we want to load the identifiers list from a file or `INTERNAL_RESOURCE` if we want to load the list from a location loaded in the classpath. The `path` points to the location of the resource.

For example, the following configuration will load from the file `/tmp/identifiers.properties`

```json
{
  "sentenceDetectorModel": "/nlp/en/en-sent.bin",
  "mapping": {
    "ADDRESS" : "LOCATION",
    "STATES_US" : "LOCATION",
    "CITY" : "LOCATION",
    "DAY" : "DATETIME",
    "MONTH" : "DATETIME",
    "SSN_US" : "NATIONAL_ID",
    "SSN_UK" : "NATIONAL_ID",
    "HOSPITAL": "ORGANIZATION"
  },

	"identifiers": {
		"path" : "/tmp/identifiers.properties",
		"resourceType": "EXTERNAL_FILENAME"
	},

  "MIN_SHINGLE_SIZE" : 2,
  "MAX_SHINGLE_SIZE" : 10
}
```

The contents of the list must be one entry per line, where the entry is the fully qualified class name of the identifier to load.


#### Watson Natural Language Understanding (NLU)

```json
{
  "apiGateway" : "URL",
  "version" : "2017-02-27",

  "basicAuthorization" : "basic auth string here",
  "model": null,

  "mapping": {
    "Person" : "NAME",
    "Location" : "LOCATION",
    "GeographicFeature": "LOCATION",
    "EmailAddress" : "EMAIL",
    "Organization" : "ORGANIZATION",
    "Facility" : "ORGANIZATION",
    "Company" : "ORGANIZATION",
    "JobTitle" : "OCCUPATION",
    "HealthCondition" : "SYMPTOM"
  },

  "ignoreTypes" : [
    "PrintMedia",
    "Quantity"
  ]
}
```

The `apiGateway` and `version` contain the base URL and version to invoke. The `basicAuthorization` must containt the basic auth string to authenticate against the service. The `model` option defines the WKS model to be used. If it is null, the default NLU models will be used.

The `mapping` and `ignoreTypes` configuration should not be changed.



# Masking natural language text

In order to mask natural language text we need to initialize a `FreeTextMaskingProvider` based on the following constructor:

```
    public FreeTextMaskingProvider(SecureRandom random,
    			MaskingConfiguration configuration,
    			MaskingProviderFactory maskingProviderFactory,
    			NLPAnnotator identifier)
```

The `configuration` contains the masking configuration and `maskingProviderFactory` is the factory for the masking providers. See the masking API documentation on how to instantiate the configuration and the factory.

The `identifier` is an instance of a `ComplexFreeTextAnnotator` as described in the sections above.

Once the masking provider is initialized, then we can perform masking via:

```
	String mask(String input)
```

An example:

```java

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import java.security.SecureRandom;

public void testTextWithPI() {
	String phi = "John Smith went for a trip to Paris";
	MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

	MaskingProvider maskingProvider =
		new FreeTextMaskingProvider(new SecureRandom(), maskingConfiguration,
		new MaskingProviderFactory());

	String maskedText = maskingProvider.mask(phi);
	System.out.println(maskedText);
}
```


# Plugging in new annotators

Every new annotator needs to implement the NLPAnnotator interface:

```
public interface NLPAnnotator {
    List<IdentifiedEntity> identify(String text) throws IOException;
    String getName();
}
```

The IdentifiedEntity contains the necessary information for an identified part of text: the value as a string, starting and ending offset, the data types identified and the part of speech associated.
The newly developed annotator can then be plugged in to the pipeline through its FQDN.
