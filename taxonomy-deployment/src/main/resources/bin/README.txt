#################### To deploy Taxonomy server execute the following steps: ########################################

- Login in ebi cluster machine
- become uni_adm
- ssh to stage server
- cd /net/isilonP/public/rw/homes/uni_adm/restful/taxonomy/bin (This bin folder contains all necessary scripts to deploy
     taxonomy service)
- execute the command "./stop.sh" (to make sure it is stopped, you can execute the command "ps -ef | grep taxonomy"
- We have two deploy options, one that include refresh neo4j database information, and another without refresh it
     - if you want to  deploy taxonomy service and do not refresh neo4j database data, execute the command "
     ./deploy-taxonomy-without-update-neo4j.sh". This scrip will do the following steps
            - clean $SERVICE_TARGET_PATH directory
            - Get most update taxonomy source code from git
            - Build taxonomy-restful-service project libs
            - Execute Neo4J taxonomy-import process
            - Stop taxonomy service
            - Create backup and update Neo4J and taxonomy libs
            - Start taxonomy service

      - if you want to  deploy taxonomy service and refresh neo4j database data, execute the command "
      ./deploy-taxonomy.sh" This scrip will do the following steps
            - clean $SERVICE_TARGET_PATH directory
            - Get most update taxonomy source code from git
            - Build taxonomy-restful-service project libs
            - Execute Neo4J taxonomy-import process
            - Stop taxonomy service
            - Create backup and update Neo4J and taxonomy libs
            - Start taxonomy service
- Check the log to make sure that taxonomy service started or open your browser and try it. http://ves-ebi-ca.ebi.ac
.uk:9090/proteins/api/taxonomy/id/329


###################  Important  ##########################################################################
Do not update any script here, please update in GIT repository and execute refresh-bin-script-from-git.sh
This way we keep all scripts updated in GIT.


#################### To create a new deployment environment execute the folowing steps: ##################

1- Login in ebi cluster machine
2- become uni_adm
3- ssh to stage server
4- inside your preferred path, create a folder named taxonomy "mkdir taxonomy"
5- inside just created taxonomy folder, create a new folder named "git-repository"
6- inside just created git-repository folder, execute the command "git clone https://user:password@scm.ebi.ac.uk/git/unp
.fw.taxonomy.git". (You should use Jenkins "uni_scm" user if it is a new VM Machine)
    6.1 (Optional) For test pourpose you can use a branch, and in this case you should use the following commands
        - cd unp.fw.taxonomy
        - git checkout -b "branch-name"
        - git pull origin "branch-name"
7- inside taxonomy folder copy deployment bin folder to it "cp -rf git-repository/unp.fw
.taxonomy/taxonomy-deployment/src/main/resources/* ."
8- execute command "chmod 755 *.sh", "cd bin", "chmod 755 *.*"
9- make sure your environment.properties and config.properties are correct

#################### Create Release Files ##################

1- inside bin folder execute the command "./deploy-taxonomy.sh"
2- done