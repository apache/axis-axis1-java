/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis.handlers;

import java.security.Key ;
import java.security.cert.Certificate ;

import org.w3c.dom.NodeList ;
import org.w3c.dom.Element ;

import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.util.Logger ;
import org.apache.axis.util.xml.DOMHandler ;

import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.SOAPHeaderEntry;

public class Signer implements Handler {
    public static final String NS_URI_DSIG = "http://www.w3.org/2001/07/xmldsig#" ;
    public static final String NS_URI_SIGREQ = "http://www.ibm.com/soap/security/Signer" ;
    public static final String ELEM_REFERENCE = "Reference" ;
    public static final String ELEM_SIGREQ = "SignatureRequest" ;
    public static final String ID_BODY = "body" ;

    private final String actor ;
    private final Key key ;
    private final Certificate cert ;
    private final String verifierURI ;

    /**
     * Creates a Signer which attaches a signature for all messages.
     * The object of signature is <code>&lt;SOAP-ENV:Body&gt;</code>.
     * @param key A private key for signature
     * @param cert A certificate for signature
     * @param verifierURI The verifier URI
     */
    public Signer(Key key, Certificate cert, String verifierURI) {
        this(key, cert, verifierURI, null);
    }

    /**
     * Creates a Signer which attaches signatures for messages which have
     * <code>&lt;SignatureRequest&gt;</code> header entry.
     * The objects of signature should be specified in the entry.
     * @param key A private key for signature
     * @param cert A certificate for signature
     * @param actor The actor URI for this instance
     */
    public Signer(Key key, Certificate cert, String verifierURI, String actor) {
        this.key = key ;
        this.cert = cert ;
        this.verifierURI = verifierURI ;
        this.actor = actor ;
    }

    public void init(){}
    public void cleanup(){}
    public void invoke(MessageContext context) 
    // throws MustUnderstandException 
    {
        SOAPDocument msg= context.getMessage() ;
        try {
            processRequests(msg) ;
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void processRequests(SOAPDocument doc)
    //    throws MustUnderstandException
        throws Exception
    {
        if (actor != null) {
            SOAPHeaderEntry[] entries = (SOAPHeaderEntry[])doc.getEnvelope().getHeader().getHeaderEntries();
            for (int i = 0 ; i < entries.length ; i++)
                if (isRequest(entries[i]))
                    processRequest(entries[i], doc) ;
        } else {
            String uri = "#" + ID_BODY;
            Logger.normal("Attaching a signature to '" + uri + "'...", 4) ;
            doc.getEnvelope().getBody().getDOMEntity().setAttribute(Signature.ATTR_ID, ID_BODY);
            try {
                Signature.getInstance().sign(doc, uri, key, cert, verifierURI) ;
            } catch (Exception e) {
//                throw new MustUnderstandException(e.getMessage());
                e.printStackTrace();
            }
            Logger.normal("Done.", 4) ;
        }
    }

    private boolean isRequest(SOAPHeaderEntry entry) {
        Element elem = entry.getDOMEntity();
        return (NS_URI_SIGREQ.equals(elem.getNamespaceURI()) &&
                ELEM_SIGREQ.equals(elem.getLocalName()) &&
                actor.equals(entry.getActor())) ;
    }

    private void processRequest(SOAPHeaderEntry entry, SOAPDocument doc)
//        throws MustUnderstandException
        throws Exception
    {
        NodeList list = entry.getDOMEntity().getElementsByTagNameNS(NS_URI_DSIG, ELEM_REFERENCE) ;
        int length ;

        if ((length = list.getLength()) == 0)
            throw new Exception( "Signer.processRequest: 1");
 //           throw new MustUnderstandException("No 'Reference' found") ;

        String uri ;
        for (int i = 0 ; i < length ; i++) {
            if ("".equals(uri = ((Element)list.item(i)).getAttribute("URI")))
//                throw new MustUnderstandException("No 'URI' attribute found in 'Reference'") ;
                  throw new Exception( "Signer.processRequest: 2");

            Logger.normal("Attaching a signature to '" + uri + "'...", 4) ;
            try {
                Signature.getInstance().sign(doc, uri, key, cert, verifierURI) ;
            } catch (Exception e) {
//                throw new MustUnderstandException(e.getMessage());
                  throw new Exception( "Signer.processRequest: 3");
            }
            doc.getEnvelope().getHeader().removeHeaderEntry(entry) ;
            Logger.normal("Done.", 4) ;
        }
    }
}
