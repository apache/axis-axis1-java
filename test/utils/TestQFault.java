package test.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.utils.QFault;
import org.apache.axis.utils.QName;

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
        assertTrue("qfault not instance of QName", qfault instanceof QName);
        assertTrue("qfault not instance of QFault", qfault instanceof QFault); // ?? is this necessary?  It cannot help but be a QFault
        assertEquals("LocalPart is not 'include.extra', it is: " + qfault.getLocalPart(),
                     "include.extra", qfault.getLocalPart());
    }   
    
    public void testQFaultConstructorWithQFaultParam()
    {
        QFault qfault = new QFault("rdf","title");
        QFault qfaultWithMinorCode = new QFault(qfault,"extraBits");
        assertTrue("qfault not instanceof QFault", qfaultWithMinorCode instanceof QFault); // ?? is this necessary?  It cannot help but be a QFault
        assertEquals("LocalPart is not 'title.extraBits', it is: " + qfaultWithMinorCode.getLocalPart(),
                     "title.extraBits", qfaultWithMinorCode.getLocalPart());
    }   
    
    public void testQFaultConstructorWithQNameParam()
    {
        QName qname = new QFault("rdf", "title");
        QFault qfault = new QFault(qname, "someCode");
        assertEquals("LocalPart is not 'title.someCode', it is: " + qfault.getLocalPart(),
                     "title.someCode", qfault.getLocalPart());
    }   
    
    public void testAppendMinorCode()
    {
        QFault qfault = new QFault("rdf","title");
        qfault.appendMinorCode("minor-code");
        assertEquals("Namespace URI is not 'rdf', it is: " + qfault.getNamespaceURI(),
                     "rdf", qfault.getNamespaceURI());
        assertEquals("LocalPart is not 'title.minor-code', it is: " + qfault.getLocalPart(),
                     "title.minor-code", qfault.getLocalPart());
    }   
}
