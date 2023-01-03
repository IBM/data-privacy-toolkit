## Invoking the uniqueness calculator Spark executor

The executor requires the input path as well as the configuration file.
It can be invoked as:

```
spark-submit \
	--class com.ibm.research.drl.dpt.spark.risk.TransactionUniqueness \
	dpt-spark-$VERSION-jar-with-dependencies.jar \
	-i test.csv \
	-c uniqueness_configuration.json \
	-o report.jsom
```

where `uniqueness_configuration.json` is the configuration file, `test.csv` is the input file and `report.json` is the output filename. 

The full list of parameters to the executor are:

| Parameter         | Description                                                           |
|-------------------|-----------------------------------------------------------------------|
| -i,--input <arg>  | Path to the file to be masked (required)                              |
| -o,--output <arg> | Output filename for the report (required)                             |
| -c,--conf <arg>   | Path to the configuration file (required)                             |
| --remoteConf      | If set, then the configuration file will be read from HDFS (optional) |
| --remoteOutput    | If set, then the report file will be saved to HDFS (optional)         |
| -basePath \<arg\> | Specify the base path of the input (optional)                         |


## Writing the configuration file

The configuration file has the following structure:

```json
{
   "delimiter": ",",
   "quoteChar": "\"",
   "hasHeader": true,
   "trimFields": false,
	
   "inputFormat": "PARQUET",

	"idColumn": ["id"],
	"targetColumns": ["chain", "date"],
	"factor": 1,
	"threshold": 1,
	
	"joinRequired": false,
	"joinInformation" : {
      "rightTable" : "locations.csv",
      "rightTableInputFormat": "CSV",
      "rightTableDatasetOptions" : {
        "delimiter": ",",
        "quoteChar": "\"",
        "hasHeader": true,
        "trimFields": false
      },
      "rightColumn" : "shop_id",
      "leftColumn" : "loc_id"
  }
}
```
* `delimiter`, `quoteChar`, `hasHeader` and `trimFields` are CSV-specific options and control the delimiter, quote character, how to treat the first line and if leading and trailing whitespaces should be trimmed from the values respectively
* `inputFormat` specifies the input format. Valid options are `CSV`, `JSON` and `PARQUET`.
* `idColumn` specifies the column(s) to be used as the primary ID for the dataset. 
* `targetColumns` defines the columns(s) to be used as the value for each record
* `factor` is a placeholder for an upcoming feature and should be set to 1
* `threshold` defines the uniqueness threshold. For example, if set to 5, unique transactions will be considered the ones that appear 5 or less times.
* `joinRequired` is a boolean value that specifies if we need to join the dataset under examination with another dataset before the analysis begins.

If a join is required then the `joinInformation` object needs to be present. This object must contain the following fields:
* `rightTable` which is the location of the table to join with
* `rightTableInputFormat` specifies the input format of the `rightTable`
* `rightColumn` is the column of the `rightTable` to join with
* `leftColumn` is the column of the dataset under examination to join with
* `rightTableDatasetOptions` is an object and includes input format specific options: `delimiter`, `quoteChar`, `hasHeader` and `trimFields` as specified above

The calculation job has also the ability to mask records before calculating the uniqueness metrics. If the user wants to mask the input dataset, then the configuration also needs to include the masking configuration. See masking documentation for more details.

## Output 

The uniqueness job will produce the following output:

```json
{
  "total_ids" : 15281683,
  "total_transactions" : 27145034,
  "unique_ids" : 14974833,
  "unique_transactions" : 25920255
}
```

* `total_ids` and `total_transactions` are the distinct IDs and total transactions found in the dataset. The IDs are calculated based on the `idColumn` input configuration
* `unique_transactions` is the number of transactions that appear equal or fewer times than the `threshold` input configuration value
* `unique_ids` are the distinct IDs that correspond to the unique transactions

## Errors

A runtime exception will be thrown at the following cases:

* misconfiguration in the configuration file (wrong types or values)
* the input file cannot be found
* the user has no permission to read the input file or write to the output directory
