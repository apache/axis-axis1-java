/**
 * AttachmentsBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.mime.doc;

public class AttachmentsBindingImpl implements test.wsdl.interop4.groupG.mime.doc.AttachmentsPortType{
    public org.apache.axis.attachments.OctetStream echoAttachment(org.apache.axis.attachments.OctetStream in) throws java.rmi.RemoteException {
        return in;
    }

    public org.apache.axis.attachments.OctetStream[] echoAttachments(org.apache.axis.attachments.OctetStream item[]) throws java.rmi.RemoteException {
        return item;
    }

    public test.wsdl.interop4.groupG.mime.doc.xsd.Binary echoAttachmentAsBase64(org.apache.axis.attachments.OctetStream in) throws java.rmi.RemoteException {
        return new test.wsdl.interop4.groupG.mime.doc.xsd.Binary(in.getBytes());
    }

    public org.apache.axis.attachments.OctetStream echoBase64AsAttachment(test.wsdl.interop4.groupG.mime.doc.xsd.Binary in) throws java.rmi.RemoteException {
        return new org.apache.axis.attachments.OctetStream(in.getValue());
    }
}
