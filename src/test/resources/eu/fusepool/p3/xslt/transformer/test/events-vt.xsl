<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:template match="/">
# RDF data transformed from the data set available at the url
# http://www.visittrentino.it/media/eventi/eventi.xml
# xslt version 1.0.0-20141114

@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
@prefix geo: &lt;http://www.w3.org/2003/01/geo/wgs84_pos#&gt; .
@prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .
@prefix schema: &lt;http://schema.org/&gt;
    <xsl:apply-templates select="events"/>
  </xsl:template>
 
  <xsl:template match="events">

       <xsl:for-each select="event">         
&lt;urn:event:uuid:<xsl:value-of select="alfId"/>&gt; rdf:type schema:Event ;
            rdfs:label "<xsl:value-of select="name/value[@xml:lang='it']"/>"@it ; 
             <xsl:if test="name/value[@xml:lang='en'] != ''">
            rdfs:label "<xsl:value-of select="name/value[@xml:lang='en']"/>"@en ;
            </xsl:if>
            schema:description "<xsl:value-of select="shortDescription/value[@xml:lang='it']"/>"@it ; 
            schema:category "<xsl:value-of select="searchCategories/searchCategory/value[@xml:lang='it']"/>"@it ;
            schema:category "<xsl:value-of select="searchCategories/searchCategory/value[@xml:lang='en']"/>"@en ;
            schema:startDate "<xsl:value-of select="startDate"/>" ;
            schema:endDate "<xsl:value-of select="endDate"/>" ;
            schema:location &lt;urn:location:uuid:<xsl:value-of select="alfId"/>&gt; ;
            schema:organizer &lt;urn:organization:uuid:<xsl:value-of select="alfId"/>&gt; .
&lt;urn:location:uuid:<xsl:value-of select="alfId"/>&gt; rdf:type schema:PostalAddress ;
            rdfs:label "<xsl:value-of select="eventLocation/value"/>" ;
            schema:addressLocality "<xsl:value-of select="location/place/value"/>" ;
            <xsl:if test="location/country/value != ''">
            schema:addressCountry "<xsl:value-of select="location/country/value"/>" ;
            </xsl:if>
            <xsl:if test="location/street/value != ''">
            schema:streetAddress "<xsl:value-of select="location/street/value"/> <xsl:value-of select="location/number"/>" ;
            </xsl:if>
            <xsl:if test="location/zipCode != ''">
            schema:postalCode "<xsl:value-of select="location/zipCode"/>" ;
            </xsl:if>
            geo:lat "<xsl:value-of select="coordinates/latitude"/>"^^xsd:double ;
            geo:long  "<xsl:value-of select="coordinates/longitude"/>"^^xsd:double .
&lt;urn:organization:uuid:<xsl:value-of select="alfId"/>&gt;  rdf:type schema:Organization ;
            rdfs:label "<xsl:value-of select="info/companyName/value"/>" ;
            schema:name "<xsl:value-of select="info/companyName/value"/>" ;
            schema:telephone "<xsl:value-of select="contacts/phoneNumber1"/>" ;
            schema:email "<xsl:value-of select="contacts/mailInfo"/>" .
            
       </xsl:for-each>
    
  </xsl:template>

</xsl:stylesheet>
