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

package org.apache.axis ;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.message.InputStreamBody;
import org.apache.axis.message.SOAPDocumentImpl;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.message.MimeHeaders;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.SessionUtils;
import org.apache.commons.logging.Log;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Vector;

/**
 * The SOAPPart provides access to the root part of the Message which
 * contains the envelope.
 * <p>
 * SOAPPart implements Part, providing common MIME operations.
 * <p>
 * SOAPPart also allows access to its envelope,
 * as a string, byte[], InputStream, or SOAPEnvelope.  (This functionality
 * used to be in Message, and has been moved here more or less verbatim
 * pending further cleanup.)
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Heejune Ahn (cityboy@tmax.co.kr)
 */
public class SOAPPart extends javax.xml.soap.SOAPPart implements Part
{
    protected static Log log =
        LogFactory.getLog(SOAPPart.class.getName());

    public static final int FORM_STRING       = 1;
    public static final int FORM_INPUTSTREAM  = 2;
    public static final int FORM_SOAPENVELOPE = 3;
    public static final int FORM_BYTES        = 4;
    public static final int FORM_BODYINSTREAM = 5;
    public static final int FORM_FAULT        = 6;
    private int currentForm;

    //private Hashtable headers = new Hashtable();
    private MimeHeaders mimeHeaders = new MimeHeaders();

    private static final String[] formNames =
    { "", "FORM_STRING", "FORM_INPUTSTREAM", "FORM_SOAPENVELOPE",
      "FORM_BYTES", "FORM_BODYINSTREAM", "FORM_FAULT" };

    /**
     * The current representation of the SOAP contents of this part.
     * May be a String, byte[], InputStream, or SOAPEnvelope, depending
     * on whatever was last asked for.  (ack)
     * <p>
     * currentForm must have the corresponding value.
     * <p>
     * As someone once said:  "Just a placeholder until we figure out what the actual Message
     * object is."
     */
    private Object currentMessage ;

    // These two fields are used for caching in getAsString and getAsBytes
    private String currentMessageAsString = null;
    private byte[] currentMessageAsBytes = null;
    private org.apache.axis.message.SOAPEnvelope currentMessageAsEnvelope= null;

    /**
     * Message object this part is tied to. Used for serialization settings.
     */
    private Message msgObject;

    /** Field contentSource. */
    private Source contentSource = null;

    /**
     * The original message.  Again, may be String, byte[], InputStream,
     * or SOAPEnvelope.
     */
    // private Object originalMessage ; //free up reference  this is not in use.

    /**
     * Create a new SOAPPart.
     * <p>
     * Do not call this directly!  Should only be called by Message.
     *
     * @param parent  the parent <code>Message</code>
     * @param initialContents  the initial contens <code>Object</code>
     * @param isBodyStream if the body is in a stream
     */
    public SOAPPart(Message parent, Object initialContents, boolean isBodyStream) {

        setMimeHeader(HTTPConstants.HEADER_CONTENT_ID , SessionUtils.generateSessionId());
        setMimeHeader(HTTPConstants.HEADER_CONTENT_TYPE , "text/xml");

        msgObject=parent;
        // originalMessage = initialContents;
        int form = FORM_STRING;
        if (initialContents instanceof SOAPEnvelope) {
            form = FORM_SOAPENVELOPE;
        } else if (initialContents instanceof InputStream) {
            form = isBodyStream ? FORM_BODYINSTREAM : FORM_INPUTSTREAM;
        } else if (initialContents instanceof byte[]) {
            form = FORM_BYTES;
        } else if (initialContents instanceof AxisFault) {
            form = FORM_FAULT;
        }

        if (log.isDebugEnabled()) {
            log.debug("Enter: SOAPPart ctor(" + formNames[form] + ")");
        }

        setCurrentMessage(initialContents, form);

        if (log.isDebugEnabled()) {
            log.debug("Exit: SOAPPart ctor()");
        }
    }


    /**
     * Get the <code>Message</code> for this <code>Part</code>.
     *
     * @return the <code>Message</code> for this <code>Part</code>
     */
    public Message getMessage(){
      return msgObject;
    }

    /**
     * Set the Message for this Part.
     * Do not call this Directly. Called by Message.
     *
     * @param msg  the <code>Message</code> for this part
     */
    public void setMessage (Message msg) {
        this.msgObject= msg;
    }

    /**
     * Content type is always "text/xml" for SOAPParts.
     *
     * @return the content type
     */
    public String getContentType() {
        return "text/xml";
    }

    /**
     * Get the content length for this SOAPPart.
     * This will force buffering of the SOAPPart, but it will
     * also cache the byte[] form of the SOAPPart.
     *
     * @return the content length in bytes
     */
    public int getContentLength() {
        try {
            byte[] bytes = this.getAsBytes();
            return bytes.length;
        } catch (AxisFault fault) {
            return 0;  // ?
        }
    }
    /**
     * This set the SOAP Envelope for this part.
     * <p>
     * Note: It breaks the chicken/egg created.
     *  I need a message to create an attachment...
     *  From the attachment I should be able to get a reference...
     *  I now want to edit elements in the envelope in order to
     *    place the  attachment reference to it.
     *  How do I now update the SOAP envelope with what I've changed?
     *
     * @param env   the <code>SOAPEnvelope</CODE> for this <code>SOAPPart</code>
     */

    public void setSOAPEnvelope(org.apache.axis.message.SOAPEnvelope env){
       setCurrentMessage(env, FORM_SOAPENVELOPE) ;
    }

    /**
     * Get the total size in bytes, including headers, of this Part.
     * TODO: For now, since we aren't actually doing MIME yet,
     * this is the same as getContentLength().  Soon it will be
     * different.
     *
     * @return the total size
     */
    public int getSize() {
        // for now, we don't ever do headers!  ha ha
        return this.getContentLength();
    }

    /**
     * Write the contents to the specified writer.
     *
     * @param writer  the <code>Writer</code> to write to
     */
    public void writeTo(Writer writer) throws IOException {

        if ( currentForm == FORM_FAULT ) {
            AxisFault env = (AxisFault)currentMessage;
            try {
                env.output(new SerializationContextImpl(writer, getMessage().getMessageContext()));
            } catch (Exception e) {
                log.error(Messages.getMessage("exception00"), e);
                throw env;
            }
            return;
        }

        if ( currentForm == FORM_SOAPENVELOPE ) {
            SOAPEnvelope env = (SOAPEnvelope)currentMessage;
            try {
                env.output(new SerializationContextImpl(writer, getMessage().getMessageContext()));
            } catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            return;
        }

        writer.write(this.getAsString());
        // easy, huh?
    }

    /**
     * Get the current message, in whatever form it happens to be right now.
     * Will return a String, byte[], InputStream, or SOAPEnvelope, depending
     * on circumstances.
     * <p>
     * The method name is historical.
     * TODO: rename this for clarity; should be more like getContents.
     *
     * @return the current content
     */
    public Object getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Set the current message
     * @param currMsg
     * @param form
     */
    public void setCurrentMessage(Object currMsg, int form) {
      currentMessageAsString = null; //Get rid of any cached stuff this is new.
      currentMessageAsBytes = null;
      currentMessageAsEnvelope= null;
      setCurrentForm(currMsg, form);
    }
    /**
     * Set the current contents of this Part.
     * The method name is historical.
     * TODO: rename this for clarity to something more like setContents???
     *
     * @param currMsg  the new content of this part
     * @param form  the form of the message
     */
    private void setCurrentForm(Object currMsg, int form) {
        if (log.isDebugEnabled()) {
            String msgStr;
            if (currMsg instanceof String) {
                msgStr = (String)currMsg;
            } else {
                msgStr = currMsg.getClass().getName();
            }
            log.debug(Messages.getMessage("setMsgForm", formNames[form],
                    "" + msgStr));
        }
        currentMessage = currMsg ;
        currentForm = form ;
        if(currentForm == FORM_SOAPENVELOPE)
          currentMessageAsEnvelope= (org.apache.axis.message.SOAPEnvelope )currMsg;
    }

    /**
     * Get the contents of this Part (not the headers!), as a byte
     * array.  This will force buffering of the message.
     *
     * @return an array of bytes containing a byte representation of this Part
     * @throws AxisFault if this Part can't be serialized to the byte array
     */
    public byte[] getAsBytes() throws AxisFault {
        log.debug("Enter: SOAPPart::getAsBytes");
        if ( currentForm == FORM_BYTES ) {
            log.debug("Exit: SOAPPart::getAsBytes");
            return (byte[])currentMessage;
        }

        if ( currentForm == FORM_BODYINSTREAM ) {
            try {
                getAsSOAPEnvelope();
            } catch (Exception e) {
                log.fatal(Messages.getMessage("makeEnvFail00"), e);
                log.debug("Exit: SOAPPart::getAsBytes");
                return null;
            }
        }

        if ( currentForm == FORM_INPUTSTREAM ) {
            // Assumes we don't need a content length
            try {
                InputStream  inp = null;
                byte[]  buf = null;
                try{
                    inp = (InputStream) currentMessage ;
                    ByteArrayOutputStream  baos = new ByteArrayOutputStream();
                    buf = new byte[4096];
                    int len ;
                    while ( (len = inp.read(buf,0,4096)) != -1 )
                        baos.write( buf, 0, len );
                    buf = baos.toByteArray();
                }finally{
                  if(inp != null &&
                    currentMessage instanceof org.apache.axis.transport.http.SocketInputStream )
                    inp.close();
                }
                setCurrentForm( buf, FORM_BYTES );
                log.debug("Exit: SOAPPart::getAsBytes");
                return (byte[])currentMessage;
            }
            catch( Exception e ) {
                log.error(Messages.getMessage("exception00"), e);
            }
            log.debug("Exit: SOAPPart::getAsBytes");
            return null;
        }

        if ( currentForm == FORM_SOAPENVELOPE ||
             currentForm == FORM_FAULT ){
            // Set message to FORM_STRING and
            // translate to bytes below
            getAsString();
        }

        if ( currentForm == FORM_STRING ) {
            // If the current message was already converted from
            // a byte[] to String, return the byte[] representation
            // (this is done to avoid unnecessary conversions)
            if (currentMessage == currentMessageAsString &&
                currentMessageAsBytes != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Exit: SOAPPart::getAsBytes()");
                }
                return currentMessageAsBytes;
            }
            // Save this message in case it is requested later in getAsString
            currentMessageAsString = (String) currentMessage;
            try{
                // set encoding of string from message context.
                String encoding = null;
                if (msgObject != null) {
                    try {
                        encoding = (String) msgObject.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);
                    } catch (SOAPException e) {
                    }
                }
                if (encoding == null) {
                    encoding = "UTF-8";
                }
                setCurrentForm( ((String)currentMessage).getBytes(encoding),
                    FORM_BYTES );
            }catch(UnsupportedEncodingException ue){
               setCurrentForm( ((String)currentMessage).getBytes(),
                               FORM_BYTES );
            }
            currentMessageAsBytes = (byte[]) currentMessage;

            log.debug("Exit: SOAPPart::getAsBytes");
            return (byte[])currentMessage;
        }

        log.error(Messages.getMessage("cantConvert00", ""+currentForm));

        log.debug("Exit: SOAPPart::getAsBytes");
        return null;
    }

    /**
     * Get the contents of this Part (not the headers!), as a String.
     * This will force buffering of the message.
     *
     * @return a <code>String</code> containing the content of this message
     * @throws AxisFault  if there is an error serializing this part
     */
    public String getAsString() throws AxisFault {
        log.debug("Enter: SOAPPart::getAsString");
        if ( currentForm == FORM_STRING ) {
            if (log.isDebugEnabled()) {
                log.debug("Exit: SOAPPart::getAsString(): " + currentMessage);
            }
            return (String)currentMessage;
        }

        if ( currentForm == FORM_INPUTSTREAM ||
             currentForm == FORM_BODYINSTREAM ) {
            getAsBytes();
            // Fall thru to "Bytes"
        }

        if ( currentForm == FORM_BYTES ) {
            // If the current message was already converted from
            // a String to byte[], return the String representation
            // (this is done to avoid unnecessary conversions)
            if (currentMessage == currentMessageAsBytes &&
                currentMessageAsString != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Exit: SOAPPart::getAsString(): " + currentMessageAsString);
                }
                return currentMessageAsString;
            }
            // Save this message in case it is requested later in getAsBytes
            currentMessageAsBytes = (byte[]) currentMessage;
            try{
                setCurrentForm( new String((byte[]) currentMessage,"UTF-8"),
                                   FORM_STRING );
            }catch(UnsupportedEncodingException ue){
                setCurrentForm( new String((byte[]) currentMessage),
                                   FORM_STRING );
            }
            currentMessageAsString = (String) currentMessage;
            if (log.isDebugEnabled()) {
                log.debug("Exit: SOAPPart::getAsString(): " + currentMessage);
            }
            return (String)currentMessage;
        }

        if ( currentForm == FORM_FAULT ) {
            StringWriter writer = new StringWriter();
            try {
                this.writeTo(writer);
            } catch (Exception e) {
                log.error(Messages.getMessage("exception00"), e);
                return null;
            }
            setCurrentForm(writer.getBuffer().toString(), FORM_STRING);
            if (log.isDebugEnabled()) {
                log.debug("Exit: SOAPPart::getAsString(): " + currentMessage);
            }
            return (String)currentMessage;
        }

        if ( currentForm == FORM_SOAPENVELOPE ) {
            StringWriter writer = new StringWriter();
            try {
                this.writeTo(writer);
            } catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            setCurrentForm(writer.getBuffer().toString(), FORM_STRING);
            if (log.isDebugEnabled()) {
                log.debug("Exit: SOAPPart::getAsString(): " + currentMessage);
            }
            return (String)currentMessage;
        }

        log.error( Messages.getMessage("cantConvert01", ""+currentForm));

        log.debug("Exit: SOAPPart::getAsString()");
        return null;
    }

    /**
     * Get the contents of this Part (not the MIME headers!), as a
     * SOAPEnvelope.  This will force a complete parse of the
     * message.
     *
     * @return a <code>SOAPEnvelope</code> containing the message content
     * @throws AxisFault if the envelope could not be constructed
     */
    public SOAPEnvelope getAsSOAPEnvelope()
        throws AxisFault
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: SOAPPart::getAsSOAPEnvelope()");
            log.debug(Messages.getMessage("currForm", formNames[currentForm]));
        }
        if ( currentForm == FORM_SOAPENVELOPE )
            return (SOAPEnvelope)currentMessage;


        if (currentForm == FORM_BODYINSTREAM) {
            InputStreamBody bodyEl =
                             new InputStreamBody((InputStream)currentMessage);
            SOAPEnvelope env = new SOAPEnvelope();
            env.addBodyElement(bodyEl);
            setCurrentForm(env, FORM_SOAPENVELOPE);
            return env;
        }

        InputSource is;

        if ( currentForm == FORM_INPUTSTREAM ) {
            is = new InputSource( (InputStream) currentMessage );
            // set encoding of input source from message context.
            if (getMessage().getMessageContext() != null) {
                String encoding = 
                    (String) getMessage().getMessageContext().getProperty(SOAPMessage.CHARACTER_SET_ENCODING);
                if (encoding != null) {
                    is.setEncoding(encoding);
                }
            }
        } else {
            is = new InputSource(new StringReader(getAsString()));
        }
        DeserializationContext dser = new DeserializationContextImpl(is,
                                           getMessage().getMessageContext(),
                                           getMessage().getMessageType());

        // This may throw a SAXException
        try {
            dser.parse();
        } catch (SAXException e) {
            Exception real = e.getException();
            if (real == null)
                real = e;
            throw AxisFault.makeFault(real);
        }

        SOAPEnvelope nse= dser.getEnvelope();
        if(currentMessageAsEnvelope != null){
          //Need to synchronize back processed header info.
          Vector newHeaders= nse.getHeaders();
          Vector oldHeaders= currentMessageAsEnvelope.getHeaders();
          if( null != newHeaders && null != oldHeaders){
           Iterator ohi= oldHeaders.iterator();
           Iterator nhi= newHeaders.iterator();
           while( ohi.hasNext() && nhi.hasNext()){
             SOAPHeaderElement nhe= (SOAPHeaderElement)nhi.next();
             SOAPHeaderElement ohe= (SOAPHeaderElement)ohi.next();

             if(ohe.isProcessed()) nhe.setProcessed(true);
           }
          }

        }

        setCurrentForm(nse, FORM_SOAPENVELOPE);

        log.debug("Exit: SOAPPart::getAsSOAPEnvelope");
        return (SOAPEnvelope)currentMessage;
    }

    /**
     * Add the specified MIME header, as per JAXM.
     *
     * @param header  the header to add
     * @param value   the value of that header
     */
    public void addMimeHeader (String header, String value) {
        mimeHeaders.addHeader(header, value);
    }

    /**
     * Get the specified MIME header.
     *
     * @param header  the name of a MIME header
     * @return the value of the first header named <code>header</code>
     */
    private String getFirstMimeHeader (String header) {
        String[] values = mimeHeaders.getHeader(header);
        if(values != null && values.length>0)
            return values[0];
        return null;
    }

    /**
     * Total size in bytes (of all content and headers, as encoded).
    public abstract int getSize();
     */

    /**
     * Content location.
     *
     * @return the content location
     */
    public String getContentLocation() {
        return getFirstMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION);
    }

    /**
     * Set content location.
     *
     * @param loc  the content location
     */
    public void setContentLocation(String loc) {
        setMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION, loc);
    }

    /**
     * Sets Content-Id of this part.
     *  already defined.
     * @param newCid new Content-Id
     */
    public void setContentId(String newCid){
        setMimeHeader(HTTPConstants.HEADER_CONTENT_ID,newCid);
    }

    /**
     * Content ID.
     *
     * @return the content ID
     */
    public String getContentId() {
        return getFirstMimeHeader(HTTPConstants.HEADER_CONTENT_ID);
    }
    /**
     * Content ID.
     *
     * @return the contentId reference value that should be used directly
     * as an href in a SOAP element to reference this attachment.
     * <B>Not part of JAX-RPC, JAX-M, SAAJ, etc. </B>
     */
    public String getContentIdRef() {
      return org.apache.axis.attachments.Attachments.CIDprefix +
         getContentId();
    }


    /**
     * Get all headers that match.
     *
     * @param match  an array of <code>String</code>s giving mime header names
     * @return an <code>Iterator</code> over all values matching these headers
     */
    public java.util.Iterator getMatchingMimeHeaders( final String[] match){
        return mimeHeaders.getMatchingHeaders(match);
    }

    /**
     * Get all headers that do not match.
     *
     * @param match  an array of <code>String</code>s giving mime header names
     * @return an <code>Iterator</code> over all values not matching these
     *          headers
     */
    public java.util.Iterator getNonMatchingMimeHeaders( final String[] match){
        return mimeHeaders.getNonMatchingHeaders(match);
    }

    /**
     * Sets the content of the <CODE>SOAPEnvelope</CODE> object
     * with the data from the given <CODE>Source</CODE> object.
     * @param   source javax.xml.transform.Source</CODE> object with the data to
     *     be set
     * @throws  SOAPException if there is a problem in
     *     setting the source
     * @see #getContent() getContent()
     */
    public void setContent(Source source) throws SOAPException {
        if(source == null)
            throw new SOAPException(Messages.getMessage("illegalArgumentException00"));

        contentSource = source;
        InputSource in = org.apache.axis.utils.XMLUtils.sourceToInputSource(contentSource);
        InputStream is = in.getByteStream();
        if(is != null) {
            setCurrentMessage(is, FORM_INPUTSTREAM);
        } else {
            Reader r = in.getCharacterStream();
            if(r == null) {
                throw new SOAPException(Messages.getMessage("noCharacterOrByteStream"));
            }
            BufferedReader br = new BufferedReader(r);
            String line = null;
            StringBuffer sb = new StringBuffer();
            try {
                while((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                throw new SOAPException(Messages.getMessage("couldNotReadFromCharStream"), e);
            }
            setCurrentMessage(sb.toString(), FORM_STRING);
        }
    }

    /**
     * Returns the content of the SOAPEnvelope as a JAXP <CODE>
     * Source</CODE> object.
     * @return the content as a <CODE>
     *     javax.xml.transform.Source</CODE> object
     * @throws  SOAPException  if the implementation cannot
     *     convert the specified <CODE>Source</CODE> object
     * @see #setContent(javax.xml.transform.Source) setContent(javax.xml.transform.Source)
     */
    public Source getContent() throws SOAPException {
        if(contentSource == null) {
            switch(currentForm) {
            case FORM_STRING:
                String s = (String)currentMessage;
                contentSource = new StreamSource(new StringReader(s));
                break;
            case FORM_INPUTSTREAM:
                contentSource = new StreamSource((InputStream)currentMessage);
                break;
            case FORM_SOAPENVELOPE:
                SOAPEnvelope se = (SOAPEnvelope)currentMessage;
                try {
                    contentSource = new DOMSource(se.getAsDocument());
                } catch (Exception e) {
                    throw new SOAPException(Messages.getMessage("errorGetDocFromSOAPEnvelope"), e);
                }
                break;
            case FORM_BYTES:
                byte[] bytes = (byte[])currentMessage;
                contentSource = new StreamSource(new ByteArrayInputStream(bytes));
                break;
                case FORM_BODYINSTREAM:
                contentSource = new StreamSource((InputStream)currentMessage);
                break;
            }
        }
        return contentSource;
    }

    /**
     * Retrieves all the headers for this <CODE>SOAPPart</CODE>
     * object as an iterator over the <CODE>MimeHeader</CODE>
     * objects.
     * @return an <CODE>Iterator</CODE> object with all of the Mime
     *     headers for this <CODE>SOAPPart</CODE> object
     */
    public Iterator getAllMimeHeaders() {
        return mimeHeaders.getAllHeaders();
    }

    /**
     * Changes the first header entry that matches the given
     *   header name so that its value is the given value, adding a
     *   new header with the given name and value if no existing
     *   header is a match. If there is a match, this method clears
     *   all existing values for the first header that matches and
     *   sets the given value instead. If more than one header has
     *   the given name, this method removes all of the matching
     *   headers after the first one.
     *
     *   <P>Note that RFC822 headers can contain only US-ASCII
     *   characters.</P>
     * @param  name a <CODE>String</CODE> giving the
     *     header name for which to search
     * @param  value a <CODE>String</CODE> giving the
     *     value to be set. This value will be substituted for the
     *     current value(s) of the first header that is a match if
     *     there is one. If there is no match, this value will be
     *     the value for a new <CODE>MimeHeader</CODE> object.
     * @ throws java.lang.IllegalArgumentException if
     *     there was a problem with the specified mime header name
     *     or value
     * @see #getMimeHeader(java.lang.String) getMimeHeader(java.lang.String)
     */
    public void setMimeHeader(String name, String value) {
        mimeHeaders.setHeader(name,value);
    }

    /**
     * Gets all the values of the <CODE>MimeHeader</CODE> object
     * in this <CODE>SOAPPart</CODE> object that is identified by
     * the given <CODE>String</CODE>.
     * @param   name  the name of the header; example:
     *     "Content-Type"
     * @return a <CODE>String</CODE> array giving all the values for
     *     the specified header
     * @see #setMimeHeader(java.lang.String, java.lang.String) setMimeHeader(java.lang.String, java.lang.String)
     */
    public String[] getMimeHeader(String name) {
        return mimeHeaders.getHeader(name);
    }

    /**
     * Removes all the <CODE>MimeHeader</CODE> objects for this
     * <CODE>SOAPEnvelope</CODE> object.
     */
    public void removeAllMimeHeaders() {
        mimeHeaders.removeAllHeaders();
    }

    /**
     * Removes all MIME headers that match the given name.
     * @param  header  a <CODE>String</CODE> giving
     *     the name of the MIME header(s) to be removed
     */
    public void removeMimeHeader(String header) {
        mimeHeaders.removeHeader(header);
    }

    /**
     * Gets the <CODE>SOAPEnvelope</CODE> object associated with
     * this <CODE>SOAPPart</CODE> object. Once the SOAP envelope is
     * obtained, it can be used to get its contents.
     * @return the <CODE>SOAPEnvelope</CODE> object for this <CODE>
     *     SOAPPart</CODE> object
     * @throws  SOAPException if there is a SOAP error
     */
    public javax.xml.soap.SOAPEnvelope getEnvelope() throws SOAPException {
        try {
            return getAsSOAPEnvelope();
        } catch (AxisFault af) {
            throw new SOAPException(af);
        }
    }

    /**
     *  Implementation of org.w3c.Document
     *  Most of methods will be implemented using the delgate
     *  instance of SOAPDocumentImpl
     *  This is for two reasons:
     *  - possible change of message classes, by extenstion of xerces implementation
     *  - we cannot extends SOAPPart (multiple inheritance),
     *    since it is defined as Abstract class
     * ***********************************************************
     */

    private Document document = new SOAPDocumentImpl(this);
    /**
     *  @since SAAJ 1.2
     */
    public Document getSOAPDocument(){
        if(document == null){
            document = new SOAPDocumentImpl(this);
        }
        return document;
    }

    /**
     * @return
     */
    public DocumentType getDoctype(){
        return document.getDoctype();
    }

    /**
     * @return
     */
    public DOMImplementation getImplementation(){
        return document.getImplementation();
    }

    /**
     * SOAPEnvelope is the Document Elements of this XML docuement
     * @return
     */
    protected Document mDocument;

    public Element getDocumentElement()
    {
        try{
            return getEnvelope();
        }catch(SOAPException se){
            return null;
        }
    }

    /**
     *
     * @param tagName
     * @return
     * @throws DOMException
     */
    public Element createElement(String tagName) throws DOMException {
        return document.createElement(tagName);
    }

    public DocumentFragment createDocumentFragment() {
        return document.createDocumentFragment();
    }

    public Text createTextNode(String data) {
        return document.createTextNode(data);
    }

    public Comment createComment(String data){
        return document.createComment(data);
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        return document.createCDATASection(data);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data)
    throws DOMException {
        return document.createProcessingInstruction(target,data);
    }

    public Attr createAttribute(String name)throws DOMException {
        return document.createAttribute(name);
    }

    public EntityReference createEntityReference(String name) throws DOMException {
        return document.createEntityReference(name);
    }

    public NodeList getElementsByTagName(String tagname) {
        return document.getElementsByTagName(tagname);
    }

    public Node importNode(Node importedNode, boolean deep)
    throws DOMException {
        return document.importNode(importedNode, deep);
    }

    public Element createElementNS(String namespaceURI, String qualifiedName)
    throws DOMException {
        return document.createElementNS(namespaceURI, qualifiedName);
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
    throws DOMException {
        return document.createAttributeNS(namespaceURI, qualifiedName);
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return document.getElementsByTagNameNS(namespaceURI,localName);
    }

    public Element getElementById(String elementId){
        return document.getElementById(elementId);
    }

    /////////////////////////////////////////////////////////////

    public String getEncoding()
    {
        throw new UnsupportedOperationException("Not yet implemented.69");
    }

    public  void setEncoding(String s)
    {
        throw new UnsupportedOperationException("Not yet implemented.70");
    }

    public  boolean getStandalone()
    {
        throw new UnsupportedOperationException("Not yet implemented.71");
    }


    public  void setStandalone(boolean flag)
    {
        throw new UnsupportedOperationException("Not yet implemented.72");
    }

    public  boolean getStrictErrorChecking()
    {
        throw new UnsupportedOperationException("Not yet implemented.73");
    }


    public  void setStrictErrorChecking(boolean flag)
    {
        throw new UnsupportedOperationException("Not yet implemented. 74");
    }


    public  String getVersion()
    {
        throw new UnsupportedOperationException("Not yet implemented. 75");
    }


    public  void setVersion(String s)
    {
        throw new UnsupportedOperationException("Not yet implemented.76");
    }


    public  Node adoptNode(Node node)
    throws DOMException
    {
        throw new UnsupportedOperationException("Not yet implemented.77");
    }

    /**
     *  Node Implementation
     */

    public String getNodeName(){
        return document.getNodeName();
    }

    public String getNodeValue() throws DOMException {
        return document.getNodeValue();
    }

    public void setNodeValue(String nodeValue) throws DOMException{
        document.setNodeValue(nodeValue);
    }

    public short getNodeType() {
        return document.getNodeType();
    }

    public Node getParentNode(){
        return  document.getParentNode();
    }

    public NodeList getChildNodes() {
        return document.getChildNodes();
    }

    public Node getFirstChild() {
        return document.getFirstChild();
    }

    public Node getLastChild(){
        return document.getLastChild();
    }

    public Node getPreviousSibling(){
        return document.getPreviousSibling();
    }

    public Node getNextSibling(){
        return document.getNextSibling();
    }

    public NamedNodeMap getAttributes(){
        return document.getAttributes();
    }

    public Document getOwnerDocument(){
        return document.getOwnerDocument();
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return document.insertBefore(newChild, refChild);
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return document.replaceChild(newChild, oldChild);
    }

    public Node removeChild(Node oldChild) throws DOMException {
        return document.removeChild(oldChild);
    }

    public Node appendChild(Node newChild) throws DOMException {
        return document.appendChild(newChild);
    }

    public boolean hasChildNodes(){
        return document.hasChildNodes();
    }
    public Node cloneNode(boolean deep) {
        return document.cloneNode(deep);
    }

    public void normalize(){
        document.normalize();
    }

    public boolean isSupported(String feature, String version){
        return document.isSupported(feature, version);
    }

    public String getNamespaceURI() {
        return document.getNamespaceURI();
    }

    public String getPrefix() {
        return document.getPrefix();
    }

    public void setPrefix(String prefix) throws DOMException {
        document.setPrefix(prefix);
    }
    public String getLocalName() {
        return document.getLocalName();
    }

    public boolean hasAttributes(){
        return document.hasAttributes();
    }
}

