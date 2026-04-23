#!/usr/bin/env bash
set -euo pipefail

# library
pushd library
./gradlew build
./gradlew publishToMavenLocal -x test
popd

# docker
pushd docker
./gradlew build
popd

# rest
pushd rest
./gradlew build
popd

# spark
pushd spark
./gradlew build
popd
