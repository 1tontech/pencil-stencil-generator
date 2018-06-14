# Stencil collection generator for Pencil

Allows you to generate stencil collection (definition) files for [Pencil](https://github.com/evolus/pencil). This utility is written by keeping in mind the fact that the same utility can be modified to add support for generating stencils for multiple types of icon sets

Currently supports [Font Awesome 5](https://fontawesome.com) both free version & pro version

## How to Build

* Make sure you have `java` installed on your system
* Run `./gradlew clean build` from your terminal on Unix based machines (or) run `gradlew.bat clean build` from command prompt in windows based machines

## How to generate stencil zip

### Font awesome free icons

* Run `java -jar build/libs/pencil-stencil-generator-1.0.0-SNAPSHOT-all.jar` from command line/terminal

### Font awesome pro icons

* Download font awesome premium icons from https://fontawesome.com/account/downloads
* Run `java -jar build/libs/pencil-stencil-generator-1.0.0-SNAPSHOT-all.jar --input <path to fontawesome-pro-x.y.z.zip>`  from command line/terminal

## How to contribute new set

* If you want to add support for a new Icon source, add a new entry to `IconSetType`
* Implement interface `DefinitionGenerator`
* Add an additional case statement in `StencilDefinitionGenerator.fetchParseAndWriteMetadata`
