package test.saaj;

import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;

public class TestSOAPFaults extends junit.framework.TestCase {
    public TestSOAPFaults(String name) {
        super(name);
    }

    public void testQuick() throws Exception {
        MessageFactory msgfactory = MessageFactory.newInstance();
        SOAPFactory factory = SOAPFactory.newInstance();
        SOAPMessage outputmsg = msgfactory.createMessage();
        String valueCode = "faultcode";
        String valueString = "faultString";
        SOAPFault fault = outputmsg.getSOAPPart().getEnvelope().getBody().addFault();
        fault.setFaultCode(valueCode);
        fault.setFaultString(valueString);
        Detail d;
        d = fault.addDetail();
        d.addDetailEntry(factory.createName("Hello"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (outputmsg != null) {
            if (outputmsg.saveRequired()) {
                outputmsg.saveChanges();
            }
            outputmsg.writeTo(baos);
        }
        String xml = new String(baos.toByteArray());
        assertTrue(xml.indexOf("Hello")!=-1);
    }

    public static void main(String[] args) throws Exception {
        TestSOAPFaults detailTest = new TestSOAPFaults("TestSOAPFaults");
        detailTest.testQuick();
    }
}
