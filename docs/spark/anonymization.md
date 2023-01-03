## Invoking the anonymization Spark executor

The anonymization executor requires the input and output paths as well as the configuration file.
It can be invoked as:

```
spark-submit \
	--class com.ibm.research.drl.dpt.spark.anonymization.AnonymizationExecutor \
	dpt-spark-$VERSION-jar-with-dependencies.jar \
	-c anon_conf.json -a OLA -i test.csv -o anonymized_output -F TEXTFILE
```

where `anon_conf.json` is the configuration file, `test.csv` is the input file and `anonymized_output` is the output directory. 

The full list of parameters to the executor are:

| Parameter            | Description                                                           |
|----------------------|-----------------------------------------------------------------------|
| -i,--input <arg>     | Path to the file to be masked (required)                              |
| -o,--output <arg>    | Output directory (required)                                           |
| -c,--conf <arg>      | Path to the configuration file (required)                             |
| -a,--algorithm <arg> | Algorithm name, currently the available option is OLA                 |
| --remoteConf         | If set, then the configuration file will be read from HDFS (optional) |

The `--remoteConf` controls if the configuration file should be read from HDFS instead of the local filesystem.

## Invoking the Spark RDD API

Users can invoke the Spark API as:

```java

import com.ibm.research.drl.dpt.spark.anonymization.ola.OLASpark;
import org.apache.spark.rdd.RDD;

public RDD<String> anonymizeRDD(RDD<String> input, InputStream configurationFileStream) {
	return OLASpark.run(configurationFileStream, input);
}
```

where `configurationFileStream ` is the input stream to the configuration file and `input` is the RDD containing the data to mask. 

## Writing the configuration file


The anonymization configuration file is a JSON file. 

The structure of the file is as follows:

```json
{
  "hierarchies" : {
    "gender" : [
      ["M", "Person"],
      ["F", "Person"]
    ],

    "date" : "DATE",

    "age" : {
    	"className": "com.ibm.research.hierarchies.custom.age",
    	"options": {}
    }

  },

  "columnInformation": [
    {"type": "QUASI", "isCategorical": true, "hierarchy": "date", "weight": 1.0, "maximumLevel": -1},
    {"type": "QUASI", "isCategorical": true, "hierarchy": "gender", "isForLinking": true},
    {"type": "QUASI", "isCategorical": true, "hierarchy": "age", "isForLinking": true}
  ],

  "privacyConstraints": [
    {"name": "k", "k": 2}
  ],

  "options" : {
    "suppression": 20.0
  },

  "delimiter": ",",
  "hasHeader": false,
  "quoteChar": "\"",
  "trimFields": false

}
```


* The `delimiter` specifies the delimiter character for the input file
* The `hasHeader` is a boolean flag that instructs if the first line of the input file must be treated as a header or not
* The `quoteChar` specifies the quote character for the input file
* The `options` section is optional and contains algorithm-specific options. It can contain a `suppression` key which specifies the maximum suppression rate allowed in the anonymization process. If no suppression rate is specified, its default value is 0.


### The `columnInformation` section

The `columnInformation` section contains information for each column. It is an array and each element of the array is an object. Each object needs to have the key `type` that declares if the column is a quasi-identifier (value is `QUASI`), sensitive (value is `SENSITIVE`) or if we don't handle this column (value is `NORMAL`). 

The columns with type `QUASI` will be anonymized by the anonymization algorithm. For the `QUASI` entries, we need to specify the hierarchy to which it belongs. The hierarchy value must match one of the keys in the 
`hierarchies` section. See the next section for details on how to specify hierarchies. 
We also need to specify if the attribute is categorical or not throught the `isCategorical` boolean field.

For each `QUASI` column, we can optionally specify the `weight` and `maximumLevel`. The `weight` is used in the calculation of the information loss metric and must be a positive number. Its default value is 1. The `maximumLevel` specifies the maximum level in the hierarchy to which we should anonymize. The anonymization algorithm will not anonymize that column above this level. By default, the `maximumLevel` is set to -1, which means that the algorithm is allowed to anonymize that column up to its maximum hierarchy level. 

For the columns of type `NORMAL` or `SENSITIVE` no other fields are required. 

Each column that needs to be part of the risk calculation needs to have a boolean field with the name `isForLinking` in its column information attributes and the value set to true. 

### The `hierarchies` section

Each key in the `hierarchies` section specifies a hierarchy. 

The anonymization configuration file is a JSON file.

The structure of the file is as follows:

```json
{
  "hierarchies" : {
    "gender" : [
      ["M", "Person"],
      ["F", "Person"]
    ],

    "date" : "DATE",

    "age" : {
    	"className": "com.ibm.research.hierarchies.custom.age",
    	"options": {}
    }

  },

  "columnInformation": [
    {"type": "QUASI", "isCategorical": true, "hierarchy": "date", "weight": 1.0, "maximumLevel": -1},
    {"type": "QUASI", "isCategorical": true, "hierarchy": "gender", "isForLinking": true},
    {"type": "QUASI", "isCategorical": true, "hierarchy": "age", "isForLinking": true}
  ],

  "privacyConstraints": [
    {"name": "k", "k": 2}
  ],

  "options" : {
    "suppression": 20.0
  },

  "informationLossMetric": "CP",

  "delimiter": ",",
  "hasHeader": false,
  "quoteChar": "\"",

  "estimateUniqueness": true,
  
  "riskMetric": "BINOM",
  "riskMetricOptions": {
    "N": 123123123,
    "useGlobalP": false
  }
}
```


* The `delimiter` specifies the delimiter character for the input file
* The `hasHeader` is a boolean flag that instructs if the first line of the input file must be treated as a header or not
* The `quoteChar` specifies the quote character for the input file
* The `informationLossMetric` specifies the short name of the metric to be used for reporting the information loss after the anonymization process. Its default value is `CP`. The table below contains the possible values for the information loss metric with their description

| Short name | Description                              | Lower bound                       | Upper bound                       |
|------------|------------------------------------------|-----------------------------------|-----------------------------------|
| CP         | Categorical Precision                    | 0                                 | 1                                 |
| AECS       | Average Equivalence Class Size           | Depends on the anonymized dataset | Depends on the anonymized dataset |
| DM         | Discernibility                           | Depends on the original dataset   | Depends on the original dataset   |
| DMSTAR     | Discernibility (monotonic version)       | Depends on the original dataset   | Depends on the original dataset   |
| GLM        | Generalized Loss Metric                  | 0                                 | Depends on the original dataset   |
| NUE        | Non-Uniform Entropy                      | 0                                 | Depends on the original dataset   |
| NP         | Numerical precision (only for numerical) | 0                                 | 1                                 |

* The `options` section is optional and contains algorithm-specific options. It can contain a `suppression` key which specifies the maximum suppression rate allowed in the anonymization process. If no suppression rate is specified, its default value is 0.
* The `riskMetric` specifies the short name of the metric to be used for reporting the risk after the anonymization process. It has no default value and it will cause an error if missing. The table below contains the possible values for the `riskMetric`:

| Short name | Description                                                  |
|------------|--------------------------------------------------------------|
| ZAYATZ     | Zayatz estimator of uniqueness                               |
| KRM        | K-Ratio risk metric                                          |
| FKRM       | K-Ratio metric with probabilistic superpopulation estimation |
| HGEORM     | Hypergeometric-based superpopulation risk model              |
| BINOM      | Binomial-based superpopulation risk model                    |

* The `riskMetricOptions` field specifies the options of the risk metric defined in the `riskMetric` field. Its value is a JSON object. The fields of such an object depend on the risk metric required. The following table specifies the options required by each metric:

| Metric name | Option     | Description                                                                                             |
|-------------|------------|---------------------------------------------------------------------------------------------------------|
| ZAYATZ      | N          | Population size, must be an integer greater than 0                                                      |
| KRM         | gamma      | Ratio of the dataset with respect to the original population. The value must be a real number in (0, 1] |
| FKRM        | population | Population size, must be an integer greater than 0                                                      |
| HGEORM      | N          | Population size, must be an integer greater than 0                                                      |
| BINOM       | N          | Population size, must be an integer greater than 0                                                      |
|             | useGlobalP | Boolean value (true, false) to specify if using a global (true) or local (false) population model       |

* The `estimateUniqueness` field specifies if the output should include the estimation results for uniques. The estimated real uniques are calculated based on the columns that are marked for linking. The `N` option must be set to the `riskMetricOptions` for the estimator to work.

### The `columnInformation` section

The `columnInformation` section contains information for each column. It is an array and each element of the array is an object. Each object needs to have the key `type` that declares if the column is a quasi-identifier (value is `QUASI`), sensitive (value is `SENSITIVE`) or if we don't handle this column (value is `NORMAL`).

The columns with type `QUASI` will be anonymized by the anonymization algorithm. For the `QUASI` entries, we need to specify the hierarchy to which it belongs. The hierarchy value must match one of the keys in the
`hierarchies` section. See the next section for details on how to specify hierarchies.
We also need to specify if the attribute is categorical or not throught the `isCategorical` boolean field.

For each `QUASI` column, we can optionally specify the `weight` and `maximumLevel`. The `weight` is used in the calculation of the information loss metric and must be a positive number. Its default value is 1. The `maximumLevel` specifies the maximum level in the hierarchy to which we should anonymize. The anonymization algorithm will not anonymize that column above this level. By default, the `maximumLevel` is set to -1, which means that the algorithm is allowed to anonymize that column up to its maximum hierarchy level.

For the columns of type `NORMAL` or `SENSITIVE` no other fields are required.

Each column that needs to be part of the risk calculation needs to have a boolean field with the name `isForLinking` in its column information attributes and the value set to true.

### The `hierarchies` section

This section contains information about the generalization hierarchies that will be used during the
anonymization process. It is a JSON object and its keys are used as references in the `columnInformation` section.

Each key in the `hierarchies` section specifies a hierarchy. We have three ways to specify a hierarchy:

* Materialized hierarchy. In that case, the value of the key is a JSON array of arrays. Each individual array consists of string elements. An example of a materialized hierarchy is as follows:

```json
[
    ["M", "Person"],
    ["F", "Person"]    
]
```

In the configuration example above, the `gender` hierarchy is a materialized hierarchy.

* Predefined hierarchy. The toolkit comes with a set of predefined materialized hierarchies. In this case, the value of the key is a string that defines the name of the predefined hierarchy.

In the configuration example above, the `date` hierarchy is a predefined materialized hierarchy that points to the `DATE`.

The following table contains a list of the available hierarchies along with the provider type that needs to be used as a parameter and the description of the generalization levels:

| Hierarchy      | Key name       | Generalization levels                                                         |
|----------------|----------------|-------------------------------------------------------------------------------|
| City           | CITY           | City, country, continent, \*                                                  |
| Country        | COUNTRY        | Country, continent, \*                                                        |
| Gender         | GENDER         | Gender, \*                                                                    |
| Race           | RACE           | Race, \*                                                                      |
| Marital status | MARITAL_STATUS | Marital status, category (alone or in-marriage), \*                           |
| ZIP code       | ZIPCODE        | 5-digit value, 4-digit value, 3-digit value, 2-digit value, 1-digit value, \* |

* Custom loaded hierarchy. The user is able to implement their own hierarchies and load them in the toolkit. In this case, the value of the key is an object that contains:
    1.	The `className` which defines the class name to be loaded. It must be a fully qualified class name
    2. An `options` field that contains the options to be passed to the initialization of the hierarchy. The options are passed as `JsonNode` instances to the constructor of the hierarchy

In the configuration example above, the `age` hierarchy is a custom loaded hiearchy. The class
`com.ibm.research.hierarchies.custom.age` is loaded and empty options are passed to its constructor.


### The `privacyConstraints` sections

The anonymization algorithm requires a list of privacy constraints to operate. This list must include the k-anonymity constraint and then optionally the l-diversity constraint. The `privacyConstraints` section is a JSON array containing objects, each object modelling a privacy constraint. Each privacy constraint has a field `name` and then a set of constraint-specific options. The valid constraint names are:

* `k` for k-anonymity
* `distinctL` for distinct L-diversity

The k-anonymity constraint requires an option with the name `k` that contains the k-value as an integer.
An example of a k-anonymity object is the following:

```
{"name": "k", "k": 2}
```
The l-diversity constraint requires an option with the name `l` that contains the l-value as an integer.
An example of a l-diversity object is the following:

```
{"name": "distinctL", "l": 2}
```

* `entropyL` for entropy-based L-diversity. Also requires an `l` parameter:

```
{"name": "entropyL", "l": 2}
```

* `recursiveCL` for recursive CL-diversity. Requires two parameters, `c` and `l`:

```
{"name": "recursiveCL", "l": 2, "c": 1}
```

* `tCloseness` for t-closeness

The t-closeness constraint requires an option with the name `t`. An example is the following:

```
{"name": "tCloseness", "t": 0.2}
```

### Report output

At the end of the anonymization process, a report will be printed in the standard output.
A sample output follows:

```
{
    "enforcedSuppressionRate": 0.0,
    "estimateUniqueness": {
        "estimatedRealUniques": 0.0,
        "uniques": 0.0
    },
    "generalizationLevel": "0:0:0",
    "globalInformationLoss": {
        "lowerBound": 0.0,
        "name": "Categorical Precision",
        "upperBound": 1.0,
        "value": 0.0
    },
    "perColumnInformationLoss": [
        {
            "lowerBound": 0.0,
            "name": "Categorical Precision",
            "upperBound": 1.0,
            "value": 0.0
        },
        {
            "lowerBound": 0.0,
            "name": "Categorical Precision",
            "upperBound": 1.0,
            "value": 0.0
        },
        {
            "lowerBound": 0.0,
            "name": "Categorical Precision",
            "upperBound": 1.0,
            "value": 0.0
        }
    ],
    "risk": 0.17166997233063938
}
```

* The `generalizationLevel` will describe the number of performed generalization steps for each column
* The `enforcedSuppressionRate` contains the suppression rate actually performed by the anonymization algorithm. It is a percentage of records and its value ranges from 0 too 100
* The `globalInformationLoss` reports the information loss for the entire dataset. The information loss report includes the lower and upper bound values of the metric, named `lowerBound` and `upperBound` respectively, the name of the metric used and the report value.
* The `perColumnInformationLoss` reports the information loss for each quasi column. The reporting order follows the order of columns declared as QUASI. The structure of the information loss report for each column is the same as the one reported in the `globalInformationLoss` field
* The `risk` attribute contains the calculated risk value
* The `estimateUniqueness` attribute contains the uniqueness estimation results. The results include the uniques found in the data (field name is `uniques`) and the estimated real uniques, under the field `estimatedRealUniques`. Only the columns with the `isForLinking` set to true are considered for uniqueness estimation. If the input configuration has the `estimateUniqueness` attribute set to `false` then this attribute will be set to `null`.

### The `privacyConstraints` sections

The anonymization algorithm requires a list of privacy constraints to operate. This list must include the k-anonymity constraint and then optionally the l-diversity constraint. The `privacyConstraints` section is a JSON array containing objects, each object modelling a privacy constraint. Each privacy constraint has a field `name` and then a set of constraint-specific options. The valid constraint names are:

* `k` for k-anonymity
* `distinctL` for distinct L-diversity

The k-anonymity constraint requires an option with the name `k` that contains the k-value as an integer. 
An example of a k-anonymity object is the following:

```
{"name": "k", "k": 2}
```
The l-diversity constraint requires an option with the name `l` that contains the l-value as an integer. 
An example of a l-diversity object is the following:

```
{"name": "distinctL", "l": 2}
```

### Report output

At the end of the anonymization process, a report will be printed in the standard output. 
A sample output follows:

```
{
    "enforcedSuppressionRate": 0.0,
    "generalizationLevel": "0:0:0"
}
```

* The `generalizationLevel` will describe the number of performed generalization steps for each column
* The `enforcedSuppressionRate` contains the suppression rate actually performed by the anonymization algorithm. It is a percentage of records and its value ranges from 0 too 100


### Developer guide for implementing a generalization hierarchy

A class that wants to implement a generalization hierarchy needs to implement the `com.ibm.research.drl.dpt.algorithms.anonymization.hierarchies.GeneralizationHierarchy` interface. 

The interface contains the following methods:

```
int getHeight();
int getTotalLeaves();
int leavesForNode(String value);
Set<String> getNodeLeaves(String value);
int getNodeLevel(String value);
String getTopTerm();
String encode(String value, int level, boolean randomizeOnFail);
```

The `getHeight` function returns the height of the hierarchy.

The `getTotalLeaves` returns the number of leaves of the hierarchy.

The `leavesForNode` returns the number of leaves of a specific node in the hierarchy. The `getNodeLeaves` functions returns a set of these leaves.

The `getNodeLevel` returns the level of a value in the hierarchy.

The `getTopTerm` returns the value of the top term in the hierarchy.

The `encode` function is responsible for encoding a value to a generalization level. 

Let's assume that we want to implement a hierarchy for 5-digit ZIP codes. In each level of the hierarchy, a digit of the ZIP code will be replaced by an asterisk. For example, the ZIP code 12345 will become 1234* at level 1, 123** at level 2 and so on and finally at level 5 it will be *****. 

This hierachy has 6 levels:

```
int getHeight() { 
	return 6;
}
```

The total leaves of the hierarchy are 10000 (all 5-digit ZIP codes):

```
int getTotalLeaves() {
	return 10000;
}
```

The top term for the hierarchy is `*****`:

```
String getTopTerm() {
	return "*****";
}
```

For an input value, its level in the hierarchy is the number of asterisks it has:

```
 public int getNodeLevel(String value) {
        int level = 0;

        for(int i = (value.length() - 1); i >= 0; i++) {
            if (value.charAt(i) == '*') {
                level++;
            }
        }

        return level;
}
```


If we have as input value "1234*", then we know that it has 10 leaves

```
int leavesForNode(String value) {
	int level = getNodeLevel(value);
   	return (int)Math.pow(10, level);
}
```

The `encode` function replaces the digits of the ZIP code based on the level specified (one level means the last digit replaces, two levels means the last two digits replaced and so on).

```
 public String encode(String value, int level, boolean randomizeOnFail) {
        if (level <= 0) {
            return value;
        }

        if (level >= getHeight()) {
            return getTopTerm();
        }

        if (value.length() != 5) {
            return getTopTerm();
        }

        String prefix = value.substring(0, value.length() - level);

        for(int i = 0; i < level; i++) {
            prefix += "*";
        }

        return prefix;
    }
```

## Errors

A runtime exception will be thrown at the following cases:

* misconfiguration in the masking configuration file (wrong types or values)
* the input file cannot be found
* the user has no permission to read the input file or write to the output directory
* a suitable generalization level cannot be found



