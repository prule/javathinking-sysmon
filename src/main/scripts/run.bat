@echo off
setLocal EnableDelayedExpansion
set JTSM_CLASSPATH="
for /R ../lib %%a in (*.jar) do (
  set JTSM_CLASSPATH=!JTSM_CLASSPATH!;%%a
)
set JTSM_CLASSPATH=!JTSM_CLASSPATH!"


java -cp ..\conf;%JTSM_CLASSPATH% com.javathinking.jtsysmon.core.cli.CliUi %*
