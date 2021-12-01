# Astra Software Development Kit

<img src="https://github.com/datastax/astra-sdk-java/blob/main/docs/img/astra-sdk-logo.png?raw/true" height="70"  align="left"/>

## Overview
####

This SDK *(Software Development Kit)* makes it easy to call Stargate and/or Astra services using idiomatic Java APIs. 

<center>
<img src="https://github.com/datastax/astra-sdk-java/blob/main/docs/img/sdk-overview.png?raw/true" />
</center>

- **The Stargate SDK** works with both Stargate standalone installations and Stargate deployed in Astra. With standalone Stargate deployments you will initialize the framework with the class `StargateClient` and provide the list of nodes. To test it locally please follow the [Stargate SDK quickstart](https://github.com/datastax/astra-sdk-java/wiki/Stargate-SDK-Quickstart)

- **The Astra SDK** reuses the previous library with Astra environments. You will then work with the class `AstraClient` that will configure `StargateClient` under the hood for you. As you can see on the figure below the `AstraClient` handle not only Stargate Apis but also the Astra Devops Api and Apache Pulsar deployed in Astra. To test it please follow the [Astra SDK quickstart](https://github.com/datastax/astra-sdk-java/wiki/Astra-SDK-Quickstart)

- **The Astra Spring Boot Starter** provides a straight forward way to configure both `Astra SDK` and `Spring Data Cassandra` to work with Astra. By Reading custom keys in spring boot `application.yaml` the starter will initialize for you all the beans you need. To test it please follow the [Astra Spring Boot Starter QuickStart](https://github.com/datastax/astra-sdk-java/wiki/Spring-Boot-Starter-Quickstart).

## What's NEXT ?

1. [QuickStart for Stargate](https://github.com/datastax/astra-sdk-java/wiki/Stargate-SDK-Quickstart)
2. [QuickStart for Astra](https://github.com/datastax/astra-sdk-java/wiki/Astra-SDK-Quickstart)
3. [QuickStart for Astra Spring Boot Starter](https://github.com/datastax/astra-sdk-java/wiki/Spring-Boot-Starter-Quickstart)



