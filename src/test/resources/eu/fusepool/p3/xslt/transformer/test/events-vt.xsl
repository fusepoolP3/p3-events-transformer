<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://ipsoft.it/xsd" version="1.0">

  <xsl:output method="text" media-type="text/turtle" encoding="UTF-8"/>

  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
# RDF data transformed from the data set available at the url
# http://www.visittrentino.it/media/eventi/eventi.xml
# xslt version 1.0.0-20150128_2

@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
@prefix geo: &lt;http://www.w3.org/2003/01/geo/wgs84_pos#&gt; .
@prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .
@prefix schema: &lt;http://schema.org/&gt; .
    <xsl:apply-templates select="events"/>
  </xsl:template>

  <xsl:template match="events">
        <xsl:variable name="apos"><xsl:text>'</xsl:text></xsl:variable>
        <xsl:variable name="double_quote"><xsl:text>"</xsl:text></xsl:variable>
       <xsl:for-each select="event">
&lt;urn:event:uuid:<xsl:value-of select="alfId"/>&gt; rdf:type schema:Event ;
            <xsl:variable name="quotedlabel" select="name/value[@xml:lang='it']"/>
            rdfs:label "<xsl:value-of select="translate($quotedlabel,$double_quote,$apos)" />"@it ;
             <xsl:if test="name/value[@xml:lang='en'] != ''">
                <xsl:variable name="quoted_en_label" select="name/value[@xml:lang='en']"/>
            rdfs:label "<xsl:value-of select="translate($quoted_en_label,$double_quote,$apos)"/>"@en ;
            </xsl:if>
            <xsl:variable name="quoted_description" select="shortDescription/value[@xml:lang='it']"/>
            <xsl:variable name="nolinefeed_description" select="translate($quoted_description,'&#10;&#13;',' ')"/>
            schema:description "<xsl:value-of select="translate($nolinefeed_description,$double_quote,$apos)"/>"@it ;
            schema:category "<xsl:value-of select="searchCategories/searchCategory/value[@xml:lang='it']"/>"@it ;
            <xsl:variable name="quoted_en_description" select="shortDescription/value[@xml:lang='en']"/>
            <xsl:variable name="nolinefeed_en_description" select="translate($quoted_en_description,'&#10;&#13;',' ')"/>
            schema:description "<xsl:value-of select="translate($nolinefeed_en_description,$double_quote,$apos)"/>"@en ;
            schema:category "<xsl:value-of select="searchCategories/searchCategory/value[@xml:lang='en']"/>"@en ;
            <xsl:if test="startDate != ''">
            schema:startDate "<xsl:value-of select="startDate"/>"^^xsd:date ;
            </xsl:if>
             <xsl:if test="endDate != ''">
            schema:endDate "<xsl:value-of select="endDate"/>"^^xsd:date ;
            </xsl:if>
            schema:location &lt;urn:location:uuid:<xsl:value-of select="alfId"/>&gt; ;
            schema:organizer &lt;urn:organization:uuid:<xsl:value-of select="alfId"/>&gt; .
&lt;urn:location:uuid:<xsl:value-of select="alfId"/>&gt; rdf:type schema:PostalAddress ;
            <xsl:variable name="location" select="eventLocation/value" />
            rdfs:label "<xsl:value-of select="translate($location,$double_quote,$apos)"/>" ;
            <xsl:variable name="place" select="location/place/value" />
            schema:addressLocality "<xsl:value-of select="translate($place,$double_quote,$apos)"/>" ;
            <xsl:if test="location/country/value != ''">
            schema:addressCountry "<xsl:value-of select="location/country/value"/>" ;
            </xsl:if>
            <xsl:if test="location/street/value != ''">
            <xsl:variable name="address" select="location/street/value"/>
            schema:streetAddress "<xsl:value-of select="translate($address,$double_quote,$apos)"/> <xsl:value-of select="location/number"/>" ;
            </xsl:if>
            <xsl:if test="location/zipCode != ''">
            schema:postalCode "<xsl:value-of select="location/zipCode"/>" ;
            </xsl:if>
            geo:lat "<xsl:value-of select="coordinates/latitude"/>"^^xsd:double ;
            geo:long  "<xsl:value-of select="coordinates/longitude"/>"^^xsd:double .
&lt;urn:organization:uuid:<xsl:value-of select="alfId"/>&gt;  rdf:type schema:Organization ;
            <xsl:variable name="organization_label" select="info/companyName/value"/>
            rdfs:label "<xsl:value-of select="translate($organization_label,$double_quote,$apos)"/>" ;
            schema:name "<xsl:value-of select="translate($organization_label,$double_quote,$apos)"/>" ;
            schema:telephone "<xsl:value-of select="contacts/phoneNumber1"/>" ;
            schema:email "<xsl:value-of select="contacts/mailInfo"/>" .

       </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>
