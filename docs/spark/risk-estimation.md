## Invoking the Risk Estimation Spark executor

The Spark JAR was built for Java 11 and Spark 3.2.3.
The toolkit is packaged together with its dependencies so there is no requirement for additional configuration of classpaths or internet connection to artifact repositories.

The executor requires the input path as well as the configuration file. It can be invoked as:

```
spark-submit \
	--class com.ibm.research.drl.dpt.spark.risk.RiskEstimation \
	dpt-spark-$VERSION-jar-with-dependencies.jar \
	-i input_path \
	-c risk_estiamtion_configuration.json \
	-o report.json
```

where `risk_estimation_configuration.json` is the configuration file, `input_path` is the input file and `report.json` is the output filename.

The full list of parameters to the executor are:

| Parameter           | Description                                                           |
|---------------------|-----------------------------------------------------------------------|
| -i,--input \<arg\>  | Path to the file or directory to be processed (required)              |
| -o,--output \<arg\> | Output directory (required)                                           |
| -c,--conf \<arg\>   | Path to the configuration file (required)                             |
| --remoteConf        | If set, then the configuration file will be read from HDFS (optional) |
| --remoteOutput      | If set, then the report file will be saved to HDFS (optional)         |
| -basePath \<arg\>   | Specify the base path of the input (optional)                         |

## Writing the configuration file

The configuration file has the following structure:

```json
{
  "metricType": "BINOMIAL",
  "selectionColumn": [
    "c1",
    "c2"
  ],
  "options": {
    "POPULATION": 10000000,
    "USE_GLOBAL_P": false
  }
}
```
where:
* `metricType` specifies the risk metric to be computed, currently the accepted values are:
  * `BINOMIAL`
  * `FK`
* `selectionColumns` specifies the column(s) involved in the risk estimation.
* `options` is a dictionary of metric dependent parameters.

## Output structure

The output consists of a JSON object as the following:

```json
{
  "metricType": "BINOMIAL",
  "risk": 0.01
}
```
where:
* `metricType` reflects the metric type specified in the configuration file
* `risk` is the privacy risk as captured by the risk metric. In general, this value is the maximum risk detected in  the dataset.

## Errors

A runtime exception will be thrown at the following cases:

* misconfiguration in the configuration file (wrong types or values)
* the input file cannot be found
* the user has no permission to read the input file or write to the output directory
* no selection columns have been specified
