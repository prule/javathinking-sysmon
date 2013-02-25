#!/bin/bash
JTSM_CLASSPATH=
for i in ../lib/*.jar
do
  JTSM_CLASSPATH="$JTSM_CLASSPATH:$i"
done

java -cp ../conf:$JTSM_CLASSPATH com.javathinking.jtsysmon.core.cli.CliUi $@
