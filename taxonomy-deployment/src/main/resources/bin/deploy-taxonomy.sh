#!/bin/bash

# This script will execute all steps necessary to deploy taxonomy taxonomy service
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
TAXONOMY_REPO_DIR="$SERVICE_BIN_PATH/../git-repository/unp.fw.taxonomy"
SERVICE_TARGET_PATH="$(readlink -f $SERVICE_BIN_PATH/../$TARGET_DIR)"
TAXONOMY_LIB_DIR="$(readlink -f $SERVICE_BIN_PATH/../$LIB_DIR)"
TAXONOMY_NEO4J_DIR="$(readlink -f $SERVICE_BIN_PATH/../$TAXONOMY_DATABASE_DIR)"


function executeBuildProcess(){
    echo "=================== 1- Get most update taxonomy source code from git ================================"
    cd $TAXONOMY_REPO_DIR
    git fetch
    git checkout $GIT_BRANCH
    git pull
    cd $SERVICE_BIN_PATH
    echo "=================== 2- Build taxonomy-restful-service project libs =================================="
    $SERVICE_BIN_PATH/build-taxonomy-jars.sh
    echo "=================== 3- Execute Neo4J taxonomy-import process ========================================"
    $SERVICE_BIN_PATH/index-neo4j-data.sh
    echo "=================== 4- Stop taxonomy service ========================================================"
    $SERVICE_BIN_PATH/stop.sh
    echo "=================== 5- Update Neo4J and taxonomy libs and create backup ============================="
    echo "Moving files from target to lib and data dir"

    cp $SERVICE_TARGET_PATH/$LIB_DIR/*.jar $TAXONOMY_LIB_DIR
    rm -rf $TAXONOMY_NEO4J_DIR
    mv $SERVICE_TARGET_PATH/$TAXONOMY_DATABASE_DIR  $TAXONOMY_NEO4J_DIR

    rm -rf $SERVICE_TARGET_PATH

    echo "=================== 6- Start taxonomy service ======================================================="
    $SERVICE_BIN_PATH/start.sh
    echo "Deployment completed...."
}


echo "This script will execute the following tasks:";
echo "1- Get most update taxonomy source code from git";
echo "2- Build taxonomy-restful-service project libs";
echo "3- Execute Neo4J taxonomy-import process";
echo "4- Stop taxonomy service";
echo "5- Update Neo4J and taxonomy libs and create backup";
echo "6- Start taxonomy service";
echo "Are you sure that you want to execute these steps above? Yes/No";
while true; do
    read yn;
    case $yn in
        Yes) executeBuildProcess;
             break;;
        No) echo "Deploy taxonomy canceled ";
             exit 1;;
        * ) echo "Please answer yes or no.";;
    esac
done
echo "done"