<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" media-type="text/turtle" encoding="UTF-8"/>

  <!--xsl:strip-space elements="*"/-->

  <xsl:template match="/">
# XSLT transformation of OSM XML data for ways and nodes.

@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
@prefix geo: &lt;http://www.w3.org/2003/01/geo/wgs84_pos#&gt; .
@prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .
@prefix schema: &lt;http://schema.org/&gt; .
@prefix ngeo: &lt;http://geovocab.org/geometry#&gt; .
@prefix ogc: &lt;http://www.opengis.net/ont/geosparql#&gt; .

    <xsl:apply-templates select="osm"/>
  </xsl:template>

  <xsl:template match="osm">
       <xsl:for-each select="way">

        <xsl:if test="@id > 0 ">
          <xsl:if test="tag[@k='highway']">
            <xsl:if test="tag[@k='name']/@v != ''">
              &lt;urn:osm:way:uuid:<xsl:value-of select="@id"/>&gt; rdf:type schema:PostalAddress ;
                schema:streetAddress "<xsl:value-of select="tag[@k='name']/@v"/>" ;
                ngeo:geometry &lt;urn:osm:way:geometry:uuid:<xsl:value-of select="@id"/>&gt; .

                &lt;urn:osm:way:geometry:uuid:<xsl:value-of select="@id"/>&gt; ogc:asWKT "LINESTRING(
                <xsl:for-each select="nd">
                  <xsl:variable name="node_ref" select="@ref"/>
                  <xsl:for-each select="//node[@id=$node_ref]">
                    <xsl:value-of select="@lon"/><xsl:text> </xsl:text><xsl:value-of select="@lat"/>,
                  </xsl:for-each>
                </xsl:for-each>)"
            </xsl:if>
          </xsl:if>
        </xsl:if>


       </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>
