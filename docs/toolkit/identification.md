# Identification Task

This task takes care of scanning the entire datasource and classify the values to known types.

The `taskConfiguration` field for this task accepts the following structure:

```json
{
   "localization": "ISO CODE",
  "firstN": 100,
  "identifiers": [ "IDENTIFIERS TO BE LOADED" ]
}
```

Where `localization` specified the localization that is required for this classification process.
`firstN` allows to limit the processing to the first `N` values. `N` must me a number greater than 0. If not specified or if a value less or equal to 0 is passed all the dataset is used. Similarly, if the value of `N` is greated the the dataset size no error will be produced.
`identifiers` is an (optional) field that allows the specification of the type identifiers enabled for this specific scan.
This field is an array accepting two types of values:
* strings, which must map be the fully qualified class name of a Java class. The default list of available identifers is presented in the following of the documentation.
* object, which allows the definition of custom identifiers.
If missing, the default identifiers will be used.

## List of available identifiers

What follows is the list of identifiers available with the command line version of DPT. The list can be extende with any class implementing the `com.ibm.research.drl.dpt.providers.identifiers.Identifier` interface that is available in the class path of the executing JVM.

|Full Name|Short Name|
|---|---|
|com.ibm.research.drl.dpt.providers.identifiers.AddressForFreeTextIdentifier|ADDRESS
|com.ibm.research.drl.dpt.providers.identifiers.AddressIdentifier|ADDRESS
|com.ibm.research.drl.dpt.providers.identifiers.AgeIdentifier|AGE
|com.ibm.research.drl.dpt.providers.identifiers.AnimalSpeciesIdentifier|ANIMAL
|com.ibm.research.drl.dpt.providers.identifiers.ATCIdentifier|ATC
|com.ibm.research.drl.dpt.providers.identifiers.AustraliaAddressIdentifier|LOCATION
|com.ibm.research.drl.dpt.providers.identifiers.CityIdentifier|CITY
|com.ibm.research.drl.dpt.providers.identifiers.ContinentIdentifier|CONTINENT
|com.ibm.research.drl.dpt.providers.identifiers.CountryIdentifier|COUNTRY
|com.ibm.research.drl.dpt.providers.identifiers.CountyIdentifier|COUNTY
|com.ibm.research.drl.dpt.providers.identifiers.CreditCardIdentifier|CREDIT_CARD
|com.ibm.research.drl.dpt.providers.identifiers.CreditCardTypeIdentifier|CREDIT_CARD_TYPE
|com.ibm.research.drl.dpt.providers.identifiers.DateTimeIdentifier|DATETIME
|com.ibm.research.drl.dpt.providers.identifiers.DayIdentifier|DAY
|com.ibm.research.drl.dpt.providers.identifiers.DependentIdentifier|DEPENDENT
|com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier|EMAIL
|com.ibm.research.drl.dpt.providers.identifiers.FreeTextNamesIdentifier|NAME
|com.ibm.research.drl.dpt.providers.identifiers.GenderIdentifier|GENDER
|com.ibm.research.drl.dpt.providers.identifiers.HospitalIdentifier|HOSPITAL
|com.ibm.research.drl.dpt.providers.identifiers.IBANIdentifier|IBAN
|com.ibm.research.drl.dpt.providers.identifiers.ICDv9Identifier|ICDv9
|com.ibm.research.drl.dpt.providers.identifiers.IMEIIdentifier|IMEI
|com.ibm.research.drl.dpt.providers.identifiers.IMSIIdentifier|IMSI
|com.ibm.research.drl.dpt.providers.identifiers.InternationalPhoneIdentifier|PHONE
|com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier|IP_ADDRESS
|com.ibm.research.drl.dpt.providers.identifiers.ItalianFiscalCodeIdentifier|ITALIAN_FISCAL_CODE
|com.ibm.research.drl.dpt.providers.identifiers.ItalianVATCodeIdentifier|ITALIAN_VAT
|com.ibm.research.drl.dpt.providers.identifiers.LatitudeLongitudeIdentifier|LATITUDE_LONGITUDE
|com.ibm.research.drl.dpt.providers.identifiers.LotusNotesIdentifier|PERSON
|com.ibm.research.drl.dpt.providers.identifiers.MACAddressIdentifier|MAC_ADDRESS
|com.ibm.research.drl.dpt.providers.identifiers.MaritalStatusIdentifier|MARITAL_STATUS
|com.ibm.research.drl.dpt.providers.identifiers.MedicalPatternIdentifier|MRN
|com.ibm.research.drl.dpt.providers.identifiers.MedicineIdentifier|MEDICINE
|com.ibm.research.drl.dpt.providers.identifiers.MonthIdentifier|MONTH
|com.ibm.research.drl.dpt.providers.identifiers.NameIdentifier|NAME
|com.ibm.research.drl.dpt.providers.identifiers.NationalRegistrationIdentityCardIdentifier|NRIC
|com.ibm.research.drl.dpt.providers.identifiers.NumericIdentifier|NUMERIC
|com.ibm.research.drl.dpt.providers.identifiers.OccupationIdentifier|OCCUPATION
|com.ibm.research.drl.dpt.providers.identifiers.OSIdentifier|OS-NAMES
|com.ibm.research.drl.dpt.providers.identifiers.PatientIDIdentifier|EMAIL
|com.ibm.research.drl.dpt.providers.identifiers.PhoneIdentifier|PHONE
|com.ibm.research.drl.dpt.providers.identifiers.POBOXIdentifier|ADDRESS
|com.ibm.research.drl.dpt.providers.identifiers.RaceEthnicityIdentifier|RACE
|com.ibm.research.drl.dpt.providers.identifiers.ReligionIdentifier|RELIGION
|com.ibm.research.drl.dpt.providers.identifiers.SortCodeIdentifier|SORT_CODE
|com.ibm.research.drl.dpt.providers.identifiers.SSNUKIdentifier|SSN_UK
|com.ibm.research.drl.dpt.providers.identifiers.SSNUSIdentifier|SSN_US
|com.ibm.research.drl.dpt.providers.identifiers.StatesUSIdentifier|STATES_US
|com.ibm.research.drl.dpt.providers.identifiers.StreetTypeIdentifier|STREET_TYPES
|com.ibm.research.drl.dpt.providers.identifiers.SWIFTCodeIdentifier|SWIFT
|com.ibm.research.drl.dpt.providers.identifiers.UKPostCodeIdentifier|POSTCODE
|com.ibm.research.drl.dpt.providers.identifiers.URLIdentifier|URL
|com.ibm.research.drl.dpt.providers.identifiers.USPhoneIdentifier|PHONE
|com.ibm.research.drl.dpt.providers.identifiers.VINIdentifier|VIN
|com.ibm.research.drl.dpt.providers.identifiers.YOBIdentifier|YOB
|com.ibm.research.drl.dpt.providers.identifiers.ZIPCodeIdentifier|ZIPCODE

## Custom identifier

DPT allows the definition of custom identifiers. This is done by specifing the required identification strategy and customizing the identification patterns.

Currently, there are two supported types of custom identifiers, regular expression and dictionary based.
The skeleton for defining a custom identifier is the following:

```json
{
	"type":"IDENTIFIER TYPE",
	"providerType":"SEMANTIC TYPE"
}
```

where `IDENTIIFER TYPE` is either `REGEX` or `DICTIONARY` and where `providerType` specifies the semantic type that the custom identifier needs to return in case of match. Thus, the provider type is the "label" that the user wants to associated to this specific identifier.

### Regular expression-based custom identifiers

If the type of custom identifier is `REGEX` the following structure can be used:

```json
{
	"type":"REGEX",
	"providerType":"MY_TYPE",
	"regex":[
		"PATTERN1",
		"PATTERN2"
	]
}
```

Where `regex` is a list of patterns following the specification of [Java Pattern](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html).

### Dictionary-based custom identifiers

If the type of custom identifier is `DICTIONARY` the following structure can be used:

```json
{
	"type":"DICTIONARY",
	"providerType":"MY_TYPE",
	"terms":[
		"TERM1",
		"TERM2"
	],
	"ignoreCase": true
}
```

Where `terms` is a list of dictionary terms
and where `ignoreCase` is a (optional) boolean value specifying how to treat the terms.

Note that if the list of terms is too long, it is possible to replace `terms` with `paths` as follows:

```json
{
	"type":"DICTIONARY",
	"providerType":"MY_TYPE",
	"paths":[
		"/my/file/path/file1.txt",
		"/my/file/path/file2.txt"
	],
	"ignoreCase": true
}
```

Where `paths` is a list of file paths from which the terms will be loaded.

**The structure of the dictionary file is assumed to be a list of terms, one per line.**

Note that replicated terms will be discarded and also note that the files need to be accessible from the JVM running the task.

## Generated report

For each processed file a report file will be generated. The file name of the report will be the same as the original one, so pay attention that input and output references do not refer to the same file or folder.


The output will be a dictionary. The output contains two sections: the `rawResults` section that contains the raw counters of the identification process and the `bestTypes` which infers the best matching type based on the raw counters.

The keys to each section will be the field names. For each key the value is an array of the identified types information, which includes the data type as `typeName` and the number of instances that was found in the data as `count`.

If the input file has two columns and the column names are specified in the header: `f0`, `f1` and their data types are `NAME`, `EMAIL` then the output will be a JSON object with the identified column names as keys and the type name and instances detected as values. In our example, the output will look like this:

```json
{
  "bestTypes" : {
    "f0" : {
      "typeName" : "NAME",
      "count" : 66
    },
    "f1" : {
      "typeName" : "EMAIL",
      "count" : 60
    }
  },
  "rawResults": {
      "f0" : [ {
        "typeName" : "NAME",
        "count" : 66
      } ],
      "f1" : [
              { "typeName" : "EMAIL", "count" : 60 },
              { "typeName" : "EMPTY", "count" : 6 }
      ]
  }
}
```

In case no header is present (for example if the field `hasHeader` set to false with a CSV input) the columns will be named "Column 0", "Column 1", ... etc.

If a type is not detected for a column, then the type name will be `UNKNOWN`.

### Notes

For proper output format, the JVM needs to be set up to use UTF-8

Two options:

a) use `-Dfile.encoding=UTF-8` option when invoking the toolkit

b) set the value of LC_ALL environmental variable in the shell where the toolkit will run: `export LC_ALL="en_US.UTF-8"`

## Examples

Example of config.json for the Identification of structured data (CSV):

```json
{
  "task":"Identification",

  "inputFormat":"CSV",
  "inputOptions":{
    "fieldDelimiter":",",
    "quoteChar":"\"",
    "hasHeader":true,
    "trimFields":true
  },

  "taskOptions":{
    "localization":"en-US",
    "firstN":10000,
    "identificationPatterns":"",
    "identificationLookup":"",
    "piList":""
  }
}
```

Example of config.json for the Identification of structured data (JSON):

```json
{
  "task":"Identification",
  "extension":"json",
  "inputFormat":"JSON",
  "inputOptions":{
  },

  "taskOptions":{
    "localization":"en-US"
  }
}
```
