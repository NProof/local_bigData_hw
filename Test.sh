#! /bin/bash

javac src/Test.java src/Point.java -d bin/

#$1 := command line argument [1]
java -classpath bin/ Test $1
