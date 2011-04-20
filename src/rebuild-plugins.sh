#!/bin/bash

sed -i 's/<!--STARTPLUGINS-->/<!--STARTPLUGINS/' commons/pom.xml
sed -i 's/<!--ENDPLUGINS-->/ENDPLUGINS-->/' commons/pom.xml

cd commons
mvn clean install -DskipTests=true
cd ..

sed -i 's/<!--STARTPLUGINS/<!--STARTPLUGINS-->/' commons/pom.xml
sed -i 's/ENDPLUGINS-->/<!--ENDPLUGINS-->/' commons/pom.xml

find plugins -iname "pom.xml" -execdir mvn clean install -DskipTests=true \;


cd commons
mvn clean install -DskipTests=true
cd ..
