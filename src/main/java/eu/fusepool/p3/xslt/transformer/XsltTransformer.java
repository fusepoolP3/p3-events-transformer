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
package eu.fusepool.p3.xslt.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fusepool.p3.transformer.HttpRequestEntity;
import eu.fusepool.p3.transformer.SyncTransformer;
import eu.fusepool.p3.transformer.commons.Entity;

/**
 * A data transformer. The data set URI is specified at construction.
 */
class XsltTransformer implements SyncTransformer {

	public static final String DATA_MIME_TYPE = "application/xml"; //MIME type of the data fetched from the url provided by the client
	public static final String DATA_QUERY_PARAM = "data";
	
    private static final Logger log = LoggerFactory.getLogger(XsltTransformer.class);
    
    final XsltProcessor processor; 
    final String xsltUrl;
    
    XsltTransformer(XsltProcessor processor, String xsltUrl) {
    	this.processor = processor;
    	this.xsltUrl = xsltUrl;
    	}

    @Override
    public Set<MimeType> getSupportedInputFormats() {
        try {
          Set<MimeType> mimeSet = new HashSet<MimeType>();             
          mimeSet.add(new MimeType("application/xml"));
          return Collections.unmodifiableSet(mimeSet);
        } catch (MimeTypeParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public Set<MimeType> getSupportedOutputFormats() {
        try {
          Set<MimeType> mimeSet = new HashSet<MimeType>();             
          mimeSet.add(new MimeType("text/turtle"));
          return Collections.unmodifiableSet(mimeSet);
        } catch (MimeTypeParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Takes a data set and an URL to dereference the XSLT rules for the transformation.
     * Returns the result of the transformation.    
     */
    @Override
	public Entity transform(HttpRequestEntity entity) throws IOException {
    	Entity transformedEntity = null;
        String mediaType = entity.getType().toString();
        log.debug(mediaType);
        InputStream xmlDataIn = entity.getData();
        
        // Fetch the xslt transformation from the url.
    	// The xslt transformation url must be specified as a query parameters
    	log.info("XSLT Url : " + xsltUrl);
    	if( xmlDataIn != null ){
    		// transform the xml data using the xslt fetched from the url
    		if( xsltUrl != null ){
    			try {
    			  InputStream transformedIn = processor.processXml(xsltUrl, xmlDataIn);
    			  transformedEntity = new TransformedEntity(transformedIn,XsltUtil.getOutputMediaType(xsltUrl));
    			  
    			}
    			catch(TransformerConfigurationException tce){
    				throw new RuntimeException(tce.getMessage());
    			} 
    			catch (TransformerException te) {				
					throw new RuntimeException(te.getMessage());
				}
                
    			
    		}
    		else {
    			throw new IllegalArgumentException("A url to fetch the xslt rules must be provided.");    			
    		}    		
    	}
    	else {
    		throw new RuntimeException("No XML data to transform.");
    	}
    	
        
        return transformedEntity;
        
    }
  
    @Override
    public boolean isLongRunning() {
        // downloading the dataset can be time consuming
        return true;
    }

}
