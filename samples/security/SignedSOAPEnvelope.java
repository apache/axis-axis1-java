/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samples.security;

import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;


public class SignedSOAPEnvelope extends SOAPEnvelope {
    static String SOAPSECNS = "http://schemas.xmlsoap.org/soap/security/2000-12";
    static String SOAPSECprefix = "SOAP-SEC";

    static String keystoreType = "JKS";
    static String keystoreFile = "keystore.jks";
    static String keystorePass = "xmlsecurity";
    static String privateKeyAlias = "test";
    static String privateKeyPass = "xmlsecurity";
    static String certificateAlias = "test";
    private MessageContext msgContext;

    static {
        org.apache.xml.security.Init.init();
    }

    public SignedSOAPEnvelope(MessageContext msgContext, SOAPEnvelope env, String baseURI, String keystoreFile) {
        this.msgContext = msgContext;
        init(env, baseURI, keystoreFile);
    }

    public SignedSOAPEnvelope(SOAPEnvelope env, String baseURI) {
        init(env, baseURI, keystoreFile);
    }

    private void init(SOAPEnvelope env, String baseURI, String keystoreFile) {
        try {
            System.out.println("Beginning Client signing...");
            env.addMapping(new Mapping(SOAPSECNS, SOAPSECprefix));
            env.addAttribute(Constants.URI_SOAP11_ENV, "actor", "some-uri");
            env.addAttribute(Constants.URI_SOAP11_ENV, "mustUnderstand", "1");

            SOAPHeaderElement header = 
                new SOAPHeaderElement(XMLUtils.StringToElement(SOAPSECNS,
                                                               "Signature",
                                                               ""));
            env.addHeader(header);

            Document doc = getSOAPEnvelopeAsDocument(env, msgContext);

            KeyStore ks = KeyStore.getInstance(keystoreType);
            FileInputStream fis = new FileInputStream(keystoreFile);

            ks.load(fis, keystorePass.toCharArray());

            PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
                    privateKeyPass.toCharArray());

            Element soapHeaderElement = (Element) ((Element) doc.getFirstChild()).getElementsByTagNameNS("*", "Header").item(0);
            Element soapSignatureElement = (Element) soapHeaderElement.getElementsByTagNameNS("*", "Signature").item(0);

            XMLSignature sig = new XMLSignature(doc, baseURI,
                    XMLSignature.ALGO_ID_SIGNATURE_DSA);

            soapSignatureElement.appendChild(sig.getElement());
            sig.addDocument("#Body");


            X509Certificate cert =
                    (X509Certificate) ks.getCertificate(certificateAlias);


            sig.addKeyInfo(cert);
            sig.addKeyInfo(cert.getPublicKey());
            sig.sign(privateKey);

            Canonicalizer c14n = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
            byte[] canonicalMessage = c14n.canonicalizeSubtree(doc);

            InputSource is = new InputSource(new java.io.ByteArrayInputStream(canonicalMessage));
            DeserializationContextImpl dser = null;
            if (msgContext == null) {
                AxisClient tmpEngine = new AxisClient(new NullProvider());
                msgContext = new MessageContext(tmpEngine);
            }
            dser = new DeserializationContextImpl(is, msgContext,
                    Message.REQUEST, this);

            dser.parse();
            System.out.println("Client signing complete.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }

    private Document getSOAPEnvelopeAsDocument(SOAPEnvelope env, MessageContext msgContext)
            throws Exception {
        StringWriter writer = new StringWriter();
        SerializationContext serializeContext = new SerializationContextImpl(writer, msgContext);
        env.output(serializeContext);
        writer.close();

        Reader reader = new StringReader(writer.getBuffer().toString());
        Document doc = XMLUtils.newDocument(new InputSource(reader));
        if (doc == null)
            throw new Exception(
                    Messages.getMessage("noDoc00", writer.getBuffer().toString()));
        return doc;
    }
}
