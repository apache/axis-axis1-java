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

import java.io.IOException;
import java.security.Key;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.xml.dsig.SignatureStructureException;
import com.ibm.xml.dsig.Validity;
import com.ibm.xml.dsig.Reference;
import com.ibm.xml.dsig.Canonicalizer;
import com.ibm.xml.dsig.DigestMethod;
import com.ibm.xml.dsig.IDResolver;
import com.ibm.xml.dsig.KeyInfoGeneratorX509;
import com.ibm.xml.dsig.SignatureGenerator;
import com.ibm.xml.dsig.SignatureMethod;
import com.ibm.xml.dsig.SignatureStructureException;
import com.ibm.xml.dsig.Transform;
import com.ibm.xml.dsig.TransformException;
import com.ibm.xml.dsig.XSignature;
import com.ibm.xml.dsig.TransformException;

import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.impl.SOAPDocumentImpl;
import org.apache.axis.SOAPException;

import org.apache.axis.util.Logger;
import org.apache.axis.util.xml.DOMConverter;

final public class Signature {
    public static final String URI_SOAP_SEC =
        "http://schemas.xmlsoap.org/soap/security/";
    private static final String PROPERTY_ID = "com.ibm.trl.soapimpl.security.SignatureIDAttribute";
    public static final String ATTR_ID_DEFAULT = "id";
    public static final String ATTR_ID;
    static {
        String value;
        if ((value = System.getProperties().getProperty(PROPERTY_ID)) != null)
            ATTR_ID = value;
        else
            ATTR_ID = ATTR_ID_DEFAULT;
    }

    private class IDResolverImpl implements IDResolver {
        public Element resolveID(Document doc, String id) {
            if (doc == null)
                return null;
            return resolveID(new SOAPDocumentImpl(doc).getEnvelope().getBody().getDOMEntity(), id);
        }
        private Element resolveID(Element element, String id) {
            String value;

            // this element is identified by the 'id'.
            if (!"".equals(value = element.getAttribute(ATTR_ID)) &&
                id.equals(value))
                return element;

            // searches in the child nodes
            NodeList list = element.getChildNodes();
            int length = list.getLength();

            Node node;
            Element elem;
            for (int i = 0; i < length; i++)
                if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE &&
                    (elem = resolveID((Element)node, id)) != null)
                    return elem; // found

            // not found in this subtree.
            return null;
        }
    }

    private IDResolver idResolver = new IDResolverImpl();
    private Signature() { }
    private static final Signature singleton = new Signature();
    static public Signature getInstance() { return singleton; }

    public void sign(SOAPDocument env, String uri,
                     Key key, Certificate cert,
                     String verifierURI)
        throws IOException,
               SOAPException,
               TransformException,
               SignatureException,
               SignatureStructureException,
               InvalidKeyException,
               NoSuchAlgorithmException,
               NoSuchProviderException,
               SAXException
    {
        if (!(cert instanceof X509Certificate)) {
            String name = cert == null ? null : cert.getClass().getName();
            throw new SOAPException("The cert " + name +
                                    " is not supported in " + 
                                    getClass().getName() + ".");
        }
        Element bodyElement = env.getEnvelope().getBody().getDOMEntity();

        SignatureGenerator siggen = new SignatureGenerator(env.getDocument(), DigestMethod.SHA1, Canonicalizer.W3C, SignatureMethod.DSA, null);
        siggen.setKeyInfoGenerator(new KeyInfoGeneratorX509((X509Certificate)cert));

        Element headerElement = env.getEnvelope().getHeader().getDOMEntity();
        Reference ref = siggen.createReference(uri);
        ref.addTransform(Transform.W3CC14N);
        siggen.addReference(ref);

        Element sigElem = siggen.getSignatureElement();
        XSignature.sign(sigElem, key, null, idResolver, null, null);

        Element soapSecurity = 
//        (Element)com.ibm.trl.util.xml.DOMHandler.importNode(env.getDocument(), DOMConverter.toDOM("<SOAP-SEC:signature xmlns:SOAP-SEC=\"" + URI_SOAP_SEC + "\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" SOAP-ENV:actor=\"" + verifierURI + "\" SOAP-ENV:mustUnderstand=\"1\"/>").getDocumentElement());
        (Element)env.getDocument().importNode(DOMConverter.toDOM("<SOAP-SEC:signature xmlns:SOAP-SEC=\"" + URI_SOAP_SEC + "\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" SOAP-ENV:actor=\"" + verifierURI + "\" SOAP-ENV:mustUnderstand=\"1\"/>").getDocumentElement(),true);
        soapSecurity.appendChild(sigElem);

        env.getEnvelope().getHeader().addHeaderEntry(env.createHeaderEntry(soapSecurity));
    }

    public Element getSignatureElement(SOAPDocument env)
//        throws MustUnderstandException
        throws SOAPException
    {
//        NodeList list = com.ibm.trl.util.xml.DOMHandler.getElementsByTagNameNS(env.getEnvelope().getHeader().getDOMEntity(), URI_SOAP_SEC, "signature");
        NodeList list = env.getEnvelope().getHeader().getDOMEntity().getElementsByTagNameNS( URI_SOAP_SEC, "signature");   
        int length;
        if ((length = list.getLength()) == 0)
//            throw new MustUnderstandException("No signature entry found.");
            throw new SOAPException("No signature entry found.");
        if (length > 1)
//            throw new MustUnderstandException("More than one signature entries found.");
            throw new SOAPException("More than one signature entries found.");
        Element element;
        if ((element = (Element)((Element)list.item(0)).getFirstChild()) == null)
//            throw new MustUnderstandException("No signature element found.");
            throw new SOAPException("No signature element found.");
        return element;
    }

    public boolean verify(SOAPDocument env) 
        throws CertificateException,
               NoSuchAlgorithmException,
               InvalidKeySpecException,
//               MustUnderstandException,
               SOAPException
    {
        Validity validity = XSignature.verify(getSignatureElement(env), null, idResolver);
        Logger.normal("Core validity=" + validity.getCoreValidity(), 4);
        Logger.normal("Signed info validity=" + validity.getSignedInfoValidity(), 4);
        Logger.normal("Signed info message=" + validity.getSignedInfoMessage(), 4);
        int count = validity.getNumberOfReferences();
        for (int i = 0; i < count; i++) {
            Logger.normal("Ref["+i+"](validity=" + validity.getReferenceValidity(i)+", message=" + validity.getReferenceMessage(i)+", uri=" + validity.getReferenceURI(i)+", type=" + validity.getReferenceType(i)+")", 4);
        }
        return validity.getCoreValidity();
    }
}
