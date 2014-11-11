package eu.fusepool.p3.events.transformer.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.clerezza.rdf.core.Graph;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.clerezza.rdf.ontologies.RDFS;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.fusepool.p3.events.transformer.XsltProcessorImpl;
import eu.fusepool.p3.events.transformer.XsltProcessor;

public class XsltProcessorImplTest {
	
	XsltProcessor processor;
	
	private static final Logger log = LoggerFactory.getLogger(XsltProcessorImplTest.class);

	@Before
	public void setUp() throws Exception {
		processor = new XsltProcessorImpl();
	}

	@Test
	public void testProcessTrentinoXml() throws TransformerConfigurationException, FileNotFoundException, 
	                                  TransformerException, IOException {
		InputStream xmlIn = getClass().getResourceAsStream("foo.xml");
		String xslUrl = getClass().getResource("foo.xsl").getFile();
		InputStream rdfIn = processor.processXml(xslUrl, xmlIn);
		Graph graph = Parser.getInstance().parse(rdfIn, SupportedFormat.TURTLE);
		Iterator<Triple> tripleIter = graph.filter(null, RDFS.label, null);
		Assert.assertTrue(tripleIter.hasNext());
		
	}

}
