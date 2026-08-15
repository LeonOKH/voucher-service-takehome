#!/bin/sh

DIRNAME=`dirname "$0"`
APP_HOME=`cd "$DIRNAME" && pwd`
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
