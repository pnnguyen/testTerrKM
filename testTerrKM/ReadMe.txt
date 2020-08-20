#PNN 20190608: tested on Windows 10
1. This simple 'testTerr' code illustrates a working example of using a knowledge model in RDF/OWL.
2. The knowledge model 'terroristExample.owl' contains 'subClassOf' and 'type' relationships in a taxonomy.
3. The Java classes ingest the RDF/OWL model, load it into memory as a Jena 2.11 (pre-Apache) ontology model, and execute.
   The 2 main execution use-cases are:
   a. Default: without any input parameters, the code searches for root classes in the taxonomy and lists all direct child classes.
   b. One input parameter: when a fully-qualified class name is input, the code lists all outbound triples for that class.
4. The 'TaxoNav.txt' file contains specific Java commands and optional input parameters tor execute above 2 use-cases.
5. To run, edit 'TaxoNav.txt' to uncomment the appropriate lines and change file name to 'TaxoNav.bat'.
6. Edit the 'TaxoNav.properties' file to point to your deployed RDF/OWL model.
