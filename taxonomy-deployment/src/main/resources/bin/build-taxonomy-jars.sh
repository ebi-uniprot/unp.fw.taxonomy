#! /bin/bash

##=========================================================================================
# This script execute maven install for taxonomy and copy generated jars to update folder.
##=========================================================================================

set -euo pipefail
IFS=$'\n\t '

# ======= OVERRRIDE EXISTINGS ENVIRONMENT VARIABLES ======
export JAVA_HOME=/nfs/web-hx/uniprot/software/java/jdks/latest_1.8
# ======= CONSTANTS =======================================
RELEASE_REPO_TYPE="release"
RELEASE_REPO_NAME="uniprot-artifactory-release"
SNAPSHOT_REPO_TYPE="snapshot"
SNAPSHOT_REPO_NAME="uniprot-artifactory-snapshots"
PERMITTED_USER="uni_adm"
JAVA="$JAVA_HOME/bin/java"
MAVEN="$MAVEN_HOME/bin/mvn"

# ======= check the right user runs this script =======================================
if ! echo "$PERMITTED_USER" | grep "$USER" > /dev/null 2>&1; then
    echo "This service can only be run as user(s), '$PERMITTED_USER'";
    exit 1;
fi;


# ======= read the variables used by the control scripts =======================================
source "environment.properties" || {
    echo "Please create a file called, environment.properties, containing the necessary environment variables."
    exit 1;
}


# ======= validate variables used by the control scripts =======================================
[ ! -z "$TAXONOMY_ARTIFACT_GROUP" ] || [ ! -z "$TAXONOMY_VERSION" ] ||
[ ! -z "$TAXONOMY_SERVICE_ARTIFACT_ID" ] || [ ! -z "$TAXONOMY_RESTFUL_ARTIFACT_ID" ] ||
[ ! -z "$TAXONOMY_IMPORT_ARTIFACT_ID" ] || {
    echo "Please specify the environment variables TAXONOMY_ARTIFACT_GROUP, TAXONOMY_VERSION, \
    TAXONOMY_SERVICE_ARTIFACT_ID, TAXONOMY_RESTFUL_ARTIFACT_ID and TAXONOMY_IMPORT_ARTIFACT_ID, \
    in file, environment.properties. to get taxonomy-service-restful project"
    exit 1
}

SERVICE_CONF_PATH="$(pwd -P)"
SERVICE_UPDATE_BASE="$(readlink -f $SERVICE_CONF_PATH/../update)"
LIB_DIR="$SERVICE_UPDATE_BASE/lib"
TAXONOMY_REPO_DIR="$SERVICE_CONF_PATH/../git-repository/unp.fw.taxonomy"

if [ ! -d "$TAXONOMY_REPO_DIR" ]; then
    echo " please make sure you have cloned your git repository at "
fi

if [ ! -d "$LIB_DIR" ]; then
    mkdir -p $LIB_DIR
fi

# ======= install the REST artifacts before fetching them =======================================
cd $TAXONOMY_REPO_DIR
mvn -DskipTests install
cd $SERVICE_CONF_PATH

# ======= build the variables required for artifact retrieval =======================================
JAR_NAME="$TAXONOMY_RESTFUL_ARTIFACT_ID-$TAXONOMY_VERSION.jar"
GROUP_AS_URL="$(echo $TAXONOMY_ARTIFACT_GROUP | sed -e 's/\./\//g')"

REPO_TYPE="$RELEASE_REPO_TYPE"
REPO_NAME="$RELEASE_REPO_NAME"
if echo "$JAR_NAME" | grep 'SNAPSHOT' > /dev/null; then
    REPO_TYPE="$SNAPSHOT_REPO_TYPE"
    REPO_NAME="$SNAPSHOT_REPO_NAME"
fi
REPO_URL="http://wwwdev.ebi.ac.uk/uniprot/artifactory/$REPO_TYPE"


# ======= get the artifact from maven =======================================
TAXONOMY_RESTFUL_GAV="$TAXONOMY_ARTIFACT_GROUP:$TAXONOMY_RESTFUL_ARTIFACT_ID:$TAXONOMY_VERSION"
TAXONOMY_IMPORT_GAV="$TAXONOMY_ARTIFACT_GROUP:$TAXONOMY_IMPORT_ARTIFACT_ID:$TAXONOMY_VERSION"
echo "Fetching artifact: $TAXONOMY_RESTFUL_GAV"
# first get the pom, so that later we can get the jar
mvn -U org.apache.maven.plugins:maven-dependency-plugin:2.7:get \
        -DremoteRepositories="$REPO_URL" \
        -DrepoId="$REPO_NAME" \
        -Dartifact="$TAXONOMY_RESTFUL_GAV" \
        -Dtype=pom
# ... and get the jar
mvn -U org.apache.maven.plugins:maven-dependency-plugin:2.7:copy \
        -DremoteRepositories="$REPO_URL" \
        -DrepoId="$REPO_NAME" \
        -Dartifact="$TAXONOMY_RESTFUL_GAV" \
        -Dtype=jar \
        -DoutputDirectory="$LIB_DIR"

echo "Fetching artifact: $TAXONOMY_IMPORT_GAV"
mvn -U org.apache.maven.plugins:maven-dependency-plugin:2.7:get \
        -DremoteRepositories="$REPO_URL" \
        -DrepoId="$REPO_NAME" \
        -Dartifact="$TAXONOMY_IMPORT_GAV" \
        -Dtype=pom
# ... and get the jar
mvn -U org.apache.maven.plugins:maven-dependency-plugin:2.7:copy \
        -DremoteRepositories="$REPO_URL" \
        -DrepoId="$REPO_NAME" \
        -Dartifact="$TAXONOMY_IMPORT_GAV" \
        -Dtype=jar \
        -DoutputDirectory="$LIB_DIR"