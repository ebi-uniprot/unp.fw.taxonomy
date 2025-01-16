###################  Important  ##########################################################################
Do not update any script here, please update in GIT repository and execute refresh-bin-script-from-git.sh
This way we keep all scripts updated in GIT.


#################### To create a new deployment environment execute the folowing steps: ##################

1- Login in ebi cluster machine
2- become uni_adm
3- ssh to stage server
4- inside your preferred path, create a folder named taxonomy "mkdir taxonomy"
5- inside just created taxonomy folder, create a new folder named "mkdir git-repository"
6- inside just created git-repository folder, execute the command "git clone https://user:password@scm.ebi.ac.uk/git/unp
.fw.taxonomy.git". (You should use Jenkins "uni_scm" user if it is a new VM Machine)
    6.1 (Optional) For test purpose you can use a branch, and in this case you should use the following commands
        - cd unp.fw.taxonomy
        - git checkout -b "branch-name"
        - git pull origin "branch-name"
7- inside taxonomy folder copy deployment bin folder to it "cp -rf git-repository/unp.fw
.taxonomy/taxonomy-deployment/src/main/resources/* ."
8- execute command "chmod 755 *.sh", "cd bin", "chmod 755 *.*"
9- make sure your environment.properties and config.properties are correct

#################### Create Release Files ##################
1- Login in ebi cluster machine
2- become uni_adm
3- ssh to stage server
4- inside taxonomy bin folder execute the command "./create-taxonomy-release-files.sh" passing release name and
environment as parameter for example: ./create-taxonomy-release-files.sh 2016_01_PROD stage
5- Make sure that inside taxonomy folder there is currentrelease link. If there is not, execute the command:
        ln -s releases/2016_01_PROD currentrelease
6- Make sure that inside taxonomy bin folder there is config.properties link. If there is not, execute the command:
        ln -s ../currentrelease/conf/config.properties config.properties
7- IMPORTANT: This step will be always called by Production team (indexed neo4j data must be sync with uniprot
taxonomy):
        inside taxonomy bin folder execute the command ./index-neo4j-data.sh 2016_01_PROD
8- done

#################### Deploy Release Files to production##################
Production environment pre requirement:
    1- make sure that production has the following structure
    taxonomy
       bin
         config.properties -> ../currentrelease/2016_01_PROD/conf/config.properties (example)
         environment.properties
         start.sh
         stop.sh
       currentrelease -> releases

1- Login in ebi cluster machine
2- become uni_adm
3- ssh to stage server
4- Make sure that Release Files were created properly and it has (conf/config.properties, lib/, logs/, and
neo4j-taxonomy-database/) directories with proper files inside.
5- inside taxonomy bin folder execute the command ./deploy-taxonomy-to-production.sh 2016_01_PROD fallback
6- Make sure that fallback server is running properly
7- inside taxonomy bin folder execute the command ./deploy-taxonomy-to-production.sh 2016_01_PROD prod
8- Make sure that prod server is running properly
9. Done