# Data Privacy Toolkit - CLI

This project contains the command line interface (CLI) and the corresponding Docker image of the Data Privacy Toolkit (DPT).
Refer to the [documentation](../docs/toolkit/README.md) for information about the offered capabilities.

## Requirements

The DPT CLI requires **Java 21** and is tested against the following distributions on every build:

* Eclipse Temurin 21
* Microsoft Build of OpenJDK 21
* Amazon Corretto 21

It builds using Gradle 8.14.3, with a Gradle wrapper included for convenience.

## Building the project as a self-contained jar

Note that the following instructions must be executed from within the `/docker` subfolder of the repository.

Build and test the project:

```bash
./gradlew build
```

This task compiles the project, executes all tests under `/src/test/java`, and produces the final jar in `/build/libs`.

To create the uberjar (a single jar bundled with all dependencies):

```bash
./gradlew shadowJar
```

After the uberjar is created, run it with:

```bash
java -jar build/libs/data-privacy-toolkit-cli-${VERSION}-all.jar
```

where `VERSION` is the current version of the project, currently `6.0.0-SNAPSHOT`.
Refer to the `version` value in `build.gradle` for an up-to-date reference.

### Note
The uberjar does not bundle NLP models that may be required by the free text processing capabilities of the toolkit.
These models are generally released under specific licences and must be independently added to the jar, or made available to the JVM via the classpath.

## Building the project as a Docker image

After building the uberjar, create the Docker image:

```bash
docker build -t data-privacy-toolkit:local .
```

Run the image:

```bash
docker run --rm -it data-privacy-toolkit:local
```

## Pulling the image from Quay.io

Docker images are automatically published to [quay.io/data_privacy_toolkit](https://quay.io/data_privacy_toolkit) by the CI/CD pipeline on every merge to `main`.
Two tags are pushed: one matching the Git commit SHA and one as `latest`.

Pull the latest image:

```bash
docker pull quay.io/data_privacy_toolkit/cli
```

Run it:

```bash
docker run --rm -it quay.io/data_privacy_toolkit/cli
```

### Execution details

The DPT Docker image relies on volume mounts for its inputs and outputs (see the [Dockerfile](Dockerfile)):

| Mount target   | Purpose                                                                      |
|----------------|------------------------------------------------------------------------------|
| `/input`       | Folder containing the input dataset(s)                                       |
| `/output`      | Folder where results will be written (do not share with `/input`)            |
| `/config`      | Folder containing a `config.json` configuration file                        |
| `/consistency` | *(Optional)* Folder for persisting consistency/persistency state across runs |

Example command using all options, assuming `input`, `output`, `config`, and `consistency` directories exist in the working directory:

```bash
docker run --rm -it \
  --mount type=bind,source=$PWD/input,target=/input \
  --mount type=bind,source=$PWD/output,target=/output \
  --mount type=bind,source=$PWD/config,target=/config \
  --mount type=bind,source=$PWD/consistency,target=/consistency \
  quay.io/data_privacy_toolkit/cli
```

Please refer to the [end-to-end tests](scripts/e2e_tests) and the [documentation](../docs/toolkit) for configuration examples covering the various tasks.
