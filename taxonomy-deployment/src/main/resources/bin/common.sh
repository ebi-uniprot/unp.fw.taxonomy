#!/bin/bash

#This script contains common functions that is used in the build scripts

function createDirectory(){
    DIRECTORY=$1
    CLEAN=$2
    if [ ! -d "$DIRECTORY" ]; then
	echo "creating $DIRECTORY dir"
        mkdir -p $DIRECTORY
    else
	echo "$DIRECTORY dir already exist clean = $CLEAN";
	if [ $CLEAN == 1 ]; then
	    backupDirectory $DIRECTORY;	    		    
	    echo "cleaning $DIRECTORY content...";
	    rm -rf "$DIRECTORY/";
    	    mkdir -p $DIRECTORY;
	fi;
    fi;
}

function backupDirectory(){
    DIRECTORY=$1
    DIRECTORY_NAME="$(basename $DIRECTORY)"
    BACKUP_DIR="$(readlink -m $DIRECTORY/../backup)"
    # creating a date to to use as postfix in the backup file
    DATE=$(date +%Y_%m_%d_%H_%M)
    
    if [ ! -d "$BACKUP_DIR" ]; then
	echo "creating $BACKUP_DIR dir"
        mkdir -p $BACKUP_DIR
    fi

    tar -cvzf $BACKUP_DIR/$DIRECTORY_NAME-$DATE.tar.gz $DIRECTORY
    echo "Backup saved at $BACKUP_DIR/$DIRECTORY_NAME-$DATE.tar.gz"

    #cleaning old backup files, keeping last 5 for lib folder jar files
    echo "cleaning old backup files, keeping only 5"
    ls -t $BACKUP_DIR/$DIRECTORY_NAME*| sed -e '1,5d' | xargs -d '\n' rm || {
        echo "There are less than 5 files to be cleaned from backup folder"
    }
}
