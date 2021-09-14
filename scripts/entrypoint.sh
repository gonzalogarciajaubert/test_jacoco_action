#!/bin/sh
eval "exec java $JAVA_OPTS -cp /app/resources:/app/classes:/app/libs/* $1"
