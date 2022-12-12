## DPT Command-line Toolkit

**DPT Command-line Toolkit** is a command line tool exposing the DPT Java library functionality in a more user-friendly manner.
The toolkit is packaged together with its dependencies so there is no requirement for additional configuration of classpath or internet connection to artifact repositories.

A containerised version of the command line tool also exists, which nicely integrates into data pipelines - for further information, please refer to the last section of this page.

The tool requires an input and an output path, along with a configuration file. It can be invoked as follows:

```
java -jar ibm-data-privacy-toolkit-cli.jar \
	-c config.json \
	-i input.csv \
	-o output.csv
```

Where the parameters can be described as follows:

|Parameter|Description|
|---------|-----------|
|-i,--input <arg>|Path to the input file(s). This can be the path to a specific file, or the path to a folder which contains multiple files or subfolders to be processed.|
|-c,--config <arg>|Path to a JSON or YAML configuration file. See below section.
|-o,--output <arg>|Path to the desired output file(s). This can be the path to a specific file, or the path to a folder which will contain multiple files or subfolders.|


## Writing the configuration file

The tool requires a configuration file in order to understand the input format and to control the task behavior.
For the reminded of this documentation, we will present only the JSON version of the configuration files. A direct translation to YAML is also possible.

The skeleton of a configuration for all the available tasks is:

```json
{
  "task":"<TASK NAME>",

  "inputFormat":"<INPUT FORMAT>",
  "inputOptions":{
  // input format specific options
  },
  "taskOptions":{
  // task specific options
  }
}
```

**Explanation**

* *task*: Defines the privacy task that needs to be carried out.

The following values are supported for the tasks field:

|Task enumeration|
|----|
|[Identification](identification.md)|
|[Masking](masking.md)|
|[Vulnerability](vulnerability.md)|
|[Anonymization](anonymization.md)|
|[TransactionUniqueness](transaction_uniqueness.md)|
|[Exploration](exploration.md)|

* *inputFormat*: Defines the input format.

The following data input formats are supported:

|Data input format type enumeration|
|----|
|CSV|
|JSON|
|DICOM|
|XLS|
|XLSX|
|XML|
|HL7|
|FHIR_JSON|
|PARQUET|

* *inputOptions*: Defines options that are specific to the chosen input format.

The following options are supported:

* For CSV inputFormat: `quoteChar`, `delimiter`, `hasHeader`, and `trimFields`

* *taskOptions*: (require) defines options that are specific to the chosen privacy task.

Please refer to the appropriate documentation on the left menu for a description of each specific task options.

## DPT Command-line Toolkit Docker Container

The DPT Command-line toolkit is also available as a Docker container. In order to run it, 3 folders must be prepared beforehand:

* *an input folder*, where the input file(s) must be stored
* *a config folder*, where a config.json file must be stored
* *an output folder*: where dpt will store the output file(s) of the privacy task

The image is available on IBM's internal Artifactory, and can be run as:

```
docker run \
  --mount type=bind,source=/absolute/path/to/input/folder/,target=/input/ \
  --mount type=bind,source=/absolute/path/to/config/folder/,target=/config/ \
  --mount type=bind,source=/absolute/path/to/output/folder/,target=/output/ \
  res-drl-docker-local.artifactory.swg-devops.com/dpt/base-cli:latest
```

Where the three paths `/absolute/path/to/input/folder`, `/absolute/path/to/config/folder`, and `/absolute/path/to/output/folder` must be specified accordingly.

Upon completion, the output file(s) of the privacy task will be available in the provided output folder.


