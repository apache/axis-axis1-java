/**
 * DocumentTestSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.document;

import org.apache.axis.AxisFault;
import org.apache.axis.utils.XMLUtils;

import java.io.ByteArrayInputStream;

public class DocumentTestSoapBindingImpl implements test.wsdl.document.TestService{
    String xml = "<hello>world</hello>";

    public org.w3c.dom.Element getElement() throws java.rmi.RemoteException {
        try {
            return XMLUtils.newDocument(new ByteArrayInputStream(xml.getBytes())).getDocumentElement();
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public org.w3c.dom.Document getDocument() throws java.rmi.RemoteException {
        try {
            return XMLUtils.newDocument(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

}
