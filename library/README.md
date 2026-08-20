# Data Privacy Toolkit - Library

This project contains the Java Library at the core of the Data Privacy Toolkit (DPT).
Refer to the [documentation](../docs/README.md) for information about the offered capabilities.

## Requirements

The DPT library requires **Java 21** and is tested against the following distributions on every build:

* Eclipse Temurin 21
* Microsoft Build of OpenJDK 21
* Amazon Corretto 21

It builds using Gradle 8.14.3, with a Gradle wrapper included for convenience.

## Building the project

The library can be built and tested by running:

```bash
./gradlew build
```

This task compiles the project, executes all tests under `/src/test/java`, and produces the final jar in `/build/libs`.

## Dependency declaration

The DPT library is publicly available on Maven Central.

Add it to any Java/Scala/Kotlin project as a standard dependency:

### Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation("com.ibm.research.drl.dpt:data-privacy-toolkit:${dpt_version}")
}
```

### Gradle (Groovy DSL):
```groovy
dependencies {
    implementation "com.ibm.research.drl.dpt:data-privacy-toolkit:${dpt_version}"
}
```

### Maven:
```xml
<dependency>
    <groupId>com.ibm.research.drl.dpt</groupId>
    <artifactId>data-privacy-toolkit</artifactId>
    <version>${dpt_version}</version>
</dependency>
```

where `dpt_version` is the version of the library to use, currently `6.0.0-SNAPSHOT`.
