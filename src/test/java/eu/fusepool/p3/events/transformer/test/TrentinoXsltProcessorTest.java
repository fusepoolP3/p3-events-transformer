package eu.fusepool.p3.events.transformer.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.clerezza.rdf.core.Graph;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fusepool.p3.events.transformer.TrentinoXsltProcessor;
import eu.fusepool.p3.events.transformer.XsltProcessor;

public class TrentinoXsltProcessorTest {
	
	XsltProcessor processor;
	
	private static final Logger log = LoggerFactory.getLogger(TrentinoXsltProcessorTest.class);

	@Before
	public void setUp() throws Exception {
		processor = new TrentinoXsltProcessor();
	}

	@Test
	public void testProcessTrentinoXml() throws TransformerConfigurationException, FileNotFoundException, 
	                                  TransformerException, IOException {
		String xmlUrl = getClass().getResource("foo.xml").getFile();
		String xsltUrl = getClass().getResource("foo.xsl").getFile();
		InputStream rdfIn = processor.processXml(xsltUrl, xmlUrl);
		
		try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(rdfIn));
	        String line;
	        while((line = reader.readLine()) != null){
	            log.debug(line);
	        }	
    	}
    	catch(IOException ioe){
    		
    	}
		
		//Graph graph = Parser.getInstance().parse(rdfIn, SupportedFormat.TURTLE);
		
	}

}
