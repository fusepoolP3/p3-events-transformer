package eu.fusepool.p3.xslt.transformer.test;

import java.net.ServerSocket;
import java.util.Iterator;

import org.apache.clerezza.rdf.core.Graph;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;

import eu.fusepool.p3.transformer.server.TransformerServer;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import eu.fusepool.p3.transformer.client.Transformer;
import eu.fusepool.p3.transformer.client.TransformerClientImpl;
import eu.fusepool.p3.transformer.commons.Entity;
import eu.fusepool.p3.transformer.commons.util.WritingEntity;
import eu.fusepool.p3.xslt.transformer.EventsTransformerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.clerezza.rdf.core.impl.TypedLiteralImpl;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.ontologies.FOAF;
import org.apache.clerezza.rdf.ontologies.RDFS;
import org.apache.clerezza.rdf.ontologies.XSD;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.junit.Rule;
import org.slf4j.LoggerFactory;

public class EventsTransformerTest {
	
	private static final UriRef LONG = new UriRef("http://www.w3.org/2003/01/geo/wgs84_pos#long");
    private static final UriRef LAT = new UriRef("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
	
	// client data (xml)
	final String CLIENT_XML_DATA = "eventi-visittrentino.xml";
	// data used by the mock server
	final String MOCK_XSLT = "events-vt.xsl";
	
	public static final String DATA_MIME_TYPE = "application/xml"; //MIME type of the xslt file fetched from the url provided by the client
	
	final static String TRANSFORMER_MIME_TYPE = "text/turtle";
	
    private static MimeType transformerMimeType;
    private static MimeType clientDataMimeType;
    static {
        try {
        	transformerMimeType = new MimeType(TRANSFORMER_MIME_TYPE);
        	clientDataMimeType = new MimeType(DATA_MIME_TYPE);
        } catch (MimeTypeParseException ex) {
            Logger.getLogger(EventsTransformerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
	private static int mockPort = 0;
	private int transformerServerPort = 0;
    private byte[] mockXslt;
    private byte[] clientXmlData;
    private String transformerBaseUri;
	
	
	@BeforeClass
	public static void setMockPort() {
		mockPort = findFreePort();
		
	}
    
	
	@Before
    public void setUp() throws Exception {
		//load the client xml data
		clientXmlData = IOUtils.toByteArray(getClass().getResourceAsStream(CLIENT_XML_DATA));
		// load the xslt transformation
		mockXslt = IOUtils.toByteArray(getClass().getResourceAsStream(MOCK_XSLT));
		
		// set up the transformer
		transformerServerPort = findFreePort();
        transformerBaseUri = "http://localhost:" + transformerServerPort + "/";
        RestAssured.baseURI = transformerBaseUri;
        TransformerServer server = new TransformerServer(transformerServerPort, false);
        server.start(new EventsTransformerFactory());
    
	}
	
	
	@Rule
    public WireMockRule wireMockRule = new WireMockRule(mockPort);
    
    @Test
    public void testTurtleSupported()  throws MimeTypeParseException {
        Transformer t = new TransformerClientImpl(RestAssured.baseURI);
        Set<MimeType> types = t.getSupportedInputFormats();
        Assert.assertTrue("No supported Output format", types.size() > 0);
        boolean turtleFound = false;
        for (MimeType mimeType : types) {
            if (transformerMimeType.match(mimeType)) {
                turtleFound = true;
            }
        }
        Assert.assertTrue("None of the supported output formats is turtle", turtleFound);
    }
        

    /**
     * The transformer receives data and a url from the client, fetches the data set from the url, applies a transformation
     * and then check if the transformation is in the result graph.
     * @throws Exception
     */
	@Test
    public void testTransformation() throws Exception {
	    // Set up a service in the mock server to respond to a get request that must be sent by the transformer
		// on behalf of its client to fetch the xslt.
        stubFor(get(urlEqualTo("/xslt/" + MOCK_XSLT))
                .willReturn(aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/xml")
                    .withBody(mockXslt)));
        
        // prepare the client HTTP POST message with the xml data and the url where to dereference the xslt 
        String xsltUrl = "http://localhost:" + mockPort + "/xslt/" + MOCK_XSLT ;
        // the client sends a request to the transformer with the url of the events data to be fetched
        String queryString = "xslt=" + URLEncoder.encode(xsltUrl, "UTF-8");
        String clientRequestUrl = RestAssured.baseURI+"?"+queryString;
        Transformer t = new TransformerClientImpl(clientRequestUrl);
        // the transformer fetches the xslt from the mock server, applies its transformation and sends the RDF result to the client
        {
            Entity response = t.transform(new WritingEntity() {

                @Override
                public MimeType getType() {
                    return clientDataMimeType;
                }

                @Override
                public void writeData(OutputStream out) throws IOException {
                    out.write(clientXmlData);
                }
            }, transformerMimeType);

            // the client receives the response from the transformer
            Assert.assertEquals("Wrong media Type of response", transformerMimeType.toString(), response.getType().toString());  
            // Parse the RDF data returned by the transformer after the xslt transformation has been applied to the xml data
            final Graph responseGraph = Parser.getInstance().parse(response.getData(), "text/turtle");
            //checks for the presence of a specific property added by the transformer
            final Iterator<Triple> propertyIter = responseGraph.filter(null, RDFS.label, null);
            Assert.assertTrue("No specific property found in response", propertyIter.hasNext());
            //verify that the xslt has been loaded from the (mock) server (one call)
            //verify(1,getRequestedFor(urlEqualTo("/xslt/" + MOCK_XSLT)));
            
        }
                
	    
	}
    
	
	public static int findFreePort() {
        int port = 0;
        try (ServerSocket server = new ServerSocket(0);) {
            port = server.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("unable to find a free port");
        }
        return port;
    }

}
