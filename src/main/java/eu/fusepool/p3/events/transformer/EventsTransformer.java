/*
 * Copyright 2014 Bern University of Applied Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.fusepool.p3.events.transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.clerezza.rdf.core.impl.TypedLiteralImpl;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.apache.clerezza.rdf.ontologies.RDFS;
import org.apache.clerezza.rdf.ontologies.XSD;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fusepool.p3.transformer.HttpRequestEntity;
import eu.fusepool.p3.transformer.RdfGeneratingTransformer;

/**
 * A data transformer. The data set URI is specified at construction.
 */
class EventsTransformer extends RdfGeneratingTransformer {

	public static final String DATA_MIME_TYPE = "application/xml"; //MIME type of the data fetched from the url provided by the client
	public static final String DATA_QUERY_PARAM = "data";
	
    private static final Logger log = LoggerFactory.getLogger(EventsTransformer.class);
    
    final ExampleEnricher exampleEnricher;
    final String dataUrl;

    EventsTransformer(ExampleEnricher exampleEnricher, String dataUrl) {
        this.exampleEnricher = exampleEnricher;
        this.dataUrl = dataUrl;
    }

    @Override
    public Set<MimeType> getSupportedInputFormats() {
        Parser parser = Parser.getInstance();
        try {
            Set<MimeType> mimeSet = new HashSet<MimeType>();
            for (String mediaFormat : parser.getSupportedFormats()) {           
              mimeSet.add(new MimeType(mediaFormat));
            }
            return Collections.unmodifiableSet(mimeSet);
        } catch (MimeTypeParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Takes from the client RDF data with a location and dates and a URL to fetch events nearby.
     * Returns the original RDF data with the events information.    
     */
    @Override
    protected TripleCollection generateRdf(HttpRequestEntity entity) throws IOException {
    	TripleCollection resultGraph = new SimpleMGraph();
        String mediaType = entity.getType().toString();   
        Parser parser = Parser.getInstance();
        TripleCollection clientGraph = parser.parse( entity.getData(), mediaType);
        
        // add the client data to the result graph
        resultGraph.addAll(clientGraph);
        
        // graph containing the data feched by the url if provided.
        TripleCollection dataGraph = null;
        
        // Fetch the events from the url.
    	// The data url must be specified as a query parameter
    	log.info("Data Url : " + dataUrl);
    	if(dataUrl != null){
    		
    		// enrich the client data using the data fetched from the url
    		if( (dataGraph = fetchDataFromUrl(dataUrl) ) != null ){
               resultGraph.addAll(dataGraph);    
    		}
    		else {
    			throw new RuntimeException("Failed to transform the events source data.");
    		}
    		
    	}
    	else {
    		throw new IllegalArgumentException("A url to fetch the data must be provided.");
    	}
    	
        
        return resultGraph;
        
    }
    
    /**
     * Fetches the data from the url sent by the client transformer.
     * Transform the data into RDF, if in different format maybe using another transformer.
     * The data MIME type must be the one that can be handled by the transformer. 
     * @param dataUrl
     * @return
     * @throws IOException
     */
    private TripleCollection fetchDataFromUrl(String dataUrl)throws IOException {
    	TripleCollection rdfData = null;
        URL sourceUrl = new URL(dataUrl);
        URLConnection connection = sourceUrl.openConnection();
        InputStream in =  connection.getInputStream();
        
        printIntupStream(in);
        
        // use only for tests
        UriRef GEO_LONG = new UriRef("http://www.w3.org/2003/01/geo/wgs84_pos#long");
        UriRef GEO_LAT = new UriRef("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
        UriRef res1 = new UriRef("http://example.org/res1");
        UriRef poi1 = new UriRef("http://example.org/poi1");
        rdfData = new SimpleMGraph();
        rdfData.add(new TripleImpl(res1,FOAF.based_near,poi1));
        rdfData.add(new TripleImpl(poi1, RDFS.label, new PlainLiteralImpl("Ski2015 Monte Bondone")));        
        rdfData.add(new TripleImpl(poi1, GEO_LAT, new TypedLiteralImpl("46.2220374200606", XSD.double_)));
        rdfData.add(new TripleImpl(poi1, GEO_LONG, new TypedLiteralImpl("10.7963137713743", XSD.double_)));
        
        
        return rdfData;
    	
    }
    
    private void printIntupStream(InputStream in) {
    	try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        String line;
	        while((line = reader.readLine()) != null){
	            log.debug(line);
	        }	
    	}
    	catch(IOException ioe){
    		
    	}
    	
    }
  
    @Override
    public boolean isLongRunning() {
        // downloading the dataset can be time consuming
        return true;
    }

}
