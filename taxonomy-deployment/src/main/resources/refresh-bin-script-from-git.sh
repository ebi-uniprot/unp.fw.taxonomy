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

CURRENT_PATH="$(pwd -P)"
TAXONOMY_REPO_DIR=$CURRENT_PATH/git-repository/unp.fw.taxonomy

echo "Pulling information from git at $GIT_BRANCH"
cd $TAXONOMY_REPO_DIR
git fetch
git checkout $GIT_BRANCH
git pull
cd $CURRENT_PATH

echo "Moving deploying scripts from $TAXONOMY_REPO_DIR/taxonomy-deployment/src/main/resources/bin to $CURRENT_PATH/bin"

rm -rf $CURRENT_PATH/bin/*.sh
cp $TAXONOMY_REPO_DIR/taxonomy-deployment/src/main/resources/bin/*.sh $CURRENT_PATH/bin

echo "Done"


