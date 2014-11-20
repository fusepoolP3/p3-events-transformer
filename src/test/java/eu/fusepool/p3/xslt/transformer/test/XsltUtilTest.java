package eu.fusepool.p3.xslt.transformer.test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.fusepool.p3.xslt.transformer.XsltUtil;

public class XsltUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		String xslName = "https://raw.githubusercontent.com/fusepoolP3/p3-xslt-transformer/master/src/test/resources/eu/fusepool/p3/xslt/transformer/test/foo.xsl";
		String mediaType = XsltUtil.getOutputMediaType(xslName);
		Assert.assertTrue("text/turtle".equals(mediaType));
	}

}
