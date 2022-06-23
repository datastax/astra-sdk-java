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

- Download and extract the zip archive

```
unzip astra-shell-alpha1.zip
```

- Initialization 

```
cd astra-shell
chmod 700 astra
source auto-completions.bash
```

- Configuration


You are already set and can start typing commands

```
astra show dbs -t <TOKEN>
```

But it is annoying each time having to provide a token. What if you could save it ?

```
astra setup
```

- You can now 

```
astra show dbs
```

## Configuration file

- List available configurations

```
astra show configs
astra config list
```

- Create a new configuration providing a name

```
astra config create dev -t <TOKEN>
astra config list
```

- Show details of a dev

```
astra config show dev
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
astra config delete dev
astra config list
```

## Let's start the SHOW

```
astra show dbs
```

```
astra show roles
```

```
astra show role <ROLE>
```

```
astra show users
```

```
astra show user <USER>
```

## Shell we started now ?


```
astra shell
```


## Configuration

```
show config <CONFIG>
config load --name <CONFIG>
config save --name <CONFIG>
config setdefault --name <XXX>
```

