/**
 * AttachmentsBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.dime.rpc;

public class AttachmentsBindingImpl implements test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType{
    public org.apache.axis.attachments.OctetStream echoAttachment(org.apache.axis.attachments.OctetStream in) throws java.rmi.RemoteException {
        return in;
    }

    public test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[] echoAttachments(test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[] in) throws java.rmi.RemoteException {
        return in;
    }

    public byte[] echoAttachmentAsBase64(org.apache.axis.attachments.OctetStream in) throws java.rmi.RemoteException {
        return in.getBytes();
    }

    public org.apache.axis.attachments.OctetStream echoBase64AsAttachment(byte[] in) throws java.rmi.RemoteException {
        return new org.apache.axis.attachments.OctetStream(in);
    }

    public void echoUnrefAttachments() throws java.rmi.RemoteException {
    }

    public java.lang.String echoAttachmentAsString(java.lang.String in) throws java.rmi.RemoteException {
        return in;
    }
}
