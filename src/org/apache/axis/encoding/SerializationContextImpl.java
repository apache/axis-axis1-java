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
import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.Call;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.XMLUtils;
import org.apache.log4j.Category;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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
    static Category category =
            Category.getInstance(SerializationContextImpl.class.getName());

    private NSStack nsStack = new NSStack();
    private boolean writingStartTag = false;
    private boolean onlyXML = true;
    private int indent=0;
    private boolean startOfDocument = true;
    private Stack elementStack = new Stack();
    private Writer writer;
    private int lastPrefixIndex = 1;
    private MessageContext msgContext;
    private boolean pretty = false;


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
                sendXSIType = false;
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
        if (msgContext == null)
            return null;
        
        return (TypeMapping) msgContext.getTypeMappingRegistry().getTypeMapping(Constants.URI_CURRENT_SOAP_ENC);
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
        return getPrefixForURI(uri, null);
    }
    
    /**
     * Get a prefix for the given namespace URI.  If one has already been
     * defined in this serialization, use that.  Otherwise, map the passed
     * default prefix to the URI, and return that.  If a null default prefix
     * is passed, use one of the form "ns<num>"
     */ 
    public String getPrefixForURI(String uri, String defaultPrefix)
    {
        if ((uri == null) || (uri.equals("")))
            return null;

        String prefix = nsStack.getPrefix(uri);

        if (prefix == null && uri.equals(Constants.URI_CURRENT_SOAP_ENC)) {
            prefix = Constants.NSPREFIX_SOAP_ENC;
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
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("register00", prefix, uri));
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
     * Convert QName to a string of the form <prefix>:<localpart>
     * @param QName
     * @return prefixed qname representation for serialization.
     */
    public String qName2String(QName qName)
    {
        String prefix = getPrefixForURI(qName.getNamespaceURI());
        return (((prefix != null)&&(!prefix.equals(""))) ? prefix + ":" : "") +
               qName.getLocalPart();
    }

    /**
     * Get the QName associated with the specified class.
     * @param Class of an object requiring serialization.
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

        if (String.class.isAssignableFrom(javaType)) return true;
        if (Date.class.isAssignableFrom(javaType)) return true;
        if (Hex.class.isAssignableFrom(javaType)) return true;
        if (Element.class.isAssignableFrom(javaType)) return true;
        if (byte[].class.isAssignableFrom(javaType)) return true;
        
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
     * Serialize the indicated value as an element named qName.  The attributes object are 
     * additional attributes that will be serialized with the qName.  The value
     * could be serialized directly or could be serialized as an href (with the
     * actual serialize taking place later)
     * @param qName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param javaType is the "real" type of the value.  For primitives, the value is the
     * associated java.lang class.  So the javaType is needed to know that the value 
     * is really a wrapped primitive.
     */
    public void serialize(QName qName, Attributes attributes, Object value, Class javaType)
        throws IOException
    {
        if (value == null) {
            // If the value is null, the element is
            // passed with xsi:nil="true" to indicate that no object is present.
            //
            // There are three approaches that could be taken...
            // 1) (Currently Implemented) Use xsi:nil="true".
            // 2) Emit an empty element.  (This would be deserialized incorrectly.)
            // 3) Don't emit an element.  (This could also cause deserialization problems.)
            AttributesImpl attrs = new AttributesImpl();
            if (attributes != null)
                attrs.setAttributes(attributes);
            attrs.addAttribute(Constants.URI_2001_SCHEMA_XSI, "nil", "xsi:nil",
                               "CDATA", "true");
            startElement(qName, attrs);
            endElement();
        }

        Message msg= getCurrentMessage();
        if(null != msg){
            //Get attachments. returns null if no attachment support.
            Attachments attachments= getCurrentMessage().getAttachments();

            if( null != attachments && attachments.isAttachment(value)){
             //Attachment support and this is an object that should be treated as an attachment.

             //Allow an the attachment to do its own serialization.
              serializeActual(qName, attributes, value, javaType);
              
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

            String href = (String)multiRefValues.get(value);
            if (href == null) {
                multiRefIndex++;
                href = "id" + multiRefIndex;
                multiRefValues.put(value, href);

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
            }

            AttributesImpl attrs = new AttributesImpl();
            if (attributes != null)
                attrs.setAttributes(attributes);
            attrs.addAttribute("", Constants.ATTR_HREF, "href",
                               "CDATA", "#" + href);

            startElement(qName, attrs);
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
        serializeActual(qName, attributes, value, javaType);
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

        // explicitly state that this attribute is not a root
        String prefix = getPrefixForURI(Constants.URI_CURRENT_SOAP_ENC);
        String root = prefix + ":root";
        attrs.addAttribute(Constants.URI_CURRENT_SOAP_ENC, Constants.ATTR_ROOT, root, 
                           "CDATA", "0");

        Iterator i = ((HashMap)multiRefValues.clone()).keySet().iterator();
        while (i.hasNext()) {
            while (i.hasNext()) {
                Object val = i.next();
                String id = (String)multiRefValues.get(val);
                attrs.setAttribute(0, "", Constants.ATTR_ID, "id", "CDATA",
                                   id);
                forceSer = val;
                // Now serialize the value.  Note that it is safe to 
                // set the javaType argument using value.getClass() because 
                // values that represent primitives will never get to this point
                // because they cannot be multi-ref'ed
                serialize(new QName("","multiRef"), attrs, val, val.getClass());
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
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("startElem00",
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
        String elementQName = qName2String(qName);
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
                                                     prefix);
                        }
                    }
                    if (!prefix.equals("")) {
                        qname = prefix + ":" + attributes.getLocalName(i);
                    } else {
                        qname = attributes.getLocalName(i);
                    }
                } else {
                    qname = attributes.getLocalName(i);
                    if(qname == null)
                        qname = attributes.getQName(i);
                }

                writer.write(qname);
                writer.write("=\"");
                writer.write(XMLUtils.xmlEncodeString(attributes.getValue(i)));
                writer.write("\"");
            }
        }

        ArrayList currentMappings = nsStack.peek();
        for (int i = 0; i < currentMappings.size(); i++) {
            Mapping map = (Mapping)currentMappings.get(i);
            writer.write(" xmlns");
            if (!map.getPrefix().equals("")) {
                writer.write(":");
                writer.write(map.getPrefix());
            }
            writer.write("=\"");
            writer.write(map.getNamespaceURI());
            writer.write("\"");
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

        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("endElem00", "" + elementQName));
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
        SerializerFactory dserF = null;
        Serializer dser = null;
        try { 
            dserF = (SerializerFactory) getTypeMapping().getSerializer(javaType);
        } catch (JAXRPCException e) {
        }
        if (dserF != null) {
            try {
                dser = (Serializer) dserF.getSerializerAs(Constants.AXIS_SAX);
            } catch (JAXRPCException e) {}
        }
        return null;
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
            ((attributes != null) &&
             (attributes.getIndex(Constants.URI_CURRENT_SCHEMA_XSI,
                                "type") != -1)))
            return attributes;
        
        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null)
            attrs.setAttributes(attributes);
        
        String prefix = getPrefixForURI(Constants.URI_CURRENT_SCHEMA_XSI,
                                           "xsi");

        attrs.addAttribute(Constants.URI_CURRENT_SCHEMA_XSI,
                           "type",
                           prefix + ":type",
                           "CDATA", qName2String(type));
        return attrs;
    }
    
    /**
     * Invoked to do the actual serialization of the qName (called by serialize above).
     * additional attributes that will be serialized with the qName. 
     * @param qName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param javaType is the "real" type of the value.  For primitives, the value is the
     * associated java.lang class.  So the javaType is needed to know that the value 
     * is really a wrapped primitive.
     */
    public void serializeActual(QName name, Attributes attributes, Object value, Class javaType)
        throws IOException
    {
        if (value != null) {
            TypeMapping tm = getTypeMapping();
            
            if (tm == null) {
                throw new IOException(JavaUtils.getMessage("noSerializer00",
                                                           value.getClass().getName(), "" + this));
            }

            Class_Serializer pair = getSerializer(javaType, value);
            if ( pair != null ) {
                QName type = tm.getTypeQName(pair.javaType);
                attributes = setTypeAttribute(attributes, type);
                pair.ser.serialize(name, attributes, value, this);
                return;
            }

            throw new IOException(JavaUtils.getMessage("noSerializer00",
                    value.getClass().getName(), "" + this));
        }
        // !!! Write out a generic null, or get type info from somewhere else?
    }

    class Class_Serializer {
        Serializer ser;
        Class javaType;
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
    private Class_Serializer getSerializer(Class javaType, Object value) {
        Class_Serializer pair = null;
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
                serFactory = (SerializerFactory) tm.getSerializer(_class);
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
            // the Object class because if we reach that then
            // there's an error and this error message return 
            // here is better than the one returned by the
            // ObjSerializer.
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
        
        // Using the serialization factory, create a serializer and
        // serialize the value.
        Serializer ser = null;
        if ( serFactory != null ) {
            try {
                ser = (Serializer) serFactory.getSerializerAs(Constants.AXIS_SAX);
            } catch (JAXRPCException e) {
            }
        }
        if (ser != null) {
            pair = new Class_Serializer();
            pair.ser = ser;
            pair.javaType = _class;
        }
        return pair; 
    }

}
