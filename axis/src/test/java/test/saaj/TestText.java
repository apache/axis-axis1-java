package test.saaj;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPPart;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;

public class TestText extends junit.framework.TestCase {

    public TestText(String name) {
        super(name);
    }

    // Test SAAJ addTextNode performance
    public void testAddTextNode() throws Exception {
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPHeader header = message.getSOAPHeader();
        SOAPBody body = message.getSOAPBody();

        // Create the base element
        Name bodyName = soapFactory.createName("VBGenReceiver", "xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

        // Create the MetaData Tag
        Name name = soapFactory.createName("MetaData");
        SOAPElement metaData = bodyElement.addChildElement(name);

        //Create the SKey Tag
        name = soapFactory.createName("SKey");
        SOAPElement sKey = metaData.addChildElement(name);
        sKey.addTextNode("SKEY001");

        //Create Object Tag
        name = soapFactory.createName("Object");
        SOAPElement object = bodyElement.addChildElement(name);

        //Create Book ID Tag
        name = soapFactory.createName("BookID");
        SOAPElement bookID = object.addChildElement(name);
        bookID.addTextNode("BookID002");

        //Create OrderID tag
        name = soapFactory.createName("OrderID");
        SOAPElement orderID = object.addChildElement(name);
        orderID.addTextNode("OrderID003");

        //create PurchaseID tage
        name = soapFactory.createName("PurchaseID");
        SOAPElement purchaseID = object.addChildElement(name);
        purchaseID.addTextNode("PurchaseID005");

        //create LanguageID Tag
        name = soapFactory.createName("LanguageID");
        SOAPElement languageID = object.addChildElement(name);
        languageID.addTextNode("LanguageID004");

        //create LanguageID Tag
        name = soapFactory.createName("LanguageName");
        SOAPElement languageName = object.addChildElement(name);
        languageName.addTextNode("LanguageName006");

        //create LanguageID Tag
        name = soapFactory.createName("Title");
        SOAPElement title = object.addChildElement(name);
        title.addTextNode("Title007");

        //create LanguageID Tag
        name = soapFactory.createName("Author");
        SOAPElement author = object.addChildElement(name);
        author.addTextNode("Author008");

        //create LanguageID Tag
        name = soapFactory.createName("Format");
        SOAPElement format = bodyElement.addChildElement(name);

        //create LanguageID Tag
        name = soapFactory.createName("Type");
        SOAPElement formatType = format.addChildElement(name);
        formatType.addTextNode("Type009");

        //create LanguageID Tag
        name = soapFactory.createName("Delivery");
        SOAPElement delivery = bodyElement.addChildElement(name);

        //create LanguageID Tag
        name = soapFactory.createName("Name");
        SOAPElement delName = delivery.addChildElement(name);
        delName.addTextNode("Name010");

        //create LanguageID Tag
        name = soapFactory.createName("Address1");
        SOAPElement address1 = delivery.addChildElement(name);
        address1.addTextNode("Address1011");

        //create LanguageID Tag
        name = soapFactory.createName("Address2");
        SOAPElement address2 = delivery.addChildElement(name);
        address2.addTextNode("Address2012");

        //create LanguageID Tag
        name = soapFactory.createName("City");
        SOAPElement city = delivery.addChildElement(name);
        city.addTextNode("City013");

        //create LanguageID Tag
        name = soapFactory.createName("State");
        SOAPElement state = delivery.addChildElement(name);
        state.addTextNode("State014");

        //create LanguageID Tag
        name = soapFactory.createName("PostalCode");
        SOAPElement postalCode = delivery.addChildElement(name);
        postalCode.addTextNode("PostalCode015");

        System.out.println("The message is lll:\n");
        message.writeTo(System.out);
    }
    
    public void testTraverseDOM() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<book year=\"1992\">\n" +
                "  <title>Advanced Programming in the Unix environment</title>\n" +
                "  <author><last>Stevens</last><first>W.</first></author>\n" +
                "  <publisher>Addison-Wesley</publisher>\n" +
                "  <price>65.95</price>\n" +
                "</book>\n" +
                "";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document payload = builder.parse(new ByteArrayInputStream(xml.getBytes()));

        MessageFactory soapMsgFactory = MessageFactory.newInstance();
        SOAPMessage soapMsg = soapMsgFactory.createMessage();

        SOAPPart soapPart = soapMsg.getSOAPPart();
        SOAPEnvelope soapEnv = soapPart.getEnvelope();
        SOAPBody soapBody = soapEnv.getBody();

        soapBody.addDocument(payload);

        System.out.println("***************");
        soapMsg.writeTo(System.out);

        processNode(soapPart);
    }

    private void processNode(Node currentNode) {
        switch (currentNode.getNodeType()) {
            // process a Document node
            case Node.DOCUMENT_NODE:
                Document doc = (Document) currentNode;
                System.out.println("Document node: " + doc.getNodeName() +
                        "\nRoot element: " +
                        doc.getDocumentElement().getNodeName());
                processChildNodes(doc.getChildNodes());
                break;

                // process an Element node
            case Node.ELEMENT_NODE:
                System.out.println("\nElement node: " +
                        currentNode.getNodeName());
                NamedNodeMap attributeNodes =
                        currentNode.getAttributes();
                for (int i = 0; i < attributeNodes.getLength(); i++) {
                    Attr attribute = (Attr) attributeNodes.item(i);
                    System.out.println("\tAttribute: " +
                            attribute.getNodeName() + " ; Value = " +
                            attribute.getNodeValue());
                }
                processChildNodes(currentNode.getChildNodes());
                break;

                // process a text node and a CDATA section
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                Text text = (Text) currentNode;
                if (!text.getNodeValue().trim().equals(""))
                    System.out.println("\tText: " +
                            text.getNodeValue());
                break;
        }
    }

    private void processChildNodes(NodeList children) {
        if (children.getLength() != 0)
            for (int i = 0; i < children.getLength(); i++)
                processNode(children.item(i));
    }

    
    public static void main(String[] args) throws Exception {
        TestText tester = new TestText("TestEnvelope");
        tester.testAddTextNode();
    }
}
