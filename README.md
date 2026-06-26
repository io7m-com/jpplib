jpplib
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jpplib/com.io7m.jpplib.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jpplib%22)
[![Maven Central (snapshot)](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fcom%2Fio7m%2Fjpplib%2Fcom.io7m.jpplib%2Fmaven-metadata.xml&style=flat-square)](https://central.sonatype.com/repository/maven-snapshots/com/io7m/jpplib/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/jpplib.svg?style=flat-square)](https://codecov.io/gh/io7m-com/jpplib)
![Java Version](https://img.shields.io/badge/8-java?label=java&color=5ce6e6)

![com.io7m.jpplib](./src/site/resources/jpplib.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.windows.temurin.lts)|

## Repository Relocation

Development of this project has moved to an
[open-source but not open-contribution](https://sqlite.org/copyright.html#notopencontrib)
model.

Source code and commits will remain publicly available perpetually, but issues
and/or pull requests will be rejected and/or ignored. Additionally, this project
will now only be available via a read-only mirror at:

  https://codeberg.org/io7m-com/jpplib


## Building

```
$ mvn clean package
```

Note that although the project is Java 8 compatible at runtime (and the compiled
artifacts on Maven Central are Java 8 bytecode), we do not support _building_ the
project on anything less than JDK 21.

