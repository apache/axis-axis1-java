package org.apache.axis.rpc;

import java.util.Vector;
import java.io.NotSerializableException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.SOAPBodyEntry;
import org.apache.axis.message.impl.Constants;
import org.apache.axis.message.impl.SOAPDocumentImpl;
import org.apache.axis.rpc.encoding.Encoder;
import org.apache.axis.rpc.encoding.Decoder;
import org.apache.axis.rpc.encoding.NoSuchTypeMappingException;
import org.apache.axis.rpc.encoding.MalformedEncodingException;

abstract class Message {
    public static final String NSPREFIX_STRUCT = "m";
    public static final String URI_STRUCT =
        "http://www.trl.ibm.com/soap/rpc/RPCEnc";
    final SOAPDocument doc;

    Message(SOAPDocument doc) { this.doc = doc; }

    Message(String structName, Document fac) {
        this.doc = new SOAPDocumentImpl(fac);
        this.doc.setEnvelope(doc.createEnvelope());

        SOAPBodyEntry struct = createStruct(doc, structName);

        doc.getEnvelope().getBody().addBodyEntry(struct);
    }

    Message(String structName,
            Parameter[] params,
            Encoder enc,
            Document fac)
        throws NoSuchTypeMappingException, NotSerializableException
    {
        this(structName, fac);
        if (params != null) {
            SOAPBodyEntry struct = doc.getEnvelope().getBody().getBodyEntry(0);
            Element[] elems = createParameters(enc, params);
            for (int i = 0; i < elems.length; i++)
                struct.getDOMEntity().appendChild(elems[i]);
        }

    }

    private SOAPBodyEntry createStruct(SOAPDocument doc, String structName) {
        SOAPBodyEntry struct = doc.createBodyEntry(doc.getDocument().createElementNS(URI_STRUCT, NSPREFIX_STRUCT+':'+structName));
        struct.declareNamespace(URI_STRUCT, NSPREFIX_STRUCT);
        struct.declareNamespace(Constants.URI_SOAP_ENC,
                                Constants.NSPREFIX_SOAP_ENC);
        return struct;
    }

    private Element[] createParameters(Encoder enc, Parameter[] params)
        throws NoSuchTypeMappingException, NotSerializableException
    {
        for (int i = 0; i < params.length; i++)
            enc.encodeRoot(params[i].getType(),
                           params[i].getName(),
                           params[i].getValue());
        return enc.getEncodedObjects();
    }

    SOAPBodyEntry getStruct() {
        return doc.getEnvelope().getBody().getBodyEntry(0);
    }

    String getStructName() {
        return getStruct().getDOMEntity().getLocalName();
    }

    public SOAPDocument getSOAPDocument() { return doc; }

    Object[] getParameters(Decoder dec, Class[] types)
        throws MalformedEncodingException,
               NoSuchTypeMappingException,
               NoSuchFieldException,
               InstantiationException,
               IllegalAccessException
    {
        NodeList list = getStruct().getDOMEntity().getChildNodes();
        int length = list.getLength();
        Vector buf = new Vector();
        for (int i = 0; i < length; i++)
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                buf.addElement(list.item(i));
        Element[] elems = new Element[buf.size()];
        for (int i = 0; i < elems.length; i++)
            elems[i] = (Element)buf.elementAt(i);
        return dec.decodeRoot(types, elems);
    }
}
