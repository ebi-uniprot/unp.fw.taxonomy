#!/bin/bash

# This script will execute all necessary steps to deploy taxonomy taxonomy service
# 1- clean $SERVICE_TARGET_PATH directory
# 2- Get most update taxonomy source code from git
# 3- Build taxonomy-restful-service project libs
# 4- Execute Neo4J taxonomy-import process
# 5- Stop taxonomy service
# 6- Update Neo4J and taxonomy libs and create backup
# 7- Start taxonomy service
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
SERVICE_TARGET_PATH="$(readlink -f $SERVICE_BIN_PATH/../$TARGET_DIR)"
TAXONOMY_LIB_DIR="$(readlink -f $SERVICE_BIN_PATH/../$LIB_DIR)"
TAXONOMY_NEO4J_DIR="$(readlink -f $SERVICE_BIN_PATH/../$TAXONOMY_DATABASE_DIR)"
TAXONOMY_BACKUP_DIR="$(readlink -f $SERVICE_BIN_PATH/../$TAXONOMY_DATABASE_DIR)"


function executeBuildProcess(){
    echo "=================== 1- clean $SERVICE_TARGET_PATH directory ================================"
    rm -rf $SERVICE_TARGET_PATH
    mkdir -p $SERVICE_TARGET_PATH
    echo "$SERVICE_TARGET_PATH cleaned"
    echo "=================== 2- Get most update taxonomy source code from git ================================"
    $SERVICE_BIN_PATH/refresh-git-repository.sh
    echo "=================== 3- Build taxonomy-restful-service project libs =================================="
    $SERVICE_BIN_PATH/build-taxonomy-jars.sh
    echo "=================== 4- Execute Neo4J taxonomy-import process ========================================"
    $SERVICE_BIN_PATH/index-neo4j-data.sh
    echo "=================== 5- Stop taxonomy service ========================================================"
    $SERVICE_BIN_PATH/stop.sh
    echo "=================== 6- Update Neo4J and taxonomy libs and create backup ============================="

    echo "Creating backups"
    $SERVICE_BIN_PATH/backup-libs-dir.sh

    $SERVICE_BIN_PATH/backup-neo4j-taxonomy-database.sh

    echo "Moving files from $TARGET_DIR to $LIB_DIR and $TAXONOMY_NEO4J_DIR dir"

    cp $SERVICE_TARGET_PATH/$LIB_DIR/*.jar $TAXONOMY_LIB_DIR
    rm -rf $TAXONOMY_NEO4J_DIR
    mv $SERVICE_TARGET_PATH/$TAXONOMY_DATABASE_DIR  $TAXONOMY_NEO4J_DIR
    chmod 764 -R $TAXONOMY_NEO4J_DIR

    rm -rf $SERVICE_TARGET_PATH

    echo "=================== 7- Start taxonomy service ======================================================="
    $SERVICE_BIN_PATH/start.sh
    echo "Deployment completed...."
}


echo "This script will execute the following tasks:";
echo "1- clean $SERVICE_TARGET_PATH directory ";
echo "2- Get most update taxonomy source code from git";
echo "3- Build taxonomy-restful-service project libs";
echo "4- Execute Neo4J taxonomy-import process";
echo "5- Stop taxonomy service";
echo "6- Update Neo4J and taxonomy libs and create backup";
echo "7- Start taxonomy service";
echo "Are you sure that you want to execute these steps above? Yes/No";
while true; do
    read yn;
    case $yn in
        Yes) executeBuildProcess;
             break;;
        No) echo "Deploy taxonomy canceled ";
             exit 1;;
        * ) echo "Please answer Yes or No.";;
    esac
done
echo "done"