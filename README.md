# Workshop Registration System

A Maven multi-module Java project (EX2 — Maven Evolution).

## One-time setup: installing the API jars

The `model`, `logic`, and `workshop-tester` jars (version 2.0) are not published on
Maven Central — they are given as plain jars in `libs/`. Before building this project
on a new machine, install them into your local Maven repository (the `.m2` folder)
once, by running these three commands from the repository root.

The commands below work unchanged in both **CMD** and **PowerShell** (every `-D`
argument is quoted):

```
mvn install:install-file "-Dfile=libs/model-2.0.jar" "-DgroupId=patmal.workshops" "-DartifactId=model" "-Dversion=2.0" "-Dpackaging=jar"
mvn install:install-file "-Dfile=libs/logic-2.0.jar" "-DgroupId=patmal.workshops" "-DartifactId=logic" "-Dversion=2.0" "-Dpackaging=jar"
mvn install:install-file "-Dfile=libs/workshop-tester-2.0.jar" "-DgroupId=patmal.workshops" "-DartifactId=workshop-tester" "-Dversion=2.0" "-Dpackaging=jar"
```

After that, a normal build works from the repository root:

```
mvn clean install
```

## Building and running

`mvn clean install` builds every module and produces the runnable uber jar
`app/target/workshop-cli.jar`, which can be run with:

```
java -jar app/target/workshop-cli.jar
```
