#!/bin/sh

VIRGO_SERVER="virgo-tomcat-server-3.6.1.RELEASE"

# Compile
mvn -f hello/pom.xml install

# Copy dependencies to Virgo
cp hello.*/target/classes/lib/* $VIRGO_SERVER/repository/usr/

# Copy compiled application to Virgo
cp hello.*/target/*.jar $VIRGO_SERVER/pickup/
cp hello.*/target/*.war $VIRGO_SERVER/pickup/

# Start the Virgo server
./$VIRGO_SERVER/bin/startup.sh
