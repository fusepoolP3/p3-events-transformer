Fusepool P3 XSLT Transformer
============================

The transformer takes as input xml data and the url of a xslt template and transforms the data in RDF.
The xml data set must be sent to the transformer via HTTP POST with the url of the XSLT file as parameter. 
The transformer will return the result RDF data to the client.

Compile the Maven project running the command

    mvn install

Start the component with 

    mvn exec:java

Some xslt files are bundled with the source code to run test in src/test/resources. The corresponding xslt transformations
are available in the same folder and in the project repository on Github. The xslt url must be sent as a query parameter with 
'xslt' as the parameter name. From that folder use the following command to send an
 xml file to the transformer once it is started

    curl -i -X POST -H "Content-Type: application/xml" -T foo.xml http://localhost:7100?xslt=https://raw.githubusercontent.com/fusepoolP3/p3-xslt-transformer/master/src/test/resources/eu/fusepool/p3/xslt/transformer/test/foo.xsl

The command starts an asynchronous task and the server sends the location header where the result will be made available
to the client

    HTTP/1.1 100 Continue
    
    HTTP/1.1 202 Accepted
    Date: Tue, 18 Nov 2014 09:26:32 GMT
    Location: /job/19714db2-221e-48e3-813b-74d5b001acd1
    Transfer-Encoding: chunked
    Server: Jetty(9.2.0.RC0)

In order to retrieve the result the client has to send an HTTP GET  request to the server 

    curl http://localhost:7100/job/19714db2-221e-48e3-813b-74d5b001acd1 
