package test.functional;

import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import junit.framework.TestCase;

/**
 * Test MIME headers.
 */
public class TestMimeHeaders extends TestCase {

    public TestMimeHeaders(String s) {
        super(s);
    }

    public void testTransferingMimeHeadersToHttpHeaders() throws Exception {
        SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection con = scFactory.createConnection();

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        String headerName = "foo";
        String headerValue = "bar";
        message.getMimeHeaders().addHeader(headerName, headerValue);

        URLEndpoint endpoint = new URLEndpoint("http://localhost:8080/axis/services/TestMimeHeaderService");
        SOAPMessage response = con.call(message, endpoint);
        String[] responseHeader = response.getMimeHeaders().getHeader(headerName);
        assertTrue(responseHeader != null);
        assertEquals(1, responseHeader.length);
        assertEquals(headerValue, responseHeader[0]);
    }
}
