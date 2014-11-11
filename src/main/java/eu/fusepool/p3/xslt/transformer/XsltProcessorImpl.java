package eu.fusepool.p3.xslt.transformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.InputSource;

/**
 * Implements a transformation from XML to RDF of the data set
 * http://www.visittrentino.it/media/eventi/eventi.xml
 * @author luigi
 *
 */

public class XsltProcessorImpl implements XsltProcessor {
	
	public InputStream processXml(String xsltUrl, InputStream xmlDataIn) throws TransformerException, TransformerConfigurationException, 
	FileNotFoundException, IOException {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		
		Transformer transformer = tFactory.newTransformer(new StreamSource( xsltUrl ));
		
		transformer.transform(new StreamSource( xmlDataIn ), new StreamResult( outputStream ));
		
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

}
