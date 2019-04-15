#!/bin/bash

JAR=target/studentgenerator-0.1-jar-with-dependencies.jar

if [ ! -e $JAR ] ; then
    mvn package
fi

java -jar ${JAR}  $@
