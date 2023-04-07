# Data Privacy Toolkit - CLI

This project contains the command line interface (CLI), and the corresponding Docker image, of the Data Privacy Toolkit (DPT).
Refer to the [documentation](../docs/toolkit/README.md) for information about the offered capabilities.

## Requirements

The DPT library is a Java project, and it is currently being tested against the following releases:
* AdoptOpenJDK Hotspot 11
* AdoptOpenJDK Hotspot 17
* Microsoft Build of OpenJDK 11
* Microsoft Build of OpenJDK 17
* Amazon Corretto Build of OpenJDK 11
* Amazon Corretto Build of OpenJDK 17

and it currently builds using gradle version 8.0.2, offering gradle wrapper as a convenience tool.

## Building the project as self-contained jar

Note that the following instructions need to be executed within the `/docker` subfolder of the repository.

The CLI can be tested by running:

```bash
./gradlew build
```

This gradle task will compile the project, execute all the tests specified in the `/src/test/java` subfolder, and create
the final jar file in the `/build/libs` subfolder.

Running the following command gradle will create the uberjar, i.e. a jar file that also contains all the dependencies required to run DPT as an application.
```bash
./gradlew shadowJar
```

After having created the uberjar, it can be executed as follows:

```bash
java -jar build/libs/data-privacy-toolkit-cli-${VERSION}-all.jar
```

where `VERSION` is the current version of the project. At the moment of writing this value is set to `6.0.0-SNAPSHOT`.
Refer to the `version` value in `build.gradle` for more updated reference.

### Note
The process of creating the jar with the dependencies does not pack models that might be required by the free text processing capabilities of the toolkit.
These models are generally released under specific licences and must therefore be independently added to the jar file, or made available to the java VM via classpath.

## Building the project as docker image

After having built the uberjar, it is possible to create a docker image with the following steps:

```bash
docker build -t data-privacy-toolkit:local .
```

where `data-privacy-toolkit` is a name assigned to the final image.
Note that the tag is set to `local` as a convention.
Any tag can be set, as long as the name does not overwrite required existing images.

After that, the DPT docker image can be executed as follows:

```bash
docker run --rm -it data-privacy-toolkit:local
```

## Pulling image from Quay.io

New versions of the DPT docker image are automatically deployed to the image registry [`https://quay.io`](https://quay.io) by the CI/CD pipeline.
Namely, every PR to the `main` branch will cause a new docker image to tbe available in the registry.
More precisely, the deployment steps of the CI/CD pipeline (see corresponding [workflow](../.github/workflows/deploy-toolkit.yml)) will deploy two tags for the image.
First, it will push an image having tag set to the current GIT commit hash, and it will then tag the same image as the new `latest`.

The following command allows the retrieval of the image currently labeled as `latest`.

```bash
docker pull quay.io/data_privacy_toolkit/cli
```

To run such image, one simply needs to execute the following command:

```bash
docker run --rm -it quay.io/data_privacy_toolkit/cli
```

### Execution details for the docker image

The DPT docker image expects certain information to be provided through volume mounting, see the specifications in the [Dockerfile](Dockerfile).
Namely, the image expects the following:
* The input dataset to be available within a folder mounted as `/input`;
* The result of the task execution will be made available in folder mounted as `/output`, note that it is generally not recommended to mount `/input` and `/output` on the same folder;
* The configuration should to be written in a file named `config.json` and it should be made available within a folder mounted as `/config`;
* Optionally, the file structures created by the persistency/consistency features of DPT can be saved by mounting a folder as `/consistency` 

Thus, the following is docker run command using all options, and assuming the folder local folder `input`, `output`, `config`, and `consistency` are available.

```bash
docker run --rm -it \
  --mount type=bind,source=$PWD/input,target=/input \
  --mount type=bind,source=$PWD/output,target=/output \
  --mount type=bind,source=$PWD/config,target=/config \
  --mount type=bind,source=$PWD/consistency,target=/consistency  quay.io/data_privacy_toolkit/cli
```
Please refer to the existing [end-to-end tests](scripts/e2e_tests) and the [documentation](../docs/toolkit) for examples of configuration for the various tasks.
q