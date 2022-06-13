# Astra Shell

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Overview

Astra Shell is a standalone command line interface. 

- (1). It can be used as a stateless cli providing all options in the command line for automation purposes.

```
astra create db 
 --token AstraCS:...... \ 
 --name workshop \
 --region eu-central-1 \
 --keyspace nosql1
```

- (2). It provides a configuration file to avoid users to enter credentials (tokens) each time.

```
astra create db 
 --config my_env_dev \ 
 --name workshop \
 --region eu-central-1 \
 --keyspace nosql1
```

- (3). It provides a shell, an interactive repl with users interactions.

```
astra --config my_env_dev

$myAstraOrganization>create db  --name workshop --region eu-central-1 --keyspace nosql1
```

## Prequisites

- A JRE/JDK 8+ installed is required.

## Installations

- Download the zip archive
- run `astra-install.sh`
- run `astra-autocomplete.bash`
- run `astra version` to validate installation

## Setup

- Will ask for token and initialize config file
- Each time you want to add a new token you can use it

```
astra setup
```

## Configuration file

- List available configurations

```
astra show configs
astra config list
```

- Create a new configuration providing a name

```
astra config create my_new_section -t <TOKEN>
```

- Show details of a configuration

```
astra config show my_new_section
```

- Set a configuration as default

```
astra config default my_new_section
```

- Rename on organization

```
astra config rename my_new_section --name section2
```

- Delete a configuration

```
astra config delete my_new_section
```



astra show dbs
astra show db <DB>

astra show roles
astra show role <ROLE>

astra show users
astra show user <USER>
```

## Configuration

```
show config <CONFIG>
config load --name <CONFIG>
config save --name <CONFIG>
config setdefault --name <XXX>
```

