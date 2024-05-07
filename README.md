jpplib
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jpplib/com.io7m.jpplib.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jpplib%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.jpplib/com.io7m.jpplib?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/jpplib/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/jpplib.svg?style=flat-square)](https://codecov.io/gh/io7m-com/jpplib)
![Java Version](https://img.shields.io/badge/8-java?label=java&color=007fff)

![com.io7m.jpplib](./src/site/resources/jpplib.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpplib/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/jpplib/actions?query=workflow%3Amain.windows.temurin.lts)|

## Building

```
$ mvn clean package
```

Note that although the project is Java 8 compatible at runtime (and the compiled
artifacts on Maven Central are Java 8 bytecode), we do not support _building_ the
project on anything less than JDK 21.

