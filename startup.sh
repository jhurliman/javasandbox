#!/bin/sh

VIRGO_SERVER="virgo-tomcat-server-3.6.1.RELEASE"

mvn -f hello.backend/pom.xml install
cp hello.backend/target/*.jar $VIRGO_SERVER/pickup/

mvn -f hello.frontend/pom.xml install
cp hello.frontend/target/*.war $VIRGO_SERVER/pickup/

./$VIRGO_SERVER/bin/startup.sh
