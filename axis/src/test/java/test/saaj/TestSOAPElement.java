package test.saaj;

import junit.framework.TestCase;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.Text;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.ByteArrayInputStream;

import org.w3c.dom.NodeList;

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

    public void testGetElementsByTagName() throws Exception {
    	String soapMessageWithLeadingComment =
    		"<?xml version='1.0' encoding='UTF-8'?>" + 
			"<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
			"<env:Body>" +
                "<echo>" +
                " <data>\n" +
                "   <tag name=\"First\" >\n" +
                "      <Line> One </Line>\n" +
                "      <Line> Two </Line>\n" +
                "   </tag>\n" +
                "   <tag name =\"Second\" >\n" +
                "      <Line> Three </Line>\n" +
                "      <Line> Four </Line>\n" +
                "   </tag>\n" +
                "   <tag name =\"Third\" >\n" +
                "      <Line> Five </Line>\n" +
                "      <Line> Six </Line>\n" +
                "   </tag>\n" +
                "</data>" +
                "</echo>" +
            "</env:Body>" +
			"</env:Envelope>";
    	
    	MessageFactory factory = MessageFactory.newInstance();
    	SOAPMessage message =
    		factory.createMessage(new MimeHeaders(), 
    				new ByteArrayInputStream(soapMessageWithLeadingComment.getBytes()));
        SOAPPart part = message.getSOAPPart();
        SOAPEnvelope envelope = (SOAPEnvelope) part.getEnvelope();
        NodeList nodes = envelope.getElementsByTagName("tag");
        assertEquals(nodes.getLength(), 3);
        NodeList nodes2 = envelope.getElementsByTagName("Line");
        assertEquals(nodes2.getLength(), 6);

        NodeList nodes3 = part.getElementsByTagName("tag");
        assertEquals(nodes3.getLength(), 3);
        NodeList nodes4 = part.getElementsByTagName("Line");
        assertEquals(nodes4.getLength(), 6);
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
