@echo off
cls

rem ambiente senior
if exist C:\Program Files (x86)\Java\jdk1.8.0_101 (
  echo "Updating JAVA_HOME..."
  set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.8.0_101
  echo "JAVA_HOME  updated to %JAVA_HOME%"
)

set COAP_CLASSPATH=.\lib\californium-core-1.1.0-SNAPSHOT.jar;.\lib\element-connector-1.1.0-SNAPSHOT.jar;.\bin;

set MAIN_CLASS=test.coap.TestCoapClient
set DATA_DIR=..\..\data-sample
set LOG_FILE=coap-performance.csv
set URI="coap://localhost:5683"
set INTERVAL=300

echo "Executing TestCoapClient..."
"%JAVA_HOME%\bin\java.exe" -classpath %COAP_CLASSPATH% %MAIN_CLASS% %DATA_DIR% %LOG_FILE% %URI% %INTERVAL%
echo "Execution finished!"
