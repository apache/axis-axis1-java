/**
 * AttachmentTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.attachments;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class AttachmentTestCase extends junit.framework.TestCase {
    public AttachmentTestCase(java.lang.String name) {
        super(name);
    }

    private MimeMultipart createMimeMultipart(String data) throws Exception {
        // create the root multipart
        MimeMultipart mpRoot = new MimeMultipart("mixed");
        
        // Add text
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(data);
        mpRoot.addBodyPart(mbp1);
        return mpRoot;
    }

    public void test1AttachmentPortRPCGetCompanyInfo2() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.String value = null;
        value = binding.getCompanyInfo2(0, new java.lang.String("GetCompanyInfo2"), null);
        assertEquals(value, "GetCompanyInfo2");
    }

    public void test2AttachmentPortRPCInputPlainText() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        binding.inputPlainText(new java.lang.String("InputPlainText"));
        // TBD - validate results
    }

    public void test3AttachmentPortRPCInoutPlainText() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.String value = null;
        value = binding.inoutPlainText(new java.lang.String("InoutPlainText"));
        // TBD - validate results
        assertEquals(value, "InoutPlainText");
    }

    public void test4AttachmentPortRPCEchoPlainText() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.String value = null;
        value = binding.echoPlainText(new java.lang.String("EchoPlainText"));
        // TBD - validate results
        assertEquals(value, "EchoPlainText");
    }

    public void test5AttachmentPortRPCOutputPlainText() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.String value = null;
        value = binding.outputPlainText();
        // TBD - validate results
        assertEquals(value, "OutputPlainText");
    }

    public void test6AttachmentPortRPCInputMimeMultipart() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        binding.inputMimeMultipart(createMimeMultipart("InputMimeMultipart"));
        // TBD - validate results
    }

    public void test7AttachmentPortRPCInoutMimeMultipart() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        javax.mail.internet.MimeMultipart value = null;
        value = binding.inoutMimeMultipart(createMimeMultipart("InoutMimeMultipart"));
        // TBD - validate results
    }

    public void test8AttachmentPortRPCEchoMimeMultipart() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        javax.mail.internet.MimeMultipart value = null;
        value = binding.echoMimeMultipart(createMimeMultipart("EchoMimeMultipart"));
        // TBD - validate results
    }

    public void test9AttachmentPortRPCOutputMimeMultipart() throws Exception {
        test.wsdl.attachments.Pt1 binding;
        try {
            binding = new test.wsdl.attachments.AttachmentLocator().getAttachmentPortRPC();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        javax.mail.internet.MimeMultipart value = null;
        value = binding.outputMimeMultipart();
        // TBD - validate results
    }

}
