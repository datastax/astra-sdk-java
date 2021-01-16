## 🔥 Astra JAVA SDK 🔥 

The SDK makes it easy to call Astra services using idiomatic Java APIs.

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
 
## Table of contents

1. [Getting Started](#1-getting-started)
- 1.1 [Import library in your project](#11---import-library-in-your-project)
- 1.2 [Configure the Client](#11---import-library-in-your-project)

2. [DocumentAPI](#)
- 2.1 [Working with Collections](#)
- 2.2 [Create a Document](#)
- 2.3 [Test document existence](#)
- 2.4 [Read a document](#)


3. [Rest Api](#)

4. [CQLSession](#)

5. [DevopsApi](#)

6. More Resource



## 1. Getting Started

### 1.1 - Import library in your project

You can import the library with the following coordinate in Maven

```xml
<dependency>
  <groupId>com.datastax.astra</groupId>
  <artifactId>astra-sdk-java</artifactId>
  <version>2021.1-SNAPSHOT</version>
</dependency>
```

### 1.2 - Configure the client

There are multiple ways to start working with SDK. We target versatility to be included in multiple form of java applications, standalone, Spring, Quarkus, Camel, batches...

You will work with a single object named `AstraClient` that could work both with managed service [Astra](astra.datastax.com) and Standalone [Stargate](stargate.io).

**✅ Environment Variables**

You rely on environment variables listed on the connect tab in ASTRA UI to initialize the client. We are here using an approach based on convention.

```bash
export ASTRA_DB_ID=<youdbId>
export ASTRA_DB_REGION=<youdbregion>
export ASTRA_DB_USERNAME=<username>
export ASTRA_DB_PASSWORD=<password>
```

You might noticed than the `ASTRA_DB_KEYSPACE` here is not specifed as the client will allows you to work with DocApi, RestAPI and CQL and thus on all `namespaces` and `keyspaces` available on the target platform.

If you are NOT using ASTRA but simply StandAlone stargate those are the variables to define. (*if you define both Astra and Stargate keys priority will go to stargate.*)


```bash
 export USERNAME=<username>
 export PASSWORD=<password>
 export BASE_URL=<stargate_url>
```

Same variables as the [JavaScript SDK](https://github.com/datastax/astrajs) have been used for consitency. Some extra variable can still be provided to customize and specialize the client.

```bash
#Time to live of the authentication token
export TOKEN_TTL=300
```

Using the convention here is the code for you to start.

```java
AstraClient astraClient = AstraClient.builder().build();
```

Expected Output:
```
- Initializing Client: BaseUrl=https://e92195f2-159f-492e-9777-3dadda3ff1a3-europe-west1.apps.astra.datastax.com/api/rest, username=todouser,passwordLenght=13
- Successfully Authenticated, token will live for 300 second(s).
```

**✅ Builder**

Modern java Api now use a fluent API approach with the builder pattern. You can provide the same values as listed before to initiate the `AstraClient`


```java
// Using Astra
AstraClient astraClient = AstraClient.builder()
                .astraDatabaseId(<youdbId>")
                .astraDatabaseRegion("<youdbregion>")
                .username("<username>")
                .password("<>password")
                .tokenTtl(Duration.ofSeconds(300))
                .build()
                
// Using StandAlone Stargate
 AstraClient stargateClient = AstraClient.builder()
                .baseUrl("<stargate_url>")
                .username("<username>")
                .password("<>password")
                .tokenTtl(Duration.ofSeconds(300))
                .build()               
```

