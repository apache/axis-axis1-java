
package test.saaj;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.InputSource;

import javax.xml.soap.DetailEntry;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

public class TestSOAPFaultDetail extends junit.framework.TestCase
{
    private  MessageContext _msgContext;

    public TestSOAPFaultDetail(String name)
    {
        super(name);
        _msgContext = new MessageContext(new AxisServer());
    }

    String xmlString =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
" <soapenv:Body>" +
"  <soapenv:Fault>" +
"   <faultcode>soapenv:Server.generalException</faultcode>" +
"   <faultstring></faultstring>" +
"   <detail>" +
"    <tickerSymbol xsi:type=\"xsd:string\">MACR</tickerSymbol>" +
"   <ns1:exceptionName xmlns:ns1=\"http://xml.apache.org/axis/\">test.wsdl.faults.InvalidTickerFaultMessage</ns1:exceptionName>" +
"   </detail>" +
"  </soapenv:Fault>" +
" </soapenv:Body>" +
"</soapenv:Envelope>";


    public void testDetails() throws Exception
    {
        Reader reader = new StringReader(xmlString);
        InputSource src = new InputSource(reader);
        SOAPBodyElement bodyItem = getFirstBody(src);
        assertTrue("The SOAPBodyElement I got was not a SOAPFault, it was a " +
                bodyItem.getClass().getName(), bodyItem instanceof SOAPFault);
        SOAPFault flt = (SOAPFault)bodyItem;
        flt.addDetail();
        javax.xml.soap.Detail d = flt.getDetail();
        Iterator i = d.getDetailEntries();
        while (i.hasNext())
        {
            DetailEntry entry = (DetailEntry) i.next();
            String name = entry.getElementName().getLocalName();
            if ("tickerSymbol".equals(name)) {
                assertEquals("the value of the tickerSymbol element didn't match",
                        "MACR", entry.getValue());
            } else if ("exceptionName".equals(name)) {
                assertEquals("the value of the exceptionName element didn't match",
                        "test.wsdl.faults.InvalidTickerFaultMessage", entry.getValue());
            } else {
                assertTrue("Expecting details element name of 'tickerSymbol' or 'expceptionName' - I found :" + name, false);
            }
        }
        assertTrue(d != null);
    }

    private SOAPBodyElement getFirstBody(InputSource msgSource)
            throws Exception
    {
        DeserializationContext dser = new DeserializationContextImpl(
                msgSource, _msgContext, Message.RESPONSE);
        dser.parse();
        SOAPEnvelope env = dser.getEnvelope();

        return env.getFirstBody();
    }

    /**
     * Main
     */
    public static void main(String[] args)
            throws Exception
    {
        TestSOAPFaultDetail detailTest = new TestSOAPFaultDetail("faultdetails");
        detailTest.testDetails();
    }

}
