/*
 * Created by IntelliJ IDEA.
 * User: gdaniels
 * Date: Jan 14, 2002
 * Time: 2:13:08 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package test.soap;

import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisFault;
import org.apache.axis.message.SOAPEnvelope;

/**
 * This is a test Handler which interacts with the TestService below in
 * order to test header processing.  If a particular header appears in
 * the message, we mark the MessageContext so the TestService knows to
 * double its results (which is a detectable way of confirming header
 * processing on the client side).
 */ 
public class TestHandler extends BasicHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
        SOAPEnvelope env = msgContext.getRequestMessage().getSOAPEnvelope();
        if (env.getHeaderByName(TestHeaderAttrs.GOOD_HEADER_NS, 
                                TestHeaderAttrs.GOOD_HEADER_NAME) != null) {
            // Just the header's presence is enough - mark the property
            // so it can be picked up by the service (see below)
            msgContext.setProperty(TestHeaderAttrs.PROP_DOUBLEIT, Boolean.TRUE);
        }
    }
}
