package test.utils;

import junit.framework.*;
import org.apache.axis.utils.QFault;
import org.apache.axis.utils.QName;
import org.w3c.dom.*;

public class TestQFault extends TestCase
{
    public TestQFault (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestQFault.class);
    }

    protected void setup() {
    }

    public void testQFaultConstructorWith3Params()
    {
        QFault qfault = new QFault("xsl","include","extra");
        assert(qfault instanceof QName);
        assert(qfault instanceof QFault);
        assertEquals("include.extra", qfault.getLocalPart());
    }   
    
    public void testQFaultConstructorWithQFaultParam()
    {
        QFault qfault = new QFault("rdf","title");
        QFault qfaultWithMinorCode = new QFault(qfault,"extraBits");
        assert(qfaultWithMinorCode instanceof QFault);
        assertEquals("title.extraBits", qfaultWithMinorCode.getLocalPart());
    }   
    
    public void testQFaultConstructorWithQNameParam()
    {
        QName qname = new QFault("rdf", "title");
        QFault qfault = new QFault(qname, "someCode");
        assertEquals("title.someCode", qfault.getLocalPart());
    }   
    
    public void testAppendMinorCode()
    {
        QFault qfault = new QFault("rdf","title");
        qfault.appendMinorCode("minor-code");
        assertEquals("rdf", qfault.getNamespaceURI());
        assertEquals("title.minor-code", qfault.getLocalPart());
    }   
}
