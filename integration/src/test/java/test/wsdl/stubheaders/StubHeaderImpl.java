/**
 * StubHeaderImpl.java
 *
 * Test implimentation.
 * Make sure the service sees a SOAP header added by the Sub API.
 * Set a different header in the response to the test can verify it.
 */

package test.wsdl.stubheaders;

import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;

public class StubHeaderImpl implements test.wsdl.stubheaders.StubHeaderInterface {

    public java.lang.String echo(java.lang.String in) throws java.rmi.RemoteException {
        String ret = null;
        MessageContext mc = MessageContext.getCurrentContext();

        // Verify the existence of in the input header
        SOAPEnvelope env = mc.getRequestMessage().getSOAPEnvelope();
        SOAPHeaderElement header = env.getHeaderByName("http://test.org/inputheader", "headerin");
        if (header != null)
        {
            ret = header.getObjectValue().toString();
        }

        // add a different output header to the response
        env = mc.getResponseMessage().getSOAPEnvelope();
        SOAPHeaderElement hdr =
                new SOAPHeaderElement("http://test.org/outputheader", "headerout", "outputvalue");
        env.addHeader(hdr);

        // just return the input header, so test can validate it
        return ret;
    }

}
