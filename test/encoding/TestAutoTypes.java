package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;

import javax.xml.namespace.QName;

/**
 * Test auto-typing.
 */
public class TestAutoTypes extends TestCase {

    private AxisServer server = new AxisServer();

    public TestAutoTypes(String name) {
        super(name);
    }
    
    public void testAutoTypes() throws Exception
    {
        TypeMappingRegistry tmr = server.getTypeMappingRegistry();
        TypeMappingImpl tm = (TypeMappingImpl) tmr.getDefaultTypeMapping();
        tm.setDoAutoTypes(true);
        
        QName qname = tm.getTypeQName( AttributeBean.class );
        assertEquals( "http://encoding.test", 
                      qname.getNamespaceURI() );
        assertEquals( "AttributeBean", qname.getLocalPart() );
        
        assertTrue( tm.getDeserializer(qname) != null );
        assertTrue( tm.getSerializer(AttributeBean.class) != null );
    }
}
