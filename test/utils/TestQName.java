package test.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

    public void testQName2StringConstructor()
    {
        QName qname = new QName("rdf","article");
        assertNotNull("qname was null.  Something really wrong here.", qname); 
        assertEquals("Namespace URI was not 'rdf', it was: " + qname.getNamespaceURI(),
                     "rdf", qname.getNamespaceURI()); 
        assertEquals("LocalPart was not 'article', it was: " + qname.getLocalPart(),
                     "article", qname.getLocalPart()); 
    }

    public void testToString()
    {
        QName qname = new QName("PREFIX", "LOCALPART");
        assertEquals("qname was not the same as '{PREFIX}LOCALPART', it was: " + qname.toString(),
                     "{PREFIX}LOCALPART", qname.toString());
    }

    public void testNullQname()
    {
        QName qname1 = new QName("LOCALPART");
        QName qname2 = new QName(null, "LOCALPART");
        QName qname3 = new QName("", "LOCALPART");

        assertEquals("omitted namespace ", "LOCALPART", qname1.toString());
        assertEquals("null namespace ", "LOCALPART", qname2.toString());
        assertEquals("empty namespace ", "LOCALPART", qname3.toString());
    }

    public void testEquals()
    {
        QName qname1 = new QName("", "");
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

    public void testSerializable() throws Exception
    {
        QName q1 = new QName("Matthew", "Duftler");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(q1);
        oos.flush();
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        QName q2 = (QName)ois.readObject();

        assertEquals("q1 is not the same as q2", q1, q2);
        assertEquals("q2 is not the same as q1", q2, q1);
    }
}
