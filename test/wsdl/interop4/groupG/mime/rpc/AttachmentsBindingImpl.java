/**
 * AttachmentsBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.mime.rpc;

import org.apache.axis.attachments.OctetStream;

public class AttachmentsBindingImpl implements test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType{
    public OctetStream echoAttachment(OctetStream in) throws java.rmi.RemoteException {
        return in;
    }

    public OctetStream echoAttachments(OctetStream in) throws java.rmi.RemoteException {
        return in;
    }

    public byte[] echoAttachmentAsBase64(OctetStream in) throws java.rmi.RemoteException {
        return in.getBytes();
    }

    public OctetStream echoBase64AsAttachment(byte[] in) throws java.rmi.RemoteException {
        return new OctetStream(in);
    }

}
