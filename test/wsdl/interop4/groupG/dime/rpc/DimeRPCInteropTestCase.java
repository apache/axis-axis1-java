/**
 * DimeRPCInteropTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.dime.rpc;

import java.net.URL;

public class DimeRPCInteropTestCase extends junit.framework.TestCase {
    public DimeRPCInteropTestCase(java.lang.String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        if(url == null) {
            url = new URL(new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPortAddress());
        }
    }    
    public void test1DimeRPCSoapPortEchoAttachment() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.attachments.OctetStream input = new org.apache.axis.attachments.OctetStream("EchoAttachment".getBytes());
        org.apache.axis.attachments.OctetStream output = null;
        output = binding.echoAttachment(input);
        // TBD - validate results
    }

    public void test2DimeRPCSoapPortEchoAttachments() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[] input = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[2];
        input[0] = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary("EchoAttachments0".getBytes());
        input[1] = new test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary("EchoAttachments1".getBytes());
        test.wsdl.interop4.groupG.dime.rpc.xsd.ReferencedBinary[] output = null;
        output = binding.echoAttachments(input);
        // TBD - validate results
    }

    public void test3DimeRPCSoapPortEchoAttachmentAsBase64() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        org.apache.axis.attachments.OctetStream input = new org.apache.axis.attachments.OctetStream("EchoAttachmentAsBase64".getBytes());
        // Test operation
        byte[] value = null;
        value = binding.echoAttachmentAsBase64(input);
        // TBD - validate results
    }

    public void test4DimeRPCSoapPortEchoBase64AsAttachment() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        byte[] input = "EchoBase64AsAttachment".getBytes();
        // Test operation
        org.apache.axis.attachments.OctetStream output = null;
        output = binding.echoBase64AsAttachment(input);
        // TBD - validate results
    }

    public void test5DimeRPCSoapPortEchoUnrefAttachments() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        binding.echoUnrefAttachments();
        // TBD - validate results
    }

    public void test6DimeRPCSoapPortEchoAttachmentAsString() throws Exception {
        test.wsdl.interop4.groupG.dime.rpc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        java.lang.String input = "EchoAttachmentAsString";
        
        // Test operation
        java.lang.String output = null;
        output = binding.echoAttachmentAsString(input);
        // TBD - validate results
    }

    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new test.wsdl.interop4.groupG.dime.rpc.DimeRPCInteropLocator().getDimeRPCSoapPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(DimeRPCInteropTestCase.class));
    } // main
}
