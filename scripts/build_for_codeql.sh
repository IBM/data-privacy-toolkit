#!/usr/bin/env bash
#
# Copyright IBM Corp. 2022
#
set -x

if [ -d library ]; then
  pushd library
  ./gradlew build
  ./gradlew publishToMavenLocal
  popd
fi

if [ -d docker ]; then
  pushd docker
  ./gradlew build
fi

if [ -d spark ]; then
  pushd spark
  ./gradlew build
fi
