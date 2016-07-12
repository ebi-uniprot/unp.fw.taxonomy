###############################  Stress test was executed at 17-Jun-2016 ##############################################

 It was created a stress test script that (taxonomyStressTest.jmx) provided me the following indicatives:

  - We are able to process around 80 request in one second with using between 12GB and 16GB of memory and around 90%
  usage of a 4CPU machine.



###############################  Stress test was executed after these changes at 12-July-2016 #########################

- added /ancestor,
- added /lineage
- added /id/{id}/node
- added /name/{name}/node
- added pagination for name search results

 It was updated the stress test script that (taxonomyStressTest.jmx) to also execute new added features and it provided
me the following indicatives:

  - We still are able to process around 80 request in one second with using between 12GB and 16GB of memory and
  around 95% usage of a 4CPU machine.

#######################################################################################################################