package test.md5attach;

import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.utils.Options;

/**
 * A convenient little test program which will send a message as is to
 * the server.  Useful for debugging interoperability problems or 
 * handling of ill-formed messages that are hard to reproduce programmatically.
 *
 * Accepts the standard options, followed by a list of files containing
 * the contents to be sent.
 */
public class MD5AttachTest {
    static void main(String[] args) throws Exception {
        Options opts = new Options(args);
        String action = opts.isValueSet('a');

        Call call = new Call(opts.getURL());
        if (action != null) {
            call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
            call.setProperty(Call.SOAPACTION_URI_PROPERTY, action);
        }

        args = opts.getRemainingArgs();

        if (null == args || args.length != 1) {
            System.err.println("Must specify file to send as an attachment!");
            System.exit(8);
        }

        //Create the attachment.
        javax.activation.DataHandler dh = new javax.activation.DataHandler(new javax.activation.FileDataSource(args[0]));

        org.apache.axis.message.SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();

        //Build the body elements.
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();
        org.w3c.dom.Element methodElement = doc.createElementNS("foo", "foo:MD5Attach");
        org.w3c.dom.Element paramElement = doc.createElementNS("foo", "foo:thefile");
        long startTime = System.currentTimeMillis();
        methodElement.appendChild(paramElement);
        paramElement.appendChild(doc.createTextNode("" + startTime));


        org.apache.axis.message.SOAPBodyElement sbe = new org.apache.axis.message.SOAPBodyElement(methodElement);
        env.addBodyElement(sbe);

        org.apache.axis.Message msg = new org.apache.axis.Message(env);

        //Add the attachment content to the message.
        org.apache.axis.attachments.Attachments attachments = msg.getAttachments();
        org.apache.axis.Part attachmentPart = attachments.createAttachmentPart(dh);
        String href = attachmentPart.getContentId();
        //Have the parameter element set an href attribute to the attachment.
        paramElement.setAttribute(org.apache.axis.Constants.ATTR_HREF, href);
        env.clearBody();
        env.addBodyElement(sbe);

        msg.getSOAPPart().setSOAPEnvelope(env);

        call.setRequestMessage(msg);
        //go on now....
        call.invoke();

        MessageContext mc = call.getMessageContext();
        // System.out.println(mc.getResponseMessage().getAsString());
            
        env = mc.getResponseMessage().getSOAPPart().getAsSOAPEnvelope();
        sbe = env.getFirstBody();
        org.w3c.dom.Element sbElement = sbe.getAsDOM();
        //get the first level accessor  ie parameter
        org.w3c.dom.Node n = sbElement.getFirstChild();
        for (; n != null && !(n instanceof org.w3c.dom.Element); n = n.getNextSibling()) ;
        paramElement = (org.w3c.dom.Element) n;

        org.w3c.dom.Node respNode = paramElement.getFirstChild();
        long elapsedTime = -1;
        if (respNode != null && respNode instanceof org.w3c.dom.Text) {

            String respStr = ((org.w3c.dom.Text) respNode).getData();
            String timeStr = null;
            String MD5String = null;
            java.util.StringTokenizer st = new java.util.StringTokenizer(respStr);
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.startsWith("elapsedTime="))
                    timeStr = s.substring(12);
                else if (s.startsWith("MD5=")) MD5String = s.substring(4);
            }
            if (timeStr != null) {
                long time = Long.parseLong(timeStr);
                timeStr = (time / 100.0) + " sec.";
            } else {
                timeStr = "Unknown";
            }
            System.out.println("The time to send was:" + timeStr);
            if (MD5String == null) {
                System.err.println("Sorry no MD5 data was received.");
            } else {
                System.out.println("Calculating MD5 for local file...");
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] MD5received = org.apache.axis.encoding.Base64.decode(MD5String);
                java.io.InputStream attachmentStream = dh.getInputStream();
                int bread = 0;
                byte[] buf = new byte[64 * 1024];
                do {
                    bread = attachmentStream.read(buf);
                    if (bread > 0) {
                        md.update(buf, 0, bread);
                    }
                } while (bread > -1);
                attachmentStream.close();
                buf = null;
                //Add the mime type to the digest.
                String contentType = dh.getContentType();
                if (contentType != null && contentType.length() != 0) {
                    md.update(contentType.getBytes("US-ASCII"));
                }
                byte[] MD5orginal = md.digest();
                if (java.util.Arrays.equals(MD5orginal, MD5received)) {
                    System.out.println("All is well with Axis's attachment support!");
                    System.exit(0);
                } else {
                    System.err.println("Miss match in MD5");
                }
            }
        } else {
            System.err.println("Sorry no returned data.");
        }
        System.err.println("You've found a bug:\"http://nagoya.apache.org/bugzilla/\"");
        System.exit(8);
    }

}
