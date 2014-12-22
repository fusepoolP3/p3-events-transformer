Fusepool P3 XSLT Transformer
============================

The transformer takes as input xml data and the url of a xslt template and transforms the data in RDF.
The xml data set must be sent to the transformer via HTTP POST with the url of the XSLT file as parameter. 
The transformer will return the result RDF data to the client. This transformer is used within [FP-209](https://fusepool.atlassian.net/browse/FP-209) to transform XML data about events into RDF.

[![Build Status](https://travis-ci.org/fusepoolP3/p3-xslt-transformer.svg)](https://travis-ci.org/fusepoolP3/p3-xslt-transformer)

Compile the Maven project running the command

    mvn install

Start the component with 

    mvn exec:java

Some xslt files are bundled with the source code to run test in src/test/resources. The corresponding xslt transformations
are available in the same folder and in the project repository on Github. The xslt url must be sent as a query parameter with 
'xslt' as the parameter name. As an example we use a foo.xml file with just an element

    <?xml version="1.0"?>
    <doc>Monte Bondone Hotel</doc>

and an xslt file, foo.xsl, that transforms the xml into RDF/TURTLE

    <?xml version="1.0"?>
      <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
      <xsl:output method="text" media-type="text/turtle" encoding="UTF-8"/>
      
      <xsl:template match="doc">
        @prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
        @prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
    
        &lt;http://example.org/res1&gt; rdfs:label "<xsl:value-of select="."/>" .
    
      </xsl:template>
      
    </xsl:stylesheet>

The output format in the xslt should always be specified as in the example. A test can be done with curl. If the xslt file is in a local folder (e.g. /home/user/ ) run the command 

    curl -i -X POST -H "Content-Type: application/xml" -T foo.xml http://localhost:7100?xslt=file:///home/user/foo.xsl

If the xslt can be put in a web server, just use its http url in place of the file url. The xslt transformation is assumed to be synchronous by default and the result is sent to the client as soon as the transformation is done. In the example the result will be 

    @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
    @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
    
    <http://example.org/res1> rdfs:label "Monte Bondone Hotel" .

