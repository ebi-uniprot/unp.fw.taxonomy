#!/bin/bash

# This script is used to start taxonomy restful service project
# please Refer to http://redsymbol.net/articles/unofficial-bash-strict-mode/ for details.
set -euo pipefail
IFS=$'\n\t'

# ======= read the variables used by the control scripts =======================================
source "environment.properties" || {
    echo "Please create a file called, environment.properties, containing the necessary environment variables."
    exit 1;
}

# ======= check the right user runs this script =======================================
if ! echo "$PERMITTED_USER" | grep "$USER" > /dev/null 2>&1; then
    echo "This service can only be run as user(s), '$PERMITTED_USER'";
    exit 1;
fi;

SERVICE_BIN_PATH="$(pwd -P)"
PIDFILE="$SERVICE_BIN_PATH/run.pid"
CONFIG_FILE="$SERVICE_BIN_PATH/config.properties"
LOG_DIR="$(readlink -f $SERVICE_BIN_PATH/../logs)"
LOGFILE="$LOG_DIR/log.txt"


JAR_NAME="$TAXONOMY_RESTFUL_ARTIFACT_ID-$TAXONOMY_VERSION.jar"
echo "Using the service jar: $JAR_NAME"
TAXONOMY_JAR_PATH="$(readlink -f $SERVICE_BIN_PATH/../lib/$JAR_NAME)"


if [ -e $PIDFILE ]
then
    echo "Service has already been started: `cat $PIDFILE`"
    exit 1
:fi

# this configuration file named "config.properties" will be used if it exist.
pushd  .

# running inside the service path as PWD.
cd $SERVICE_BIN_PATH

if [ -e $CONFIG_FILE ]
then
    echo "configuration will be overrided by $CONFIG_FILE";
else
    echo "No configuration file found";
fi

# just use nohup for now, may need to use something better like
# http://www.source-code.biz/snippets/java/7.htm
export JAVA_HOME=$JAVA_PATH

nohup $JAVA_HOME/bin/java -server -XX:+UseG1GC $TAXONOMY_RESTFUL_JVM_MEM_MAX $TAXONOMY_RESTFUL_JVM_MEM_MIN  \
-Dcom.sun.management.jmxremote  \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false  \
-Dcom.sun.management.jmxremote.port=$JXM_REMOTE_PORT \
-jar $TAXONOMY_JAR_PATH > $LOGFILE 2>&1 &

sleep 5

grep "ready to service requests" $LOGFILE || {
    echo "Taxonomy Service cannot be started. please check"
    exit 1
}

popd  .

echo "testing Taxonomy service"
curl http://127.0.0.1:9090/$CONTEXT_PATH/taxonomy/id/269 > /dev/null || {
    echo "Taxonomy service is still not available, please check."
    exit 1;
}

echo "$! `hostname`" > $PIDFILE
echo "Service is running with PID: `cat $PIDFILE`"