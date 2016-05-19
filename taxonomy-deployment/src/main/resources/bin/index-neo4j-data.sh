#! /bin/bash

##=========================================================================================
# This script will execute neo4j data import
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

# ======= FOLDER VARIABLES =======================================
SERVICE_BIN_PATH="$(pwd -P)"
SERVICE_TARGET_PATH="$(readlink -f $SERVICE_BIN_PATH/../$TARGET_DIR)"
NEO4J_DATABASE_PATH="$SERVICE_TARGET_PATH/$TAXONOMY_DATABASE_DIR"

# ======= create neo4j database directory ==========================================
if [ ! -d "$NEO4J_DATABASE_PATH" ]; then
    mkdir -p $NEO4J_DATABASE_PATH
fi

# ======= execute neo4j import Job ==========================================
IMPORT_JAR_PATH="$UPDATE_BASE/lib/$TAXONOMY_IMPORT_ARTIFACT_ID-$TAXONOMY_VERSION.jar"
JAVA_OPTS="$TAXONOMY_IMPORT_JVM_MEM_MAX $TAXONOMY_IMPORT_JVM_MEM_MIN -Dneo4j.database.path=$NEO4J_DATABASE_PATH"
echo "running java command $JAVA_OPTS -jar $IMPORT_JAR_PATH"
$JAVA $JAVA_OPTS -jar $IMPORT_JAR_PATH

if [ "ls -A $NEO4J_DATABASE_PATH" ]
then
  echo "SUCCESS: Import apparently was executed with success"
else
  echo "ERROR: There is nothing in $NEO4J_DATABASE_PATH folder"
fi
echo "done!!"



