/**
 * DimeRPCInteropTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.dime.rpc;

import java.util.Arrays;

public class DimeRPCInteropTestCase extends junit.framework.TestCase {
    public DimeRPCInteropTestCase(java.lang.String name) {
        super(name);
    }
    public void test1DimeRPCSoapPortEchoAttachment() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary input = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary("EchoAttachment".getBytes());
        // Test operation
        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary output = null;
        output = binding.echoAttachment(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input.getValue(),output.getValue()));
    }

    public void test2DimeRPCSoapPortEchoAttachments() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary input[] = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[2];
        input[0] = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary("EchoAttachments0".getBytes()); 
        input[1] = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary("EchoAttachments1".getBytes());
        
        // Test operation
        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[] output = null;
        output = binding.echoAttachments(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input[0].getValue(),output[0].getValue()));
        assertTrue(Arrays.equals(input[1].getValue(),output[1].getValue()));
    }

    public void test3DimeRPCSoapPortEchoAttachmentAsBase64() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary input = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary("EchoAttachmentAsBase64".getBytes());
        // Test operation
        byte[] output = null;
        output = binding.echoAttachmentAsBase64(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input.getValue(),output));
    }

    public void test4DimeRPCSoapPortEchoBase64AsAttachment() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        byte input[] = "EchoBase64AsAttachment".getBytes();
        // Test operation
        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary output = null;
        output = binding.echoBase64AsAttachment(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input,output.getValue()));
    }

    public void test5DimeRPCSoapPortEchoUnrefAttachments() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // TODO: What do we do here?
        // Test operation
        binding.echoUnrefAttachments();
        // TBD - validate results
    }

    public void test6DimeRPCSoapPortEchoAttachmentAsString() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedText input = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedText("3344");
        // Test operation
        java.lang.String output = null;
        output = binding.echoAttachmentAsString(input);
    }
}
