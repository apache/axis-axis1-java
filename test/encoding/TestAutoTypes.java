package test.encoding;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.server.AxisServer;
import org.apache.axis.wsdl.fromJava.Namespaces;
import org.apache.axis.wsdl.fromJava.Types;

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

        assertEquals(
            "http://encoding.test",
            Namespaces.makeNamespace(AttributeBean[].class.getName()));
        assertEquals(
            "AttributeBean[]",
            Types.getLocalNameFromFullName(AttributeBean[].class.getName()));

        qname = tm.getTypeQName( AttributeBean[].class );
        assertEquals( "http://encoding.test", 
                      qname.getNamespaceURI() );
        assertEquals( "AttributeBean[]", qname.getLocalPart() );

    }
}
