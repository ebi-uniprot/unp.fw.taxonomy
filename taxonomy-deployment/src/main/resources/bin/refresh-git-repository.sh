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
cd $TAXONOMY_REPO_DIR
$GIT fetch
if [ "$GIT_BRANCH" == "master" ]
then
   echo "checkout from master"
   $GIT checkout $GIT_BRANCH || {
        echo "master is already being used"
   }
else
   echo "checkout from branch $GIT_BRANCH"
   $GIT checkout -b $GIT_BRANCH || {
        echo "$GIT_BRANCH is already being used"
   }
fi
$GIT pull origin $GIT_BRANCH
cd $SERVICE_BIN_PATH