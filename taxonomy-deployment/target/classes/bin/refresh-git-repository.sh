#!/bin/bash

# This script will update/refresh current git repository based on
# parameter that is received by user
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
if [ $# == 0 ]; then
   echo "You must pass as parameter git branch name, for example, master";
   exit 1;
fi

SERVICE_BIN_PATH="$(pwd -P)"
TAXONOMY_REPO_DIR="$SERVICE_BIN_PATH/../git-repository/unp.fw.taxonomy"
cd $TAXONOMY_REPO_DIR
$GIT fetch
if [ "$1" == "master" ]
then
   echo "checkout from master"
   $GIT checkout $1 || {
        echo "master is already being used"
   }
else
   echo "checkout from branch $1"
   $GIT checkout -b $1 || {
        echo "$1 is already being used"
   }
fi
$GIT pull origin $1
cd $SERVICE_BIN_PATH