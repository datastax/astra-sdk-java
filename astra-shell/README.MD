# Astra Shell

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Overview

Astra Shell is a standalone command line interface. 

(1) It can be use as a stateless cli providing all options in the command line for automation purporse

```
astra create db 
 --token AstraCS:...... \ 
 --name workshop \
 --region eu-central-1 \
 --keyspace nosql1
```

(2) It provides a configuration management system to avoid you to enter credentials (tokens) for each command.

```
astra create db 
 --config my_env_dev \ 
 --name workshop \
 --region eu-central-1 \
 --keyspace nosql1
```

(3) It provides a shell, an interactive repl for users interactions.

```
astra --config my_env_dev

$myAstraOrganization>create db  --name workshop --region eu-central-1 --keyspace nosql1
```

## Prequisites

The program is written in Java. You need to have a JRE/JDK 8+ install

## Installations

### Manual installation

### Homebrew install (Mac Users)

*Not available yet*

```
brew install astra
```

### Apt-Get install (Linux Users)

*Not available yet*

```
apt-get install astra
```

### Yum Install (Linux Users)

*Not available yet*

```
yum install astra
```

### Install with sdkman

*Not available yet*

```
sdk install astra
```

### Install with Chocolatey (Windows Users)

*Not available yet*

```
sdk install astra
```

## Setup

## Getting Started

```
astra setup
```

```
astra show configs
astra show config default
astra show dbs
astra show roles
astra show users
astra show user <U>
astra show role <R>
```

```
astra
>show configs
>show config <CONF>
>config load --name <CONF>
>config save --name <CONF>
```

