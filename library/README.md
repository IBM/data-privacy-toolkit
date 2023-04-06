# Data Privacy Toolkit - Library

This project contains the Java Library at the core of the Data Privacy Toolkit (DPT).
Refer to the [documentation](../docs/README.md) for information about the offered capabilities.

## Requirements

The DPT library is a Java project, and it is currently being tested against the following releases:
* AdoptOpenJDK Hotspot 11
* AdoptOpenJDK Hotspot 17
* Microsoft Build of OpenJDK 11
* Microsoft Build of OpenJDK 17
* Amazon Corretto Build of OpenJDK 11
* Amazon Corretto Build of OpenJDK 17

and it currently builds using gradle version 8.0.2, offering gradle wrapper as a convenience tool.

## Building the project

The library can be tested by running:

```bash
./gradlew build
```

This gradle task will compile the project, execute all the tests specified in the `/src/test/java` subfolder, and create
the final jar file in the `/build/libs` subfolder.

## Dependency declaration

DPT library is publicly available in MavenCentral.
Therefore, the library can be used in any Java/Scala/Kotlin project by adding it as a traditional dependency.
Examples are in the following.

### Gradle:
```kotlin
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
    <type>jar</type>
</dependency>
```

where `dpt_version` is a variable specifying the latest version of the library, currently `6.0.0`.