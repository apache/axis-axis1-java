/**
 * AttachmentsBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.dime.rpc;

public class AttachmentsBindingImpl implements test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType{
    public test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary echoAttachment(test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary in) throws java.rmi.RemoteException {
        return in;
    }

    public test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[] echoAttachments(test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[] in) throws java.rmi.RemoteException {
        return in;
    }

    public byte[] echoAttachmentAsBase64(test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary in) throws java.rmi.RemoteException {
        return in.getValue();
    }

    public test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary echoBase64AsAttachment(byte[] in) throws java.rmi.RemoteException {
        return new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary(in);
    }

    public void echoUnrefAttachments() throws java.rmi.RemoteException {
        //TODO: What should we do here?
    }

    public java.lang.String echoAttachmentAsString(test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedText in) throws java.rmi.RemoteException {
        return new String(in.getValue());
    }
}
