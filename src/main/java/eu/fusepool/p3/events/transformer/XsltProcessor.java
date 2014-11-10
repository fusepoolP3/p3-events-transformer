package eu.fusepool.p3.events.transformer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public interface XsltProcessor {

	public InputStream processXml(String xsltUrl, String xmlUrl) throws TransformerException, TransformerConfigurationException, 
	FileNotFoundException, IOException;
}
