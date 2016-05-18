#!/bin/bash

# unoffical bash strict mode.
# please Refer to http://redsymbol.net/articles/unofficial-bash-strict-mode/ for details.
set -euo pipefail
IFS=$'\n\t'

SERVICES=${1:-}

if [ -z $SERVICES ]
  then
    echo "no service is specified"
    exit 1;
  else
    echo "starting services: $SERVICES"
    export RestServices=$SERVICES
fi


# check current user,  only uni_adm can runs it.
if [ $USER != "uni_adm" ] ; then
    echo "This service can only be run with user 'uni_adm'";
    exit 1;
fi;

SERVICE_PATH="/nfs/public/rw/uniprot/restful"
UPDATE_PATH="$SERVICE_PATH/update"
UPDATE_FILES="www services/bin services/lib"

#bring down the service, if it is already running.
$UPDATE_PATH/services/bin/stop.sh || true

#copy the files from the update directory
pushd .
cd $UPDATE_PATH
cp -rf www  $SERVICE_PATH
cp -rf services/bin  $SERVICE_PATH/services
cp -rf services/lib  $SERVICE_PATH/services
popd

#start the services.
$SERVICE_PATH/services/bin/start.sh $SERVICES









BACKUP_LIB_DIR="$SERVICE_PATH/lib/backups"
TIMESTAMPED_BACKUP_DIR="$(addTimeStamp $BACKUP_LIB_DIR/version)"

# ======= FUNCTIONS ======================================================================
# ======= take a string and add a time-stamp to it =======================================
function addTimeStamp() {
    local dirname="$(dirname $1)"
    local fname=$(basename "$1")
    local fext=""
    if echo "$fname" | grep '\.'; then
        fext=".${fname##*.}"
    fi
    local fname="${fname%.*}"
    echo "$dirname/$fname-$(date '+%s.%N')$fext"
}
# ======= keep only last 5 backups =======================================
pushd . > /dev/null
cd "$BACKUP_LIB_DIR"
# double check we're actually in the backup directory, before deleting anything!
if [ "$(basename $(pwd))" == "$(basename $BACKUP_LIB_DIR)" ]; then
    echo "Deleting oldest backups, but keeping newest 5";
    (ls -t|head -n 5;ls)|sort|uniq -u|xargs rm -rf;
fi
popd > /dev/null

# ======= backup old lib =======================================
if ls $LIB_DIR/*.jar > /dev/null 2>&1; then
    if [ ! -d "$TIMESTAMPED_BACKUP_DIR" ]; then
        mkdir $TIMESTAMPED_BACKUP_DIR
    fi
    for lib in "$(ls $LIB_DIR/*.jar)"; do
        ls "$lib" > /dev/null 2>&1 && mv "$lib" "$TIMESTAMPED_BACKUP_DIR"
    done
fi

# ======= move new artifact into lib =======================================
for new_lib in "$(ls $TEMP_DIR/*.jar)"; do
    ls "$new_lib" > /dev/null 2>&1 && mv "$new_lib" "$LIB_DIR"
done
rmdir $TEMP_DIR || {
    echo "Could not clean up temp directory: '$TEMP_DIR'. Please check it is empty."
}

# ======= add a readme describing the contents of the app directory =======================================
if [ -d "$SERVICE_BASE" ]; then
    mkdir -p $SERVICE_BASE
    echo "Contains live details (libraries, logs and PID files) for applications that " \
          "correspond to their configuration details in ../bin." > "$SERVICE_BASE/readme.txt"
fi


echo "done"