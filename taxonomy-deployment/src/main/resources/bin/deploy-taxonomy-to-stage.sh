#!/bin/bash

# This script will execute steps below to deploy taxonomy service to production
# 1- Verify if release files were created
# 2- Stop stage taxonomy service
# 3- Change curent release symbolic link to new release
# 4- Start stage taxonomy service
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

if [ $# -ne 1 ]; then
   echo "Missing parameter, You must pass as parameter release name, for example (2017_02)";
   echo "command example: deploy-taxonomy-to-stage.sh 2017_02";
   exit 1;
fi

RELEASE_NAME=$1
SERVICE_BIN_PATH="$(dirname `which $0`)"
cd $SERVICE_BIN_PATH
BUILD_RELEASE_DIR="$(readlink -m $SERVICE_BIN_PATH/../$RELEASE_DIR/$RELEASE_NAME)"
echo "=================== 1- Verify if release files were created at $BUILD_RELEASE_DIR ==================="
if [ ! -d "$BUILD_RELEASE_DIR" ]; then
   echo "$BUILD_RELEASE_DIR does not exist";
   exit 1;
fi

if [ ! -d "$BUILD_RELEASE_DIR/$CONF_DIR" ]; then
   echo "$BUILD_RELEASE_DIR/$CONF_DIR does not exist";
   exit 1;
fi
if [ ! -n "$(ls -A $BUILD_RELEASE_DIR/$CONF_DIR/*.properties)" ]; then
   echo "$BUILD_RELEASE_DIR/$CONF_DIR is empty";
   exit 1;
fi

if [ ! -d "$BUILD_RELEASE_DIR/$LIB_DIR" ]; then
   echo "$BUILD_RELEASE_DIR/$LIB_DIR does not exist";
   exit 1;
fi
if [ ! -n "$(ls -A $BUILD_RELEASE_DIR/$LIB_DIR/*.jar)" ]; then
   echo "$BUILD_RELEASE_DIR/$LIB_DIR is empty";
   exit 1;
fi

if [ ! -d "$BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR" ]; then
   echo "$BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR does not exist";
   exit 1;
fi
if [ ! -n "$(ls -A $BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR/neostore.*)" ]; then
   echo "$BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR is empty";
   exit 1;
fi

echo "=================== 2- Stop stage taxonomy service ==================="
./stop.sh
echo "=================== 3 Changing $CURRENT_RELEASE_LINK_NAME to $RELEASE_NAME ==================="
unlink ../$CURRENT_RELEASE_LINK_NAME
ln -s $BUILD_RELEASE_DIR ../$CURRENT_RELEASE_LINK_NAME

echo "=================== 4- Start stage taxonomy service  ==================="
./start.sh

echo "done"
