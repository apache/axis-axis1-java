/**
 * DimeDOCInteropTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.dime.doc;

import java.net.URL;
import java.util.Arrays;

import org.apache.axis.attachments.OctetStream;

public class DimeDOCInteropTestCase extends junit.framework.TestCase {
    public DimeDOCInteropTestCase(java.lang.String name) {
        super(name);
    }
    protected void setUp() throws Exception {
        if(url == null) {
            url = new URL(new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPortAddress());
        }
    }    
    public void test1DimeDOCSoapPortEchoAttachment() throws Exception {
        test.wsdl.interop4.groupG.dime.doc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
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

    public void test2DimeDOCSoapPortEchoAttachments() throws Exception {
        test.wsdl.interop4.groupG.dime.doc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        OctetStream[] input = new OctetStream[2];

        input[0] = new OctetStream("EchoAttachments0".getBytes());
        input[1] = new OctetStream("EchoAttachments1".getBytes());
        
        //TODO: Need to fix wsdl2java generation. Getting a "Bad Types" 
        //      Exception if we enable the following code.
        /*
        // Test operation
        OctetStream[] output = null;
        output = binding.echoAttachments(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input[0].getBytes(), output[0].getBytes()));
        assertTrue(Arrays.equals(input[1].getBytes(), output[1].getBytes()));
        */
    }

    public void test3DimeDOCSoapPortEchoAttachmentAsBase64() throws Exception {
        test.wsdl.interop4.groupG.dime.doc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
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

    public void test4DimeDOCSoapPortEchoBase64AsAttachment() throws Exception {
        test.wsdl.interop4.groupG.dime.doc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
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

    public void test5DimeDOCSoapPortEchoUnrefAttachments() throws Exception {
        test.wsdl.interop4.groupG.dime.doc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPort(url);
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

    public void test6DimeDOCSoapPortEchoAttachmentAsString() throws Exception {
        test.wsdl.interop4.groupG.dime.doc.AttachmentsPortType binding;
        try {
            binding = new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPort(url);
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
        assertEquals(input, output);
    }

    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(DimeDOCInteropTestCase.class));
    } // main
}
