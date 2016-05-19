#!/bin/bash

# This script is responsible to stop Taxonomy Service
# please Refer to http://redsymbol.net/articles/unofficial-bash-strict-mode/ for details.
set -euo pipefail

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
PIDFILE="$SERVICE_BIN_PATH/run.pid"
LOGFILE="$SERVICE_BIN_PATH/log/log.txt"

if [ ! -r $PIDFILE ]
then
    echo "pid file doesn't exist. please check if Taxonomy service is running"
    exit 1;
fi

#read PIDFILE to check the hostname.
PIDSTR=`cat $PIDFILE`
read -ra PIDFILE_ARRAY <<<"$PIDSTR"

if [ ${PIDFILE_ARRAY[1]} != `hostname` ]
then
    echo "Current host: `hostname`, PID host: ${PIDFILE_ARRAY[1]} "
    echo "Need to run stop on the same host start runs."
    exit 1
fi

if [ -r $LOGFILE ]
then
    echo "backing up logfile"
    mv $LOGFILE $LOGFILE.`date +"%Y-%m-%d-%T"`
fi

echo "killing the job of pid: ${PIDFILE_ARRAY[0]}."
kill ${PIDFILE_ARRAY[0]} || {
    echo "processing killing failed, please check the process ID manually: ${PIDFILE_ARRAY[0]}"
}

rm $PIDFILE

echo "done"