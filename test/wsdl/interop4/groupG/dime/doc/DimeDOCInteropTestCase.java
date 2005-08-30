/**
 * DimeDOCInteropTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupG.dime.doc;

import java.net.URL;
import java.util.Arrays;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;

import org.apache.axis.attachments.OctetStream;
import org.apache.axis.client.Call;

import javax.activation.DataSource;
import javax.activation.DataHandler;

public class DimeDOCInteropTestCase extends junit.framework.TestCase {
    public DimeDOCInteropTestCase(java.lang.String name) {
        super(name);
    }

    public void testDimeDOCSoapPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getServiceName());
        assertTrue(service != null);
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
        
        // Test operation
        OctetStream[] output = null;
        output = binding.echoAttachments(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input[0].getBytes(), output[0].getBytes()));
        assertTrue(Arrays.equals(input[1].getBytes(), output[1].getBytes()));
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

        class Src implements DataSource{
            InputStream m_src;
            String m_type;

            public Src(InputStream data, String type){
                m_src=data;
                m_type=type;
            }
            public String getContentType(){
                return m_type;
            }
            public InputStream getInputStream() throws IOException{
                m_src.reset();
                return m_src;
            }
            public String getName(){
                return "Some-Data";
            }
            public OutputStream getOutputStream(){
                throw new UnsupportedOperationException("I don't give output streams");
            }
        }
        ByteArrayInputStream ins=new ByteArrayInputStream("EchoUnrefAttachments".getBytes());
        DataHandler dh=new DataHandler(new Src(ins,"application/octetstream"));

        
        ((org.apache.axis.client.Stub)binding).addAttachment(dh);
        ((org.apache.axis.client.Stub)binding)._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
        // Test operation
        binding.echoUnrefAttachments();
        // TBD - validate results
        Object attachments[] = ((org.apache.axis.client.Stub)binding).getAttachments();
        assertEquals(1, attachments.length);
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
