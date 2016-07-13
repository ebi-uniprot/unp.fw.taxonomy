#! /bin/bash

##=========================================================================================
# This script will execute neo4j data import and will save generated data inside neo4j release folder
# received as parameter
##=========================================================================================

set -euo pipefail
IFS=$'\n\t '

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

if [ $# == 0 ]; then
   echo "You must pass as parameter taxonomy data release name, for example 2016_01";
else

    # ======= FOLDER VARIABLES =======================================
    SERVICE_BIN_PATH="$(pwd -P)"
    SERVICE_LIB_PATH="$(readlink -f $SERVICE_BIN_PATH/../$LIB_DIR)"
    NEO4J_DATABASE_PATH="$(readlink -f $SERVICE_BIN_PATH/../$TAXONOMY_DATABASE_DIR/$1)"

    export JAVA_HOME=$JAVA_PATH
    # ======= create neo4j database directory ==========================================
    if [ ! -d "$NEO4J_DATABASE_PATH" ]; then
        mkdir -p $NEO4J_DATABASE_PATH
    fi

    # ======= execute neo4j import Job ==========================================
    IMPORT_JAR_PATH="$SERVICE_LIB_PATH/$TAXONOMY_IMPORT_ARTIFACT_ID-$TAXONOMY_VERSION.jar"
    JAVA_OPTS="$TAXONOMY_IMPORT_JVM_MEM_MAX $TAXONOMY_IMPORT_JVM_MEM_MIN -Dneo4j.database.path=$NEO4J_DATABASE_PATH"
    echo "running java command $JAVA_OPTS -jar $IMPORT_JAR_PATH"
    $JAVA_HOME/bin/java $JAVA_OPTS -jar $IMPORT_JAR_PATH

    if [ "ls -A $NEO4J_DATABASE_PATH" ]
    then
      echo "SUCCESS: Import apparently was executed with success"
    else
      echo "ERROR: There is nothing in $NEO4J_DATABASE_PATH folder"
    fi
    echo "done!!"
fi