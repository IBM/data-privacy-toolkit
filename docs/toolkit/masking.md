# Masking Task

This task takes care of masking each input file and producing a masked version of it.

In this context, to mask means to replace an original value with a fictionalized one according to the configuration of the specified masking provider.

The `taskConfiguration` field for this task accepts the following structure:

```json
{
        "toBeMasked" : {
          "Column 1": "NUMBERVARIANCE"
        },
        "predefinedRelationships": {
        	// predifined relationship, if any
        },
        "maskingProvidersConfig": {
        	"_fields": {
        	},
        	"_defaults": {
        	}
        }
    }
```

Where `toBeMasked` is a dictionary that maps the field names with their masking provider. The keys of the dictionary are the field names. The value for each is the name of the masking provider (see below section "Available masking providers" for the full list of masking providers available). The number of the keys does not necessarily need to be equal to the number of fields in the file. Only the fields present in *toBeMasked* dictionary will be masked. For example, if we have three fields f1, f2 and f3 and we want to mask f2 using the `SHIFT` masking provider and f3 using the `BINNING` masking provider, then *toBeMasked* should look like this:

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

`predefinedRelationships` include the relationships between fields. See section below for more details. If there are no relationships, the field can be ommitted or set to `null`.

## Available masking providers

The list of available masking providers is the following:

|Masking Provider Name | Description |
|---|---|
|ADDRESS|Masks addresses, supports free text and PO BOX formats|
|AGE|Masks age values|
|ANIMAL|Masks animal species|
|ATC|Masks ATC codes|
|BINNING|Bins numerical values|
|BOOLEAN|Replaces boolean values with another random boolean value|
|CHAMELEON|Chameleon Pseudonyms|
|CITY|Masks city names, support masking based on closest cities|
|CONTINENT|Masks continent names|
|COUNTRY|Masks country names, supports masking based on closest countries|
|COUNTY|Replaces the county names|
|CREDIT_CARD_TYPE|Mask credit card vendor names|
|CREDIT_CARD|Masks credit cards, support vendor preservation|
|DATETIME|Masks date/time, supports multiple datetime formats or user-specified ones|
|DAY|Mask day provider type|
|DECIMAL_ROUNDING|Rounds decimal points|
|DEPENDENT|Randomizes dependents|
|DICTIONARY_BASED|Replaces values based on dictionaries|
|DIFFERENTIAL_PRIVACY|e-differential privacy|
|DUMMY|Reflects original value back|
|EMAIL|Masks e-mail addresses, supports preservation of domain names|
|FHIR|Masks FHIR objects|
|FREE_TEXT|Mask provider type|
|GENDER|Replaces the genders|
|GENERALIZATION|Generalization-based|
|GENERIC_LOCATION|Masks generic locations|
|GUID|Replaces values with a GUID|
|HASHINT|Hashes the incoming (integer) value and returns an integer (as a string)|
|HASH|Hashes the value|
|HEIGHT|Masks height values|
|HOSPITAL|Masks names of hospitals and medical centers|
|IBAN|Masks IBAN values|
|ICDv10|Masks ICD v10 codes, supports preservation of chapter and section|
|ICDv9|Masks ICD v9 codes, supports preservation of chapter and section|
|IMEI|Masks IMEI values, supports vendor preservation|
|IMSI|Masks IMSI values|
|IP_ADDRESS|Masks IP addresses, supports prefix preservation|
|LATITUDE_LONGITUDE|Masks latitude/longitude pairs, supports multiple coordinate formats|
|LOCATION|Masks generic locations|
|MAC_ADDRESS|Masks MAC addresses, supports vendor preservation|
|MARITAL_STATUS|Masks marital status|
|MEDICINE|Masks name of drugs|
|MONETARY|Redact the monetary value replacing the digits|
|MONTH|Mask month provider type|
|MRN|Medical record number|
|NAME|Masks names (with gender preservation) and surnames|
|NATIONAL_ID|Masks national ID|
|NULL|Replaces the value with an empty one|
|NUMBERVARIANCE|Replaces numerical date with a random variance|
|NUMERIC|Masks numerical values|
|OCCUPATION|Masks occupations|
|ORGANIZATION|Masks organizations|
|PERSON|Person|
|PHONE|Masks phone numbers, supports preservation of country codes and areas|
|POSTCODE|Replaces the UK Post codes|
|PROCEDURE|Mask procedures|
|RACE|Masks races and ethnicity|
|RANDOM|Changes characters of the value randomly|
|RATIO_BASED|Ratio based|
|REDACT|Remove the value replacing it with an appropriate number of *|
|RELIGION|Masks religion names|
|REPLACE_FIXED|Replace fixed|
|REPLACE|Replaces and preserves parts of the value|
|SHIFT|Shifts the value|
|SORT_CODE||
|SSN_UK|Masks social security numbers|
|SSN_US|Masks social security numbers|
|STATES_US|Replaces the US States value|
|STREET_TYPES|Randomizes street types|
|SUPPRESS_FIELD|Suppress the entire field|
|SWIFT|Masks SWIFT codes, support country preservation|
|SYMPTOM|Mask symptoms|
|TEMPORAL|Temporal pattern|
|TIME|Time pattern|
|URL|Masks URLs, supports preservation of domain names and usernames|
|VIN|Masks VINs, support vendor preservation|
|YOB|Replaces the year of birth|
|ZIPCODE|Replaces the ZIP codes|

Each masking provider has a set of configuration options to tailor the behavior according to user's needs. References of the available options is in the [live demo (accessible ONLY from IBM network)](http://irihost01.sl.cloud9.ibm.com:8082/#/configuration)

### Configuration options for the masking providers

Masking configuration options control how the masking providers behave. For example,
we want to mask a numeric field named `height` using the SHIFT masking provider. By default, the SHIFT masking provider shifts the value by +1.0, since the default value of `shift.mask.value` is set to 1.0 (see the relevant section in the list of options). So, the value `2` will be masked to `3`. Let's assume that we want to shift the value by +2.4. We have two options to alter the behavior of the masking provider:

1) We override the configuration option in the `_defaults` section of the masking configuration. The `_defaults` section will now look like:

```json
{
	"_fields": {
	},
	"_defaults": {
		"shift.mask.value": 2.4
	}
}
```
All the SHIFT masking providers will now be initialized to shift by +2.4. The `_defaults` section overrides the values of the system defaults encoded in the library.

2) We override the per-field configuration in the `_fields` section. The section will now look like:

```json
{
	"_fields": {
		"height": {
			"shift.mask.value": 2.4
		}
	},
	"_defaults": {
	}
}
```
The per-field configuration allows us to alter the behavior of that masking provider only for that field. If, for example, there is another numeric field in the file, lets name it `weight`, and we want to apply the SHIFT masking provider to it, then its masking provider will be initialized using the `_defaults` section and/or the system defaults (given the configuration above).

The configuration options in the `_fields` section have priority over the `_defaults` which in their turn have priority over the system defaults.

So in the case we have two fields, lets call them height and weight, and we want to apply different shift values to them, for example +2.4 for height and +3.5 for weight, then the configuration will look like:

```json
{
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
}
```

### Configuration for consistent masking

Let's assume that we have an input file with two columns, f0 and f1. By default, non-consistent masking is applied. We want to consistently mask the f0 column using the `BINNING` masking provider. To achieve this, we need to set `persistence.export` to true in the per-field configuration for f0. The default setting is to do consistent masking in-memory, thus the masking results will be consistent for that session but the results of the consistent replacement will be lost at exit.

The consistent masking requires a namespace to be provided every time we enable `persistence.export` to true. The namespace value is user-provided

```json
{
  "toBeMasked" : {
  	"f0": "BINNING"
  },
 "maskingProvidersConfig": {
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
}
```

If we want to have consistent masking across exports, we need to enable it by setting the correct configuration options to the per-field configuration. We need to set `persistence.type` to `"file"` and `persistence.file` should include a directory where we have permission to store the mappings.
The following example, will store the consistent masking results to `/tmp/f0_ns`.
The next time the toolkit runs, it will initialize the masking mapping for that column from that file, thus ensuring consistent results across different executions.


```json
{
  "toBeMasked" : {
  	"f0": "BINNING"
  },

  "maskingProvidersConfig": {
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
}
```

## Predefined relationships

The `predefinedRelationships` list includes a list of relationships between fields.
Each relationship is an object. An example relationship is shown below:

```json
"predefinedRelationships" : {
	"date": {
      "relationshipType": "KEY",
      "valueClass": "DATE",
      "operands": [
        {
          "name": "id",
          "type": "NUMERIC"
        }
      ]
    }
   }
}
```

The key of the `predifinedRelationship` map is the name of the fied for which the relationship is about.

The value of each key is a structured as follows:
* `valueClass` , this can be `TEXT`, `DATE`
* a list of operands defined by the `operands` array. Each operand is an object with two keys: `name` which is the field name of the operand and `type` which is the semantic type of the operand (unused for now)
* `relationshipType` which defines the relationship type between `fieldName` and its operands. Relationship types can be `EQUAL`, `LESS`, `GREATER`, `KEY` and `LINKED`.

The following compound relationships are supported:

|Masking provider|Relationship types|Description|
|----|---|----|
|DATETIME|LESS, GREATER, EQUAL, DISTANCE|The masked result will respect the date difference between the fields and its operands. LESS means we know that the date is before the operand, GREATER means it is after. The DISTANCE type identifies the distance on a value basis and respects it in the masked result|
|DATETIME|KEY|The date will be masked using the operand as the key|
|REPLACE|KEY|Check if we want to replace a value based on operand value|
|RATIO_BASED|KEY|Use the operand as the ratio value to mask|
|RATIO_BASED|LINKED|Respect the ratio between the value to mask and the operand value|
|COUNTRY|LINKED|Mask the country value with the country where the operand value belongs (e.g. city)|
|CONTINENT|LINKED|Mask the continent value with the continent where the operand value belongs (e.g. city or country)|
|DUMMY|EQUALS|Copies the value from the operand|
|FREE_TEXT|GREP\_AND\_MASK|Gets the tokens from the operands and looks them up on the operator. Located tokens will be masked according to the configuration|

