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