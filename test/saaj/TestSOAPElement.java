package test.saaj;

import junit.framework.TestCase;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test case for Axis impl of SAAJ {@link SOAPElement} interface ({@link org.apache.axis.message.MessageElement}).
 *
 * @author Ian P. Springer
 */
public class TestSOAPElement extends TestCase
{
    private SOAPElement soapElem;

    protected void setUp() throws Exception
    {
        soapElem = SOAPFactory.newInstance().createElement( "Test", "test", "http://test.apache.org/" );
    }

    /**
     * Test for Axis impl of {@link SOAPElement#addTextNode(String)}.
     *
     * @throws Exception on error
     */
    public void testAddTextNode() throws Exception
    {
        assertNotNull( soapElem );
        final String value = "foo";
        soapElem.addTextNode( value );
        assertEquals( value, soapElem.getValue() );
        Text text = assertContainsText( soapElem );
        assertEquals( value, text.getValue() );
    }

    private Text assertContainsText( SOAPElement soapElem )
    {
        assertTrue( soapElem.hasChildNodes() );
        List childElems = toList( soapElem.getChildElements() );
        assertTrue( childElems.size() == 1 );
        Node node = (Node) childElems.get( 0 );
        assertTrue( node instanceof Text );
        return (Text) node;
    }

    private List toList( Iterator iter )
    {
        List list = new ArrayList();
        while ( iter.hasNext() )
        {
            list.add( iter.next() );
        }
        return list;
    }

}
