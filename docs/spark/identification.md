## Invoking the type identification Spark executor

The identification executor requires the input and output paths as well as the configuration file.
It can be invoked as:

```
spark-submit \
	--class com.ibm.research.drl.dpt.spark.identification.IdentificationExecutor \
	dpt-spark-$VERSION-jar-with-dependencies.jar \
	-c identification_conf.json \
	-i test.csv \
	-o report.json
```

where `identification_conf.json` is the configuration file, `test.csv` is the input file and `report.json` is the output filename to save the report.

The full list of parameters to the executor are:

| Parameter         | Description                                                           |
|-------------------|-----------------------------------------------------------------------|
| -i,--input <arg>  | Path to the file to be masked (required)                              |
| -o,--output <arg> | Output file (required)                                                |
| -c,--conf <arg>   | Path to the configuration file (required)                             |
| --remoteConf      | If set, then the configuration file will be read from HDFS (optional) |
| --remoteOutput    | If set, the report will be saved to HDFS                              |
| -basePath \<arg\> | Specify the base path of the input (optional)                         |

The `--remoteConf` controls if the configuration file should be read from HDFS instead of the local filesystem.

## Writing the configuration file
The identification action requires a configuration file in order to understand the input format and to control the identification behavior.

This is a sample template for the identification configuration:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": true,
  "inputFormat": "CSV",
  "identifiers": null,

  "specificField": null,
  "getFirstN": false,
  "doSampling": true,
  "sampleFraction": 0.01,
  "identificationStrategy": "FREQUENCY_BASED",
  "defaultPriority" : 50,
  "defaultFrequencyThreshold": 20,
  "considerEmptyForFrequency": false,
  "priorities" : {
  		"EMAIL": 90
  },
  "frequencyThresholds": {}
}
```

**Explanation**

* *inputFormat*: defines the input format for the export.

The following data input formats are supported:

| Data input format type enumeration |
|------------------------------------|
| CSV                                |
| JSON                               |
| DICOM                              |
| XLS                                |
| XLSX                               |
| XML                                |
| HL7                                |
| FHIR_JSON                          |
| PARQUET                            |

* options `quoteChar`, `delimiter`, `hasHeader` and `trimFields` are input-specific options
* the option `getFirstN` controls if we want to inspect only the first records from the input. If it is set to `true` the parameter `N` controls how many rows will be inspected.
* the option `doSampling` controls if we want to inspect a sample of the data. If it is set to `true` then the `sampleFraction` defines the sample fraction. `sampleFraction` needs to be a value between 0 and 1 (inclusive)
* `identificationStrategy` defines the strategy to calculate the best type inference. It can have two values: `FREQUENCY_BASED` and `PRIORITY_BASED`
* `defaultPriority` defines the default priority value for detected entities. Users can specify their own priorities via the `priorities` dictionary.
* `defaultFrequencyThreshold` defines the minimum frequency threshold for the frequency based strategy. Users can specify per-type frequency thresholds via the `frequencyThresholds`.
* `considerEmptyForFrequency` defines if empty values will be considered as part of the frequency calculation.
* the option `identifiers` specifiers which identifiers to load. If set to `null` it will load the system default list of identifiers. Otherwise, the list can be a custom one. See the section below for the configuration of identifiers list.
* the option `specificField` controls if we want to inspect only a specific field of the input. If it is set to `null`, all fields will be inspected

Notes

* if both `getFirstN` and `doSampling` are set to true, an error will be thrown
* if `getFirstN` is set to true and a negative `N` is specified, an error will be thrown

**Example for CSV files**

In this example, we will inspect a CSV file and we will only consider the first 1000 rows

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,

  "inputFormat": "CSV",
  "identifiers": null,
  "specificField": null,

  "getFirstN": false,
  "N": 1000,

  "doSampling": true,
  "sampleFraction": 0.01,

  "identificationStrategy": "PRIORITY_BASED",
  "defaultPriority" : 50,
  "defaultFrequencyThreshold": 20,
  "considerEmptyForFrequency": false,
  "priorities" : {
  		"EMAIL": 90
  },
  "frequencyThresholds": {}

}
```

## Identification strategies

The identification component includes two strategies for inferring the best type: frequency based and priority based.

For each type detected, the engine checks the frequency threshold (as defined in `defaultFrequencyThreshold` or the `frequencyThresholds`). If the threshold is not exceeded, the type is ignored.

Once the frequency threshold is exceeded, the priority mechanism is invoked to infer tge best type. Frequency-based strategy selects the type with the highest frequency.  If two types have the same frequency, the one with the highest priority wins. Priority-based strategy selects the type with the highest priority. If two types have the same priority, the one with the highest frequency wins.


## Output structure

The output will be a dictionary. The output contains two sections: the `rawResults` section that contains the raw counters of the identification process and the `bestTypes` which infers the best matching type based on the raw counters.

The keys to each section will be the field names. For each key the value is an array of the identified types information, which includes the data type as `typeName` and the number of instances that was found in the data as `count`.

```json
{
  "bestTypes" : {
    "id" : [ {
      "typeName" : "NUMERIC",
      "count" : 66
    } ],
    "email" : [ {
      "typeName" : "EMAIL",
      "count" : 60
    } ]
  },
  "rawResults": {
  	"id" : [ {
  	  "typeName" : "NUMERIC",
  	  "count" : 66
  	} ],
  	"email" : [
  		{
  	  		"typeName" : "EMAIL",
  	  		"count" : 60
  		},
  		{
  	  		"typeName" : "EMPTY",
  	  		"count" : 6
  		}
  	]
  }
}
```

## Path notation

### CSV 

Fields are named after the column names as specified in the CSV header. If the header is missing the fields will be named `Column 0`, `Column 1`, etc.

### PARQUET

The field name as specified in the Parquet headers

### JSON 

The path notation follows the XPath standards. If an element is an array then the user has the option to specify either a specific index or  use `*` for the entire array. Assume the following JSON object

```json
{
   "a": {
       "b" : {
           "c" : [
              "value1",
              "value2",
              "value3"
           ]
       },
       "d": "foo@foo.com"
   }
}
```

If we want to specify the path for element `d` then we would write `a/b/d`.
If we want to get/process the second value of element `c` then we would write `/a/b/c/1` (the indexing is zero-based). If we want to process all elements of `c` then the path would be `/a/b/c/*`.

### XML 

The path notation follows the XPath standards.

### DICOM 

The path notation is `(group number, element number)` where the group and element numbers are represented by their hex values. The full list of tags can be viewed
here [DICOM tags](https://www.dicomlibrary.com/dicom/dicom-tags/)

### HL7

The notation of the element paths is based on the xref path defined here: [Terser documentation](https://hapifhir.github.io/hapi-hl7v2/base/apidocs/ca/uhn/hl7v2/util/Terser.html)

### XLS/XLSX

The path notation is the following: `/workbook_name/cell_reference`. The `workbook_name` is the workbook name. The `cell_reference` can either refer to :

* a single cell. In that case the notation is `$column$row`, for example `$A$2`.
* a range of cells. In that case the notation is `$column1$row1:$column2$row2`, for example `$A$2:$D$4`. The first cell reference defines the upper left cell of the region and the second one the bottom right cell.


## Customizing the list of identifiers


If the `identifiers` option is set to null, then the system defaults will be loaded.
However, the user might want to customize the list of identifiers to run. In that case,
the `identifiers` option can be an array that contains either the FQCN of the identifiers to load or the specifications of a pluggable identifier.
For example, if we want to load only the email identifier, the configuration must look like the following:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,

  "inputFormat": "CSV",
  "identifiers": [
  	"com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier"
  ],
  
  "specificField": null,
  
  	"N": -1,
	"getFirstN": false,
	"doSampling": true,
	"sampleFraction": 0.001,
  
  "defaultPriority" : 50,
  "defaultFrequencyThreshold": 20,
  "considerEmptyForFrequency": false,
  "identificationStrategy": "PRIORITY_BASED",
  "priorities" : {},
  "frequencyThresholds": {}

}
```

The FQCN can be retrieved from the table of the following section.

If we want to load a pluggable identifier, then in the `identifiers` list we need to specify an object. There are two types of pluggable identifiers: regex-based and dictionary-based. 

Pluggable regex identifiers expect a list of regular expressions in their configuration. The regex engine will look for matches of these patterns on the entire input and not a part of it. If a match is found the identifier will return true. If the matching regex contains groups, then the identifier will return the offset and depth of the first group only. For example, if the regex is `foo(.*)` and the input is `foobar`, the pluggable identifier will return that the match started at offset 3 with depth 3 (the `bar` part)

If we want to load a regex-based identifier, then the object should look like this:

```json
{
	"type": "REGEX",
	"providerType": "MY_TYPE",
	"regex": [
		".*foo.*"
	],
	"isPOSIndependent": true
}
```

* `type` should be `REGEX`
* `providerType` is the name of the detected type we want to report. It can be any of the existing types or an entirely new one
* `regex` is a list of regular expressions. See [https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) for specifications.
* the `isPOSIndependent` flag is optional. It control if we want this identifier to be independent of part-of-speech type when using it within natural language text identification. The default value is true.

If we want to load a dictionary-based identifier, then the object should look like this:

```json
{
	"type": "DICTIONARY",
	"providerType": "MY_TYPE",
	"terms": [
		"foo"
	],
	"ignoreCase": false,
	"isPOSIndependent": true
}
```

* `type` should be `DICTIONARY`
* `providerType` is the name of the detected type we want to report. It can be any of the existing types or an entirely new one
* `terms` is a list of the dictionary terms
* `ignoreCase` is a boolean flag that denotes if we want to ignore case when we are looking the dictionary terms in the data
* the `isPOSIndependent` flag is optional. It controls if we want this identifier to be independent of part-of-speech type when using it within natural language text identification. The default value is true.

In the example below, we load the email identifier, the URL identifier and a pluggable regex-based identifier together:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,

  "inputFormat": "CSV",
  "identifiers": [
  	"com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier",
  	{
		"type": "REGEX",
		"providerType": "MY_TYPE",
		"regex": [
			".*foo.*"
		]
	},
	"com.ibm.research.drl.dpt.providers.identifiers.URLIdentifier"
  ],
  
  "specificField": null,
  
  "N": -1,
  "getFirstN": false,
  "doSampling": true,
  "sampleFraction": 0.001,
  
  "defaultPriority" : 50,
  "defaultFrequencyThreshold": 20,
  "considerEmptyForFrequency": false,
  "identificationStrategy": "PRIORITY_BASED",
  "priorities" : {},
  "frequencyThresholds": {}

}
```

## List of entities recognized

What follows is the list of identifiers available with the command line version of DPT. The list can be extended with any class implementing the `com.ibm.research.drl.dpt.providers.identifiers.Identifier` interface that is available in the class path of the executing JVM.

| Full Name                                                                                 | Short Name          |
|-------------------------------------------------------------------------------------------|---------------------|
| com.ibm.research.drl.dpt.providers.identifiers.AddressForFreeTextIdentifier               | ADDRESS             |
| com.ibm.research.drl.dpt.providers.identifiers.AddressIdentifier                          | ADDRESS             |
| com.ibm.research.drl.dpt.providers.identifiers.AgeIdentifier                              | AGE                 |
| com.ibm.research.drl.dpt.providers.identifiers.AnimalSpeciesIdentifier                    | ANIMAL              |
| com.ibm.research.drl.dpt.providers.identifiers.ATCIdentifier                              | ATC                 |
| com.ibm.research.drl.dpt.providers.identifiers.AustraliaAddressIdentifier                 | LOCATION            |
| com.ibm.research.drl.dpt.providers.identifiers.CityIdentifier                             | CITY                |
| com.ibm.research.drl.dpt.providers.identifiers.ContinentIdentifier                        | CONTINENT           |
| com.ibm.research.drl.dpt.providers.identifiers.CountryIdentifier                          | COUNTRY             |
| com.ibm.research.drl.dpt.providers.identifiers.CountyIdentifier                           | COUNTY              |
| com.ibm.research.drl.dpt.providers.identifiers.CreditCardIdentifier                       | CREDIT_CARD         |
| com.ibm.research.drl.dpt.providers.identifiers.CreditCardTypeIdentifier                   | CREDIT_CARD_TYPE    |
| com.ibm.research.drl.dpt.providers.identifiers.DateTimeIdentifier                         | DATETIME            |
| com.ibm.research.drl.dpt.providers.identifiers.DayIdentifier                              | DAY                 |
| com.ibm.research.drl.dpt.providers.identifiers.DependentIdentifier                        | DEPENDENT           |
| com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier                            | EMAIL               |
| com.ibm.research.drl.dpt.providers.identifiers.FreeTextNamesIdentifier                    | NAME                |
| com.ibm.research.drl.dpt.providers.identifiers.GenderIdentifier                           | GENDER              |
| com.ibm.research.drl.dpt.providers.identifiers.HospitalIdentifier                         | HOSPITAL            |
| com.ibm.research.drl.dpt.providers.identifiers.IBANIdentifier                             | IBAN                |
| com.ibm.research.drl.dpt.providers.identifiers.ICDv9Identifier                            | ICDv9               |
| com.ibm.research.drl.dpt.providers.identifiers.IMEIIdentifier                             | IMEI                |
| com.ibm.research.drl.dpt.providers.identifiers.IMSIIdentifier                             | IMSI                |
| com.ibm.research.drl.dpt.providers.identifiers.InternationalPhoneIdentifier               | PHONE               |
| com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier                        | IP_ADDRESS          |
| com.ibm.research.drl.dpt.providers.identifiers.ItalianFiscalCodeIdentifier                | ITALIAN_FISCAL_CODE |
| com.ibm.research.drl.dpt.providers.identifiers.ItalianVATCodeIdentifier                   | ITALIAN_VAT         |
| com.ibm.research.drl.dpt.providers.identifiers.LatitudeLongitudeIdentifier                | LATITUDE_LONGITUDE  |
| com.ibm.research.drl.dpt.providers.identifiers.LotusNotesIdentifier                       | PERSON              |
| com.ibm.research.drl.dpt.providers.identifiers.MACAddressIdentifier                       | MAC_ADDRESS         |
| com.ibm.research.drl.dpt.providers.identifiers.MaritalStatusIdentifier                    | MARITAL_STATUS      |
| com.ibm.research.drl.dpt.providers.identifiers.MedicalPatternIdentifier                   | MRN                 |
| com.ibm.research.drl.dpt.providers.identifiers.MedicineIdentifier                         | MEDICINE            |
| com.ibm.research.drl.dpt.providers.identifiers.MonthIdentifier                            | MONTH               |
| com.ibm.research.drl.dpt.providers.identifiers.NameIdentifier                             | NAME                |
| com.ibm.research.drl.dpt.providers.identifiers.NationalRegistrationIdentityCardIdentifier | NRIC                |
| com.ibm.research.drl.dpt.providers.identifiers.NumericIdentifier                          | NUMERIC             |
| com.ibm.research.drl.dpt.providers.identifiers.OccupationIdentifier                       | OCCUPATION          |
| com.ibm.research.drl.dpt.providers.identifiers.OSIdentifier                               | OS-NAMES            |
| com.ibm.research.drl.dpt.providers.identifiers.PatientIDIdentifier                        | EMAIL               |
| com.ibm.research.drl.dpt.providers.identifiers.PhoneIdentifier                            | PHONE               |
| com.ibm.research.drl.dpt.providers.identifiers.POBOXIdentifier                            | ADDRESS             |
| com.ibm.research.drl.dpt.providers.identifiers.RaceEthnicityIdentifier                    | RACE                |
| com.ibm.research.drl.dpt.providers.identifiers.ReligionIdentifier                         | RELIGION            |
| com.ibm.research.drl.dpt.providers.identifiers.SortCodeIdentifier                         | SORT_CODE           |
| com.ibm.research.drl.dpt.providers.identifiers.SSNUKIdentifier                            | SSN_UK              |
| com.ibm.research.drl.dpt.providers.identifiers.SSNUSIdentifier                            | SSN_US              |
| com.ibm.research.drl.dpt.providers.identifiers.StatesUSIdentifier                         | STATES_US           |
| com.ibm.research.drl.dpt.providers.identifiers.StreetTypeIdentifier                       | STREET_TYPES        |
| com.ibm.research.drl.dpt.providers.identifiers.SWIFTCodeIdentifier                        | SWIFT               |
| com.ibm.research.drl.dpt.providers.identifiers.UKPostCodeIdentifier                       | POSTCODE            |
| com.ibm.research.drl.dpt.providers.identifiers.URLIdentifier                              | URL                 |
| com.ibm.research.drl.dpt.providers.identifiers.USPhoneIdentifier                          | PHONE               |
| com.ibm.research.drl.dpt.providers.identifiers.VINIdentifier                              | VIN                 |
| com.ibm.research.drl.dpt.providers.identifiers.YOBIdentifier                              | YOB                 |
| com.ibm.research.drl.dpt.providers.identifiers.ZIPCodeIdentifier                          | ZIPCODE             |

## Errors

A runtime exception will be thrown at the following cases:

* misconfiguration in the configuration file (wrong types or values)
* the input file cannot be found
* the user has no permission to read the input file or write to the output directory



