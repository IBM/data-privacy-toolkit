## Invoking the masking Spark executor

The masking executor requires the input and output paths as well as the configuration file.
It can be invoked as:

```
spark-submit \
	--class com.ibm.research.drl.dpt.spark.masking.DataMaskingExecutor \
	dpt-spark-$VERSION-jar-with-dependencies.jar \
	-c masking_conf.json \
	-i test.csv \
	-o masked_output
```

where `masking_conf.json` is the masking configuration file, `test.csv` is the input file and `masked_output` is the output directory. 

The full list of parameters to the executor are:

| Parameter           | Description                                                                                                                                               |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| -i,--input \<arg\>  | Path to the file to be masked (required)                                                                                                                  |
| -o,--output \<arg\> | Output directory (required)                                                                                                                               |
| -c,--conf \<arg\>   | Path to the configuration file (required)                                                                                                                 |
| -p \<arg\>          | Partition output. <arg> is a comma-separated list of fields to be used for partitioning. The order of this list defines the partitioning order (optional) |
| -a                  | Write output in append mode (optional)                                                                                                                    |
| -basePath \<arg\>   | Specify the base path of the input (optional)                                                                                                             |
| --remoteConf        | If set, then the configuration file will be read from HDFS (optional)                                                                                     |

The `--remoteConf` controls if the configuration file should be read from HDFS instead of the local filesystem.

## Invoking the Spark RDD API

Users can invoke the Spark API as:

```java

import com.ibm.research.drl.dpt.spark.masking.DataMasking;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Row;

public Dataset<Row> maskRDD(Dataset<Row> input, InputStream confStream) {
	return DataMasking.run(confStream, input);
}
```

where `confStream` is the input stream to the masking configuration file and `input` is the RDD containing the data to mask. 

## Writing the masking configuration file

The masking action requires a configuration file in order to understand the input formats, the fields in the file and the masking configuration for each field.

This is a sample template for the masking configuration:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": true,

  "toBeMasked" : {},

  "inputFormat": "CSV",
  "outputFormat": "CSV",
  
  "predefinedRelationships" : [],

  "_fields" : {
  },
  "_defaults": {
  }
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
| VCF                                |

* *toBeMasked* is a dictionary that maps the field names with their masking provider. The keys of the dictionary are the field names. The value for each is the name of the masking provider (see below section "Masking configuration options" for the full list of masking providers available). The number of the keys does not necessarily need to be equal to the number of fields in the file. Only the fields present in *toBeMasked* dictionary will be masked. For example, if we have three fields f1, f2 and f3 and we want to mask f2 using the `SHIFT` masking provider and f3 using the `BINNING` masking provider, then *toBeMasked* should look like this:

```json
	"toBeMasked" : {
		"f2": "SHIFT",
		"f3": "BINNING"
	}
```

In the example above, the masked result of `f2` column will be saved to the same column. In case the user does not want inline replacement, then they can specify the target path to save the masking result in the following way:

```json
	"toBeMasked" : {
		"f2": {
		   "providerType": "SHIFT",
		   "targetPath" : "f4"
		},
		"f3": "BINNING"
	}
```

This way, the masked values of `f2` will be saved to column `f4`.

* `outputFormat` defines the output format.
* options `quoteChar`, `delimiter`, `hasHeader` and `trimFields` are input-specific options
* `predefinedRelationships` include the relationships between fields. See section below for more details. If there are no relationships, the field can be ommitted or set to `null` or be an empty array.
* *_fields* is a dictionary that includes the per-field masking configuration (see below section "Masking configuration options" for more details). The dictionary is keyed using the field names of the CSV file.
* *_defaults* is a dictionary with key-value pairs that override the default masking configuration (see below section "Masking configuration options" for more details)

**Example for CSV files**

The following examples assumes two columns in the CSV dataset: f0 and f1. We set that f0 will be masked using the `SHIFT` masking provider.

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,

  "toBeMasked" : {
  	"Column 0": "SHIFT"
  },

  "inputFormat": "CSV",
  "outputFormat": "CSV",

  "_fields" : {
  },
  "_defaults": {
  }
}
```


## Configuration options for the masking providers

Masking configuration options control how the masking providers behave. For example,
we want to mask a numeric field named `height` using the SHIFT masking provider. By default, the SHIFT masking provider shifts the value by +1.0, since the default value of `shift.mask.value` is set to 1.0 (see the relevant section in the list of options). So, the value `2` will be masked to `3`. Let's assume that we want to shift the value by +2.4. We have two options to alter the behavior of the masking provider:

1. We override the configuration option in the `_defaults` section of the masking configuration. The `_defaults` section will now look like:

```json
	"_fields": {
	},
	"_defaults": {
		"shift.mask.value": 2.4
	}
```
All the SHIFT masking providers will now be initialized to shift by +2.4. The `_defaults` section overrides the values of the system defaults encoded in the library.

2. We override the per-field configuration in the `_fields` section. The section will now look like:

```json
	"_fields": {
		"height": {
			"shift.mask.value": 2.4
		}
	},
	"_defaults": {
	}
```
The per-field configuration allows us to alter the behavior of that masking provider only for that field. If, for example, there is another numeric field in the file, lets name it `weight`, and we want to apply the SHIFT masking provider to it, then its masking provider will be initialized using the `_defaults` section and/or the system defaults (given the configuration above).

The configuration options in the `_fields` section have priority over the `_defaults` which in their turn have priority over the system defaults.

So in the case we have two fields, lets call them height and weight, and we want to apply different shift values to them, for example +2.4 for height and +3.5 for weight, then the configuration will look like:

```json
	"_fields": {
		"height": {
			"shift.mask.value": 2.4
		},
		"weight": {
			"shift.mask.value": 3.5
		}
	},
	"_defaults": {
	}
```

### Configuration for consistent masking

Let's assume that we have an input file with two columns, f0 and f1. By default, non-consistent masking is applied. We want to consistently mask the f0 column using the `BINNING` masking provider. To achieve this, we need to set `persistence.export` to true in the per-field configuration for f0. The default setting is to do consistent masking in-memory, thus the masking results will be consistent for that session but the results of the consistent replacement will be lost at exit.

The consistent masking requires a namespace to be provided every time we enable `persistence.export` to true. The namespace value is user-provided

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,

  "toBeMasked" : {
  	"f0": "BINNING"
  },

  "inputFormat": "CSV",
  "outputFormat": "CSV",

  "_fields" : {
   "f0": {
  		"persistence.export": true,
  		"persistence.type": "memory",
  		"persistence.namespace": "f0_ns"
  	}
  },
  "_defaults": {
  }
}
```

If we want to have consistent masking across exports, we need to enable it by setting the correct configuration options to the per-field configuration. We need to set `persistence.type` to `"file"` and `persistence.file` should include a directory where we have permission to store the mappings.
The following example, will store the consistent masking results to `/tmp/f0_ns`.
The next time the toolkit runs, it will initialize the masking mapping for that column from that file, thus ensuring consistent results across different executions.


```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,

  "toBeMasked" : {
  	"f0": "BINNING"
  },


  "inputFormat": "CSV",
  "outputFormat": "CSV",

  "_fields" : {
   "f0": {
  		"persistence.export": true,
  		"persistence.type": "file",
  		"persistence.file": "/tmp/",
  		"persistence.namespace": "f0_ns"
  	}
  },
  "_defaults": {
  }
}
```

### Predefined relationships

The `predefinedRelationships` list includes a list of relationships between fields.
Each relationship is an object. An example relationship is shown below:

```json
"predefinedRelationships" : [
    {
      "fieldName": "date",
      "relationshipType": "KEY",
      "valueClass": "DATE",
      "operands": [
        {
          "name": "id",
          "type": "NUMERIC"
        }
      ]
    }
  ],
```

Each relationship needs to define the following keys:

* `fieldName` which is the name of the field for which the relationship is about
* `valueClass` , this can be `TEXT`, `DATE`
* a list of operands defined by the `operands` array. Each operand is an object with two keys: `name` which is the field name of the operand and `type` which is the semantic type of the operand (unused for now)
* `relationshipType` which defines the relationship type between `fieldName` and its operands. Relationship types can be `EQUAL`, `LESS`, `GREATER`, `KEY` and `LINKED`.

The following compound relationships are supported:

| Masking provider | Relationship types             | Description                                                                                                                                                                                                                                                                |
|------------------|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DATETIME         | LESS, GREATER, EQUAL, DISTANCE | The masked result will respect the date difference between the fields and its operands. LESS means we know that the date is before the operand, GREATER means it is after. The DISTANCE type identifies the distance on a value basis and respects it in the masked result |
| DATETIME         | KEY                            | The date will be masked using the operand as the key                                                                                                                                                                                                                       |
| REPLACE          | KEY                            | Check if we want to replace a value based on operand value                                                                                                                                                                                                                 |
| RATIO_BASED      | KEY                            | Use the operand as the ratio value to mask                                                                                                                                                                                                                                 |
| RATIO_BASED      | LINKED                         | Respect the ratio between the value to mask and the operand value                                                                                                                                                                                                          |
| COUNTRY          | LINKED                         | Mask the country value with the country where the operand value belongs (e.g. city)                                                                                                                                                                                        |
| CONTINENT        | LINKED                         | Mask the continent value with the continent where the operand value belongs (e.g. city or country)                                                                                                                                                                         |
| DUMMY            | EQUALS                         | Copies the value from the operand                                                                                                                                                                                                                                          |
| FREE_TEXT        | GREP\_AND\_MASK                | Gets the tokens from the operands and looks them up on the operator. Located tokens will be masked according to the configuration                                                                                                                                          |

## CSV masking

The following examples assumes two columns in the CSV dataset: f0 and f1. We set that f0 will be masked using the `SHIFT` masking provider.

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,
	
  "toBeMasked" : {
  	"Column 0": "SHIFT"
  },

  "inputFormat": "CSV",
  "outputFormat": "CSV"
}
```

* The `delimiter` and `quoteChar` values are used to determine the delimiter and quote char being used for the CSV format and the *hasHeader* value is used to determine if the first row of the file should be treated as a header.
* `trimFields` specifies if we want to trim the trailing spaces of CSV fields

Fields are named after the column names as specified in the CSV header. If the header is missing the fields will be named `Column 0`, `Column 1`, etc.

## JSON masking

Let's assume that our input is the following input record:

```json
{
	"name" : "John",
	"surname" : "Smith"
}
```
and we want to use the name masking provider on the `name` field and the hashing masking provider on the `surname` field.


For JSON, we need to pass the following data masking options:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,
  
  "toBeMasked" : {
  	"/name": "NAME",
  	"/surname": "HASH"
  },

  "inputFormat": "JSON",
  "outputFormat": "JSON"
}
```
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

## XML masking

Let's assume that the input record is the following XML:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<note>
  <to>Tove</to>
  <from>Jani</from>
  <email>lala@gmail.com</email>
  <heading>Reminder</heading>
  <body>Don't forget me this weekend!</body>
</note>
```

and we want to hash the `email` record.

For XML, we need to pass the following data masking options:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,

  "toBeMasked" : {
  	"/note/email": "HASH"
  },

  "inputFormat": "XML",
  "outputFormat": "XML"
}
```

The path notation is the following: `/workbook_name/cell_reference`. The `workbook_name` is the workbook name. The `cell_reference` can either refer to :

* a single cell. In that case the notation is `$column$row`, for example `$A$2`.
* a range of cells. In that case the notation is `$column1$row1:$column2$row2`, for example `$A$2:$D$4`. The first cell reference defines the upper left cell of the region and the second one the bottom right cell.


## XLS/XLSX masking

The library supports masking of individual cells in XLS/XLSX spreadsheets.

Let us assume that our input spreadsheet has two workbooks, `notes` and `notes2`.
In the `notes` workbook we want to hash the contents of the cell A2 and in the
`notes2` workbook we want to hash the contents of the cell B2.

For spreadsheets, we need to pass the following data masking options:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,
  
  "toBeMasked" : {
  	"/notes/$A$2": "HASH",
  	"/notes2/$B$2": "HASH"
  },

  "inputFormat": "XLS",
  "outputFormat": "XLS"
}
```

If the input format is XLSX, then we need to change the values of `inputFormat` and `outputFormat` to `XLSX`.

The path notation is the following: `/workbook_name/cell_reference`. The `workbook_name` is the workbook name. The `cell_reference` can either refer to :

* a single cell. In that case the notation is `$column$row`, for example `$A$2`.
* a range of cells. In that case the notation is `$column1$row1:$column2$row2`, for example `$A$2:$D$4`. The first cell reference defines the upper left cell of the region and the second one the bottom right cell.

By default, if a cell cannot be found, it will be ignored. For example, if `notes2` is not an existing workbook, the toolkit will silently ignore the masking rule. This behavior can change by setting the `excel.mask.ignoreNonExistent` configuration option to `false` in the `_defaults` or `_fields` section of the configuration.


## DICOM masking


The library supports the de-identification of DICOM attributes.

Let's assume we want to hash the patient tag (tag value is `(00010,00010)`) of a DICOM file. We need to pass the following masking options:

```json
{
  "delimiter": ",",
  "quoteChar": "\"",
  "hasHeader": false,
  "trimFields": false,

  "toBeMasked" : {
  	"(00010,00010)": "HASH"
  },

  "inputFormat": "DICOM",
  "outputFormat": "DICOM"
}
```

The path notation is `(group number, element number)` where the group and element numbers are represented by their hex values. 
The full list of tags can be viewed
here [DICOM tags](https://www.dicomlibrary.com/dicom/dicom-tags/)

## FHIR masking

The library supports the de-identification of JSON-based FHIR DSTUv2. Since the input format is JSON, the same principles apply from the JSON masking section. However, FHIR can contain complex objects, for example an Address object that needs to be masked on its entirety.

Let's assume we have a Patient resource and we want to hash the code found in path `/identifier/type/coding/code`, delete the field in `/identifier/type/coding/id` and use the FHIR Address masking provider for the `/address` field.

If we want to mask the example FHIR above, then we need to pass the following masking options:

```json
{
  "toBeMasked" : {
  		"Patient.code_hashing": "/identifier/type/coding/code:HASH",
  		"Patient.id_delete": "/identifier/type/coding/id:Delete",
  		"Patient.address_mask": "/address:FHIR_Address"
  },

  "inputFormat": "FHIR_JSON",
  "outputFormat": "FHIR_JSON"
}
```

We need to specify `FHIR` in the `jsonObjectType` so the library knows to treat it as a FHIR object.

The rules in `toBeMasked` follow the rules below:

1. The key of the rule needs to start with the resource name
2. The value of the rule consists of two parts, separated by semicolon. The first part is the JSON path of the field. The second one is the action to take. If the action is `Delete`, then the field will be deleted else the appropriate masking provider will be used.

## HL7 masking


This example shows how to mask a value in the HL7 object model.
Let's assume that the de-identification rule says that we need to hash the
`/.MSH-3-1` element of a v2.3 HL7 object.

We need to pass the following masking options:

```json
{
  "toBeMasked" : {
  	"/.MSH-3-1": "HASH"
  },

  "inputFormat": "HL7",
  "outputFormat": "HL7"
}
```
The notation of the element paths is based on the xref path defined here: [Terser documentation](https://hapifhir.github.io/hapi-hl7v2/base/apidocs/ca/uhn/hl7v2/util/Terser.html)

## VCF masking

VCF is very similar to CSV, but it lacks control of delimiters and quote characters. Headers are mandatory.

```json
{	
  "toBeMasked" : {
  	"INFO": "SHIFT"
  },

  "inputFormat": "VCF",
  "outputFormat": "VCF"
}
```

## List of masking providers with their options

There are some options that apply to many masking providers.

| Option name | Type    | Description                                                                                                                                                                                                                                                                             | Default value |
|-------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| fail.mode   | Integer | Controls how to handle cases where the value cannot be parsed or masked. Possible values are 1, 2, 3 and 4. Value 1 means that the original value will be returned. Value 2 means an empty value will be returned. Value 2 throws an error. Value 4 returns a randomly generated value. | 2             |

#### ADDRESS

Masks an address with a random one. Various elements of the address can be preserved like street names and road types.

| Option name                 | Type    | Description                                   | Default value |
|-----------------------------|---------|-----------------------------------------------|---------------|
| address.postalCode.nearest  | Boolean | Select nearest postal code                    | false         |
| address.roadType.mask       | Boolean | Mask road type (street, avenue, etc)          | true          |
| address.postalCode.nearestK | Integer | Number of closest postal codes to select from | 10            |
| address.country.mask        | Boolean | Mask country                                  | true          |
| address.postalCode.mask     | Boolean | Mask postal code                              | true          |
| address.number.mask         | Boolean | Mask number                                   | true          |
| address.city.mask           | Boolean | Mask city                                     | true          |
| address.mask.pseudorandom   | Boolean | Mask based on pseudorandom function           | false         |
| address.streetName.mask     | Boolean | Mask street name                              | true          |

#### PHONE

Replaces a phone number with a random one. There are options to preserve the country code and area code.

| Option name                | Type    | Description           | Default value |
|----------------------------|---------|-----------------------|---------------|
| phone.countryCode.preserve | Boolean | Preserve country code | true          |
| phone.areaCode.preserve    | Boolean | Preserve area code    | true          |

#### VIN

Masks vehicle identifier number with the options to preserve the manufacturer and vehicle description information.

| Option name      | Type    | Description                                    | Default value |
|------------------|---------|------------------------------------------------|---------------|
| vin.wmi.preserve | Boolean | Preserve manufacturer information (WMI)        | true          |
| vin.vds.preserve | Boolean | Preserve vehicle description information (VDS) | false         |

#### SSN_US

Masks Social Security Numbers based on the US with the option to preserve their prefix

| Option name                   | Type    | Description          | Default value |
|-------------------------------|---------|----------------------|---------------|
| ssnus.mask.preserveAreaNumber | Boolean | Preserve area number | true          |
| ssnus.mask.preserveGroup      | Boolean | Preserve group       | true          |

#### HASH

Hashes the original value given an algorithm name. See <https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest> for a list of available providers.

| Option name               | Type   | Description                    | Default value |
|---------------------------|--------|--------------------------------|---------------|
| hashing.algorithm.default | String | Default algorithm              | SHA-256       |
| hashing.salt              | String | Salt to be used during hashing | ""            |

#### COUNTY

Masks a county by replacing it with a random one.

| Option name              | Type    | Description                         | Default value |
|--------------------------|---------|-------------------------------------|---------------|
| county.mask.pseudorandom | Boolean | Mask based on pseudorandom function | false         |

#### URL

Masks URLs with the options to remove the query part, preserve domain levels, mask ports and username/passwords present

| Option name               | Type    | Description                   | Default value |
|---------------------------|---------|-------------------------------|---------------|
| url.mask.port             | Boolean | Mask port                     | false         |
| url.mask.removeQuery      | Boolean | Remove query part             | false         |
| url.preserve.domains      | Integer | Number of domains to preserve | 1             |
| url.mask.usernamePassword | Boolean | Mask username and password    | true          |
| url.mask.maskQuery        | Boolean | Mask query part               | false         |

#### NAME

Masks names. There are three options to mask names and there are checked in that order: a) generate a pseudorandom name (`names.mask.pseudorandom` controls this behavior) b) use custom masking functionality through virtual fields (`names.mask.virtualField` controls this) c) return a random name from the loaded dictionaries when the previous options are both disabled.

| Option name               | Type    | Description                                                                                                     | Default value |
|---------------------------|---------|-----------------------------------------------------------------------------------------------------------------|---------------|
| names.masking.allowUnisex | Boolean | Allow unisex names to be used for masking                                                                       | false         |
| names.masking.separator   | String  | The separator to use when splitting a name into its individual tokens                                           |
| names.mask.pseudorandom   | Boolean | Provide pseudodandom consistence                                                                                | false         |
| names.mask.virtualField   | String  | Treat name tokens as a virtual field and mask them according to the masking configuration of that virtual field | null          |

#### RACE

Replace races with a random one.

| Option name                | Type    | Description                                                         | Default value |
|----------------------------|---------|---------------------------------------------------------------------|---------------|
| race.mask.probabilityBased | Boolean | Masks the value based on probabilities defined in the resource file | false         |

#### GENDER

Replaces a gender with a random one. This provider does not have any configuration options

#### RELIGION

Replaces a religion with a random one.

| Option name                    | Type    | Description                                                         | Default value |
|--------------------------------|---------|---------------------------------------------------------------------|---------------|
| religion.mask.probabilityBased | Boolean | Masks the value based on probabilities defined in the resource file | false         |

#### BINNING

Bins numerical values based on a bin size and a configurable format. Default is bin size of 5 and the format is %s-%s, for example 2002 will be replaced by 2000-2005

| Option name                | Type    | Description                    | Default value |
|----------------------------|---------|--------------------------------|---------------|
| binning.mask.format        | String  | The format of the binned value | %s-%s         |
| null.mask.returnNull       | Boolean | The format of the binned value | false         |
| binning.mask.binSize       | Integer | The bin size                   | 5             |
| binning.mask.returnBinMean | Boolean | Return the mean of the bin     | false         |

#### SHIFT

Shifts a numerical value by a fixed offset.

| Option name      | Type   | Description             | Default value |
|------------------|--------|-------------------------|---------------|
| shift.mask.value | Double | The offset for shifting | 1.0           |

#### IP_ADDRESS

Masks IP addresses with the option to preserve subnets.

| Option name                | Type    | Description                    | Default value |
|----------------------------|---------|--------------------------------|---------------|
| ipaddress.subnets.preserve | Integer | Number of prefixes to preserve | 0             |

#### EMAIL

Masks e-mail addresses with the option to preserve certains level of the host domain and
create a username based on random combination of names/surnames.

| Option name             | Type    | Description                   | Default value |
|-------------------------|---------|-------------------------------|---------------|
| email.preserve.domains  | Integer | Number of domains to preserve | 1             |
| email.nameBasedUsername | Boolean | Name-based usernames          | false         |

#### IMSI

Masks IMSI identifiers with the option to preserve MCC and MNC

| Option name           | Type    | Description  | Default value |
|-----------------------|---------|--------------|---------------|
| imsi.mask.preserveMNC | Boolean | Preserve MNC | true          |
| imsi.mask.preserveMCC | Boolean | Preserve MCC | true          |

#### Export
| Option name                    | Type    | Description                        | Default value |
|--------------------------------|---------|------------------------------------|---------------|
| export.relationships.perRecord | Boolean | Per record relationship extraction | false         |
| export.sampling                | Integer | Sampling percentage                | 100           |

#### OrderPreservingEncryption

Masks a date using order preserving encryption. This provider is extremely slow for large scale masking

| Option name     | Type    | Description             | Default value                        |
|-----------------|---------|-------------------------|--------------------------------------|
| ope.out.start   | Integer | Default OUT-range start | 0                                    |
| ope.default.key | String  | Default encryption key  | 7579687b-33f5-4f5d-9127-3d7d21d9e028 |
| ope.out.end     | Integer | Default OUT-range end   | 2147483646                           |
| ope.in.start    | Integer | Default IN-range start  | 0                                    |
| ope.in.end      | Integer | Default IN-range end    | 32767                                |

#### SSN_UK

Masks SSN based on the UK with the option to preserve their prefix

| Option name               | Type    | Description     | Default value |
|---------------------------|---------|-----------------|---------------|
| ssnuk.mask.preservePrefix | Boolean | Preserve prefix | true          |


#### REPLACE

Replaces a value with either asterisks or with random characters. Digits are replaced by digits and alpha characters with random alpha characters. Other characters like dashes, commas, etc. are preserved.

| Option name                       | Type    | Description                                                 | Default value |
|-----------------------------------|---------|-------------------------------------------------------------|---------------|
| replace.mask.replaceWithAsterisks | Boolean | Replace the rest of the value with asterisks                | false         |
| replace.mask.preserve             | Integer | Number of characters to preserve                            | 3             |
| replace.mask.offset               | Integer | Starting offset for preserving                              | 0             |
| replace.mask.replaceWithRandom    | Boolean | Replace the rest of the value with random digits/characters | false         |

#### CREDIT_CARD

Masks credit card number with the option to preserve the issuer (VISA, Mastercard, etc.)

| Option name                | Type    | Description     | Default value |
|----------------------------|---------|-----------------|---------------|
| creditCard.issuer.preserve | Boolean | Preserve issuer | true          |

#### IBAN

Masks IBAN account numbers with the option to preserve country

| Option name               | Type    | Description           | Default value |
|---------------------------|---------|-----------------------|---------------|
| iban.mask.preserveCountry | Boolean | Preserve country code | true          |

#### MONETARY

Replaces monetary values

| Option name                  | Type    | Description                      | Default value |
|------------------------------|---------|----------------------------------|---------------|
| monetary.replacing.character | String  | Replacement character for digits | X             |
| monetary.preserve.size       | Boolean | Preserve the number of digits    | true          |

#### DATETIME

Masks datetime objects. There are several options like shifting dates, generalizing to month, year etc. or adding random offsets to the various datetime elements (years, months, days, hours, seconds etc.). If multiple options are set to true the following priority list is respected (examples are for 10th of January 2016):

1. Shift date by constant amount. Option `datetime.mask.shiftDate` controls this behavior
2. Generalize to week number/year (like 02/2016). See option `datetime.generalize.weekyear`
3. Generalize to month/year (like 01/2016). See option `datetime.generalize.monthyear`
4. Generalize to quarter year (like 01/2016). See option `datetime.generalize.quarteryear`
5. Generalize to year (2016). See option `datetime.generalize.year`
6. Generalize to N-year interval (like 2015-2020). See option `datetime.generalize.nyearinterval`
7. Replace day with another day of the same class (weekday, weekend). See option `datetime.mask.replaceDaySameClass`
8. Apply differential privacy to the dates. See option `datetime.mask.replaceDayWithDiffPriv`
9. Add random offsets to year, month, day, hour, minutes, seconds as specified. See options `datetime.year.mask`, `datetime.month.mask`, `datetime.day.mask`, `datetime.hour.mask`,  `datetime.minutes.mask`, `datetime.seconds.mask`

For specifying fixed date formats, the notation as specified in [https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html) must be used. Important notice: for specifying years the year-of-era symbol `y` must be used. Specifying year in the week-based-year symbol `Y` will result into an error.

| Option name                                 | Type    | Description                                                                                     | Default value |
|---------------------------------------------|---------|-------------------------------------------------------------------------------------------------|---------------|
| datetime.mask.shiftDate                     | Boolean | Shift date by a constant amount                                                                 | false         |
| datetime.mask.shiftSeconds                  | Integer | Seconds to shift date by                                                                        | 0             |
| datetime.generalize.year                    | Boolean | Generalize to year                                                                              | false         |
| datetime.generalize.nyearinterval           | Boolean | Generalize to N-year interval                                                                   | false         |
| datetime.generalize.weekyear                | Boolean | Generalize to week/year                                                                         | false         |
| datetime.generalize.monthyear               | Boolean | Generalize to mm/year                                                                           | false         |
| datetime.generalize.quarteryear             | Boolean | Generalize to quarter/year                                                                      | false         |
| datetime.generalize.nyearintervalvalue      | Integer | Value of for N-year interval generalization                                                     | 0             |
| datetime.mask.replaceDaySameClass           | Boolean | Replace a weekday with another random weekday and a weekend day with another random weekend day | false         |
| datetime.mask.replaceDayWithDiffPriv        | Boolean | Replace date using differential privacy                                                         | false         |
| datetime.mask.replaceDayWithDiffPrivEpsilon | Double  | Epsilon to be used when applying differential privacy                                           | 3.0           |
| datetime.year.mask                          | Boolean | Mask year                                                                                       | true          |
| datetime.year.rangeUp                       | Integer | Mask year range upwards                                                                         | 0             |
| datetime.year.rangeDown                     | Integer | Mask year range downwards                                                                       | 10            |
| datetime.month.mask                         | Boolean | Mask month                                                                                      | true          |
| datetime.month.rangeUp                      | Integer | Mask month range upwards                                                                        | 0             |
| datetime.month.rangeDown                    | Integer | Mask month range downwards                                                                      | 12            |
| datetime.day.mask                           | Boolean | Mask day                                                                                        | true          |
| datetime.day.rangeUp                        | Integer | Mask day range upwards                                                                          | 0             |
| datetime.day.rangeDown                      | Integer | Mask day range downwards                                                                        | 7             |
| datetime.hour.mask                          | Boolean | Mask hour                                                                                       | true          |
| datetime.hour.rangeUp                       | Integer | Mask hour range upwards                                                                         | 0             |
| datetime.hour.rangeDown                     | Integer | Mask hour range downwards                                                                       | 100           |
| datetime.minutes.mask                       | Boolean | Mask minutes                                                                                    | true          |
| datetime.minutes.rangeUp                    | Integer | Mask minutes range upwards                                                                      | 0             |
| datetime.minutes.rangeDown                  | Integer | Mask minutes range downwards                                                                    | 100           |
| datetime.seconds.mask                       | Boolean | Mask seconds                                                                                    | true          |
| datetime.seconds.rangeUp                    | Integer | Mask seconds range upwards                                                                      | 0             |
| datetime.seconds.rangeDown                  | Integer | Mask seconds range downwards                                                                    | 100           |
| datetime.format.fixed                       | String  | Datetime format                                                                                 | null          |
| datetime.mask.keyBasedMaxDays               | Integer | Maximum number of days when using key-based compound masking                                    | 100           |
| datetime.mask.keyBasedMinDays               | Integer | Minimum number of days when using key-based compound masking                                    | 0             |

#### ZIPCODE

Masks a ZIP code. The system includes the ZIP code information for US. The user can specify if minimum population is required to be checked and if true the minimum population threshold.
The minimum population is checked against the information stored from the latest US census (2010) and it is checked at a prefix-based level based on the number of digits specified by `zipcode.mask.minimumPopulationPrefixDigits`. By default, minimum population is set to 20000 and it is checked based on 3-digit prefixes.


| Option name                                | Type    | Description                                  | Default value |
|--------------------------------------------|---------|----------------------------------------------|---------------|
| zipcode.mask.requireMinimumPopulation      | Boolean | Require minimum population                   | true          |
| zipcode.mask.minimumPopulationPrefixDigits | Integer | Prefix for minimum population                | 3             |
| zipcode.mask.countryCode                   | String  | Country for the zip codes (2-digit ISO code) | US            |
| zipcode.mask.minimumPopulation             | Integer | Minimum Population                           | 20000         |
| zipcode.mask.minimumPopulationUsePrefix    | Boolean | Use prefix for minimum population            | true          |


#### HASHINT

Returns an integer value by hashing the original value.

| Option name               | Type    | Description                                                   | Default value |
|---------------------------|---------|---------------------------------------------------------------|---------------|
| hashint.budget.amount     | Integer | Amount of randomization to be added                           | 10            |
| hashint.budget.use        | Boolean | Limit the randomization within a budget                       | false         |
| hashint.algorithm.default | String  | Default algorithm                                             | SHA-256       |
| hashint.sign.coherent     | Boolean | The masked value has to be coherente with respect to the sign | true          |

#### REDACT

Redacts a value by replacing it with a character (default is asterisk). The length of the original value can be optionally preserved

| Option name              | Type    | Description                   | Default value |
|--------------------------|---------|-------------------------------|---------------|
| redact.preserve.length   | Boolean | Preserve token length         | true          |
| redact.replace.character | String  | Default replacement character | X             |

#### NULL

Replaces the original value with an empty string

#### IMEI

Masks IMEI attributes with the option to preserve the TAC information

| Option name           | Type    | Description         | Default value |
|-----------------------|---------|---------------------|---------------|
| imei.mask.preserveTAC | Boolean | Preserve TAC prefix | true          |

#### CITY

Masks a city with a random one or based on one of its neighbors (geographical distance).

| Option name            | Type    | Description                             | Default value |
|------------------------|---------|-----------------------------------------|---------------|
| city.mask.closest      | Boolean | Select one of the near cities           | false         |
| city.mask.closestK     | Integer | Number of closest cities to select from | 10            |
| city.mask.pseudorandom | Boolean | Mask based on pseudorandom function     | false         |

#### ICDv9

Masks ICDv9 codes. Codes can also be generalized to their chapters or categories.

| Option name            | Type    | Description               | Default value |
|------------------------|---------|---------------------------|---------------|
| icd.randomize.chapter  | Boolean | Randomize by chapter      | false         |
| icd.randomize.category | Boolean | Randomize by 3-digit code | true          |

#### ICDv10

Masks ICDv10 codes. Codes can also be generalized to their chapters or categories.

| Option name            | Type    | Description               | Default value |
|------------------------|---------|---------------------------|---------------|
| icd.randomize.chapter  | Boolean | Randomize by chapter      | false         |
| icd.randomize.category | Boolean | Randomize by 3-digit code | true          |

#### MAC_ADDRESS

Masks MAC addresses with the option to preserve the vendor information

| Option name                | Type    | Description                 | Default value |
|----------------------------|---------|-----------------------------|---------------|
| mac.masking.preserveVendor | Boolean | Preserve vendor information | true          |

#### Persistence

These options control the persistence when masking value. If `persistence.export` is set to true then an original value will always be masked to the same value. `persistence.type` defines the medium to use for storing the mappings. File-based, memory-based and database backends are supported.

| Option name                     | Type    | Description                                                                                             | Default value |
|---------------------------------|---------|---------------------------------------------------------------------------------------------------------|---------------|
| persistence.export              | Boolean | Persistence per export                                                                                  | false         |
| persistence.type                | String  | The backend system to use for storing the mappings. Possible values are `file`, `memory` and `database` | memory        |
| persistence.namespace           | String  | Persistence global namespace                                                                            | null          |
| persistence.schema              | Boolean | Persistence per schema                                                                                  | false         |
| persistence.file                | String  | Directory for the file-based backend                                                                    |
| persistence.database.username   | String  | Username for database connection                                                                        | ""            |
| persistence.database.password   | String  | Password for database connection                                                                        | ""            |
| persistence.database.driverName | String  | Driver name for database connection                                                                     | ""            |
| persistence.database.connection | String  | Connection string for database connection                                                               | ""            |
| persistence.database.cacheLimit | Integer | Cache limit in bytes for database-backed persistence                                                    | ""            |

#### ATC

Masks an ATC code with the option to preserve certain levels.

| Option name           | Type    | Description              | Default value |
|-----------------------|---------|--------------------------|---------------|
| atc.mask.levelsToKeep | Integer | Number of levels to keep | 4             |

#### OCCUPATION

Replaces an occupation with a random one or generalizes it to its category. Categories are based on the 2010 SOC classification.

| Option name                | Type    | Description                       | Default value |
|----------------------------|---------|-----------------------------------|---------------|
| occupation.mask.generalize | Boolean | Generalize to occupation category | false         |

#### CONTINENT

Masks a continent by replacing it with a random one or by a closest one (distance based on an approximate centroid of the continent's bounding box)

| Option name             | Type    | Description                                | Default value |
|-------------------------|---------|--------------------------------------------|---------------|
| continent.mask.closest  | Boolean | Select one of the nearest continents       | false         |
| continent.mask.closestK | Integer | Number of neighbors for nearest continents | 5             |

#### HOSPITAL

Replaces a hospital name with another one

| Option name                   | Type    | Description                             | Default value |
|-------------------------------|---------|-----------------------------------------|---------------|
| hospital.mask.preserveCountry | Boolean | Select a hospital from the same country | true          |

#### Defaults

The `default.masking.provider` option controls what masking provider to invoke in case none is specified or a provider is not found

| Option name              | Type   | Description              | Default value |
|--------------------------|--------|--------------------------|---------------|
| default.masking.provider | String | Default masking provider | RANDOM        |

#### NUMBERVARIANCE

Masks a numeric value by adding a +/- offset based on given percentages. Limits cannot be negative numbers. If precision digits is -1, the final result will be returned with its original precision, else the digit part will be trimmed accordingly.

| Option name                      | Type    | Description                | Default value |
|----------------------------------|---------|----------------------------|---------------|
| numvariance.mask.limitUp         | Double  | Up percentage limit        | 10.0          |
| numvariance.mask.limitDown       | Double  | Down percentage limit      | 10.0          |
| numvariance.mask.precisionDigits | Integer | Number of precision digits | -1            |

#### COUNTRY

Replaces a country with another random one or by a nearest country

| Option name               | Type    | Description                                | Default value |
|---------------------------|---------|--------------------------------------------|---------------|
| country.mask.closestK     | Integer | Number of nearest countries to select from | 10            |
| country.mask.closest      | Boolean | Select one of the near countries           | false         |
| country.mask.pseudorandom | Boolean | Mask based on pseudorandom function        | false         |

#### LATITUDE_LONGITUDE

Masks latitude/longitude pairs. Recognizes several formats

| Option name                            | Type    | Description                                             | Default value |
|----------------------------------------|---------|---------------------------------------------------------|---------------|
| latlon.mask.fixedRadiusRandomDirection | Boolean | Random a point with a fixed radium but random direction | false         |
| latlon.mask.donutMasking               | Boolean | Randomize a point in a donut-shaped                     | false         |
| latlon.mask.randomWithinCircle         | Boolean | Randomize a point in a circle                           | true          |
| latlon.offset.maximumRadius            | Integer | Maximum offset radius (in meters)                       | 100           |
| latlon.offset.minimumRadius            | Integer | Minimum Offset radius (in meters)                       | 50            |

#### SWIFT

Masks SWIFT codes using in the banking domain

| Option name                | Type    | Description      | Default value |
|----------------------------|---------|------------------|---------------|
| swift.mask.preserveCountry | Boolean | Preserve Country | false         |

#### DICTIONARY_BASED

Masks based on a random value selected from a given dictionary. The contents of the file should be one dictionary term per line

| Option name                   | Type   | Description                        | Default value |
|-------------------------------|--------|------------------------------------|---------------|
| dictionaryBased.mask.filename | String | Filename of the dictionary to load | ""            |

#### HADOOP_DICTIONARY_BASED

Masks based on a random value selected from a given dictionary. The contents of the file should be one dictionary term per line. The file is retrieved from HDFS. Warning: This masking provider is available only when running the Spark version of the toolkit.

| Option name            | Type   | Description                        | Default value |
|------------------------|--------|------------------------------------|---------------|
| hadoop.dictionary.path | String | Filename of the dictionary to load | ""            |

#### RATIO_BASED

Multiplies the value by the given ration and returns the result.

| Option name                     | Type    | Description                    | Default value |
|---------------------------------|---------|--------------------------------|---------------|
| ratiobased.mask.ratio           | Double  | Ratio to use                   | 1.0           |
| ratiobased.mask.precisionDigits | Integer | Precision for the decimal part | -1            |

#### DIFFERENTIAL_PRIVACY

Applies differential privacy to a given attribute

| Option name                           | Type   | Description                                                                                                 | Default value  |
|---------------------------------------|--------|-------------------------------------------------------------------------------------------------------------|----------------|
| differentialPrivacy.mechanism         | String | Mechanism to use. Options are LAPLACE\_NATIVE, LAPLACE\_BOUNDED, LAPLACE\_TRUNCATED, CATEGORICAL and BINARY | LAPLACE_NATIVE |
| differentialPrivacy.parameter.epsilon | Double | Epsilon to use                                                                                              | 8.0            |

In case that LAPLACE\_NATIVE, LAPLACE\_BOUNDED or LAPLACE\_TRUNCATED was selected as a mechanism, then the following options are required:

| Option name                   | Type   | Description                 | Default value |
|-------------------------------|--------|-----------------------------|---------------|
| differentialPrivacy.range.min | Double | Minimum value of the domain | 0             |
| differentialPrivacy.range.max | Double | Maximum value of the domain | 0             |

In case that the BINARY mechanism is applied, the following options are required:

| Option name                       | Type   | Description             | Default value |
|-----------------------------------|--------|-------------------------|---------------|
| differentialPrivacy.binary.value1 | String | The first binary value  | TRUE          |
| differentialPrivacy.binary.value2 | String | The second binary value | FALSE         |

Finally, for the case of the CATEGORICAL mechanism:

| Option name                                   | Type     | Description                                                                                                     | Default value |
|-----------------------------------------------|----------|-----------------------------------------------------------------------------------------------------------------|---------------|
| differentialPrivacy.categorical.hierarchyName | String   | The name of the hierarchy to be used                                                                            | ""            |
| differentialPrivacy.categorical.hierarchyMap  | JsonNode | A JSON object with the hierarchies specifications. See Section "Specifying hierarchies" below for more details/ | null          |

#### GENERALIZATION

| Option name                            | Type     | Description                                                                                                    | Default value |
|----------------------------------------|----------|----------------------------------------------------------------------------------------------------------------|---------------|
| generalization.masking.hierarchyName   | String   | The name of the hierarchy to be used                                                                           | ""            |
| generalization.masking.hierarchyMap    | JsonNode | A JSON object with the hierarchies specifications. See Section "Specifying hierarchies" below for more details | null          |
| generalization.masking.hierarchyLevel  | Integer  | The generalization level to apply                                                                              | -1            |
| generalization.masking.randomizeOnFail | Boolean  | If the value is not found in the hierarchy, then return a random result                                        | true          |

#### FREE_TEXT

The default behavior of the natural language text de-identification provider is to mask the entities. It also supports the options to return only annotated text, redact or tag the entities (by default all three options are disabled). Please see separate documentation for free text configuration specification.

| Option name                                    | Type   | Description                                                                                                                                                                                               | Default value                |
|------------------------------------------------|--------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------|
| freetext.mask.complexConfigurationFilename     | String | The path to the configuration file for initializing the NLP pipeline. See separate documentation on how to write this configuration file                                                                  | /complexWithIdentifiers.json |
| freetext.mask.complexConfigurationResourceType | String | The type of the configuration resource. This can be either `INTERNAL_RESOURCE` when reading from a file loaded in the classpath or `EXTERNAL_FILENAME` when reading from a file in the filesystem         | `INTERNAL_RESOURCE`          |
| freetext.mask.maskingConfigurationFilename     | String | The path to the configuration file for masking                                                                                                                                                            | /c.json                      |
| freetext.mask.maskingConfigurationResourceType | String | The type of the masking configuration resource. This can be either `INTERNAL_RESOURCE` when reading from a file loaded in the classpath or `EXTERNAL_FILENAME` when reading from a file in the filesystem | `INTERNAL_RESOURCE`          |

The free text masking provider supports a relationship type `GREP_AND_MASK` that gets tokens from a field, looks them up in the free text field and then masks them.
As an example, let's assume we want to mask the OBX element of an HL7 message (OBX contains comments in free text). We want to get the patient names from elements
`/.PID-5-1` and then locate them and mask them in the `/.OBX-5-1` element. The following options will be used for this compound masking:

| Option name                      | Type    | Description                                                                              | Default value |
|----------------------------------|---------|------------------------------------------------------------------------------------------|---------------|
| generic.lookupTokensSeparator    | String  | Separator string for splitting into tokens                                               | "^"           |
| generic.lookupTokensIgnoreCase   | Boolean | Ignore case when searching for tokens                                                    | false         |
| generic.lookupTokensFindAnywhere | Boolean | If set to true tokens are search everywhere in the text, else they are searched as words | false         |
| generic.lookupTokensType         | String  | Type to use for the matching tokens                                                      | ""            |


#### TAG

This provider does not take any options. It returns the field name appended with a unique id based on the value.

#### Excel masking provider

| Option name                  | Type    | Description                                     | Default value |
|------------------------------|---------|-------------------------------------------------|---------------|
| excel.mask.ignoreNonExistent | Boolean | Control the behavior when a cell does not exist | true          |

##### Specifying hierarchies

!INCLUDE "../common/anonymization-hierarchy-conf.md"



## Errors

A runtime exception will be thrown at the following cases:

* misconfiguration in the masking configuration file (wrong types or values)
* the input file cannot be found
* the user has no permission to read the input file or write to the output directory
