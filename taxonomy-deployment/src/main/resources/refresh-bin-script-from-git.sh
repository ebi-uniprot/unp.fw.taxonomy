#!/bin/bash

# This script will replace bin folder content with last version in GIT repository
# please Refer to http://redsymbol.net/articles/unofficial-bash-strict-mode/ for details.
set -euo pipefail
IFS=$'\n\t'

# ======= read the variables used by the control scripts =======================================
source "bin/environment.properties" || {
    echo "Please create a file called, environment.properties, containing the necessary environment variables."
    exit 1;
}

# ======= check the right user runs this script =======================================
if ! echo "$PERMITTED_USER" | grep "$USER" > /dev/null 2>&1; then
    echo "This service can only be run as user(s), '$PERMITTED_USER'";
    exit 1;
fi;

CURRENT_PATH="$(pwd -P)"
TAXONOMY_REPO_DIR=$CURRENT_PATH/git-repository/unp.fw.taxonomy

echo "Pulling information from git at $GIT_BRANCH"
cd $CURRENT_PATH/bin
$SERVICE_BIN_PATH/bin/refresh-git-repository.sh
cd $CURRENT_PATH

echo "Moving deploying scripts from $TAXONOMY_REPO_DIR/taxonomy-deployment/src/main/resources/bin to $CURRENT_PATH/bin"

rm -rf $CURRENT_PATH/bin/*
cp $TAXONOMY_REPO_DIR/taxonomy-deployment/src/main/resources/bin/* $CURRENT_PATH/bin

echo "Done"