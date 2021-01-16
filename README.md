## 🔥 Astra JAVA SDK 🔥 

The SDK makes it easy to call Astra services using idiomatic Java APIs.

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
 
## Table of contents

**[Getting Started](#1-getting-started)**
- [Import library in your project](#11---import-library-in-your-project)
- [Configure client `AstraClient` with **Environement variables**](#11---import-library-in-your-project)
- [Configure client `AstraClient` with **Fluent API**](#11---import-library-in-your-project)
- [Configure client `AstraClient` with **Constructor**](#11---import-library-in-your-project)
- [Configure client `AstraClient` with **Spring**](#11---import-library-in-your-project)


**[Document API](#)**
- [Working with Collections](#)
- [Create a Document](#)
- [Test document existence](#)
- [Read a document](#)
- [Search for documents](#)
- [Paging](#)
- [Update a document](#)
- [Delete a document](#)


**[Rest API](#)**

**[CQLSession](#)**

5. [DevopsApi](#)

6. More Resource


## 1. Getting Started

### Import the SDK library in your project

You can import the library with the following coordinate in Maven

```xml
<dependency>
  <groupId>com.datastax.astra</groupId>
  <artifactId>astra-sdk-java</artifactId>
  <version>2021.1-SNAPSHOT</version>
</dependency>
```

### Configure client `AstraClient` with Environement variables

There are multiple ways to start working with SDK. We target versatility to be included in multiple form of java applications, standalone, Spring, Quarkus, Camel, batches...

You will work with a single object named `AstraClient` that could work both with managed service [Astra](astra.datastax.com) and Standalone [Stargate](stargate.io).

**📘 Working with Astra**

You rely on environment variables listed on the connect tab in ASTRA UI to initialize the client. We are here using an approach based on convention.

```bash
export ASTRA_DB_ID=<youdbId>
export ASTRA_DB_REGION=<youdbregion>
export ASTRA_DB_USERNAME=<username>
export ASTRA_DB_PASSWORD=<password>
```

You might noticed than the `ASTRA_DB_KEYSPACE` here is not specifed as the client will allows you to work with DocApi, RestAPI and CQL and thus on all `namespaces` and `keyspaces` available on the target platform.

**📘 Working with Stargate**

If you are NOT using ASTRA but simply StandAlone stargate those are the variables to define. (*if you define both Astra and Stargate keys priority will go to stargate.*)

```bash
 export USERNAME=<username>
 export PASSWORD=<password>
 export BASE_URL=<stargate_url>
```

**📘 Variables to configure**

Same variables as the [JavaScript SDK](https://github.com/datastax/astrajs) have been used for consitency. Some extra variable can still be provided to customize and specialize the client.

```bash
# Time to live of the authentication token
export TOKEN_TTL=300
```

**🛈 CODE**

With environment defined as before initialization is as simple as:

```java
AstraClient astraClient = AstraClient.builder().build();
```

**Expected Output:**
```
- Initializing Client: BaseUrl=https://e92195f2-159f-492e-9777-3dadda3ff1a3-europe-west1.apps.astra.datastax.com/api/rest, username=todouser,passwordLenght=13
- Successfully Authenticated, token will live for 300 second(s).
```

### Configure client `AstraClient` with Fluent API

Modern java Apis use a fluent API approach with a [builder pattern](https://en.wikipedia.org/wiki/Builder_pattern) in order to generate an immutable client.

To keep simply the `AstraClientBuilder` will work both for Astra and Stargate installations here are samples codes.

**📘 Working with Astra**

```java
AstraClient astraClient = AstraClient.builder()
                .astraDatabaseId(<youdbId>")
                .astraDatabaseRegion("<youdbregion>")
                .username("<username>")
                .password("<>password")
                .tokenTtl(Duration.ofSeconds(300))
                .build()
```

**📘 Working with Stargate**                

```                
AstraClient stargateClient = AstraClient.builder()
                .baseUrl("<stargate_url>")
                .username("<username>")
                .password("<>password")
                .tokenTtl(Duration.ofSeconds(300))
                .build()               
```

### Configure client `AstraClient` with Constructor

**📘 Working with Astra**

```java
AstraClient astraClient = new AstraClient("<youdbId>", "<youdbregion>", "<username>", "<password>");
AstraClient astraClient = new AstraClient("<youdbId>", "<youdbregion>", "<username>", "<password>", Duration.ofSeconds(300));
```

**📘 Working with Stargate**                

```                
AstraClient astraClient = new AstraClient("<baseUrl>", "<username>", "<password>");
AstraClient astraClient = new AstraClient("<baseUrl>", "<username>", "<password>", Duration.ofSeconds(300));          
```

### Configure client `AstraClient` with Spring

As the `AstraClient` can be initialize with a full constructor you want to read value from configuration files like `application.properties` or `application.yaml` and inject them in the constructor. A Spring Boot starter will look for the following key to initiate the client for you (see SPRING BOOT START). You can even reuse the environment variables defined before

```yaml
astra:
  username: $(ASTRA_DB_ID:username}
  password: <password>
  dataBaseId: <dbId>
  databaseRegion: <dbRegion>
  baseUrl: <baseUrl>
  tokenTtl: <tokenUrl>
```

```java
@Configuration
public class AstraConfiguration() {

@Value("astra.username")
private String atraUsername;
//

@Bean
protected Astraclient astraClient() {
	return new AstraClient(atraUsername, ....);
}
```


## 2. Document API

Astra and Stargate bring a great innovation by allowing Cassandra to store Document like a document oriented noSQL database. To cope with Cassandra data model constraints they implemented the [document shredding](https://stargate.io/2020/10/19/the-stargate-cassandra-documents-api.html).

As a Java developer you want to create object and let the SDK interact with the API to help you perform the operations you want *Create, Read, Update, Delete* and search.

### Namespaces and Collection

- **Namespace:** Namespace are simply keyspaces in Cassandra that will **only store documents* you cannot mix documents and non document data. As such when working with `namespace` we know that we are working with the Document API.

*Astra Client is ready to work with the document API on namespace `namespace_x`

```java
astraClient.namespace("namespace_x");
```

- **Collections:** Collections are simply tables in Cassandra. Those tables are generated and managed for you but you need to know on which collection you are working for.


### Working with collections


**✅ List available collections in a namespace. **

```java
astraClient.namespace("namespace1")                 // select namespace1
           .findAllCollections()                    // list collection names
           .stream().forEach(System.out::println);; // Show values in console
```

**✅ Check if a collection exist. **

```java
boolean isAAAExist = astraClient.namespace("namespace1").existCollection("AAA");
```

**✅ Create a new collection **

Collection name should be only Alpha Numeric characters.

```java
 astraClient.namespace("namespace1").createCollection("AAA");
```




