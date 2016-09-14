#!/bin/bash

# This is the shell startup file for Nemisys.
# Input ./start.sh while in the server directory
# to start the server.

#Change this to "true" to 
#loop Nemisys after restart!

DO_LOOP="false"

###############################
# DO NOT EDIT ANYTHING BELOW! #
###############################

clear

NEMISYS_FILE=""

if [ "$NEMISYS_FILE" == "" ]; then
	if [ -f ./nemisys*.jar ]; then
		NEMISYS_FILE="./nemisys-1.0dev.jar"
	else
		echo "[ERROR] Nemisys JAR not found!"
		exit 1
	fi
fi

LOOPS=0

while [ "$LOOPS" -eq 0 ] || [ "$DO_LOOP" == "true" ]; do
	if [ "$DO_LOOP" == "true" ]; then
		java -jar "$NEMISYS_FILE" $@
	else
		exec java -jar "$NEMISYS_FILE" $@
	fi
	((LOOPS++))
done
	