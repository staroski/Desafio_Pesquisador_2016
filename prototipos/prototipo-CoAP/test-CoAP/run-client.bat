@echo off
cls

rem ambiente senior
if exist C:\Program Files (x86)\Java\jdk1.8.0_101 (
  echo "Updating JAVA_HOME..."
  set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.8.0_101
  echo "JAVA_HOME  updated to %JAVA_HOME%"
)

set COAP_CLASSPATH=.\lib\californium-core-1.1.0-SNAPSHOT.jar;.\lib\element-connector-1.1.0-SNAPSHOT.jar;.\bin;

echo "Executing TestCoapClient..."
"%JAVA_HOME%\bin\java.exe" -classpath %COAP_CLASSPATH% test.coap.TestCoapClient
echo "Execution finished!"
