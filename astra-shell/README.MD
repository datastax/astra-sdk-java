# Getting Started with Astra CLI

[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Astra Shell is a standalone command line interface proposing functions to operate the Datastax Astra platform.

## A. Installation

### Prequisites

On your machine you will need :
- A bash shell
- Commands: `unzip`, `curl`
- A Java JRE or JDK 8+ (temporary)

You will also need to:
- [Create an Astra account](https://astra.datastax.com)
- [Create an authentication token](https://dtsx.io/create-astra-token)

### Installation

```
curl -Ls "https://dtsx.io/get-astra-cli" | bash
```

### Setup

- 1/3 - Execute the following
```
astra setup
```

- 2/3 When asked provides a token of your choice:

```
• Enter your token (AstraCS...) : 
AstraCS:.......
```
 - 3/3 Open a new TAB to finalize install

```
astra --version
astra help
```

## B. Working with configurations

- **List Configurations**

```
astra config list
```

- **Create new configuration** 

If you have multiple organizations it could be useful to switch from one to another. To create another config, the latest created org will become the default.

```
astra config create dev -t <token_of_org_2>
```

```
astra config list
```

When you issue any command the config is now loaded for you:

```
astra user list
```

- **Change default configuration**: 

You can change the default org at any time with the following command:

```
astra config default dev && astra config list
```

- Test again, user list should be different

```
astra user list
```

- Back to original org (change with your email)

```
astra config default cedrick.lunven@datastax.com && astra config list
```

- You can use any organization without switching default configuration with `--config` or `-t`

```
astra user list --config dev
```

- **Delete configuration**

You can delete any organization. I see you.. if you delete the default one you are on your own and you would need to define a new one.

```
astra config delete dev
astra config list
```

## C. Working with Databases

- **List Databases**:

```
astra db list
```

- **Create a new database:** 

If not provided the region will be the default free region and the keyspace will be the database name but you can change then with `-r` and `-k` respectivitely.

```
astra db create demo
astra db list
```

- **Get information on your database**

Notice how you never provide any the technical ids.

```
astra db get demo
```

- **Change output format**

This CLI wil be used by CI/CD and script, sometimes the output must be parsed. There are 3 outputs: *human,json,csv"

```
astra db list -o json
```

```
astra db list -o csv
```

The return type is also important to check the errors. O mean everything is OK

```
echo $?
```

```
astra db get i_do_not_exist && echo $?
```

Error codes tables

| Error             | Code      |
|-------------------|-----------|
| SUCCESS           | 0         |
| PARSE_ERROR       | 1         |
| INVALID_PARAMETER | 10        |
| NOT_FOUND         | 20        |
| CONFLICT          | 21        |
| ALREADY_EXIST     | 22        |
| CANNOT_CONNECT    | 30        |
| INTERNAL_ERROR    | 40        |


- The cli implemented `If not Exist` for both keyspace and database (convenient for workshops and training)

```
astra help db create
astra db create demo --if-not-exist
```

- All commands can also be in versbose mode anytime with `-v`

```
astra db create demo --if-not-exist -v
```

Make sure the database is now Active or you will get some expected errors.

- **Create a new keyspace**

```
astra db create-keyspace demo -k ks2
```

Need helps ?
```
astra help db create-keyspace
```

- Now let's blow your mind 🥁

```
astra db cqlsh demo
```

- Quit the shell

```
quit;
```

- Execute CQL (or CQL file)

```
astra db cqlsh demo -e "describe keyspaces;"
```

## D. Interactive mode

Commands we used so far are stateless, no connection is maintained after the execution. What if you need more an interactive shell than a cli ?

```
astra
```

- List the DB 

```
db list
```

- Show a db

```
db get demo
```

Now what if we want some commands on this particular db ?

```
db use demo
```

```
info
```

> **Note**: *Still not sure about the info command to display the current context of the shell.*

- Create a keyspace

```
create keyspace kso
```

- Access the keyspace

```
cqlsh -k kso
```

Now quit:

```
quit;
```

## E. Working with Users

- List your users

```
astra user list
```

- Invite your best friend (me). The role will be DataStax Administrator by default

```
astra user invite cedrick.lunven@datastax.com
astra db list
```

- Delete a user

```
astra user delete cedrick.lunven@datastax.com
```




## F. Working with Roles






