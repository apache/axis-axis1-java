/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 4. The names "Axis" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.encoding;

import org.apache.axis.AxisEngine;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.enum.Style;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.Call;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.XMLUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/** Manage a serialization, including keeping track of namespace mappings
 * and element stacks.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public class SerializationContextImpl implements SerializationContext
{
    protected static Log log =
            LogFactory.getLog(SerializationContextImpl.class.getName());

    private NSStack nsStack = new NSStack();
    private boolean writingStartTag = false;
    private boolean onlyXML = true;
    private int indent=0;
    private boolean startOfDocument = true;
    private Stack elementStack = new Stack();
    private Writer writer;
    private int lastPrefixIndex = 1;
    private MessageContext msgContext;

    /** The SOAP context we're using */
    private SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;

    private boolean pretty = false;
    private static QName multirefQName = new QName("","multiRef");

    /**
     * Should I write out objects as multi-refs?
     *
     * !!! For now, this is an all-or-nothing flag.  Either ALL objects will
     * be written in-place as hrefs with the full serialization at the end
     * of the body, or we'll write everything inline (potentially repeating
     * serializations of identical objects).
     */
    private boolean doMultiRefs = false;

    /**
     * Should I send an XML declaration?
     */
    private boolean sendXMLDecl = true;

    /**
     * Should I send xsi:type attributes?
     */
    private boolean sendXSIType = true;

    /**
     * A place to hold objects we cache for multi-ref serialization, and
     * remember the IDs we assigned them.
     */
    private HashMap multiRefValues = null;
    private int multiRefIndex = -1;

    class MultiRefItem {
        String id;
        Class javaType;
        QName xmlType;
        boolean sendType;
        MultiRefItem(String id, Class javaType, QName xmlType, boolean sendType) {
            this.id = id;
            this.javaType = javaType;
            this.xmlType = xmlType;
            this.sendType = sendType;
        }

    }
    /**
     * These three variables are necessary to process multi-level object graphs for multi-ref
     * serialization.
     * While writing out nested multi-ref objects (via outputMultiRef), we
     * will fill the secondLevelObjects vector with any new objects encountered.
     * The outputMultiRefsFlag indicates whether we are currently within the
     * outputMultiRef() method (so that serialization() knows to update the
     * secondLevelObjects vector).
     * The forceSer variable is the trigger to force actual serialization of the indicated object.
     */
    private HashSet secondLevelObjects = null;
    private Object forceSer = null;
    private boolean outputMultiRefsFlag = false;

    /**
     * Which schema version are we using?
     */
    SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;

    /**
     * Construct SerializationContextImpl with associated writer
     * @param writer java.io.Writer
     */
    public SerializationContextImpl(Writer writer)
    {
        this.writer = writer;
    }


    /**
     * Construct SerializationContextImpl with associated writer and MessageContext
     * @param writer java.io.Writer
     * @param msgContext is the MessageContext
     */
    public SerializationContextImpl(Writer writer, MessageContext msgContext)
    {
        this.writer = writer;
        this.msgContext = msgContext;

        AxisEngine engine = null ;
        if ( msgContext != null ) {
            // Use whatever schema is associated with this MC
            schemaVersion = msgContext.getSchemaVersion();

            engine = msgContext.getAxisEngine();
            Boolean shouldSendDecl = (Boolean)engine.getOption(
                                                  AxisEngine.PROP_XML_DECL);
            if (shouldSendDecl != null)
                sendXMLDecl = shouldSendDecl.booleanValue();

            Boolean shouldSendMultiRefs =
                  (Boolean)msgContext.getProperty(AxisEngine.PROP_DOMULTIREFS);

            if (shouldSendMultiRefs == null)
                shouldSendMultiRefs =
                        (Boolean)engine.getOption(AxisEngine.PROP_DOMULTIREFS);

            if (shouldSendMultiRefs != null)
                doMultiRefs = shouldSendMultiRefs.booleanValue();

            // The SEND_TYPE_ATTR and PROP_SEND_XSI options indicate
            // whether the elements should have xsi:type attributes.
            // Only turn this off is the user tells us to
            if ( !msgContext.isPropertyTrue(Call.SEND_TYPE_ATTR, true ))
                sendXSIType = false ;

            Boolean opt = (Boolean)engine.getOption(AxisEngine.PROP_SEND_XSI);
            if ((opt != null) && (opt.equals(Boolean.FALSE))) {
                    sendXSIType = false ;
            }

            // A Document-style service overrides the above settings. Don't
            // send xsi:type, and don't do multiref in that case.
            SOAPService service = msgContext.getService();
            if (service != null) {
                if (service.getStyle() != Style.RPC) {
                    sendXSIType = false;
                    doMultiRefs = false;
                }
            }
        }
    }

    /**
     * Get whether the serialization should be pretty printed.
     * @return true/false
     */
    public boolean getPretty() {
        return pretty;
    }

    /**
     * Indicate whether the serialization should be pretty printed.
     * @param pretty true/false
     */
    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }

    /**
     * Set whether we are doing multirefs
     */
    public void setDoMultiRefs (boolean shouldDo)
    {
        doMultiRefs = shouldDo;
    }

    /**
     * Set whether or not we should write XML declarations.
     * @param sendDecl true/false
     */
    public void setSendDecl(boolean sendDecl)
    {
        sendXMLDecl = sendDecl;
    }

    /**
     * Get whether or not to write xsi:type attributes.
     * @return true/false
     */
    public boolean shouldSendXSIType() {
        return sendXSIType;
    }

    /**
     * Get the TypeMapping we're using.
     * @return TypeMapping or null
     */
    public TypeMapping getTypeMapping()
    {
        // Always allow the default mappings
        if (msgContext == null)
            return DefaultTypeMappingImpl.getSingleton();

        String encodingStyle = msgContext.getEncodingStyle();
        if (encodingStyle == null)
            encodingStyle = soapConstants.getEncodingURI();
        return (TypeMapping) msgContext.
                        getTypeMappingRegistry().getTypeMapping(encodingStyle);
    }

    /**
     * Get the TypeMappingRegistry we're using.
     * @return TypeMapping or null
     */
    public TypeMappingRegistry getTypeMappingRegistry() {
        if (msgContext == null)
            return null;
        return (TypeMappingRegistry) msgContext.getTypeMappingRegistry();
    }

    /**
     * Get a prefix for a namespace URI.  This method will ALWAYS
     * return a valid prefix - if the given URI is already mapped in this
     * serialization, we return the previous prefix.  If it is not mapped,
     * we will add a new mapping and return a generated prefix of the form
     * "ns<num>".
     * @param uri is the namespace uri
     * @return prefix
     */
    public String getPrefixForURI(String uri)
    {
        return getPrefixForURI(uri, null, false);
    }

    /**
     * Get a prefix for the given namespace URI.  If one has already been
     * defined in this serialization, use that.  Otherwise, map the passed
     * default prefix to the URI, and return that.  If a null default prefix
     * is passed, use one of the form "ns<num>"
     */
    public String getPrefixForURI(String uri, String defaultPrefix)
    {
        return getPrefixForURI(uri, defaultPrefix, false);
    }
    public String getPrefixForURI(String uri, String defaultPrefix, boolean attribute)
    {
        if ((uri == null) || (uri.equals("")))
            return null;

        // If we're looking for an attribute prefix, we shouldn't use the 
        // "" prefix, but always register/find one.
        String prefix = nsStack.getPrefix(uri, attribute);

        if (prefix == null && uri.equals(soapConstants.getEncodingURI())) {
            prefix = Constants.NS_PREFIX_SOAP_ENC;
            registerPrefixForURI(prefix, uri);
        }
        
        if (prefix == null) {
            if (defaultPrefix == null) {
                prefix = "ns" + lastPrefixIndex++;
            } else {
                prefix = defaultPrefix;
            }
            registerPrefixForURI(prefix, uri);
        }

        return prefix;
    }

    /**
     * Register prefix for the indicated uri
     * @param prefix
     * @param uri is the namespace uri
     */
    public void registerPrefixForURI(String prefix, String uri)
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("register00", prefix, uri));
        }

        if ((uri != null) && (prefix != null)) {
            nsStack.add(uri, prefix);
        }
    }

    /**
     * Return the current message
     */
    public Message getCurrentMessage()
    {
        if (msgContext == null)
            return null;
        return msgContext.getCurrentMessage();
    }

    /**
     * Get the MessageContext we're operating with
     */
    public MessageContext getMessageContext() {
        return msgContext;
    }

    /**
     * Convert QName to a string of the form <prefix>:<localpart>
     * @param qName
     * @return prefixed qname representation for serialization.
     */
    public String qName2String(QName qName, boolean writeNS)
    {
        String prefix = null;

        if (qName.getNamespaceURI().equals("")) {
            if (writeNS) {
                // If this is unqualified (i.e. prefix ""), set the default
                // namespace to ""
                String defaultNS = nsStack.getNamespaceURI("");
                if (defaultNS != null) {
                    registerPrefixForURI("", "");
                }
            }
        } else {
            prefix = getPrefixForURI(qName.getNamespaceURI());
        }

        return (((prefix != null) && (!prefix.equals(""))) ?
                      prefix + ":" : "") +
           qName.getLocalPart();
    }

    public String qName2String(QName qName)
    {
        return qName2String(qName, false);
    }

    /**
     * Convert attribute QName to a string of the form <prefix>:<localpart>
     * There are slightly different rules for attributes:
     *  - There is no default namespace
     *  - any attribute in a namespace must have a prefix
     * 
     * @param QName
     * @return prefixed qname representation for serialization.
     */
    public String attributeQName2String(QName qName) {
        String prefix = null;

        if (! qName.getNamespaceURI().equals("")) {
            prefix = getPrefixForURI(qName.getNamespaceURI(), null, true);
        }

        String ret = (((prefix != null) && (!prefix.equals(""))) ?
                      prefix + ":" : "") +
           qName.getLocalPart();
        return ret;
    }

    /**
     * Get the QName associated with the specified class.
     * @param cls Class of an object requiring serialization.
     * @return appropriate QName associated with the class.
     */
    public QName getQNameForClass(Class cls)
    {
        return getTypeMapping().getTypeQName(cls);
    }

    /**
     * Indicates whether the object should be interpretted as a primitive
     * for the purposes of multi-ref processing.  A primitive value
     * is serialized directly instead of using id/href pairs.  Thus
     * primitive serialization/deserialization is slightly faster.
     * @param value to be serialized
     * @param javaType is the "real" java type of value.  Used to distinguish
     * between java primitives and their wrapper classes.
     * @return true/false
     */
    public boolean isPrimitive(Object value, Class javaType)
    {
        if (value == null) return true;

        if (javaType.isPrimitive()) return true;

        if (javaType == String.class) return true;
        if (Calendar.class.isAssignableFrom(javaType)) return true;
        if (Date.class.isAssignableFrom(javaType)) return true;
        if (Hex.class.isAssignableFrom(javaType)) return true;
        if (Element.class.isAssignableFrom(javaType)) return true;
        if (javaType == byte[].class) return true;

        // There has been discussion as to whether arrays themselves should
        // be regarded as multi-ref.
        // Here are the three options:
        //   1) Arrays are full-fledged Objects and therefore should always be
        //      multi-ref'd  (Pro: This is like java.  Con: Some runtimes don't
        //      support this yet, and it requires more stuff to be passed over the wire.)
        //   2) Arrays are not full-fledged Objects and therefore should
        //      always be passed as single ref (note the elements of the array
        //      may be multi-ref'd.) (Pro:  This seems reasonable, if a user
        //      wants multi-referencing put the array in a container.  Also
        //      is more interop compatible.  Con: Not like java serialization.)
        //   3) Arrays of primitives should be single ref, and arrays of
        //      non-primitives should be multi-ref.  (Pro: Takes care of the
        //      looping case.  Con: Seems like an obtuse rule.)
        //
        // Changing the code from (1) to (2) to see if interop fairs better.
        if (javaType.isArray()) return true;

        // Note that java.lang wrapper classes (i.e. java.lang.Integer) are
        // not primitives unless the corresponding type is an xsd type.
        // (If the wrapper maps to a soap encoded primitive, it can be nillable
        // and multi-ref'd).
        QName qName = getQNameForClass(javaType);
        if (qName != null && Constants.isSchemaXSD(qName.getNamespaceURI())) {
            if (qName.equals(Constants.XSD_BOOLEAN) ||
                qName.equals(Constants.XSD_DOUBLE) ||
                qName.equals(Constants.XSD_FLOAT) ||
                qName.equals(Constants.XSD_INT) ||
                qName.equals(Constants.XSD_LONG) ||
                qName.equals(Constants.XSD_SHORT) ||
                qName.equals(Constants.XSD_BYTE) ||
                qName.equals(Constants.XSD_STRING) ||
                qName.equals(Constants.XSD_INTEGER) ||
                qName.equals(Constants.XSD_DECIMAL)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive, the
     * javaType is the actual unwrapped object type.
     * The xmlType (if specified) is the QName of the type that is used to set
     * xsi:type.  If not specified, xsi:type is set by using the javaType to
     * find an appopriate xmlType from the TypeMappingRegistry.
     * The sendNull flag indicates whether null values should be sent over the
     * wire (default is to send such values with xsi:nil="true").
     * The sendType flag indicates whether the xsi:type flag should be sent
     * (default is true).
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param javaType is the "real" type of the value.
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          Class javaType)
        throws IOException {
        serialize(elemQName, attributes, value, javaType, null, true, true);
    }

    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive, the
     * javaType is the actual unwrapped object type.
     * The xmlType (if specified) is the QName of the type that is used to set
     * xsi:type.  If not specified, xsi:type is set by using the javaType to
     * find an appopriate xmlType from the TypeMappingRegistry.
     * The sendNull flag indicates whether null values should be sent over the
     * wire (default is to send such values with xsi:nil="true").
     * The sendType flag indicates whether the xsi:type flag should be sent
     * (default is true).
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param javaType is the "real" type of the value.
     * @param xmlType is the qname of the type or null.
     * @param sendNull determines whether to send null values.
     * @param sendType determines whether to set xsi:type attribute.
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          Class javaType,
                          QName xmlType,
                          boolean sendNull,
                          boolean sendType)
        throws IOException
    {
        if (value == null) {
            // If the value is null, the element is
            // passed with xsi:nil="true" to indicate that no object is present.
            if (sendNull) {
                AttributesImpl attrs = new AttributesImpl();
                if (attributes != null && 0 < attributes.getLength())
                    attrs.setAttributes(attributes);
                if (sendType)
                    attrs = (AttributesImpl) setTypeAttribute(attrs, xmlType);
                String nil = schemaVersion.getNilQName().getLocalPart();
                attrs.addAttribute(schemaVersion.getXsiURI(), nil, "xsi:" + nil,
                                   "CDATA", "true");
                startElement(elemQName, attrs);
                endElement();
            }
            return;
        }

        Message msg= getCurrentMessage();
        if(null != msg){
            //Get attachments. returns null if no attachment support.
            Attachments attachments= getCurrentMessage().getAttachmentsImpl();

            if( null != attachments && attachments.isAttachment(value)){
                //Attachment support and this is an object that should be treated as an attachment.

                //Allow an the attachment to do its own serialization.
                serializeActual(elemQName, attributes, value, javaType, xmlType, sendType);

                //No need to add to mulitRefs. Attachment data stream handled by
                // the message;
                return;
            }
        }

        // If multi-reference is enabled and this object value is not a primitive
        // and we are not forcing serialization of the object, then generate
        // an element href (and store the object for subsequent outputMultiRef
        // processing.
        if (doMultiRefs && (value != forceSer) && !isPrimitive(value, javaType)) {
            if (multiRefIndex == -1)
                multiRefValues = new HashMap();

            String id;
            MultiRefItem mri = (MultiRefItem)multiRefValues.get(value);
            if (mri == null) {
                multiRefIndex++;
                id = "id" + multiRefIndex;
                mri = new MultiRefItem (id, javaType, xmlType, sendType);
                multiRefValues.put(value, mri);

                /** Problem - if we're in the middle of writing out
                 * the multi-refs and hit another level of the
                 * object graph, we need to make sure this object
                 * will get written.  For now, add it to a list
                 * which we'll check each time we're done with
                 * outputMultiRefs().
                 */
                if (outputMultiRefsFlag) {
                    if (secondLevelObjects == null)
                        secondLevelObjects = new HashSet();
                    secondLevelObjects.add(value);
                }
            } else {
                id = mri.id;
            }

            AttributesImpl attrs = new AttributesImpl();
            if (attributes != null && 0 < attributes.getLength())
                attrs.setAttributes(attributes);
            attrs.addAttribute("", Constants.ATTR_HREF, "href",
                               "CDATA", "#" + id);

            startElement(elemQName, attrs);
            endElement();
            return;
        }

        // The forceSer variable is set by outputMultiRefs to force
        // serialization of this object via the serialize(...) call
        // below.  However, if the forced object contains a self-reference, we
        // get into an infinite loop..which is why it is set back to null
        // before the actual serialization.
        if (value == forceSer)
            forceSer = null;

        // Actually serialize the value.  (i.e. not an href like above)
        serializeActual(elemQName, attributes, value, javaType, xmlType, sendType);
    }

    /**
     * The serialize method uses hrefs to reference all non-primitive
     * values.  These values are stored and serialized by calling
     * outputMultiRefs after the serialize method completes.
     */
    public void outputMultiRefs() throws IOException
    {
        if (!doMultiRefs || (multiRefValues == null))
            return;
        outputMultiRefsFlag = true;
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("","","","","");

        String encodingURI = soapConstants.getEncodingURI();
        // explicitly state that this attribute is not a root
        String prefix = getPrefixForURI(encodingURI);
        String root = prefix + ":root";
        attrs.addAttribute(encodingURI, Constants.ATTR_ROOT, root,
                           "CDATA", "0");

        // Make sure we put the encodingStyle on each multiref element we
        // output.
        String encodingStyle;
        if (msgContext == null) {
            encodingStyle = msgContext.getEncodingStyle();
        } else {
            encodingStyle = soapConstants.getEncodingURI();
        }
        String encStyle = getPrefixForURI(soapConstants.getEnvelopeURI()) +
                                          ":" + Constants.ATTR_ENCODING_STYLE;
        attrs.addAttribute(soapConstants.getEnvelopeURI(),
                           Constants.ATTR_ENCODING_STYLE,
                           encStyle,
                           "CDATA",
                           encodingStyle);

        Iterator i = ((HashMap)multiRefValues.clone()).keySet().iterator();
        while (i.hasNext()) {
            while (i.hasNext()) {
                Object val = i.next();
                MultiRefItem mri = (MultiRefItem) multiRefValues.get(val);
                attrs.setAttribute(0, "", Constants.ATTR_ID, "id", "CDATA",
                                   mri.id);
                forceSer = val;

                // Now serialize the value.
                // The sendType parameter is set to true for interop purposes.
                // Some of the remote services do not know how to
                // ascertain the type in these circumstances (though Axis does).
                serialize(multirefQName, attrs, val,
                          mri.javaType, mri.xmlType,
                          true,
                          true);   // mri.sendType
            }

            if (secondLevelObjects != null) {
                i = secondLevelObjects.iterator();
                secondLevelObjects = null;
            }
        }
        forceSer = null;
        outputMultiRefsFlag = false;
    }

    /**
     * Writes (using the Writer) the start tag for element QName along with the
     * indicated attributes and namespace mappings.
     * @param qName is the name of the element
     * @param attributes are the attributes to write
     */
    public void startElement(QName qName, Attributes attributes)
        throws IOException
    {
        java.util.Vector vecQNames = new java.util.Vector();
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("startElem00",
                    "[" + qName.getNamespaceURI() + "]:" + qName.getLocalPart()));
        }

        if (startOfDocument && sendXMLDecl) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            startOfDocument = false;
        }

        if (writingStartTag) {
            writer.write(">");
            if (pretty) writer.write("\n");
            indent++;
        }

        if (pretty) for (int i=0; i<indent; i++) writer.write(' ');
        String elementQName = qName2String(qName, true);
        writer.write("<");

        writer.write(elementQName);

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String qname = attributes.getQName(i);
                writer.write(" ");

                String prefix = "";
                String uri = attributes.getURI(i);
                if (uri != null && !uri.equals("")) {
                    if (qname.equals("")) {
                        // If qname isn't set, generate one
                        prefix = getPrefixForURI(uri);
                    } else {
                        // If it is, make sure the prefix looks reasonable.
                        int idx = qname.indexOf(':');
                        if (idx > -1) {
                            prefix = qname.substring(0, idx);
                            prefix = getPrefixForURI(uri,
                                                     prefix, true);
                        }
                    }
                    if (!prefix.equals("")) {
                        qname = prefix + ":" + attributes.getLocalName(i);
                    } else {
                        qname = attributes.getLocalName(i);
                    }
                } else {
                   qname = attributes.getQName(i);
                    if(qname.equals(""))
                        qname = attributes.getLocalName(i);
                }
                vecQNames.add(qname);
                writer.write(qname);
                writer.write("=\"");
                writer.write(XMLUtils.xmlEncodeString(attributes.getValue(i)));
                writer.write("\"");
            }
        }

        ArrayList currentMappings = nsStack.peek();
        for (int i = 0; i < currentMappings.size(); i++) {
            Mapping map = (Mapping)currentMappings.get(i);
            StringBuffer sb = new StringBuffer("xmlns");
            if (!map.getPrefix().equals("")) {
                sb.append(":");
                sb.append(map.getPrefix());
            }
            if(vecQNames.indexOf(sb.toString())==-1){
                writer.write(" ");
                writer.write(sb.toString());
                writer.write("=\"");
                writer.write(map.getNamespaceURI());
                writer.write("\"");
            }
        }

        writingStartTag = true;

        elementStack.push(elementQName);
        nsStack.push();

        writer.flush();
        onlyXML=true;
    }

    /**
     * Writes the end element tag for the open element.
     **/
    public void endElement()
        throws IOException
    {
        String elementQName = (String)elementStack.pop();

        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("endElem00", "" + elementQName));
        }

        nsStack.pop();
        nsStack.peek().clear();

        if (writingStartTag) {
            writer.write("/>");
            if (pretty) writer.write("\n");
            writingStartTag = false;
            return;
        }

        if (onlyXML) {
            indent--;
            if (pretty) for (int i=0; i<indent; i++) writer.write(' ');
        }
        writer.write("</");
        writer.write(elementQName);
        writer.write('>');
        if (pretty) if (indent>0) writer.write('\n');
        writer.flush();
        onlyXML=true;
    }

    /**
     * Convenience operation to write out (to Writer) the characters
     * in p1 starting at index p2 for length p3.
     * @param p1 character array to write
     * @param p2 starting index in array
     * @param p3 length to write
     */
    public void writeChars(char [] p1, int p2, int p3)
        throws IOException
    {
        if (writingStartTag) {
            writer.write(">");
            writingStartTag = false;
        }
        writeSafeString(String.valueOf(p1,p2,p3));
        writer.flush();
        onlyXML=false;
    }

    /**
     * Convenience operation to write out (to Writer) the String
     * @param string is the String to write.
     */
    public void writeString(String string)
        throws IOException
    {
        if (writingStartTag) {
            writer.write(">");
            writingStartTag = false;
        }
        writer.write(string);
        writer.flush();
        onlyXML=false;
    }

    /**
     * Convenience operation to write out (to Writer) the String
     * properly encoded with xml entities (like &amp)
     * @param string is the String to write.
     */
    public void writeSafeString(String string)
        throws IOException
    {
        writeString(XMLUtils.xmlEncodeString(string));
    }

    /**
     * Output a DOM representation to a SerializationContext
     * @param el is a DOM Element
     */
    public void writeDOMElement(Element el)
        throws IOException
    {
        AttributesImpl attributes = null;
        NamedNodeMap attrMap = el.getAttributes();

        if (attrMap.getLength() > 0) {
            attributes = new AttributesImpl();
            for (int i = 0; i < attrMap.getLength(); i++) {
                Attr attr = (Attr)attrMap.item(i);
                String tmp = attr.getNamespaceURI();
                if ( tmp != null && tmp.equals(Constants.NS_URI_XMLNS) ) {
                    String prefix = attr.getLocalName();
                    if (prefix != null) {
                        if (prefix.equals("xmlns"))
                            prefix = "";
                        String nsURI = attr.getValue();
                        registerPrefixForURI(prefix, nsURI);
                    }
                    continue;
                }

                attributes.addAttribute(attr.getNamespaceURI(),
                                        attr.getLocalName(),
                                        attr.getName(),
                                        "CDATA", attr.getValue());
            }
        }

        String namespaceURI = el.getNamespaceURI();
        String localPart = el.getLocalName();
        if(namespaceURI == null || namespaceURI.length()==0)
            localPart = el.getNodeName();
        QName qName = new QName(namespaceURI, localPart);

        startElement(qName, attributes);

        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                writeDOMElement((Element)child);
            } else if (child instanceof Text) {
                writeSafeString(((Text)child).getData());
            }
        }

        endElement();
    }

    /**
     * Convenience method to get the Serializer for a specific
     * java type
     * @param javaType is Class for a type to serialize
     * @return Serializer
     */
    public final Serializer getSerializerForJavaType(Class javaType) {
        SerializerFactory serF = null;
        Serializer ser = null;
        try {
            serF = (SerializerFactory) getTypeMapping().getSerializer(javaType);
            if (serF != null) {
                ser = (Serializer) serF.getSerializerAs(Constants.AXIS_SAX);
            }
        } catch (JAXRPCException e) {
        }

        return ser;
    }

    /**
     * Obtains the type attribute that should be serialized and returns the new list of Attributes
     * @param attributes of the qname
     * @param type is the qname of the type
     * @return new list of Attributes
     */
    public Attributes setTypeAttribute(Attributes attributes, QName type)
    {
        if (type == null ||
            !shouldSendXSIType() ||
             type.getLocalPart().indexOf(SymbolTable.ANON_TOKEN) >= 0 ||
            ((attributes != null) &&
             (attributes.getIndex(Constants.URI_DEFAULT_SCHEMA_XSI,
                                "type") != -1)))
            return attributes;

        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null && 0 < attributes.getLength() )
            attrs.setAttributes(attributes);

        String prefix = getPrefixForURI(Constants.URI_DEFAULT_SCHEMA_XSI,
                                           "xsi");

        attrs.addAttribute(Constants.URI_DEFAULT_SCHEMA_XSI,
                           "type",
                           prefix + ":type",
                           "CDATA", qName2String(type));
        return attrs;
    }

    /**
     * Invoked to do the actual serialization of the qName (called by serialize above).
     * additional attributes that will be serialized with the qName.
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param javaType is the "real" type of the value.  For primitives, the value is the
     * associated java.lang class.  So the javaType is needed to know that the value
     * is really a wrapped primitive.
     * @param xmlType (optional) is the desired type QName.
     * @param sendType indicates whether the xsi:type attribute should be set.
     */
    public void serializeActual(QName elemQName,
                                Attributes attributes,
                                Object value,
                                Class javaType,
                                QName xmlType,
                                boolean sendType)
        throws IOException
    {
        if (value != null) {
            TypeMapping tm = getTypeMapping();

            if (tm == null) {
                throw new IOException(
                        JavaUtils.getMessage("noSerializer00",
                                             value.getClass().getName(),
                                             "" + this));
            }

            SerializerInfo info = null;
// If the javaType is abstract, try getting a
            // serializer that matches the value's class. 
            if (javaType != null &&
                !javaType.isPrimitive() &&
                !javaType.isArray() &&
                !isPrimitive(value, javaType) &&
                Modifier.isAbstract(javaType.getModifiers())) {
                info = getSerializer(value.getClass(), value);
                if (info != null) {
                    // Successfully found a serializer for the derived object.
                    // Must serializer the type.
                    sendType = true;  
                    xmlType = null;
                }
            }
            // Try getting a serializer for the prefered xmlType
            if (info == null && xmlType != null) {
                info = getSerializer(javaType, xmlType);
            }

            // If a serializer was not found using the preferred xmlType,
            // try getting any serializer.
            if (info == null) {
                info = getSerializer(javaType, value);
                sendType = true;  // Must send type if it does not match preferred type
                xmlType = null;
            }

            if ( info != null ) {

                // Send the xmlType if indicated.
                if (sendType) {
                    if (xmlType == null) {
                        xmlType = tm.getTypeQName(info.javaType);
                    }
                    attributes = setTypeAttribute(attributes, xmlType);
                }
                // The multiref QName is our own fake name.
                // It may be beneficial to set the name to the
                // type name, but I didn't see any improvements
                // in the interop tests.
                //if (name.equals(multirefQName) && type != null)
                //    name = type;
                info.ser.serialize(elemQName, attributes, value, this);
                return;
            }

            throw new IOException(JavaUtils.getMessage("noSerializer00",
                    value.getClass().getName(), "" + tm));
        }
        // !!! Write out a generic null, or get type info from somewhere else?
    }

    class SerializerInfo {
        Serializer ser;
        Class javaType;
    }

    /**
     * getSerializer
     * Attempts to get a serializer for the indicated javaType and xmlType.
     * @param javaType is the type of the object
     * @param xmlType is the preferred qname type.
     * @return found class/serializer or null
     **/
    private SerializerInfo getSerializer(Class javaType, QName xmlType) {
        SerializerInfo info = null;
        SerializerFactory  serFactory  = null ;
        TypeMapping tm = getTypeMapping();

        try {
            if (!javaType.getName().equals("java.lang.Object") &&
                tm.isRegistered(javaType, xmlType)) {
                serFactory = (SerializerFactory) tm.getSerializer(javaType, xmlType);
            }
        } catch(JAXRPCException e) {}


        // Using the serialization factory, create a serializer
        Serializer ser = null;
        if ( serFactory != null ) {
            ser = (Serializer) serFactory.getSerializerAs(Constants.AXIS_SAX);
        }
        if (ser != null) {
            info = new SerializerInfo();
            info.ser = ser;
            info.javaType = javaType;
        }
        return info;
    }

    /**
     * getSerializer
     * Attempts to get a serializer for the indicated type. Failure to
     * find a serializer causes the code to look backwards through the
     * inheritance list.  Continued failured results in an attempt to find
     * a serializer for the type of the value.
     * @param javaType is the type of the object
     * @param value is the object (which may have a different type due to conversions)
     * @return found class/serializer or null
     **/
    private SerializerInfo getSerializer(Class javaType, Object value) {
        SerializerInfo info = null;
        SerializerFactory  serFactory  = null ;
        TypeMapping tm = getTypeMapping();

        // Classes is a list of the inherited interfaces to
        // check
        ArrayList  classes = null;
        boolean firstPass = true;

        // Search for a class that has a serializer factory
        Class _class  = javaType;
        while( _class != null ) {
            try {
                if (!_class.getName().equals("java.lang.Object")) {
                        serFactory = (SerializerFactory) tm.getSerializer(_class);
                }
            } catch(JAXRPCException e) {
                // For now continue if JAXRPCException
            }
            if (serFactory  != null) {
                break ;
            }
            if ( classes == null ) {
                classes = new ArrayList();
            }
            Class[] ifaces = _class.getInterfaces();
            for (int i = 0 ; i < ifaces.length ; i++ ) {
                classes.add( ifaces[i] );
            }
            _class = _class.getSuperclass();

            // Add any non-null (and non-Object) class.  We skip
            // the Object class.
            if ( _class != null &&
                 !_class.getName().equals("java.lang.Object")) {
                classes.add( _class );
            }

            _class = (!classes.isEmpty()) ?
                (Class) classes.remove( 0 ) :
                null;

            // If failed to find a serializerfactory
            // using the javaType, then use the real class of the value
            if (_class == null &&
                value != null &&
                value.getClass() != javaType &&
                firstPass) {
                firstPass = false;
                _class = value.getClass();
            }
        }

        // Using the serialization factory, create a serializer
        Serializer ser = null;
        if ( serFactory != null ) {
            ser = (Serializer) serFactory.getSerializerAs(Constants.AXIS_SAX);
        }
        if (ser != null) {
            info = new SerializerInfo();
            info.ser = ser;
            info.javaType = _class;
        }
        return info;
    }

}
