# Astra Shell

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

_This component is in heavy development that can lead to breaking changes._


## Installation

Installing ASTRA-CLI on UNIX-like platforms is as easy as ever. ASTRA-CLI installs smoothly on macOS, Linux, WSL, Cygwin, Solaris and FreeBSD. We also support Bash and ZSH shells.

- Open a new terminal and enter:

```
curl -Ls curl -s "https://dtsx.io/get-astra-cli" | bash
```

- Follow the instructions on-screen to complete installation. 

- To get autocompletion open a new terminal :

```
source "$HOME/.astra/install/astra-init.sh"
```

- Lastly, run the following code snippet to ensure that installation succeeded:

```
astra -v
```

If all went well, the version should be displayed. Something like:

```
0.3.2-SNAPSHOT
```

## Setup

- Run the following command and provide your token when needed

```
astra setup
```

- You should be good to go

```
astra config list
```

```
+-----------------------------------------+
| configuration                           |
+-----------------------------------------+
| default (cedrick.lunven@datastax.com)   |
| DatastaxDevs                            |
| cedrick.lunven@datastax.com             |
+-----------------------------------------+
```

- And access Astra

```
astra db list
```

```
+---------------------+--------------------------------------+---------------------+----------------+
| name                | id                                   | default-region      | status         |
+---------------------+--------------------------------------+---------------------+----------------+
| mtg                 | dde308f5-a8b0-474d-afd6-81e5689e3e25 | eu-central-1        | ACTIVE         |
| workshops           | 3ed83de7-d97f-4fb6-bf9f-82e9f7eafa23 | eu-west-1           | ACTIVE         |
| sdk_tests           | 06a9675a-ca62-4cd0-9b94-aefaf395922b | us-east-1           | ACTIVE         |
| api_days_dubai_2022 | 53f07b2a-8856-4741-b595-72b03be56bc6 | us-east1            | ACTIVE         |
| free_db             | 8642763c-8ba7-4ede-a031-82355c7c34a7 | us-east1            | ACTIVE         |
+---------------------+--------------------------------------+---------------------+----------------+
```




