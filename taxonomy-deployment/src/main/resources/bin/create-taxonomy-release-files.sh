#!/bin/bash

# This script will execute steps below to build taxonomy service release
# 1- clean $SERVICE_TARGET_PATH directory
# 2- Get most update taxonomy source code from git
# 3- Build taxonomy-restful-service project libs
# 4- Update taxonomy libs.
# 5- Update taxonomy configuration file
# please Refer to http://redsymbol.net/articles/unofficial-bash-strict-mode/ for details.
set -euo pipefail
IFS=$'\n\t'

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

if [ $# -ne 2 ]; then
   echo "Missing parameter, You must pass as parameter release name, for example (2016_07) and environment == (stage,dev or prod)";
   echo "command example: deploy-taxonomy.sh 2016_07 dev";
   exit 1;
fi

ENVIRONMENT=$2
if [[ ("$ENVIRONMENT" != 'stage') && ("$ENVIRONMENT" != 'dev') && ("$ENVIRONMENT" != 'prod') ]]; then
   echo "second parameter environment must be (stage,dev or prod)";
   exit 1;
fi

RELEASE_NAME=$1
SERVICE_BIN_PATH="$(pwd -P)"
SERVICE_TARGET_PATH="$(readlink -f $SERVICE_BIN_PATH/../$TARGET_DIR)"
BUILD_RELEASE_DIR="$(readlink -m $SERVICE_BIN_PATH/../$RELEASE_DIR/$RELEASE_NAME)"


function executeBuildProcess(){
    echo "=================== 1- clean $SERVICE_TARGET_PATH directory ================================"
    rm -rf $SERVICE_TARGET_PATH
    mkdir -p $SERVICE_TARGET_PATH
    echo "$SERVICE_TARGET_PATH cleaned"
    echo "=================== 2- Get most update taxonomy source code from git ================================"
    $SERVICE_BIN_PATH/refresh-git-repository.sh $GIT_BRANCH
    echo "=================== 3- Build taxonomy-restful-service project libs =================================="
    $SERVICE_BIN_PATH/build-taxonomy-jars.sh
    echo "=================== 4- Update taxonomy libs ============================="

    # create directory releases/releaseName
    createDirectory $BUILD_RELEASE_DIR 0
    
    # create release lib dir
    RELEASE_LIB_DIR="$BUILD_RELEASE_DIR/$LIB_DIR"
    createDirectory "$RELEASE_LIB_DIR" 1
    
    echo "Moving files from $TARGET_DIR to $RELEASE_LIB_DIR dir"

    cp $SERVICE_TARGET_PATH/$LIB_DIR/*.jar $RELEASE_LIB_DIR
    rm -rf $SERVICE_TARGET_PATH 1

    # create logs dir
    RELEASE_LOG_DIR="$BUILD_RELEASE_DIR/$LOG_DIR"
    createDirectory "$RELEASE_LOG_DIR" 0

    # create conf dir
    RELEASE_CONF_DIR="$BUILD_RELEASE_DIR/$CONF_DIR"
    createDirectory "$RELEASE_CONF_DIR" 1

    echo "Moving config file from $SERVICE_BIN_PATH/config-$ENVIRONMENT.properties to $RELEASE_CONF_DIR/config.properties"
    cp "$SERVICE_BIN_PATH/config-$ENVIRONMENT.properties" "$RELEASE_CONF_DIR/config.properties"

    echo "Release build completed...."
}


executeBuildProcess;
