## 🧰 Astra JAVA SDK 🧰

The SDK makes it easy to call Astra services using idiomatic Java APIs. On top of the SDK the repository provide tool like a Spring Boot starter and another samples helping you starting code with ASTRA and JAVA.

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
 
## 🏠 Table of contents

**[Getting Started](#1-getting-started)**
- [Import library in your project](#import-sdk-dependency-in-your-project)
- [Configure `AstraClient` with **Environment variables**](#configure-client-astraclient-with-environment-variables)
- [Configure `AstraClient` with **Fluent API**](#configure-client-astraclient-with-fluent-api)
- [Configure `AstraClient` with **Constructor**](#configure-client-astraclient-with-constructor)
- [Configure `AstraClient` with **Spring**](#configure-client-astraclient-with-spring-framework)
- [Test Connectivity(#test-connectivity)]

**[Schema API](#)**
- findAllNamespaces
- findAllKeyspaces
- findNamespaceById
- findKeyspaceById

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

**[DevopsApi](#)**


## 1. Getting Started

There are multiple ways to start working with SDK. We target versatility to be included in multiple form of java applications, standalone, Spring, Quarkus, Camel, batches...

You will work with a single object named `AstraClient` that could work both with managed service [Astra](astra.datastax.com) and Standalone [Stargate](stargate.io).

*Sneak preview*

```java
// Single class to interact with ASTRA
AstraClient astraClient = AstraClient.builder().build();

// --------------
//  DOCUMENT API
// --------------
// Create, no id provided create one
Person p = new Person("Cedrick", "Lunven");
String docPersonId = astraClient.namespace("namespace1").save(p);

// FindById
Optional<Person> person2 = 
	astraClient.namespace("namespace1")
	           .findById(docPersonId, Person.class);

// Update as id already exist or create a new wit this id
astraClient.namespace("namespace1").save(docPersonId, new Person("Cedric", "Lunven"));

// FindAll but with Paging ^^
int pageSize = 10;
ResultPageList<Person> personPage1 = astraClient.namespace("namespace1").findAll(pageSize, Person.class);
ResultPageList<Person> personPage2 = astraClient.namespace("namespace1").findAll(pageSize, personPage1.getPagingState(), Person.class);

// Delete
astraClient.namespace("namespace1").delete(docPersonId, Person.class);
```

### Import SDK dependency in your project

You can import the library with the following coordinate in Maven.

```xml
<dependency>
  <groupId>com.datastax.astra</groupId>
  <artifactId>astra-sdk-java</artifactId>
  <version>2021.1-SNAPSHOT</version>
</dependency>
```

### Configure client `AstraClient` with Environment variables

**📘 Working with Astra**

You rely on environment variables listed on the connect tab in ASTRA UI to initialize the client. We are here using an approach based on convention with name of environment pre-defined.

*Expected environment variables*

```bash
export ASTRA_DB_ID=<youdbId>
export ASTRA_DB_REGION=<youdbregion>
export ASTRA_DB_USERNAME=<username>
export ASTRA_DB_PASSWORD=<password>
```

You might noticed than the `ASTRA_DB_KEYSPACE` here is not specifed as the client will allows you to work with DocApi, RestAPI and CQL and thus on all `namespaces` and `keyspaces` available on the target platform.

**📘 Working with Stargate**

If you are NOT using ASTRA but a StandAlone stargate the variables to define are slightly different. (*if you define both Astra and Stargate keys - priority will go to Stargate.*)

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

**🖥️ Expected Output:**
```
- Initializing Client: BaseUrl=https://e92195f2-159f-492e-9777-3dadda3ff1a3-europe-west1.apps.astra.datastax.com/api/rest, username=todouser,passwordLenght=13
- Successfully Authenticated, token will live for 300 second(s).
```

### Configure client `AstraClient` with Fluent API

Modern java applications use fluent API approachs with a [builder pattern](https://en.wikipedia.org/wiki/Builder_pattern) in order to generate an immutable client.

To keep simply the `AstraClientBuilder` will work both for Astra and Stargate with different keys.

**📘 Setup with Astra**

```java
AstraClient astraClient = AstraClient.builder()
                .astraDatabaseId(<youdbId>")
                .astraDatabaseRegion("<youdbregion>")
                .username("<username>")
                .password("<password>")
                .tokenTtl(Duration.ofSeconds(300)) // optional default is 300s
                .build()
```

**📘 Setup with Stargate**                

```                
AstraClient stargateClient = AstraClient.builder()
                .baseUrl("<stargate_url>")
                .username("<username>")
                .password("<password>")
                .tokenTtl(Duration.ofSeconds(300)) // optional default is 300s
                .build();
```

### Configure client `AstraClient` with Constructor

**📘 Working with Astra**

```java
AstraClient astraClient = new AstraClient("<youdbId>", "<youdbregion>", "<username>", "<password>");
AstraClient astraClient = new AstraClient("<youdbId>", "<youdbregion>", "<username>", "<password>", Duration.ofSeconds(300));
```

**📘 Working with Stargate**                

```java                
AstraClient astraClient = new AstraClient("<baseUrl>", "<username>", "<password>");
AstraClient astraClient = new AstraClient("<baseUrl>", "<username>", "<password>", Duration.ofSeconds(300));          
```

### Configure client `AstraClient` with Spring Framework

As the `AstraClient` can be initialized with a constructor you want to read values from configuration files like `application.properties` or `application.yaml` and inject them in the constructor. 

A Spring Boot starter will look for the following key to initiate the client for you (see [SPRING BOOT STARTER](#). You would also reuse environment variables defined before.

```yaml
astra:
  username: $(ASTRA_DB_ID:username}
  password: <password>
  dataBaseId: <dbId>
  databaseRegion: <dbRegion>
  baseUrl: <baseUrl>
  tokenTtl: <tokenUrl>
```

*Sample Configuration Class*

```java
@Configuration
public class AstraConfiguration {

 @Value("${astra.username}")
 private String atraUsername;
 
 @Value("${astra.password}")
 private String atraPassword;
 
 @Value("${astra.dataBaseId}")
 private String dataBaseId;
 
 @Value("${astra.databaseRegion}")
 private String databaseRegion;

 @Bean
 protected Astraclient astraClient() {
	return new AstraClient(dataBaseId, databaseRegion, atraUsername, atraPassword);
 }
}
```
### Test connectivity

To interact with Astra and Stargate you need to create an `Authentication Token` and provides it in the header for each of you query using key `X-Cassandra-Token`. 
The work is done for you at the SDK level but not only. It will store it a renew it after a time to leave you setup. Before every class to the API the SDK check
the token. To create a token and as such validate your connection details you can do:

```java
boolean isConnectionEstablished = astraClient.connect();
```

## 2. Schema API

As presented in the [Astra Reference Documentation](https://docs.astra.datastax.com/reference#auth-2) the Astra Document APIS is divided in 4 spaces: Authentications, Namespace, Keyspaces and Schemas.



## 3. Document API

Astra and Stargate bring great innovation by allowing Apache Cassandra to store Documents like a document-oriented noSQL database. To cope with Cassandra data model constraints the [document shredding](https://stargate.io/2020/10/19/the-stargate-cassandra-documents-api.html) function has been used.

As a Java developer you want to work with objects (entities) and let the SDK interact with the API performing operations you need *Create, Read, Update, Delete and search*.

ARCHITECTURE PICS

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




