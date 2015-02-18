package eu.fusepool.p3.xslt.transformer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public interface XsltProcessor {

	public InputStream processXml(String xsltUrl, InputStream xmlDataIn, String localioHeader) throws TransformerException, TransformerConfigurationException, 
	FileNotFoundException, IOException;
	
}
