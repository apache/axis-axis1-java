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

        assertTrue(message.countAttachments()==1);

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

        message.removeAllAttachments();
        assertTrue(message.countAttachments()==0);
    }

    public void testMultipleAttachments() throws Exception {
        SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection con = scFactory.createConnection();

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage msg = factory.createMessage();
        java.net.URL url1 = new java.net.URL("http://slashdot.org/slashdot.xml");
        java.net.URL url2 = new java.net.URL("http://www.apache.org/LICENSE.txt");

        AttachmentPart a1 = msg.createAttachmentPart(new javax.activation.DataHandler(url1));
        a1.setContentType("text/xml");
        msg.addAttachmentPart(a1);
        AttachmentPart a2 = msg.createAttachmentPart(new javax.activation.DataHandler(url1));
        a2.setContentType("text/xml");
        msg.addAttachmentPart(a2);
        AttachmentPart a3 = msg.createAttachmentPart(new javax.activation.DataHandler(url2));
        a3.setContentType("text/plain");
        msg.addAttachmentPart(a3);

        assertTrue(msg.countAttachments()==3);

        javax.xml.soap.MimeHeaders mimeHeaders = new javax.xml.soap.MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "text/xml");

        int nAttachments = 0;
        java.util.Iterator iterator = msg.getAttachments(mimeHeaders);
	    while (iterator.hasNext()) {
            nAttachments++;
	        AttachmentPart ap = (AttachmentPart)iterator.next();
	        assertTrue(ap.equals(a1) || ap.equals(a2));
	    }
        assertTrue(nAttachments==2);
    }

    public static void main(String[] args) throws Exception {
        test.saaj.TestAttachment tester = new test.saaj.TestAttachment("TestSAAJ");
        tester.testMultipleAttachments();
        tester.testStringAttachment();
    }
}
