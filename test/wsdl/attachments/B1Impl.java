/**
 * B1Impl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.attachments;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class B1Impl implements test.wsdl.attachments.Pt1 {

    private MimeMultipart createMimeMultipart(String data) throws MessagingException {
        // create the root multipart
        MimeMultipart mpRoot = new MimeMultipart("mixed");
        
        // Add text
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(data);
        mpRoot.addBodyPart(mbp1);
        return mpRoot;
    }

    public java.lang.String getCompanyInfo2(float result, java.lang.String docs, java.awt.Image logo) throws java.rmi.RemoteException {
        return docs;
    }

    public void inputPlainText(java.lang.String body) throws java.rmi.RemoteException {
    }

    public java.lang.String inoutPlainText(java.lang.String body) throws java.rmi.RemoteException {
        return body;
    }

    public java.lang.String echoPlainText(java.lang.String body) throws java.rmi.RemoteException {
        return body;
    }

    public java.lang.String outputPlainText() throws java.rmi.RemoteException {
        return "OutputPlainText";
    }

    public void inputMimeMultipart(javax.mail.internet.MimeMultipart body) throws java.rmi.RemoteException {
    }

    public javax.mail.internet.MimeMultipart inoutMimeMultipart(javax.mail.internet.MimeMultipart body) throws java.rmi.RemoteException {
        return body;
    }

    public javax.mail.internet.MimeMultipart echoMimeMultipart(javax.mail.internet.MimeMultipart body) throws java.rmi.RemoteException {
        return body;
    }

    public javax.mail.internet.MimeMultipart outputMimeMultipart() throws java.rmi.RemoteException {
        try {
            return createMimeMultipart("outputMimeMultipart");
        } catch (MessagingException me) {
            throw new java.rmi.RemoteException(me.getMessage(), me);
        }
    }

    public org.apache.axis.attachments.OctetStream echoAttachment(org.apache.axis.attachments.OctetStream in) throws java.rmi.RemoteException {
        return in;
    }
}
