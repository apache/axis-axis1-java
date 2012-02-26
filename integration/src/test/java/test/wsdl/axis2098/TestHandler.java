package test.wsdl.axis2098;

import java.io.ByteArrayOutputStream;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

public class TestHandler extends BasicHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
        try {
            msgContext.getCurrentMessage().writeTo(baos);
        } catch (Exception e) {
            throw new AxisFault("exception", e);
        }
        String msg = baos.toString();
        System.out.println("msg = " + msg);
        if (msg.indexOf("xsi:type") >= 0) {
            throw new AxisFault("message contains xsi:type");
        }
    }
}
