/**
 * MimeRPCInteropTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.mime.rpc;

import org.apache.axis.attachments.OctetStream;

import java.util.Arrays;
import java.net.URL;

public class MimeRPCInteropTestCase extends junit.framework.TestCase {
    public MimeRPCInteropTestCase(java.lang.String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        if(url == null) {
            url = new URL(new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPortAddress());
        }
    }    

    public void test1MimeRPCSoapPortEchoAttachment() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort(url);
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        OctetStream input = new OctetStream("EchoAttachment".getBytes());
        OctetStream output = null;
        output = binding.echoAttachment(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input.getBytes(), output.getBytes()));
    }

    public void test2MimeRPCSoapPortEchoAttachments() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort(url);
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        OctetStream[] input = new OctetStream[2];

        input[0] = new OctetStream("EchoAttachments0".getBytes());
        input[1] = new OctetStream("EchoAttachments1".getBytes());
        
        // Test operation
        OctetStream[] output = null;
        output = binding.echoAttachments(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input[0].getBytes(), output[0].getBytes()));
        assertTrue(Arrays.equals(input[1].getBytes(), output[1].getBytes()));
    }

    public void test3MimeRPCSoapPortEchoAttachmentAsBase64() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort(url);
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);
        OctetStream input = new OctetStream("EchoAttachmentAsBase64".getBytes()); 
        // Test operation
        byte[] output = null;
        output = binding.echoAttachmentAsBase64(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input.getBytes(), output));
    }

    public void test4MimeRPCSoapPortEchoBase64AsAttachment() throws Exception {
        test.wsdl.interop4.groupG.mime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPort(url);
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        byte[] input = "EchoBase64AsAttachment".getBytes(); 
        // Test operation
        OctetStream output = null;
        output = binding.echoBase64AsAttachment(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input, output.getBytes()));
    }

    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new test.wsdl.interop4.groupG.mime.rpc.MimeRPCInteropLocator().getMimeRPCSoapPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(MimeRPCInteropTestCase.class));
    } // main
}
