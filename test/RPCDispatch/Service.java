package test.RPCDispatch;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.RPCElement;
import org.apache.axis.utils.DOM2Writer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test WebService
 */
public class Service {

    /**
     * Reverse the order of characters in a string
     */
    public String reverseString(String input) throws Exception {
       String result = "";
       for (int i=input.length(); i>0; ) result+=input.charAt(--i);
       return result;
    }

    /**
     * Reverse the order of a struct
     */
    public Data reverseData(Data input) throws Exception {
       Data result = new Data();
       result.setField1(input.getField3());
       result.setField2(reverseString(input.getField2()));
       result.setField3(input.getField1());
       return result;
    }

    /**
     * Return the target service (should be this!)
     */
    public String targetServiceExplicit(MessageContext mc) throws Exception {
       return mc.getTargetService();
    }

    /**
     * Return the target service (should be this!)
     */
    public String targetServiceImplicit() throws Exception {
       return MessageContext.getCurrentContext().getTargetService();
    }

    /**
     * Return the target service (should be this!)
     */
    public String argAsDOM(MessageContext mc, Data input) throws Exception {

       // get the first parameter
       Message message = mc.getRequestMessage();
       RPCElement body = (RPCElement)message.getSOAPPart().getAsSOAPEnvelope().getFirstBody();
       NodeList parms = body.getAsDOM().getChildNodes();
       Node parm1 = null;
       for (int i=0; i<parms.getLength(); i++) {
           parm1 = parms.item(i);
           if (parm1.getNodeType() == Node.ELEMENT_NODE) break;
       }

       // convert it to a DOM and back to a string, and return the result.
       return DOM2Writer.nodeToString(parm1, true);

    }

    /**
     * Return the value passed (including nulls!)
     */
    public Integer echoInt(Integer value) throws Exception {
       return value;
    }

    /**
     * Simple exception to be used in generating faults
     */
    class TestFault extends Exception {
        TestFault(String msg) throws Exception {
            super(msg);
            if (msg == null) throw new Exception("default value");
        }
    }

    /**
     * Simple fault.
     */
    public String simpleFault(String value) throws Exception {
       TestFault fault = new TestFault(value);
       throw fault;
    }


}
