# NAME

`astra` - CLI for DataStax Astra™ including an interactive mode

# SYNOPSIS

`astra` [ *group* ] *command* [ *command-args* ]

# COMMANDS

- `help`

  View help for any command

- `setup`

  Initialize configuration file

- `shell`

  Interactive mode (default if no command provided)

- `config create`

  Create a new section in configuration

- `config default`

  Set a section as default

- `config delete`

  Delete section in configuration

- `config get`

  Show details for a configuration.

- `config list`

  Show the list of available configurations.

- `db cqlsh`

  Start Cqlsh

- `db create`

  Create a database with cli

- `db create-keyspace`

  Create a new keyspace

- `db delete`

  Delete an existing database

- `db get`

  Show details of a database

- `db list`

  Display the list of Databases in an organization

- `role get`

  Show role details

- `role list`

  Display the list of Roles in an organization

- `user delete`

  Delete an existing user

- `user get`

  Show user details

- `user invite`

  Invite a user to an organization

- `user list`

  Display the list of Users in an organization

---

# NAME

`astra` `help` - View help for any command

# SYNOPSIS

`astra` `help` [ `--` ] [ *command* ]

# OPTIONS

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *command*



---

# NAME

`astra` `setup` - Initialize configuration file

# SYNOPSIS

`astra` `setup` [ `--config-file` *CONFIG_FILE* ] [ `--no-color` ] [ { `-o` |
`--output` } *FORMAT* ] [ { `-v` | `--verbose` } ]

# OPTIONS

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-v` , `--verbose`

  Verbose mode with log in console

---

# NAME

`astra` `shell` - Interactive mode (default if no command provided)

# SYNOPSIS

`astra` `shell` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [ `--config-file`
*CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ { `-o` | `--output` }
*FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` | `--verbose` } ] [
`--version` ]

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--version`

  Show version

---

# NAME

`astra` `config` `create` - Create a new section in configuration

# SYNOPSIS

`astra` `config` `create` [ `--config-file` *CONFIG_FILE* ] [ `--no-color` ] [
{ `-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AuthToken* ] [ { `-v`
| `--verbose` } ] [ `--` ] *section*

# OPTIONS

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AuthToken* , `--token` *AuthToken*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *section*

  Section in configuration file to as as default.

---

# NAME

`astra` `config` `default` - Set a section as default

# SYNOPSIS

`astra` `config` `default` [ `--config-file` *CONFIG_FILE* ] [ `--no-color` ] [
{ `-o` | `--output` } *FORMAT* ] [ { `-v` | `--verbose` } ] [ `--` ] *section*

# OPTIONS

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *section*

  Section in configuration file to as as defulat.

---

# NAME

`astra` `config` `delete` - Delete section in configuration

# SYNOPSIS

`astra` `config` `delete` [ `--config-file` *CONFIG_FILE* ] [ `--no-color` ] [
{ `-o` | `--output` } *FORMAT* ] [ { `-v` | `--verbose` } ] [ `--` ] *section*

# OPTIONS

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *section*

  Section in configuration file to as as default.

---

# NAME

`astra` `config` `get` - Show details for a configuration.

# SYNOPSIS

`astra` `config` `get` [ `--config-file` *CONFIG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-v` | `--verbose` } ] [ `--` ] *section*

# OPTIONS

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *section*

  Section in configuration file to as as defulat.

---

# NAME

`astra` `config` `list` - Show the list of available configurations.

# SYNOPSIS

`astra` `config` `list` [ `--config-file` *CONFIG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-v` | `--verbose` } ]

# OPTIONS

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-v` , `--verbose`

  Verbose mode with log in console

---

# NAME

`astra` `db` `cqlsh` - Start Cqlsh

# SYNOPSIS

`astra` `db` `cqlsh` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--debug` ] [ { `-e` | `--execute` }
*STATEMENT* ] [ `--encoding` *ENCODING* ] [ { `-f` | `--file` } *FILE* ] [ {
`-k` | `--keyspace` } *KEYSPACE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ] [ `--version` ] [ `--` ] *DB*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--debug`

  Show additional debugging information.

- `-e` *STATEMENT* , `--execute` *STATEMENT*

  Execute the statement and quit.

- `--encoding` *ENCODING*

  Output encoding. Default encoding: utf8.

- `-f` *FILE* , `--file` *FILE*

  Execute commands from a CQL file, then exit.

- `-k` *KEYSPACE* , `--keyspace` *KEYSPACE*

  Authenticate to the given keyspace.

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--version`

  Display information of cqlsh.

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *DB*

  Database name or identifier

---

# NAME

`astra` `db` `create` - Create a database with cli

# SYNOPSIS

`astra` `db` `create` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--if-not-exist` ] [ { `-k` | `--keyspace` }
*KEYSPACE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ { `-o` | `--output` }
*FORMAT* ] [ { `-r` | `--region` } *DB_REGION* ] [ { `-t` | `--token` }
*AUTH_TOKEN* ] [ { `-v` | `--verbose` } ] [ `--` ] *DB_NAME*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--if-not-exist`

  will create a new DB only if none with same name

- `-k` *KEYSPACE* , `--keyspace` *KEYSPACE*

  Default keyspace created with the Db

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-r` *DB_REGION* , `--region` *DB_REGION*

  Cloud provider region to provision

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *DB_NAME*

  Database name (not unique)

---

# NAME

`astra` `db` `create-keyspace` - Create a new keyspace

# SYNOPSIS

`astra` `db` `create-keyspace` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--if-not-exist` ] { `-k` | `--keyspace` }
*KEYSPACE* [ `--log` *LOG_FILE* ] [ `--no-color` ] [ { `-o` | `--output` }
*FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` | `--verbose` } ] [ `--` ]
*DB*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--if-not-exist`

  will create a new DB only if none with same name

- `-k` *KEYSPACE* , `--keyspace` *KEYSPACE*

  Name of the keyspace to create

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *DB*

  Database name or identifier

---

# NAME

`astra` `db` `delete` - Delete an existing database

# SYNOPSIS

`astra` `db` `delete` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ] [ `--` ] *DB*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *DB*

  Database name or identifier

---

# NAME

`astra` `db` `get` - Show details of a database

# SYNOPSIS

`astra` `db` `get` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ] [ `--` ] *DB*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *DB*

  Database name or identifier

---

# NAME

`astra` `db` `list` - Display the list of Databases in an organization

# SYNOPSIS

`astra` `db` `list` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ]

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

---

# NAME

`astra` `role` `get` - Show role details

# SYNOPSIS

`astra` `role` `get` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ] [ `--` ] *ROLE*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *ROLE*

  Role name or identifier

---

# NAME

`astra` `role` `list` - Display the list of Roles in an organization

# SYNOPSIS

`astra` `role` `list` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ]

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

---

# NAME

`astra` `user` `delete` - Delete an existing user

# SYNOPSIS

`astra` `user` `delete` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ] [ `--` ] *EMAIL*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *EMAIL*

  User email or identifier

---

# NAME

`astra` `user` `get` - Show user details

# SYNOPSIS

`astra` `user` `get` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ] [ `--` ] *EMAIL*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *EMAIL*

  User Email

---

# NAME

`astra` `user` `invite` - Invite a user to an organization

# SYNOPSIS

`astra` `user` `invite` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-r` | `--role` } *ROLE* ] [ { `-t` |
`--token` } *AUTH_TOKEN* ] [ { `-v` | `--verbose` } ] [ `--` ] *EMAIL*

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-r` *ROLE* , `--role` *ROLE*

  Role for the user (default is Database Administrator)

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

- `--`

  This option can be used to separate command-line options from the list of
  arguments (useful when arguments might be mistaken for command-line options)

- *EMAIL*

  User Email

---

# NAME

`astra` `user` `list` - Display the list of Users in an organization

# SYNOPSIS

`astra` `user` `list` [ { `-conf` | `--config` } *CONFIG_SECTION* ] [
`--config-file` *CONFIG_FILE* ] [ `--log` *LOG_FILE* ] [ `--no-color` ] [ {
`-o` | `--output` } *FORMAT* ] [ { `-t` | `--token` } *AUTH_TOKEN* ] [ { `-v` |
`--verbose` } ]

# OPTIONS

- `-conf` *CONFIG_SECTION* , `--config` *CONFIG_SECTION*

  Section in configuration file (default = ~/.astrarc)

- `--config-file` *CONFIG_FILE*

  Configuration file (default = ~/.astrarc)

- `--log` *LOG_FILE*

  Logs will go in the file plus on console

- `--no-color`

  Remove all colors in output

- `-o` *FORMAT* , `--output` *FORMAT*

  Output format, valid values are: human,json,csv

- `-t` *AUTH_TOKEN* , `--token` *AUTH_TOKEN*

  Key to use authenticate each call.

- `-v` , `--verbose`

  Verbose mode with log in console

