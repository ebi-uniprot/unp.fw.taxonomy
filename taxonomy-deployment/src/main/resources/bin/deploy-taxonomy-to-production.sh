#!/bin/bash

# This script will execute steps below to deploy taxonomy service to production
# 1- Executing rsync command to send release files to production
# 2- SSH to environment server
# 3- Stop production taxonomy service
# 4- Change curent release symbolic link to new release
# 5- Start production taxonomy service
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
   echo "Missing parameter, You must pass as parameter release name, for example (2016_07_PROD) and production environment == (prod or fallback)";
   echo "command example: deploy-taxonomy-to-production.sh 2016_07_PROD fallback";
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

if [ -d "$BUILD_RELEASE_DIR" ]; then
    if [ -d "$BUILD_RELEASE_DIR/$LIB_DIR" ]; then
        if [ -d "$BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR" ]; then
            echo "=================== 1 Executing rsync command ==================="
            rsync -avh --copy-links --chmod=Du=rwx,Dg=rwx,Do=rx,Fu=rwx,Fg=rwx,Fo=rw $BUILD_RELEASE_DIR uni_adm@$ENVIRONMENT_SERVER:$TAXONOMY_PRODUCTION_PATH/$RELEASE_DIR | tee -a "$RELEASE_LOG_DIR/taxonomy-rsync-$ENVIRONMENT.log"

            echo "rsync completed, now ssh to $ENVIRONMENT_SERVER"

            echo "=================== 2 SSH to $ENVIRONMENT_SERVER ==================="
            ssh $ENVIRONMENT_SERVER bash -c "'

                echo "=================== 3 Stopping Taxonomy Service ==================="
                cd $TAXONOMY_PRODUCTION_PATH/bin

                $TAXONOMY_PRODUCTION_PATH/bin/stop.sh

                cd $TAXONOMY_PRODUCTION_PATH

                echo "=================== 4 Changing $CURRENT_RELEASE_LINK_NAME to $RELEASE_NAME ==================="
                unlink $CURRENT_RELEASE_LINK_NAME
                ln -s $TAXONOMY_PRODUCTION_PATH/$RELEASE_DIR/$RELEASE_NAME $CURRENT_RELEASE_LINK_NAME

                echo "=================== 5 Starting Taxonomy Service  ==================="

                cd $TAXONOMY_PRODUCTION_PATH/bin
                $TAXONOMY_PRODUCTION_PATH/bin/start.sh
            '"
        else
            echo "ERROR: UNABLE TO FIND RELEASE DATABASE DIRECTORY $BUILD_RELEASE_DIR/$TAXONOMY_DATABASE_DIR"
        fi
    else
        echo "ERROR: UNABLE TO FIND RELEASE LIBRARY DIRECTORY $BUILD_RELEASE_DIR/$LIB_DIR"
    fi
else
    echo "ERROR: UNABLE TO FIND RELEASE DIRECTORY $BUILD_RELEASE_DIR"
fi