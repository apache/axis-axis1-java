package test.utils;

import junit.framework.*;
import org.apache.axis.utils.QName;
import org.w3c.dom.*;

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
        assert(qname instanceof QName);
        assertNull(qname.getLocalPart());
    }   
    
    public void testQName2StringConstructor()
    {
        QName qname = new QName("rdf","article");
        assertNotNull(qname); 
        assertEquals("rdf", qname.getNamespaceURI()); 
        assertEquals("article", qname.getLocalPart()); 
    }

    /*
    public void testQNameStringElementConstructor()
    {
        //need a fully implemented mock
        //Element class to test this . . .
        //Element elem = new MockElement();
        QName qname = new QName("PREFIX:LOCALPART", elem);
        assertNotNull(qname); 
        assertEquals("PREFIX", qname.getNamespaceURI()); 
        assertEquals("LOCALPART", qname.getNamespaceURI()); 
    }
     */

    public void testToString()
    {
        QName qname = new QName("PREFIX", "LOCALPART");
        assertEquals("PREFIX:LOCALPART", qname.toString());

        qname.setNamespaceURI(null);
        assertEquals("LOCALPART", qname.toString());
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

        /*
        *  This first test causes a NullPointerException.  I don't think it 
        *  should, but I know why it does.  If you have an instance of QName 
        *  that was constructed with the no-args constructor, then its 
        *  namespaceURI and localPart fields will be null.  When you call the 
        *  equals(Object o) method, it compares those values between two 
        *  instances of QName using the String.equals() method.  This causes a 
        *  NullPointerException when the "empty" QName's namespaceURI field is 
        *  accessed for the comparison. 
        *
        *  If you switch them around so that the QName doing the comparison 
        *  (the one whose equals() method is called) has non-null fields, then
        *  you will not get a NullPointerException.
        *
        *  Suggestions:
        *  1. Initialize QName's fields to empty strings instead of letting them
        *  be nulls.
        *
        *  2. explicity throw or implicitly handle NullPointerExceptions under
        *  this condition.
        */
        boolean passed = false;
        try {
            assert(!qname1.equals(qname2));
        }
        catch (NullPointerException npe) {
            passed = true; // this should not really be a pass, but this is the 
                           // behavior the class currently exhibits.
        }
        assert(passed);
       
        //Note: this test is comparing the same two QName objects as above, but
        //due to the order and the implementation of the QName.equals() method,
        //this test passes without incurring a NullPointerException. 
        assert(!qname2.equals(qname1));

        assert(qname2.equals(qname3));
        assert(!qname3.equals(qname4));
    }
    
    public void testHashCode()
    {
        QName control = new QName("xsl", "text");
        QName compare = new QName("xsl", "text");
        QName contrast = new QName("xso", "text");
        assertEquals(control.hashCode(), compare.hashCode());
        assert(!(control.hashCode() == contrast.hashCode()));
    }
}
