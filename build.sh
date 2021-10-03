#!/bin/bash

OUTPUT=AstralInstability.jar 
MAINCLASS=xyz.thinic.ld49.Game

rm -rf .tmp $OUTPUT
mkdir -p .tmp 
echo -e "Manifest-Version: 1.0\nMain-Class: $MAINCLASS\n" > .manifest
javac -sourcepath src src/${MAINCLASS//./\/}.java -d .tmp
jar cvfm $OUTPUT .manifest -C res . -C .tmp .
rm -rf .tmp .manifest
