package test.saaj;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

public class TestAttachment extends junit.framework.TestCase {

    public TestAttachment(String name) {
        super(name);
    }

    public void testStringAttachment() throws Exception {
        SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection con = scFactory.createConnection();

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        AttachmentPart attachment = message.createAttachmentPart();
        String stringContent = "Update address for Sunny Skies " +
                "Inc., to 10 Upbeat Street, Pleasant Grove, CA 95439";

        attachment.setContent(stringContent, "text/plain");
        attachment.setContentId("update_address");
        message.addAttachmentPart(attachment);
        java.util.Iterator it = message.getAttachments();
        while (it.hasNext()) {
            attachment = (AttachmentPart) it.next();
            Object content = attachment.getContent();
            String id = attachment.getContentId();
            System.out.println("Attachment " + id + " contains: " + content);
            assertEquals(content,stringContent);
        }
        System.out.println("Here is what the XML message looks like:");
        message.writeTo(System.out);
    }

    public static void main(String[] args) throws Exception {
        test.saaj.TestAttachment tester = new test.saaj.TestAttachment("TestSAAJ");
        tester.testStringAttachment();
    }
}
