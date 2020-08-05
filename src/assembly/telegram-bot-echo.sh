#!/bin/bash
# This is the directory where bot jar, sh and service files are
DIR_BOT=/path/to/dir/bot
JAR=${DIR_BOT}/bot-echo.jar
DIR_JAVA=${JAVA_HOME}
EXE_JAVA=${JAVA_HOME}/jre/bin/java
## If you want external extra configuration, place it on '${DIR_BOT}/config',
# uncomment the following line and...
#CP="${DIR_BOT}/config:${JAR}"
# ...comment this line
CP=${JAR}
JAVA_OPTS=""

${EXE_JAVA} ${JAVA_OPTS} -cp ${CP} org.springframework.boot.loader.JarLauncher $*