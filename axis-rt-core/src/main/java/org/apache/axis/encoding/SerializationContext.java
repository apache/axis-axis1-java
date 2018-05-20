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

package org.apache.axis.encoding;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.holders.QNameHolder;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.Call;
import org.apache.axis.components.encoding.XMLEncoder;
import org.apache.axis.components.encoding.XMLEncoderFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.ser.ArraySerializer;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.encoding.ser.SimpleListSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.types.HexBinary;
import org.apache.axis.utils.IDKey;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.cache.MethodCache;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Utils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/** Manage a serialization, including keeping track of namespace mappings
 * and element stacks.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class SerializationContext implements javax.xml.rpc.encoding.SerializationContext
{
    protected static Log log =
            LogFactory.getLog(SerializationContext.class.getName());

    // invariant member variable to track low-level logging requirements
    // we cache this once per instance lifecycle to avoid repeated lookups
    // in heavily used code.
    private final boolean debugEnabled = log.isDebugEnabled();

    private NSStack nsStack = null;
    private boolean writingStartTag = false;
    private boolean onlyXML = true;
    private int indent=0;
    private Stack elementStack = new Stack();
    private Writer writer;
    private int lastPrefixIndex = 1;
    private MessageContext msgContext;
    private QName currentXMLType;
    /** The item QName if we're serializing a literal array... */
    private QName itemQName;
    /** The item type if we're serializing a literal array... */
    private QName itemType;

    /** The SOAP context we're using */
    private SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;

    private static QName multirefQName = new QName("","multiRef");
    private static Class[] SERIALIZER_CLASSES =
            new Class[] {String.class, Class.class, QName.class};
    private static final String SERIALIZER_METHOD = "getSerializer";

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
     * Should I disable the pretty xml completely.
     */ 
    private boolean disablePrettyXML = false;


    /**
     * Should I disable the namespace prefix optimization.
     */ 
    private boolean enableNamespacePrefixOptimization = false;

    /**
     * current setting for pretty
     */ 
    private boolean pretty = false;
    
    /**
     * Should I send an XML declaration?
     */
    private boolean sendXMLDecl = true;

    /**
     * Should I send xsi:type attributes?  By default, yes.
     */
    private boolean sendXSIType = true;

    /**
     * Send an element with an xsi:nil="true" attribute for null
     * variables (if Boolean.TRUE), or nothing (if Boolean.FALSE).
     */
    private Boolean sendNull = Boolean.TRUE;

    /**
     * A place to hold objects we cache for multi-ref serialization, and
     * remember the IDs we assigned them.
     */
    private HashMap multiRefValues = null;
    private int multiRefIndex = -1;
    private boolean noNamespaceMappings = true;
    private QName writeXMLType;
    private XMLEncoder encoder = null;
    
    /** The flag whether the XML decl should be written */
    protected boolean startOfDocument = true;
 
    /** The encoding to serialize */
    private String encoding = XMLEncoderFactory.DEFAULT_ENCODING;

    class MultiRefItem {
        String id;
        QName xmlType;
        Boolean sendType;
        Object value;
        MultiRefItem(String id,
                     QName xmlType,
                     Boolean sendType, Object value) {
            this.id = id;
            this.xmlType = xmlType;
            this.sendType = sendType;
            this.value = value;
        }

    }
    /**
     * These three variables are necessary to process
     * multi-level object graphs for multi-ref serialization.
     * While writing out nested multi-ref objects (via outputMultiRef), we
     * will fill the secondLevelObjects vector
     * with any new objects encountered.
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
     * A list of particular namespace -> prefix mappings we should prefer.
     * See getPrefixForURI() below.
     */
    HashMap preferredPrefixes = new HashMap();

    /**
     * Construct SerializationContext with associated writer
     * @param writer java.io.Writer
     */
    public SerializationContext(Writer writer)
    {
        this.writer = writer;
        initialize();
    }

    private void initialize() {
        // These are the preferred prefixes we'll use instead of the "ns1"
        // style defaults.  MAKE SURE soapConstants IS SET CORRECTLY FIRST!
        preferredPrefixes.put(soapConstants.getEncodingURI(),
                              Constants.NS_PREFIX_SOAP_ENC);
        preferredPrefixes.put(Constants.NS_URI_XML,
                              Constants.NS_PREFIX_XML);
        preferredPrefixes.put(schemaVersion.getXsdURI(),
                              Constants.NS_PREFIX_SCHEMA_XSD);
        preferredPrefixes.put(schemaVersion.getXsiURI(),
                              Constants.NS_PREFIX_SCHEMA_XSI);
        preferredPrefixes.put(soapConstants.getEnvelopeURI(),
                              Constants.NS_PREFIX_SOAP_ENV);
        nsStack = new NSStack(enableNamespacePrefixOptimization);
    }


    /**
     * Construct SerializationContext with associated writer and MessageContext
     * @param writer java.io.Writer
     * @param msgContext is the MessageContext
     */
    public SerializationContext(Writer writer, MessageContext msgContext)
    {
        this.writer = writer;
        this.msgContext = msgContext;

        if ( msgContext != null ) {
            soapConstants = msgContext.getSOAPConstants();

            // Use whatever schema is associated with this MC
            schemaVersion = msgContext.getSchemaVersion();

            Boolean shouldSendDecl = (Boolean)msgContext.getProperty(
                                                  AxisEngine.PROP_XML_DECL);
            if (shouldSendDecl != null)
                sendXMLDecl = shouldSendDecl.booleanValue();

            Boolean shouldSendMultiRefs =
                  (Boolean)msgContext.getProperty(AxisEngine.PROP_DOMULTIREFS);
            if (shouldSendMultiRefs != null)
                doMultiRefs = shouldSendMultiRefs.booleanValue();
            
            Boolean shouldDisablePrettyXML =
                  (Boolean)msgContext.getProperty(AxisEngine.PROP_DISABLE_PRETTY_XML);
            if (shouldDisablePrettyXML != null)
                disablePrettyXML = shouldDisablePrettyXML.booleanValue();
            
            Boolean shouldDisableNamespacePrefixOptimization =
                  (Boolean)msgContext.getProperty(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION);
            if (shouldDisableNamespacePrefixOptimization != null) {
                enableNamespacePrefixOptimization = shouldDisableNamespacePrefixOptimization.booleanValue();
            } else {
                enableNamespacePrefixOptimization = JavaUtils.isTrue(AxisProperties.getProperty(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,
                                "true"));
            }
            boolean sendTypesDefault = sendXSIType;

            // A Literal use operation overrides the above settings. Don't
            // send xsi:type, and don't do multiref in that case.
            OperationDesc operation = msgContext.getOperation();
            if (operation != null) {
                if (operation.getUse() != Use.ENCODED) {
                    doMultiRefs = false;
                    sendTypesDefault = false;
                }
            } else {
                // A Literal use service also overrides the above settings. 
                SOAPService service = msgContext.getService();
                if (service != null) {
                    if (service.getUse() != Use.ENCODED) {
                        doMultiRefs = false;
                        sendTypesDefault = false;
                    }
                }
            }

            // The SEND_TYPE_ATTR and PROP_SEND_XSI options indicate
            // whether the elements should have xsi:type attributes.
            // Only turn this off is the user tells us to
            if ( !msgContext.isPropertyTrue(Call.SEND_TYPE_ATTR, sendTypesDefault ))
                sendXSIType = false ;

// Don't need this since the above isPropertyTrue should walk up to the engine's
// properties...?
//            
//            Boolean opt = (Boolean)optionSource.getOption(AxisEngine.PROP_SEND_XSI);
//            if (opt != null) {
//                sendXSIType = opt.booleanValue();
//            }
        } else {
            enableNamespacePrefixOptimization = JavaUtils.isTrue(AxisProperties.getProperty(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,
                            "true"));
            disablePrettyXML = JavaUtils.isTrue(AxisProperties.getProperty(AxisEngine.PROP_DISABLE_PRETTY_XML,
                            "true"));
        }

        // Set up preferred prefixes based on current schema, soap ver, etc.
        initialize();
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
        if(!disablePrettyXML) {
            this.pretty = pretty;
        }
    }

    /**
     * Are we doing multirefs?
     * @return true or false
     */
    public boolean getDoMultiRefs() {
        return doMultiRefs;
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
            return DefaultTypeMappingImpl.getSingletonDelegate();

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
        return msgContext.getTypeMappingRegistry();
    }

    /**
     * Get a prefix for a namespace URI.  This method will ALWAYS
     * return a valid prefix - if the given URI is already mapped in this
     * serialization, we return the previous prefix.  If it is not mapped,
     * we will add a new mapping and return a generated prefix of the form
     * "ns&lt;num&gt;".
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
     * is passed, use one of the form "ns&lt;num&gt;"
     */
    public String getPrefixForURI(String uri, String defaultPrefix)
    {
        return getPrefixForURI(uri, defaultPrefix, false);
    }

    /**
     * Get a prefix for the given namespace URI.  If one has already been
     * defined in this serialization, use that.  Otherwise, map the passed
     * default prefix to the URI, and return that.  If a null default prefix
     * is passed, use one of the form "ns&lt;num&gt;"
     */
    public String getPrefixForURI(String uri, String defaultPrefix, boolean attribute)
    {
        if ((uri == null) || (uri.length() == 0))
            return null;

        // If we're looking for an attribute prefix, we shouldn't use the
        // "" prefix, but always register/find one.
        String prefix = nsStack.getPrefix(uri, attribute);

        if (prefix == null) {
            prefix = (String)preferredPrefixes.get(uri);

            if (prefix == null) {
                if (defaultPrefix == null) {
                    prefix = "ns" + lastPrefixIndex++;
                    while(nsStack.getNamespaceURI(prefix)!=null) {
                        prefix = "ns" + lastPrefixIndex++;    
                    }
                } else {
                    prefix = defaultPrefix;
                }
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
        if (debugEnabled) {
            log.debug(Messages.getMessage("register00", prefix, uri));
        }

        if ((uri != null) && (prefix != null)) {
            if (noNamespaceMappings) {
                nsStack.push();
                noNamespaceMappings = false;
            }
            String activePrefix = nsStack.getPrefix(uri,true);
            if(activePrefix == null || !activePrefix.equals(prefix)) {
                nsStack.add(uri, prefix);
            }
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
     * Returns this context's encoding style.  If we've got a message
     * context then we'll get the style from that; otherwise we'll
     * return a default.
     *
     * @return a <code>String</code> value
     */
    public String getEncodingStyle() {
        return msgContext == null ? Use.DEFAULT.getEncoding() : msgContext.getEncodingStyle();
    }

    /**
     * Returns whether this context should be encoded or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isEncoded() {
        return Constants.isSOAP_ENC(getEncodingStyle());
    }

    /**
     * Convert QName to a string of the form &lt;prefix&gt;:&lt;localpart&gt;
     * @param qName
     * @return prefixed qname representation for serialization.
     */
    public String qName2String(QName qName, boolean writeNS)
    {
        String prefix = null;
        String namespaceURI = qName.getNamespaceURI();
        String localPart = qName.getLocalPart();
        
        if(localPart != null && localPart.length() > 0) {
            int index = localPart.indexOf(':');
            if(index!=-1){
                prefix = localPart.substring(0,index);
                if(prefix.length()>0 && !prefix.equals("urn")){
                    registerPrefixForURI(prefix, namespaceURI);
                    localPart = localPart.substring(index+1);
                } else {
                    prefix = null;
                }
            }
            localPart = Utils.getLastLocalPart(localPart);            
        }

        if (namespaceURI.length() == 0) {
            if (writeNS) {
                // If this is unqualified (i.e. prefix ""), set the default
                // namespace to ""
                String defaultNS = nsStack.getNamespaceURI("");
                if (defaultNS != null && defaultNS.length() > 0) {
                    registerPrefixForURI("", "");
                }
            }
        } else {
            prefix = getPrefixForURI(namespaceURI);
        }

        if ((prefix == null) || (prefix.length() == 0))
           return localPart;

        return prefix + ':' + localPart;
    }

    public String qName2String(QName qName)
    {
        return qName2String(qName, false);
    }

    /**
     * Convert attribute QName to a string of the form &lt;prefix&gt;:&lt;localpart&gt;
     * There are slightly different rules for attributes:
     *  - There is no default namespace
     *  - any attribute in a namespace must have a prefix
     *
     * @param qName QName
     * @return prefixed qname representation for serialization.
     */
    public String attributeQName2String(QName qName) {
        String prefix = null;
        String uri = qName.getNamespaceURI(); 
        if (uri.length() > 0) {
            prefix = getPrefixForURI(uri, null, true);
        }

        if ((prefix == null) || (prefix.length() == 0))
           return qName.getLocalPart();
        
        return prefix + ':' + qName.getLocalPart();
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
     * @return true/false
     */
    public boolean isPrimitive(Object value)
    {
        if (value == null) return true;

        Class javaType = value.getClass();

        if (javaType.isPrimitive()) return true;

        if (javaType == String.class) return true;
        if (Calendar.class.isAssignableFrom(javaType)) return true;
        if (Date.class.isAssignableFrom(javaType)) return true;
        if (HexBinary.class.isAssignableFrom(javaType)) return true;
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
            if (SchemaUtils.isSimpleSchemaType(qName)) {
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
     * xsi:type is set by using the javaType to
     * find an appopriate xmlType from the TypeMappingRegistry.
     * Null values and the xsi:type flag will be sent or not depending 
     * on previous configuration of this SerializationContext.
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value)
        throws IOException {
        serialize(elemQName, attributes, value, null, null, null, null);
    }

    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive, the
     * javaType is the actual unwrapped object type.
     * The xmlType is the QName of the type that is used to set
     * xsi:type.  If not specified, xsi:type is set by using the javaType to
     * find an appopriate xmlType from the TypeMappingRegistry.
     * Null values and the xsi:type flag will be sent or not depending 
     * on previous configuration of this SerializationContext.
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param xmlType is the qname of the type or null.
     * @deprecated use serialize(QName, Attributes, Object, QName, Class) instead
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          QName xmlType)
        throws IOException {
        serialize(elemQName, attributes, value, xmlType, null, null, null);
    }
    
    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive, the
     * javaType is the actual unwrapped object type.
     * The xmlType is the QName of the type that is used to set
     * xsi:type.  If not specified, xsi:type is set by using the javaType to
     * find an appopriate xmlType from the TypeMappingRegistry.
     * Null values and the xsi:type flag will be sent or not depending 
     * on previous configuration of this SerializationContext.
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param xmlType is the qname of the type or null.
     * @param javaType is the java type of the value
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          QName xmlType, Class javaType)
        throws IOException {
        serialize(elemQName, attributes, value, xmlType, javaType, null, null);
    }

    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive.
     * The xmlType (if specified) is the QName of the type that is used to set
     * xsi:type.
     * The sendNull flag indicates whether null values should be sent over the
     * wire (default is to send such values with xsi:nil="true").
     * The sendType flag indicates whether the xsi:type flag should be sent
     * (default is true).
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param xmlType is the qname of the type or null.
     * @param sendNull determines whether to send null values.
     * @param sendType determines whether to set xsi:type attribute.
     *
     * @deprecated use serialize(QName, Attributes, Object, QName,
     * Boolean, Boolean) instead.
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          QName xmlType,
                          boolean sendNull,
                          Boolean sendType)
        throws IOException
    {
        serialize( elemQName, attributes, value, xmlType, null, 
                   (sendNull) ? Boolean.TRUE : Boolean.FALSE, 
                   sendType);
    }

    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive.
     * The xmlType (if specified) is the QName of the type that is used to set
     * xsi:type.
     * The sendNull flag indicates whether to end an element with an xsi:nil="true" attribute for null
     * variables (if Boolean.TRUE), or nothing (if Boolean.FALSE).
     * The sendType flag indicates whether the xsi:type flag should be sent
     * (default is true).
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param xmlType is the qname of the type or null.
     * @param sendNull determines whether to send null values.
     * @param sendType determines whether to set xsi:type attribute.
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          QName xmlType,
                          Boolean sendNull,
                          Boolean sendType)
        throws IOException 
    {
        serialize(elemQName, attributes, value, xmlType, null, sendNull, sendType);
        
    }
    
    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive.
     * The xmlType (if specified) is the QName of the type that is used to set
     * xsi:type.
     * The sendNull flag indicates whether to end an element with an xsi:nil="true" attribute for null
     * variables (if Boolean.TRUE), or nothing (if Boolean.FALSE).
     * The sendType flag indicates whether the xsi:type flag should be sent
     * (default is true).
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param xmlType is the qname of the type or null.
     * @param javaClass is the java type of the value
     * @param sendNull determines whether to send null values.
     * @param sendType determines whether to set xsi:type attribute.
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          QName xmlType,
                          Class javaClass,
                          Boolean sendNull,
                          Boolean sendType)
        throws IOException
    {
        if (log.isDebugEnabled()) {
            log.debug("Start serializing element; elemQName=" + elemQName
                    + "; xmlType=" + xmlType + "; javaClass=" + javaClass
                    + "; sendNull=" + sendNull + "; sendType=" + sendType + "; value=" + value);
        }
        
        boolean sendXSITypeCache = sendXSIType;
        if (sendType != null) {
            sendXSIType = sendType.booleanValue();
        }
        boolean shouldSendType = shouldSendXSIType();

        try {
            Boolean sendNullCache = this.sendNull;
            if (sendNull != null) {
                this.sendNull = sendNull;
            } else {
                sendNull = this.sendNull;
            }

            if (value == null) {
                // If the value is null, the element is
                // passed with xsi:nil="true" to indicate that no object is present.
                if (this.sendNull.booleanValue()) {
                    AttributesImpl attrs = new AttributesImpl();
                    if (attributes != null && 0 < attributes.getLength())
                        attrs.setAttributes(attributes);
                    if (shouldSendType)
                        attrs = (AttributesImpl) setTypeAttribute(attrs, xmlType);
                    String nil = schemaVersion.getNilQName().getLocalPart();
                    attrs.addAttribute(schemaVersion.getXsiURI(), nil, "xsi:" + nil,
                                       "CDATA", "true");
                    startElement(elemQName, attrs);
                    endElement();
                }
                this.sendNull = sendNullCache;
                return;
            }

            Message msg= getCurrentMessage();
            if(null != msg){
                //Get attachments. returns null if no attachment support.
                Attachments attachments= getCurrentMessage().getAttachmentsImpl();

                if( null != attachments && attachments.isAttachment(value)){
                    //Attachment support and this is an object that should be treated as an attachment.

                    //Allow an the attachment to do its own serialization.
                    serializeActual(elemQName, attributes, value,
                                    xmlType, javaClass, sendType);

                    //No need to add to mulitRefs. Attachment data stream handled by
                    // the message;
                    this.sendNull = sendNullCache;
                    return;
                }
            }

            // If multi-reference is enabled and this object value is not a primitive
            // and we are not forcing serialization of the object, then generate
            // an element href (and store the object for subsequent outputMultiRef
            // processing).

            // NOTE : you'll notice that everywhere we register objects in the
            // multiRefValues and secondLevelObjects collections, we key them
            // using getIdentityKey(value) instead of the Object reference itself.
            // THIS IS IMPORTANT, and please make sure you understand what's
            // going on if you change any of this code.  It's this way to make
            // sure that individual Objects are serialized separately even if the
            // hashCode() and equals() methods have been overloaded to make two
            // Objects appear equal.

            if (doMultiRefs && isEncoded() &&
                    (value != forceSer) && !isPrimitive(value)) {
                if (multiRefIndex == -1)
                    multiRefValues = new HashMap();

                String id;

                // Look for a multi-ref descriptor for this Object.
                MultiRefItem mri = (MultiRefItem)multiRefValues.get(
                        getIdentityKey(value));
                if (mri == null) {
                    // Didn't find one, so create one, give it a new ID, and store
                    // it for next time.
                    multiRefIndex++;
                    id = "id" + multiRefIndex;
                    mri = new MultiRefItem (id, xmlType, sendType, value);
                    multiRefValues.put(getIdentityKey(value), mri);

                    /**
                     * If we're SOAP 1.2, we can "inline" the serializations,
                     * so put it out now, with it's ID.
                     */
                    if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                        AttributesImpl attrs = new AttributesImpl();
                        if (attributes != null && 0 < attributes.getLength())
                            attrs.setAttributes(attributes);
                        attrs.addAttribute("", Constants.ATTR_ID, "id", "CDATA",
                                           id);
                        serializeActual(elemQName, attrs, value, xmlType, javaClass, sendType);
                        this.sendNull = sendNullCache;
                        return;
                    }


                    /** If we're in the middle of writing out
                     * the multi-refs, we've already cloned the list of objects
                     * and so even though we add a new one to multiRefValues,
                     * it won't get serialized this time around.
                     *
                     * To deal with this, we maintain a list of "second level"
                     * Objects - ones that need serializing as a result of
                     * serializing the first level.  When outputMultiRefs() is
                     * nearly finished, it checks to see if secondLevelObjects
                     * is empty, and if not, it goes back and loops over those
                     * Objects.  This can happen N times depending on how deep
                     * the Object graph goes.
                     */
                    if (outputMultiRefsFlag) {
                        if (secondLevelObjects == null)
                            secondLevelObjects = new HashSet();
                        secondLevelObjects.add(getIdentityKey(value));
                    }
                } else {
                    // Found one, remember it's ID
                    id = mri.id;
                }

                // Serialize an HREF to our object
                AttributesImpl attrs = new AttributesImpl();
                if (attributes != null && 0 < attributes.getLength())
                    attrs.setAttributes(attributes);
                attrs.addAttribute("", soapConstants.getAttrHref(), soapConstants.getAttrHref(),
                                   "CDATA", '#' + id);

                startElement(elemQName, attrs);
                endElement();
                this.sendNull = sendNullCache;
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
            serializeActual(elemQName, attributes, value, xmlType, javaClass, sendType);
        } finally {
            sendXSIType = sendXSITypeCache;
        }
    }

    /**
     * Get an IDKey that represents the unique identity of the object.
     * This is used as a unique key into a HashMap which will
     * not give false hits on other Objects where hashCode() and equals()
     * have been overriden to match.
     *
     * @param value the Object to hash
     * @return a unique IDKey for the identity
     */
    private IDKey getIdentityKey(Object value) {
        return new IDKey(value);
    }

    /**
     * The serialize method uses hrefs to reference all non-primitive
     * values.  These values are stored and serialized by calling
     * outputMultiRefs after the serialize method completes.
     */
    public void outputMultiRefs() throws IOException
    {
        if (!doMultiRefs || (multiRefValues == null) ||
                soapConstants == SOAPConstants.SOAP12_CONSTANTS)
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
        if (msgContext != null) {
            encodingStyle = msgContext.getEncodingStyle();
        } else {
            encodingStyle = soapConstants.getEncodingURI();
        }
        String encStyle = getPrefixForURI(soapConstants.getEnvelopeURI()) +
                                          ':' + Constants.ATTR_ENCODING_STYLE;
        attrs.addAttribute(soapConstants.getEnvelopeURI(),
                           Constants.ATTR_ENCODING_STYLE,
                           encStyle,
                           "CDATA",
                           encodingStyle);

        // Make a copy of the keySet because it could be updated
        // during processing
        HashSet keys = new HashSet();
        keys.addAll(multiRefValues.keySet());
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            while (i.hasNext()) {
                AttributesImpl attrs2 = new AttributesImpl(attrs);
                Object val = i.next();
                MultiRefItem mri = (MultiRefItem) multiRefValues.get(val);
                attrs2.setAttribute(0, "", Constants.ATTR_ID, "id", "CDATA",
                                   mri.id);

                forceSer = mri.value;

                // Now serialize the value.
                // The sendType parameter is defaulted for interop purposes.
                // Some of the remote services do not know how to
                // ascertain the type in these circumstances (though Axis does).
                serialize(multirefQName, attrs2, mri.value,
                          mri.xmlType,
                          null,
                          this.sendNull,
                          Boolean.TRUE);   // mri.sendType
            }

            // Done processing the iterated values.  During the serialization
            // of the values, we may have run into new nested values.  These
            // were placed in the secondLevelObjects map, which we will now
            // process by changing the iterator to locate these values.
            if (secondLevelObjects != null) {
                i = secondLevelObjects.iterator();
                secondLevelObjects = null;
            }
        }

        // Reset maps and flags
        forceSer = null;
        outputMultiRefsFlag = false;
        multiRefValues = null;
        multiRefIndex = -1;
        secondLevelObjects = null;
    }

    public void writeXMLDeclaration() throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"");        
        writer.write(encoding);
        writer.write("\"?>");
        startOfDocument = false;        
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
        java.util.ArrayList vecQNames = null;
        if (debugEnabled) {
            log.debug(Messages.getMessage("startElem00", qName.toString()));
        }

        if (startOfDocument && sendXMLDecl) {
            writeXMLDeclaration();
        }

        if (writingStartTag) {
            writer.write('>');
            if (pretty) writer.write('\n');
            indent++;
        }

        if (pretty) for (int i=0; i<indent; i++) writer.write(' ');
        String elementQName = qName2String(qName, true);
        writer.write('<');

        writer.write(elementQName);
        
        if (writeXMLType != null) {
            attributes = setTypeAttribute(attributes, writeXMLType);
            writeXMLType = null;
        }

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String qname = attributes.getQName(i);
                writer.write(' ');

                String prefix = "";
                String uri = attributes.getURI(i);
                if (uri != null && uri.length() > 0) {
                    if (qname.length() == 0) {
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
                    if (prefix.length() > 0) {
                        qname = prefix + ':' + attributes.getLocalName(i);
                    } else {
                        qname = attributes.getLocalName(i);
                    }
                } else {
                   qname = attributes.getQName(i);
                    if(qname.length() == 0)
                        qname = attributes.getLocalName(i);
                }

                if (qname.startsWith("xmlns")) {
                  if (vecQNames == null) vecQNames = new ArrayList();
                  vecQNames.add(qname);
                }
                writer.write(qname);
                writer.write("=\"");
                
                getEncoder().writeEncoded(writer, attributes.getValue(i));
                
                writer.write('"');
            }
        }

        if (noNamespaceMappings) {
            nsStack.push();
        } else {
            for (Mapping map=nsStack.topOfFrame(); map!=null; map=nsStack.next()) {
                if (!(map.getNamespaceURI().equals(Constants.NS_URI_XMLNS) && map.getPrefix().equals("xmlns")) &&
                    !(map.getNamespaceURI().equals(Constants.NS_URI_XML) && map.getPrefix().equals("xml")))
                {                
                    StringBuffer sb = new StringBuffer("xmlns");
                    if (map.getPrefix().length() > 0) {
                        sb.append(':');
                        sb.append(map.getPrefix());
                    }
                    String qname = sb.toString();
                    if ((vecQNames==null) || (vecQNames.indexOf(qname)==-1)) {
                        writer.write(' ');
                        writer.write(qname);
                        writer.write("=\"");
                        getEncoder().writeEncoded(writer, map.getNamespaceURI());
                        writer.write('"');
                    }
                }
            }

            noNamespaceMappings = true;
        }

        writingStartTag = true;

        elementStack.push(elementQName);

        onlyXML=true;
    }

    /**
     * Writes the end element tag for the open element.
     **/
    public void endElement()
        throws IOException
    {
        String elementQName = (String)elementStack.pop();

        if (debugEnabled) {
            log.debug(Messages.getMessage("endElem00", "" + elementQName));
        }

        nsStack.pop();

        if (writingStartTag) {
            writer.write("/>");
            if (pretty) writer.write('\n');
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
        if (startOfDocument && sendXMLDecl) {
            writeXMLDeclaration();
        }
        
        if (writingStartTag) {
            writer.write('>');
            writingStartTag = false;
        }
        writeSafeString(String.valueOf(p1,p2,p3));
        onlyXML=false;
    }

    /**
     * Convenience operation to write out (to Writer) the String
     * @param string is the String to write.
     */
    public void writeString(String string)
        throws IOException
    {
        if (startOfDocument && sendXMLDecl) {
            writeXMLDeclaration();
        }
        
        if (writingStartTag) {
            writer.write('>');
            writingStartTag = false;
        }
        writer.write(string);
        onlyXML=false;
    }

    /**
     * Convenience operation to write out (to Writer) the String
     * properly encoded with xml entities (like &amp;amp;)
     * @param string is the String to write.
     */
    public void writeSafeString(String string)
        throws IOException
    {
        if (startOfDocument && sendXMLDecl) {
            writeXMLDeclaration();
        }
        
        if (writingStartTag) {
            writer.write('>');
            writingStartTag = false;
        }
        
        getEncoder().writeEncoded(writer, string);
        onlyXML=false;
    }

    /**
     * Output a DOM representation to a SerializationContext
     * @param el is a DOM Element
     */
    public void writeDOMElement(Element el)
        throws IOException
    {
        if (startOfDocument && sendXMLDecl) {
            writeXMLDeclaration();
        }
        
        // If el is a Text element, write the text and exit
        if (el instanceof org.apache.axis.message.Text) {            
            writeSafeString(((Text)el).getData());
            return;
        }   
        
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
            } else if (child instanceof CDATASection) {
                writeString("<![CDATA[");
                writeString(((Text)child).getData());
                writeString("]]>");
            } else if (child instanceof Comment) {
                writeString("<!--");
                writeString(((CharacterData)child).getData());
                writeString("-->");
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
        SchemaVersion schema = SchemaVersion.SCHEMA_2001;
        if (msgContext != null) {
            schema = msgContext.getSchemaVersion();
        }

        if (type == null ||
             type.getLocalPart().indexOf(SymbolTable.ANON_TOKEN) >= 0 ||
            ((attributes != null) &&
             (attributes.getIndex(schema.getXsiURI(),
                                "type") != -1)))
            return attributes;

        if (log.isDebugEnabled()) {
            log.debug("Adding xsi:type attribute for type " + type);
        }
        
        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null && 0 < attributes.getLength() )
            attrs.setAttributes(attributes);

        String prefix = getPrefixForURI(schema.getXsiURI(),
                                           "xsi");

        attrs.addAttribute(schema.getXsiURI(),
                           "type",
                           prefix + ":type",
                           "CDATA", attributeQName2String(type));
        return attrs;
    }

    /**
     * Invoked to do the actual serialization of the qName (called by serialize above).
     * additional attributes that will be serialized with the qName.
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param xmlType (optional) is the desired type QName.
     * @param sendType indicates whether the xsi:type attribute should be set.
     */
    private void serializeActual(QName elemQName,
                                Attributes attributes,
                                Object value,
                                QName xmlType,
                                Class javaClass,
                                Boolean sendType)
        throws IOException
    {
        boolean shouldSendType = (sendType == null) ? shouldSendXSIType() :
            sendType.booleanValue();

        if (value != null) {
            TypeMapping tm = getTypeMapping();

            if (tm == null) {
                throw new IOException(
                        Messages.getMessage("noSerializer00",
                                             value.getClass().getName(),
                                             "" + this));
            }

            // Set currentXMLType to the one desired one.
            // Note for maxOccurs usage this xmlType is the
            // type of the component not the type of the array.
            currentXMLType = xmlType;

            // if we're looking for xsd:anyType, accept anything...
            if (Constants.equals(Constants.XSD_ANYTYPE,xmlType)){
                xmlType = null;
                shouldSendType = true;
            }

            // Try getting a serializer for the prefered xmlType
            QNameHolder actualXMLType = new QNameHolder();
                        
            Class javaType = getActualJavaClass(xmlType, javaClass, value);
                        
            Serializer ser = getSerializer(javaType, xmlType,
                                           actualXMLType);

            if ( ser != null ) {
                // Send the xmlType if indicated or if
                // the actual xmlType is different than the
                // prefered xmlType
                if (shouldSendType ||
                    (xmlType != null &&
                     (!xmlType.equals(actualXMLType.value)))) {

                    if(!isEncoded()) {
                        if (Constants.isSOAP_ENC(actualXMLType.value.getNamespaceURI())) {
                            // Don't write SOAP_ENC types (i.e. Array) if we're not using encoding
                        } else if (javaType.isPrimitive() && javaClass != null && JavaUtils.getWrapperClass(javaType) == javaClass) {
                            // Don't write xsi:type when serializing primitive wrapper value as primitive type.
                        }
                        else {
                            if(!(javaType.isArray() && xmlType != null && Constants.isSchemaXSD(xmlType.getNamespaceURI())) ) {
                                writeXMLType = actualXMLType.value;
                            }
                        }
                    } else {
                        writeXMLType = actualXMLType.value;
                    }
                }

                // -----------------
                // NOTE: I have seen doc/lit tests that use
                // the type name as the element name in multi-ref cases
                // (for example <soapenc:Array ... >)
                // In such cases the xsi:type is not passed along.
                // -----------------
                // The multiref QName is our own fake name.
                // It may be beneficial to set the name to the
                // type name, but I didn't see any improvements
                // in the interop tests.
                //if (name.equals(multirefQName) && type != null)
                //    name = type;
                ser.serialize(elemQName, attributes, value, this);
                return;
            }
            throw new IOException(Messages.getMessage("noSerializer00",
                    value.getClass().getName(), "" + tm));
        }
        // !!! Write out a generic null, or get type info from somewhere else?
    }
    
    /**
     * Returns the java class for serialization. 
     * If the xmlType is xsd:anyType or javaType is array or javaType is java.lang.Object
     * the java class for serialization is the class of obj.
     * If the obj is not array and the obj's class does not match with the javaType,
     * the java class for serialization is the javaType.
     * Otherwise, the java class for serialization is the obj's class.
     * 
     * @param xmlType    the qname of xml type
     * @param javaType   the java class from serializer 
     * @param obj        the object to serialize
     * @return the java class for serialization
     */
    private Class getActualJavaClass(QName xmlType, Class javaType, Object obj) {
        Class cls = obj.getClass();
        
        if ((xmlType != null 
                    && Constants.isSchemaXSD(xmlType.getNamespaceURI()) && "anyType".equals(xmlType.getLocalPart()))
                || (javaType != null 
                        && (javaType.isArray() || javaType == Object.class))) {
            return cls;
        }
        
        if (javaType != null && !javaType.isAssignableFrom(cls) && !cls.isArray()) {
            return javaType;
        }
        
        return cls;
    }

    private Serializer getSerializerFromClass(Class javaType, QName qname) {
        
        Serializer serializer = null;
        try {
            Method method = 
                MethodCache.getInstance().getMethod(javaType,
                                                    SERIALIZER_METHOD,
                                                    SERIALIZER_CLASSES);
            if (method != null) {
                serializer = (Serializer) method.invoke(null,
                    new Object[] {getEncodingStyle(), javaType, qname});
            }
       } catch (NoSuchMethodException e) {
       } catch (IllegalAccessException e) {
       } catch (InvocationTargetException e) {
       }
       return serializer;
    }

    /**
     * Get the currently prefered xmlType
     * @return QName of xmlType or null
     */
    public QName getCurrentXMLType() {
        return currentXMLType;
    }

    /**
     * Walk the interfaces of a class looking for a serializer for that
     * interface.  Include any parent interfaces in the search also.
     *
     */
    private SerializerFactory getSerializerFactoryFromInterface(Class javaType,
                                                                QName xmlType,
                                                                TypeMapping tm)
    {
        SerializerFactory  serFactory  = null ;
        Class [] interfaces = javaType.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; i++) {
                Class iface = interfaces[i];
                serFactory = (SerializerFactory) tm.getSerializer(iface,
                                                                  xmlType);
                if (serFactory == null)
                    serFactory = getSerializerFactoryFromInterface(iface, xmlType, tm);
                if (serFactory != null)
                    break;

            }
        }
        return serFactory;
    }

    /**
     * getSerializer
     * Attempts to get a serializer for the indicated javaType and xmlType.
     * @param javaType is the type of the object
     * @param xmlType is the preferred qname type.
     * @param actualXMLType is set to a QNameHolder or null.
     *                     If a QNameHolder, the actual xmlType is returned.
     * @return found class/serializer or null
     **/
    private Serializer getSerializer(Class javaType, QName xmlType,
                                     QNameHolder actualXMLType) {
        if (log.isDebugEnabled()) {
            log.debug("Getting serializer for javaType=" + javaType + " and xmlType=" + xmlType);
        }
        
        SerializerFactory  serFactory  = null ;
        TypeMapping tm = getTypeMapping();
        if (actualXMLType != null) {
            actualXMLType.value = null;
        }

        while (javaType != null) {
            // check type mapping
            serFactory = (SerializerFactory) tm.getSerializer(javaType, xmlType);
            if (serFactory != null) {
                break;
            }

            // check the class for serializer
            Serializer serializer = getSerializerFromClass(javaType, xmlType);
            if (serializer != null) {
                if (actualXMLType != null) {
                    TypeDesc typedesc = TypeDesc.getTypeDescForClass(javaType);
                    if (typedesc != null) {
                        actualXMLType.value = typedesc.getXmlType();
                    }
                }
                return serializer;
            }

            // Walk my interfaces...
            serFactory = getSerializerFactoryFromInterface(javaType, xmlType, tm);
            if (serFactory != null) {
                break;
            }

            // Finally, head to my superclass
            javaType = javaType.getSuperclass();
        }

        // Using the serialization factory, create a serializer
        Serializer ser = null;
        if ( serFactory != null ) {
            ser = (Serializer) serFactory.getSerializerAs(Constants.AXIS_SAX);

            if (actualXMLType != null) {
                // Get the actual qname xmlType from the factory.
                // If not found via the factory, fall back to a less
                // performant solution.
                if (serFactory instanceof BaseSerializerFactory) {
                    actualXMLType.value =
                        ((BaseSerializerFactory) serFactory).getXMLType();
                }
                boolean encoded = isEncoded();
                if (actualXMLType.value == null ||
                        (!encoded &&
                        (actualXMLType.value.equals(Constants.SOAP_ARRAY) ||
                        actualXMLType.value.equals(Constants.SOAP_ARRAY12)))) {
                    actualXMLType.value = tm.getXMLType(javaType,
                                                        xmlType,
                                                        encoded);
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Serializer is " + ser);
            if (actualXMLType != null) {
                log.debug("Actual XML type is " + actualXMLType.value);
            }
        }
        
        return ser;
    }

    public String getValueAsString(Object value, QName xmlType, Class javaClass) throws IOException {
        Class cls = value.getClass();
        cls = getActualJavaClass(xmlType, javaClass, value);
        
        Serializer ser = getSerializer(cls, xmlType, null);
        
        // The java type is an array, but we need a simple type.
        if (ser instanceof ArraySerializer)
        {
            SimpleListSerializerFactory factory =
                new SimpleListSerializerFactory(cls, xmlType);
            ser = (Serializer)
                factory.getSerializerAs(getEncodingStyle());
        }

        if (!(ser instanceof SimpleValueSerializer)) {
            throw new IOException(
                    Messages.getMessage("needSimpleValueSer",
                                         ser.getClass().getName()));
        }
        SimpleValueSerializer simpleSer = (SimpleValueSerializer)ser;
        return simpleSer.getValueAsString(value, this);
    }

    public void setWriteXMLType(QName type) {
        writeXMLType = type;
    }

    public XMLEncoder getEncoder() {
        if(encoder == null) {
            encoder = XMLUtils.getXMLEncoder(encoding);
        }
        return encoder;
    }

    /**
     * get the encoding for the serialization
     * @return
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * set the encoding for the serialization
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public QName getItemQName() {
        return itemQName;
    }

    public void setItemQName(QName itemQName) {
        this.itemQName = itemQName;
    }

    public QName getItemType() {
        return itemType;
    }

    public void setItemType(QName itemType) {
        this.itemType = itemType;
    }
}