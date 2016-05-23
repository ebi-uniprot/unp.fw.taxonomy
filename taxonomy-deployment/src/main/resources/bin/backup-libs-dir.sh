#! /bin/bash

##=========================================================================================
# This script execute backup in current $LIB_DIR folder.
##=========================================================================================

set -euo pipefail
IFS=$'\n\t '

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
TAXONOMY_LIB_DIR="$(readlink -f $SERVICE_BIN_PATH/../$LIB_DIR)"
TAXONOMY_BACKUP_DIR="$(readlink -f $SERVICE_BIN_PATH/../$BACKUP_DIR)"

# creating a date to to use as postfix in the backup file
DATE=$(date +%Y_%m_%d_%H_%M)
# cleaning old backup files, keeping last 10 for lib folder jar files
echo "cleaning old backup files, keeping only 10"
ls -t $TAXONOMY_BACKUP_DIR/$LIB_DIR*| sed -e '1,10d' | xargs -d '\n' rm || {
        echo "There are less than 10 files to be cleaned from backup folder"
}

echo "Creating a tar file to backup libs"
tar -cvzf $TAXONOMY_BACKUP_DIR/$LIB_DIR-$DATE.tar.gz $TAXONOMY_LIB_DIR
echo "Backup saved at $TAXONOMY_BACKUP_DIR/$LIB_DIR-$DATE.tar.gz"