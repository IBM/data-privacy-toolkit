# Anonymization Task

This task is in charge of applying a privacy model to an input dataset.

Out of the box, the task supports the following privacy models:

* *k*-anonyity
* *l*-diversity
* *t*-closeness

The task options of anonymization task are as follow:

```json
{
  "algorithm": "ANONYMIZATION ALGORITHM",
  "privacyConstraints": [
    // privacy contraints
  ],
  "suppressionRate": 0.0,
  "informationLoss": "AN INFORMATION LOSS METRIC",
  "columnInformation": [
   // column information
  ]
}
```
where, `algorithm` is the name of the algorithm that the user wants to use for the anonymization process.
Currently DPT-CLI supports the following algorithms:

- OLA
- MONDRIAN
- KMAP
- KMEANS
- SAMPLING

Moreover, `privacyConstraints` is a list of the constrains that the user wants to be enforced, with the addition of `suppressionRate` to specify the maximum fraction of dataset that can be suppressed to allow better utility.
The utility is preserved by minimizing the metric specified in the `informationLoss` field. The information loss metrics supported are the following:

- AECS, Average Equivalence Class Size
- CP, Categorical Precision
- DM, Discernibility
- DMSTAR, Discernibility *
- GLM, Generalized Loss Metric
- GCP, Global Certain Penalty
- NUE, Non Uniform entropy
- NP, Numerical Precision
- SSM, Sensitive Similarity Measure

`columnInformation` contains the descripton of the dataset in terms of what are the characteristics of the various colums/fields.

## Privacy Constraints

The privacy constraints supported are expressed as follows.


```json
{
	"type": "KAnonymity",
	"k": 5
}
```

```json
{
	"type": "DistinctLDiversity",
	"l": 10
}
```

```json
{
	"type": "EntropyLDiversity",
	"l": 10
}
```

```json
{
	"type":"RecursiveCLDiversity",
	"l": 10,
	"c": 0.1
}
```

```json
{
	"type":"TCloseness",
	"t": 0.8
}
```

Note that the suppression rate allowed is indicated through the `suppressionRate` field.
A value of zero will prevent suppression to be used.


## Column Information

The `columnInformation` field contains the description of all the fields of the dataset to be anonymized.

Note that the specification of the dataset fields needs to follow the same order fo the actual fields.

DPT supports several field types:

* Normal
* Quasi Identifier
* Sensitive

The fields used during the anonymization process are the ones marked with either QUASI and/or Sensisitve, according to the algorithm used and privacy constraints specified.

Any other field, i.e. any field that must not be touched during anonymization, should appear with an entry as follows:

```json
{
  "class": "DefaultColumnInformation",
  "forLinking": false
}
```

Where `forLinking` is information required if dataset linking-based risk analysis will be later performed.

Sensitive fields, i.e. fields generally containing sensitive information but that should not be generalized, are specified as follows:

```json
{
  "class": "SensitiveColumnInformation",
  "forLinking": false
}

```

Again, the `forLinking` field is required if dataset linking-based risk analysis will be later performed.

The columns that contain quasi indentifers, on the other hand, should be represented by specifications of one of the following.

Columns containing numerical and discrete values can be represented with:

```
{
  "class": "NumericalInformation",
  "sortedValues": [0.1, 0.2, 10, 100],
  "columnType": "QUASI",
  "weight": 1,
  "maximumLevel": -1,
  "forLinking": true
}
```
where `sortedValues` is the list (ordered) of possible values. The system will automatically generate a generalization strategy for the values encountered.


While for categorical values, or numerical values of which one wants to have more control on the generalization, should be used:

```
{
  "class": "CategoricalInformation",
  "hierarchy": <HIERARCHY SPEC>,
  "columnType": "QUASI",
  "weight": 1,
  "maximumLevel": -1,
  "forLinking": true
}
```

Where  `hierarchy` is a specification of the hierarchy to use for generalizing the values.
The hierarchy can be specified in three manners.
First of all, it is possible to enumerate the values as paths from top node (root of the hierarchy) to leaf, for each leaf, as in the following examples (for Gender and Marrital Status):

```json
{
  "class": "CategoricalInformation",
  "hierarchy": {
    "terms": [
      [
        "Male",
        "*"
      ],
      [
        "Female",
        "*"
      ]
    ]
  },
  "columnType": "DIRECT_IDENTIFIER",
  "weight": 1,
  "maximumLevel": 4,
  "forLinking": false
}
```

```json
{
  "class": "CategoricalInformation",
  "hierarchy": {
    "terms": [
      ["Single", "Alone", "*"],
      ["Divorced", "Alone", "*"],
      ["Widowed", "Alone", "*"],
      ["Separated", "Alone", "*"],
      ["Never-married", "Alone", "*"],
      ["Never married", "Alone", "*"],
      ["Married", "In marriage", "*"],
      ["Civil Partner", "In marriage", "*"],
      ["Married-AF-spouse", "In marriage", "*"],
      ["Married-civ-spouse", "In marriage", "*"],
      ["Married-spouse-absent", "In marriage", "*"]
    ]
  },
  "columnType": "DIRECT_IDENTIFIER",
  "weight": 1,
  "maximumLevel": 4,
  "forLinking": false
}
```

Secondly, it is possible to specify one of the system provided hierarchy by name as in the following example:

```json
{
  "class": "CategoricalInformation",
  "hierarchy": "MARRITAL_STATUS",
  "columnType": "QUASI",
  "weight": 1,
  "maximumLevel": -1,
  "forLinking": true
}
```


Currently, the hierarchies provided are:

- COUNTRY
- CITY                                                                                                                
- GENDER                                                                                                              
- RACE                                                                                                                 
- MARITAL_STATUS                                                                                                       
- YOB                                                                                                                  
- ICDV9                                                                                                                
- ZIPCODE                                                                                                              
- ZIPCODE_MATERIALIZED                                                                                                 
- HEIGHT
- RELIGION
- DATE-YYYY-MM-DD


Third, it is possible to specify a fully qualified path name of a class implementing the `com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy` interface, as in the following example.

```json
{
  "class": "CategoricalInformation",
  "hierarchy": com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.DateYYYYMMDDHierarchy,
  "columnType": "QUASI",
  "weight": 1,
  "maximumLevel": -1,
  "forLinking": true
}
```

## Information Loss Metrics