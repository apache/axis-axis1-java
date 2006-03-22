package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.Handler;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;

import java.io.InputStream;
import java.io.StringBufferInputStream;

/**
 * Test WSDD undeployment.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */ 
public class TestUndeployment extends TestCase
{
    static final String HANDLER_NAME = "logger";
    static final String PARAM_NAME = "testParam";
    static final String PARAM_VAL  = "testValue";

    static final String deployDoc =
            "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
                  "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
            " <handler type=\"java:org.apache.axis.handlers.LogHandler\" " +
                      "name=\"" + HANDLER_NAME + "\">\n" +
            "  <parameter name=\"" + PARAM_NAME +
                          "\" value=\"" + PARAM_VAL + "\"/>\n" +
            " </handler>\n" +
            " <handler type=\"logger\" name=\"other\"/>\n" +
            "</deployment>";
    
    static final String undeployDoc =
            "<undeployment xmlns=\"http://xml.apache.org/axis/wsdd/\">\n" +
            " <handler name=\"other\"/>\n" +
            "</undeployment>";


   static final String typeMappingDeployDoc =
        "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" " +
        "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n" +
        "<beanMapping languageSpecificType=\"java:java.lang.String\" qname=\"ns21:LineItem1\" xmlns:ns21=\"http://www.soapinterop.org/Bid\"/>\n" +
        "<beanMapping languageSpecificType=\"java:java.lang.String\" qname=\"ns21:LineItem2\" xmlns:ns21=\"http://www.soapinterop.org/Bid\"/>\n" +
        "<typeMapping deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\" encodingStyle=\"\" qname=\"ns13:&gt;Record2\" serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\" type=\"java:java.lang.String\" xmlns:ns13=\"http://tempuri.org/\"/>" +
        "</deployment>";
    
    static final String typeMappingUndeployDoc =
        "<undeployment xmlns=\"http://xml.apache.org/axis/wsdd/\">\n" +
        "<beanMapping languageSpecificType=\"java:java.lang.String\" qname=\"ns21:LineItem1\" xmlns:ns21=\"http://www.soapinterop.org/Bid\"/>\n" +
        "<beanMapping languageSpecificType=\"java:samples.bidbuy.LineItem\" qname=\"ns21:LineItem\" xmlns:ns21=\"http://www.soapinterop.org/Bid\"/>\n" +
        "<typeMapping deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\" encodingStyle=\"\" qname=\"ns13:&gt;Record2\" serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\" type=\"java:java.lang.String\" xmlns:ns13=\"http://tempuri.org/\"/>" +
        "</undeployment>";

    public TestUndeployment (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestUndeployment.class);
    }

    protected void setup() {
    }

    /**
     * Load up a server with a couple of handlers as spec'ed above,
     * then undeploy one of them.  Confirm that all looks reasonable
     * throughout.
     */ 
    public void testUndeployHandler() throws Exception
    {
        XMLStringProvider provider = new XMLStringProvider(deployDoc);
        AxisServer server = new AxisServer(provider);
        
        Handler handler = server.getHandler("other");
        assertNotNull("Couldn't get handler", handler);

        InputStream is = new StringBufferInputStream(undeployDoc);
        WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(is));

        WSDDDeployment dep = provider.getDeployment();
        doc.deploy(dep);

        server.refreshGlobalOptions();
        
        handler = server.getHandler("other");
        assertNull("Undeployed handler is still available", handler);
        
        handler = server.getHandler(HANDLER_NAME);
        assertNotNull("Couldn't get handler (2nd time)", handler);
    }
    
    public void testUndeployTypeMapping() throws Exception
    {
        XMLStringProvider provider = 
            new XMLStringProvider(typeMappingDeployDoc);
        AxisServer server = new AxisServer(provider);
        WSDDDeployment dep = provider.getDeployment();
         
        InputStream is = new StringBufferInputStream(typeMappingUndeployDoc);
        WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(is));

        WSDDTypeMapping[] mappingsBefore = dep.getTypeMappings();

        assertEquals(3, mappingsBefore.length);

        // perform undeploy
        doc.deploy(dep);

        WSDDTypeMapping[] mappingsAfter = dep.getTypeMappings();

        assertEquals(1, mappingsAfter.length);
        assertEquals("LineItem2", mappingsAfter[0].getQName().getLocalPart());
    }
    
    public static void main(String[] args) throws Exception {
        TestUndeployment tester = new TestUndeployment("foo");
        tester.testUndeployHandler();
    }
}
