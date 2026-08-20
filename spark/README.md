# Data Privacy Toolkit - Spark

This project contains Apache Spark wrappers around the core DPT Java library, enabling privacy tasks to be executed at scale in a Spark environment.
Refer to the [documentation](../docs/spark/README.md) for information about the available wrappers.

## Requirements

* **Java 21** (Eclipse Temurin, Microsoft OpenJDK, or Amazon Corretto)
* **Apache Spark 3.5.x** (Scala 2.13)
* Gradle 8.14.3, with a Gradle wrapper included for convenience

## Building the project

First, publish the core library to your local Maven cache:

```bash
cd ../library
./gradlew publishToMavenLocal
```

Then build the Spark module:

```bash
cd ../spark
./gradlew build
```

This task compiles the project, runs all tests under `/src/test/java`, and produces the jar in `/build/libs`.

## Available wrappers

| Functionality                                       |
|-----------------------------------------------------|
| [Identification](../docs/spark/identification.md)   |
| [Masking](../docs/spark/masking.md)                 |
| [Vulnerability](../docs/spark/vulnerability.md)     |
| [Risk estimation](../docs/spark/risk-estimation.md) |
| [Anonymization](../docs/spark/anonymization.md)     |
| [Transaction Uniqueness](../docs/spark/transaction-uniqueness.md) |

## Notes

The Spark wrappers are built against Spark 3.5.x with Scala 2.13.
The jar bundles all DPT dependencies but not Spark itself, which is expected to be provided by the cluster environment.
