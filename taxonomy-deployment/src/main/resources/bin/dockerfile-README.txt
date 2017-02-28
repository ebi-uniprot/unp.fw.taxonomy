This repository contains the dockerfile to build the srosanof/taxonomy-service Dockerhub image.
In order to build the image manually you must download the latest Neo4j database file from Uniprot
as well as the oracle instant client rpm files. At build time, these files must be in the same
build directory as your dockerfile.

--------------------------------------------------------------------------------------------------
The UniProt Neo4j data file can be downloaded from:
http://ftp.ebi.ac.uk/pub/contrib/UniProtKB/taxonomy/neo4j-taxonomy-database.tar.gz

--------------------------------------------------------------------------------------------------
The oracle client driver can be downloaded from the Oracle technology network here:
http://www.oracle.com/technetwork/topics/linuxx86-64soft-092277.html

Please download the following clients from the Oracle Technology Network:
oracle-instantclient11.2-basic-11.2.0.4.0-1.x86_64.rpm
oracle-instantclient11.2-devel-11.2.0.4.0-1.x86_64.rpm
oracle-instantclient11.2-sqlplus-11.2.0.4.0-1.x86_64.rpm
--------------------------------------------------------------------------------------------------

Place all of the required files into you build directory:
    - dockerfile
    - neo4j-taxonomy-database.tar.gz
    - oracle-instantclient11.2-basic-11.2.0.4.0-1.x86_64.rpm
    - oracle-instantclient11.2-devel-11.2.0.4.0-1.x86_64.rpm
    - oracle-instantclient11.2-sqlplus-11.2.0.4.0-1.x86_64.rpm

Navigate to your build directory and build the image using the docker build command below:
docker build --tag taxonomy:v1 --force-rm .

Start the container using the docker run command below:
docker run -t -d -p 9090:9090 taxonomy:v1
