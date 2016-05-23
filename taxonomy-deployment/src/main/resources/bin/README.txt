This bin folder contains all necessary scripts to deploy taxonomy service.

To deploy taxonomy you will need to exeute one of these two scripts described below:

1- execute deploy-taxonomy.sh
    This scrip will do the following steps
        - clean $SERVICE_TARGET_PATH directory
        - Get most update taxonomy source code from git
        - Build taxonomy-restful-service project libs
        - Execute Neo4J taxonomy-import process
        - Stop taxonomy service
        - Create backup and update Neo4J and taxonomy libs
        - Start taxonomy service

2 - execute deploy-taxonomy-without-update-neo4j.sh
    This script will do the following steps:
        - clean $SERVICE_TARGET_PATH directory
        - Get most update taxonomy source code from git
        - Build taxonomy-restful-service project libs
        - Stop taxonomy service
        - Create backup and update taxonomy libs
        - Start taxonomy service

Important: Do not update any script here, please update in GIT repository and execute refresh-bin-script-from-git.sh
This way we keep all scripts updated in GIT.