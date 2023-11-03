# Astra Software Development Kit

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.datastax.astra/astra-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.datastax.astra/astra-sdk/)

## Overview

This SDK *(Software Development Kit)* makes it easy to call Stargate and/or Astra services using idiomatic Java APIs. 

- **The Stargate SDK** works with both Stargate standalone installations and Stargate deployed in Astra. With standalone Stargate deployments you will initialize the framework with the class `StargateClient` and provide a list of nodes (IP). To start locally please follow [Stargate SDK quickstart](https://github.com/datastax/astra-sdk-java/wiki/Stargate-SDK-Quickstart) guide. The nodes will run in Docker.

- **The Astra SDK** reuses the previous library and setup the connection to work with AstraDB cloud-based service. You work with the class `AstraClient` (that configure `StargateClient` for you). As you can see on the figure below the `AstraClient` handles not only Stargate Apis but also Astra Devops Api and Apache Pulsar. To get started follow the [Astra SDK quickstart](https://github.com/datastax/astra-sdk-java/wiki/Astra-SDK-Quickstart) guide.

- **The Astra Spring Boot Starter**: Imported in a Spring Boot application, it configures both `Astra SDK` and `Spring Data Cassandra` to work with AstraDB. Configuration is read in `application.yaml`. The starter will initialize any beans you would need (`AstraClient`, `CqlSession`, `StargateClient`. To get started follow the [Astra Spring Boot Starter QuickStart](https://github.com/datastax/astra-sdk-java/wiki/Spring-Boot-Starter-Quickstart) guide.

## What's NEXT ?

1. [QuickStart for Stargate](https://github.com/datastax/astra-sdk-java/wiki/Stargate-SDK-Quickstart)
2. [QuickStart for Astra](https://github.com/datastax/astra-sdk-java/wiki/Astra-SDK-Quickstart)
3. [QuickStart for Astra Spring Boot Starter](https://github.com/datastax/astra-sdk-java/wiki/Spring-Boot-Starter-Quickstart)

## Release Workflow

### Prerequisites

- [x] Start the `ssh-agent` 

```console
eval "$(ssh-agent -s)"
```
- [x] Add the ssh key to the agent

```console
cd ~/.ssh
ssh-add githubff4j
```

- [x] cleanup sources

```console
find . -type f -name *.DS_Store -ls -delete
git pull
git add -A
git commit -m "delivery"
git push
```

### Release

- [x] Run release 
```
mvn release:prepare release:perform
```

- Go to the [taglist](https://github.com/datastax/astra-sdk-java/tags) on github then create the release

- Create a release note document
```
`Fixes:`
 + XXX (#000)
`Evolutions`
 + YYY (#000)
```
