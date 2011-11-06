#!/bin/bash
#
# Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
#                Samuel Kogler     <samuel.kogler@gmail.com>,
#                Stephan Stiboller <stistc06@htlkaindorf.at>
#
# This file is part of Codesearch.
#
# Codesearch is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Codesearch is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
#


sed -i 's/<!--STARTPLUGINS-->/<!--STARTPLUGINS/' commons/pom.xml
sed -i 's/<!--ENDPLUGINS-->/ENDPLUGINS-->/' commons/pom.xml

cd commons
mvn clean install -DskipTests=true -T 2.0C
cd ..

sed -i 's/<!--STARTPLUGINS/<!--STARTPLUGINS-->/' commons/pom.xml
sed -i 's/ENDPLUGINS-->/<!--ENDPLUGINS-->/' commons/pom.xml

find plugins -iname "pom.xml" -execdir mvn clean install -DskipTests=true -T 2.0C -q \;

cd commons
mvn clean install -DskipTests=true -T 2.0C
cd ..
