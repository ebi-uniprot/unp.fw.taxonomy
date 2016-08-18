#!/bin/bash

# This script will execute steps below to deploy taxonomy service to production
# 1- Move Public link to current private in stage
# 2- Copy files to production server (in bin just copy, start, stop, environment.properties, config.properties)
# 3- Stop production taxonomy service
# 4- Start production taxonomy service
# 5- Move private link to new private release
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

if [ $# -ne 2 ]; then
   echo "Missing parameter, You must pass as parameter release name, for example (2016_07) and production environment == (prod or fallback)";
   echo "command example: deploy-taxonomy-to-production.sh 2016_07 fallback";
   exit 1;
fi

RELEASE_NAME=$1
ENVIRONMENT=$2
if [ "$ENVIRONMENT" == 'fallback' ]; then
    ENVIRONMENT_SERVER="$FALLBACK_SERVER"
else
    if [ "$ENVIRONMENT" == 'prod' ]; then
        ENVIRONMENT_SERVER="$PRODUCTION_SERVER"
    else
        echo "second parameter production environment must be (fallback or prod)";
        exit 1;
    fi
fi
SERVICE_BIN_PATH="$(pwd -P)"
BUILD_RELEASE_DIR="$(readlink -m $SERVICE_BIN_PATH/../$RELEASE_DIR/$RELEASE_NAME)"
RELEASE_LOG_DIR="$BUILD_RELEASE_DIR/$LOG_DIR"

if [ ! -d "$BUILD_RELEASE_DIR" ]; then

    echo "=================== Executing rsync command ==================="
    rsync -avh --copy-links --chmod=Du=rwx,Dg=rwx,Do=rx,Fu=rwx,Fg=rwx,Fo=rw $BUILD_RELEASE_DIR uni_adm@$ENVIRONMENT_SERVER:$TAXONOMY_PRODUCTION_PATH/$RELEASE_DIR/$RELEASE_NAME | tee -a "$RELEASE_LOG_DIR/taxonomy-rsync-$ENVIRONMENT.log"

    echo "rsync completed, now ssh to $ENVIRONMENT_SERVER"

    echo "=================== SSH to $ENVIRONMENT_SERVER ==================="
    ssh $ENVIRONMENT_SERVER

    echo "=================== Stopping Taxonomy Service ==================="
    $SERVICE_BIN_PATH/stop.sh

    cd $TAXONOMY_PRODUCTION_PATH

    echo "=================== Changing $CURRENT_RELEASE_LINK_NAME to $RELEASE_NAME ==================="
    unlink $CURRENT_RELEASE_LINK_NAME
    ln -s $BUILD_RELEASE_DIR $CURRENT_RELEASE_LINK_NAME

    echo "=================== Starting Taxonomy Service  ==================="

    $SERVICE_BIN_PATH/start.sh

else
    echo "ERROR: UNABLE TO FIND RELEASE DIRECTORY $BUILD_RELEASE_DIR"
fi