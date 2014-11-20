<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" media-type="text/turtle" encoding="UTF-8"/>
  <xsl:template match="doc">
@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
    
&lt;http://example.org/res1&gt; rdfs:label "<xsl:value-of select="."/>" .
    
  </xsl:template>
</xsl:stylesheet>
