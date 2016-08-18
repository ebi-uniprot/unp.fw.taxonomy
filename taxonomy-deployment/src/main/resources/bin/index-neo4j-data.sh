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

source common.sh

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
    SERVICE_BIN_PATH="$(pwd -P)"
    BUILD_RELEASE_DIR="$(readlink -m $SERVICE_BIN_PATH/../$RELEASE_DIR/$1)"

    # create directory releases/releaseName
    createDirectory $BUILD_RELEASE_DIR 0  

    # create logs dir
    RELEASE_LOG_DIR="$BUILD_RELEASE_DIR/$LOG_DIR"
    createDirectory $RELEASE_LOG_DIR 0

    RELEASE_LIB_PATH="$BUILD_RELEASE_DIR/$LIB_DIR)"
    NEO4J_DATABASE_PATH="$BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR"

    # ======= create neo4j database directory ==========================================
    createDirectory $NEO4J_DATABASE_PATH 1

    if [ ! -d "$RELEASE_LIB_PATH" ]; then
	RELEASE_LIB_PATH="$(readlink -f $SERVICE_BIN_PATH/../$CURRENT_RELEASE_LINK_NAME/$LIB_DIR)"
    fi    
    echo "executing taxonomy-import at $RELEASE_LIB_PATH"
    # ======= execute neo4j import Job ==========================================
    IMPORT_JAR_PATH="$RELEASE_LIB_PATH/$TAXONOMY_IMPORT_ARTIFACT_ID-$TAXONOMY_VERSION.jar"
    JAVA_OPTS="$TAXONOMY_IMPORT_JVM_MEM_MAX $TAXONOMY_IMPORT_JVM_MEM_MIN -Dneo4j.database.path=$NEO4J_DATABASE_PATH"
    echo "running java command $JAVA_OPTS -jar $IMPORT_JAR_PATH"
    $JAVA_HOME/bin/java $JAVA_OPTS -jar $IMPORT_JAR_PATH | tee -a "$RELEASE_LOG_DIR/taxonomy-import.log"

    if [ "ls -A $NEO4J_DATABASE_PATH" ]
    then
      echo "Taxonomy data created at $NEO4J_DATABASE_PATH"
    else
      echo "ERROR: There is nothing in $NEO4J_DATABASE_PATH folder"
    fi
    echo "done!!"
fi