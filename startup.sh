#!/bin/sh

VIRGO_SERVER="virgo-tomcat-server-3.6.1.RELEASE"

mvn -f hello.frontend/pom.xml package
cp hello.frontend/target/*.war $VIRGO_SERVER/pickup/
./$VIRGO_SERVER/bin/startup.sh
