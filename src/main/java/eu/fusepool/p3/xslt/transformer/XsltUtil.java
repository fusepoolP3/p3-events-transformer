package eu.fusepool.p3.xslt.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XsltUtil {
	
	private static Document xsl = null;
	/**
	 * Returns the MIME type of the XSLT transformation output.
	 * @param xslName
	 * @return
	 */
	public static String getOutputMediaType(String xslName){
		String mediaType = "";
		getDocument(xslName);
		NodeList nList = xsl.getElementsByTagName("xsl:output");
		for (int i = 0; i < nList.getLength(); i++) {
			Node outputNode = nList.item(i);
			if (outputNode.getNodeType() == Node.ELEMENT_NODE) {
			   Element outputElement = (Element) outputNode;
			   mediaType = outputElement.getAttribute("media-type");
			}
		}
		
		return mediaType;
	}
	
	private static void getDocument(String xslName){
		try {
			URL xslUri = new URL(xslName);
			InputStream xslIs = xslUri.openStream();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			xsl = dBuilder.parse(xslIs);
			xsl.getDocumentElement().normalize();
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
	}

}
