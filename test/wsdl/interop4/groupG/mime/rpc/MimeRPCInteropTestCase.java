/**
 * MimeRPCInteropTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.mime.rpc;

import java.util.Arrays;

public class MimeRPCInteropTestCase extends junit.framework.TestCase {
    public MimeRPCInteropTestCase(java.lang.String name) {
        super(name);
    }

    public void test1MimeRPCSoapPortEchoAttachment() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        byte[] input = "EchoAttachment".getBytes();
        byte[] output = null;
        output = binding.echoAttachment(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input, output));
    }

    public void test2MimeRPCSoapPortEchoAttachments() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        byte[][] input = new byte[2][];

        input[0] = "EchoAttachments0".getBytes();
        input[1] = "EchoAttachments1".getBytes();
        
        // Test operation
        byte[][] output = null;
        output = binding.echoAttachments(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input[0], output[0]));
        assertTrue(Arrays.equals(input[1], output[1]));
    }

    public void test3MimeRPCSoapPortEchoAttachmentAsBase64() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);
        byte[] input = "EchoAttachmentAsBase64".getBytes(); 
        // Test operation
        byte[] output = null;
        output = binding.echoAttachmentAsBase64(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input, output));
    }

    public void test4MimeRPCSoapPortEchoBase64AsAttachment() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        byte[] input = "EchoBase64AsAttachment".getBytes(); 
        // Test operation
        byte[] output = null;
        output = binding.echoBase64AsAttachment(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input, output));
    }

}
