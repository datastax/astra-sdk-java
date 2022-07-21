
# Generate the Asset (for now)

```
mvn package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true
cp ./target/astra-shell-0.3.2-SNAPSHOT-shaded.jar ~/.astra/cli/astra-shell.jar
```

# Executing the command

```
java -jar ./target/astra-shell-0.3.1-SNAPSHOT-shaded.jar show-dbs
```
 


sdk, brew, chocolaty

```
- run `astra-install.sh`
- run `astra-autocomplete.bash`
- run `astra version` to validate installation


# NATIVE IMAGE
https://simply-how.com/fix-graalvm-native-image-compilation-issues
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/Reflection.md
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/Limitations.md


Run with Agent
java -agentlib:native-image-agent=config-output-dir=. -jar astra-shell-0.3.1-SNAPSHOT-shaded.jar


 TO BUILD:
============
mvn clean install -Dmaven.test.skip=true



 TO BUILD IN NATIVE:
 ======================
(1) you need to install GraalVM (GU)

(2) mvn clean install -Dmaven.test.skip=true -Pnative






