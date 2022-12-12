# FHIR support
**Example**

This example shows how to mask a value in the FHIR object model. 
Let's assume that the de-identification rule says that we need to hash the 
identifier:type:coding:code element of a Patient resource.

1. In the _defaults we create an entry that instructs we need to mask the element. The key of the entry is constructed as follows: "fhir.maskingConf.*resourceType*.*unique name*". The value of the entry is a value that includes two parts separated by semicolon. The first part is the JSON path of the element we want to mask. The second part is either: a) the FHIR element type (Identifier, Reference, HumanName, etc.) b) `null` if it is a simple data type like a string or a date or c) `Delete` if we want to delete the field
2. In the _fields we create an entry that specifies the per-field configuration for the element we want to mask. The key name is constructed as "--/fhir/*resourceType*/*path of the element*" and the contents of that key are key-value pairs for the masking configuration

```json
{
  "delimiter": ",",
  "quoteChar": "\"",

  "fieldsList": [
  ],

  "toBeMasked" : {},

  "inputFormat": "JSON",
  "outputFormat": "JSON",

  "identifyRelationships": false,

  "predefinedRelationships": [],

  "_fields" : {
    "--/fhir/Patient/identifier/type/coding/code" : {
   		"default.masking.provider": "HASH"
    }
  },
  "_defaults": {
     "fhir.maskingConf.Patient.identifier.type.coding.code": "/identifier/type/coding/code:null"
  }
}
```
