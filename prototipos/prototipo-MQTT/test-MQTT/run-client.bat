@echo off
cls

rem ambiente senior
if exist C:\Program Files (x86)\Java\jdk1.8.0_101 (
  echo "Updating JAVA_HOME..."
  set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.8.0_101
  echo "JAVA_HOME  updated to %JAVA_HOME%"
)

set MQTT_CLASSPATH=.\lib\commons-codec-1.10.jar;.\lib\hazelcast-3.5.4.jar;.\lib\HdrHistogram-2.1.4.jar;.\lib\log4j-1.2.16.jar;.\lib\mapdb-1.0.8.jar;.\lib\moquette-broker-0.9-SNAPSHOT.jar;.\lib\moquette-netty-parser-0.9-SNAPSHOT.jar;.\lib\netty-buffer-4.0.33.Final.jar;.\lib\netty-codec-4.0.33.Final.jar;.\lib\netty-codec-http-4.0.33.Final.jar;.\lib\netty-common-4.0.33.Final.jar;.\lib\netty-handler-4.0.33.Final.jar;.\lib\netty-transport-4.0.33.Final.jar;.\lib\org.eclipse.paho.client.mqttv3-1.1.0.jar;.\lib\slf4j-api-1.7.5.jar;.\lib\slf4j-log4j12-1.6.4.jar;.\bin;

set MAIN_CLASS=test.mqtt.TestMqttClient
set DATA_DIR=..\..\data-sample
set LOG_FILE=mqtt-performance.csv
set URI=tcp://localhost:1883
set INTERVAL=300

echo "Executing TestMqttClient..."
"%JAVA_HOME%\bin\java.exe" -classpath %MQTT_CLASSPATH% %MAIN_CLASS% %DATA_DIR% %LOG_FILE% %URI% %INTERVAL%
echo "Execution finished!"
