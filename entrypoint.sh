#!/bin/sh

echo copying jar files &&
    mkdir -p /jars &&
    cp /project/build/libs/*.jar /jars &&
    echo jar files copied successfully
    #tail -f /dev/null
