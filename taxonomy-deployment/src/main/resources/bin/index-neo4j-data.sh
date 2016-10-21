#! /bin/bash

##=========================================================================================
# This script will execute neo4j data import and will save generated data inside neo4j release folder
# received as parameter
##=========================================================================================

set -euo pipefail
IFS=$'\n\t '

SERVICE_BIN_PATH="$(dirname `which $0`)"
# ======= read the variables used by the control scripts =======================================
source "$SERVICE_BIN_PATH/environment.properties" || {
    echo "Please create a file called, environment.properties, containing the necessary environment variables."
    exit 1;
}

source "$MONITOR_TOOLS_DIR/bin/consul-service-register.sh" || {
    echo "Make sure your monitor environment is okay at $MONITOR_TOOLS_DIR/bin/consul-service-register.sh"
    exit 1;
}

source "$MONITOR_TOOLS_DIR/bin/prometheus-push-job-metrics.sh" || {
    echo "Unable to load script, Make sure your monitor environment is okay at $MONITOR_TOOLS_DIR/bin/prometheus-push-job-metrics.sh"
    exit 1;
}

source "$SERVICE_BIN_PATH/common.sh"

# ======= check the right user runs this script =======================================
if ! echo "$PERMITTED_USER" | grep "$USER" > /dev/null 2>&1; then
    echo "This service can only be run as user(s), '$PERMITTED_USER'";
    exit 1;
fi;

if [ $# == 0 ]; then
   echo "You must pass as parameter taxonomy data release name, for example 2016_01";
else
    export JAVA_HOME=$JAVA_PATH

    # ======= FOLDER VARIABLES =======================================
    BUILD_RELEASE_DIR="$(readlink -m $SERVICE_BIN_PATH/../$RELEASE_DIR/$1)"

    # create directory releases/releaseName
    createDirectory $BUILD_RELEASE_DIR 0  

    # create logs dir
    RELEASE_LOG_DIR="$BUILD_RELEASE_DIR/$LOG_DIR"
    createDirectory $RELEASE_LOG_DIR 0

    RELEASE_LIB_PATH="$BUILD_RELEASE_DIR/$LIB_DIR)"
    NEO4J_DATABASE_PATH="$BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR"
    PUSH_METRIC_DIR="$(readlink -m $SERVICE_BIN_PATH/../$JOB_METRIC_DIR)"

    # ======= create neo4j database directory ==========================================
    createDirectory $NEO4J_DATABASE_PATH 1
    echo "$RELEASE_LIB_PATH"
    if [ ! -d "$RELEASE_LIB_PATH" ]; then
            RELEASE_LIB_PATH="$(readlink -f $SERVICE_BIN_PATH/../$CURRENT_RELEASE_LINK_NAME/$LIB_DIR)"
    fi
    echo "executing taxonomy-import at $RELEASE_LIB_PATH"
    # replace configuration file with correct neo4J database path
    sed -i "/neo4j.database.path/c\neo4j.database.path=$NEO4J_DATABASE_PATH" application.properties

    # register taxonomy-import-job in consul
    JOB_NAME="taxonomy-import"
    declare CONSUL_SERVICE=($(register_consul_service $JOB_NAME));
    if [ "${CONSUL_SERVICE[0]}" = true ]; then
        echo "registered $JOB_NAME in consul"
    else
        echo "ERROR: UNABLE TO REGISTER $JOB_NAME in CONSUL. RESPONSE: $CONSUL_SERVICE"
    fi;

    # ======= execute neo4j import Job ==========================================
    IMPORT_JAR_PATH="$RELEASE_LIB_PATH/$TAXONOMY_IMPORT_ARTIFACT_ID-$TAXONOMY_VERSION.jar"
    echo "check logs at $RELEASE_LOG_DIR/taxonomy-import.log"
$JAVA_HOME/bin/java -Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.port=9093 \
-javaagent:$MONITOR_TOOLS_DIR/dist/jmx_prometheus_javaagent-0.7-SNAPSHOT.jar=$TAXONOMY_IMPORT_JXM_REMOTE_PORT:$SERVICE_BIN_PATH/prometheus-jmx-config.yaml \
-jar $IMPORT_JAR_PATH --spring.config.location=file:$SERVICE_BIN_PATH/application.properties > "$RELEASE_LOG_DIR/taxonomy-import.log"

    # send push-gateway metrics 
    METRIC_FILE="$PUSH_METRIC_DIR/metrics.txt"
    echo "pushing metrics for metric job $JOB_NAME with metrics $METRIC_FILE"
    push-job-metrics $METRIC_FILE $JOB_NAME
    mv $METRIC_FILE $METRIC_FILE-processed-$(date +%Y_%m_%d_%H_%M)

    # deregister taxonomy-import-job in consul
    if [ "${CONSUL_SERVICE[0]}" = true ]; then
        deregister_consul_service "taxonomy_import"
        echo "deregistered $JOB_NAME in consul"
    else
        echo "ERROR: UNABLE TO DEREGISTER $JOB_NAME in CONSUL"
    fi;

    if [ "ls -A $NEO4J_DATABASE_PATH" ]
    then
      echo "Taxonomy data created at $NEO4J_DATABASE_PATH"
    else
      echo "ERROR: There is nothing in $NEO4J_DATABASE_PATH folder"
    fi
    echo "done!!"
fi