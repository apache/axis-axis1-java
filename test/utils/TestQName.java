package test.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import javax.xml.rpc.namespace.QName;

public class TestQName extends TestCase
{
    public TestQName (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestQName.class);
    }

    protected void setup() {
    }

    public void testQNameDefaultConstructor()
    {
        QName qname = new QName();
        assertTrue("qname not an instance of QName", qname instanceof QName); // ?? not really necessary.  Compiler won't compile otherwise
        assertNull("LocalPart was not null", qname.getLocalPart());
    }   
    
    public void testQName2StringConstructor()
    {
        QName qname = new QName("rdf","article");
        assertNotNull("qname was null.  Something really wrong here.", qname); 
        assertEquals("Namespace URI was not 'rdf', it was: " + qname.getNamespaceURI(),
                     "rdf", qname.getNamespaceURI()); 
        assertEquals("LocalPart was not 'article', it was: " + qname.getLocalPart(),
                     "article", qname.getLocalPart()); 
    }

    /*
    public void testQNameStringElementConstructor()
    {
        //need a fully implemented mock
        //Element class to test this . . .
        //Element elem = new MockElement();
        QName qname = new QName("PREFIX:LOCALPART", elem);
        assertNotNull("qname was null", qname); 
        assertEquals("Namespace URI was not 'PREFIX', it was: " + qname.getNamespaceURI(),
                     "PREFIX", qname.getNamespaceURI()); 
        assertEquals("LocalPart was not 'LOCALPART', it was: " + qname.getLocalPart(),
                     "LOCALPART", qname.getLocalPart()); 
    }
     */

    public void testToString()
    {
        QName qname = new QName("PREFIX", "LOCALPART");
        assertEquals("qname was not the same as 'PREFIX:LOCALPART', it was: " + qname.toString(),
                     "PREFIX:LOCALPART", qname.toString());

        qname.setNamespaceURI(null);
        assertEquals("qname was not the same as 'LOCALPART', is was: " + qname.toString(), "LOCALPART", qname.toString());
    }

    public void testEquals()
    {
        QName qname1 = new QName();
        QName qname2 = new QName("PREFIX", "LOCALPART");
        QName qname3 = new QName("PREFIX", "LOCALPART");
        QName qname4 = new QName("PREFIX", "DIFFLOCALPART");
        //need a fully implemented mock Element class...
        //Element elem = new MockElement();        
        ////QName qname5 = new QName("PREFIX:LOCALPART", elem);

        // the following should NOT throw a NullPointerException
        assertTrue("qname1 is the same as qname2", !qname1.equals(qname2));
       
        //Note: this test is comparing the same two QName objects as above, but
        //due to the order and the implementation of the QName.equals() method,
        //this test passes without incurring a NullPointerException. 
        assertTrue("qname2 is the same as qname1", !qname2.equals(qname1));

        assertTrue("qname2 is not the same as qname2", qname2.equals(qname3));
        assertTrue("qname3 is the same as qname4", !qname3.equals(qname4));
    }
    
    public void testHashCode()
    {
        QName control = new QName("xsl", "text");
        QName compare = new QName("xsl", "text");
        QName contrast = new QName("xso", "text");
        assertEquals("control hashcode does not equal compare.hashcode", control.hashCode(), compare.hashCode());
        assertTrue("control hashcode is not equivalent to compare.hashcode", !(control.hashCode() == contrast.hashCode()));
    }
}
