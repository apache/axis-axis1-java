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

package org.apache.axis;

import org.apache.log4j.Category;

import org.apache.axis.attachments.Attachments;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A complete SOAP (and/or XML-RPC, eventually) message.
 * Includes both the root part (as a SOAPPart), and zero or
 * more MIME attachments (as AttachmentParts).
 * <p>
 * Eventually should be refactored to generalize SOAPPart
 * for multiple protocols (XML-RPC?).
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Rick Rineholt 
 */
public class Message {
    static Category category =
            Category.getInstance(Message.class.getName());

    public static final String REQUEST  = "request" ;
    public static final String RESPONSE = "response" ;
    //MIME parts defined for messages.
    public static final String MIME_MULTIPART_RELATED="multipart/related";
    public static final String MIME_APPLICATION_DIME="application/dime"; //NOT SUPPORTED NOW
    public static final String MIME_UNKNOWN="  "; // look at the input stream to find the headers to decide.

    /**
     * The messageType indicates whether this is request or response.
     */
    private String messageType;
    
    /**
     * This Message's SOAPPart.  Will always be here.
     */
    private SOAPPart mSOAPPart;
    
    /**
     * This Message's Attachments object, which manages the attachments
     * contained in this Message.
     */
    private Attachments mAttachments= null;
    
    /**
     * The MessageContext we are associated with.
     */
    private MessageContext msgContext;

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public MessageContext getMessageContext()
    {
        return msgContext;
    }
    public void setMessageContext(MessageContext msgContext)
    {
        this.msgContext = msgContext;
    }
    
    /**
     * Construct a Message, using the provided initialContents as the
     * contents of the Message's SOAPPart.
     * <p>
     * Eventually, genericize this to
     * return the RootPart instead, which will have some kind of
     * EnvelopeFactory to enable support for things other than SOAP.
     * But that all will come later, with lots of additional refactoring.
     *
     * @param initialContents may be String, byte[], InputStream, SOAPEnvelope, or AxisFault.
     * @param bodyInStream is true if initialContents is an InputStream containing just
     * the SOAP body (no SOAP-ENV).
     */
    public Message(Object initialContents, boolean bodyInStream) {
      this(initialContents, bodyInStream, (String)null, (String)null );
    }
    
    /**
     * Construct a Message, using the provided initialContents as the
     * contents of the Message's SOAPPart.
     * <p>
     * Eventually, genericize this to
     * return the RootPart instead, which will have some kind of
     * EnvelopeFactory to enable support for things other than SOAP.
     * But that all will come later, with lots of additional refactoring.
	 *
	 * @param initialContents may be String, byte[], InputStream, SOAPEnvelope, or AxisFault.
     * @param bodyInStream is true if initialContents is an InputStream containing just
     * @param contentType this if the contentType has been already determined.  (as in the case of servlets); 
     * the SOAP body (no SOAP-ENV).
     */
    public Message(Object initialContents, boolean bodyInStream, String contentType, String contentLocation) {
                      setup(initialContents, bodyInStream, contentType, contentLocation);
    }

	/**
	 * Construct a Message.  An overload of Message(Object, boolean),
	 * defaulting bodyInStream to false.
	 */
	public Message (Object initialContents) {
		setup(initialContents, false, null, null);
	}

        private static Class attachImpl = null; 

        private static boolean checkForAttchmentSupport= true;//aviod testing and possibly failing everytime.

        private static boolean attachmentSupportEnabled = false;

        public static boolean isAttachmentSupportEnabled(){
             if(checkForAttchmentSupport){
                 checkForAttchmentSupport= false;//aviod testing and possibly failing everytime.
                 Class mailapiclass= null;
                 Class dataHandlerclass=null;
                 try {
                     dataHandlerclass = Class.forName("javax.activation.DataHandler");
                     mailapiclass = Class.forName("javax.mail.internet.MimeMultipart");
                     attachImpl = Class.forName("org.apache.axis.attachments.AttachmentsImpl");
                     attachmentSupportEnabled= true; 
                  } catch (ClassNotFoundException ex) {
                      // no support for it, leave mAttachments null.
                  }   catch(java.lang.NoClassDefFoundError ex) {
                      // no support for it, leave mAttachments null.
                  }
              }
              
              category.debug("Attachment support is enabled" + attachmentSupportEnabled);
              return attachmentSupportEnabled;
        }

    
	/**
	 * Do the work of construction.
	 */
	private void setup (Object initialContents, boolean bodyInStream,
           String contentType, String contentLocation) {
        
          // Try to construct an AttachmentsImpl object for attachment functionality.
          // If there is no org.apache.axis.attachments.AttachmentsImpl class,
          // it must mean activation.jar is not present and attachments are not
          // supported.
          if(isAttachmentSupportEnabled()){
                  // Construct one, and cast to Attachments.
                  // There must be exactly one constructor of AttachmentsImpl, which must
                  // take an org.apache.axis.Message!
                  Constructor attachImplConstr = attachImpl.getConstructors()[0];
                  try{
                  mAttachments = (Attachments)attachImplConstr.newInstance(
                      new Object[]{this,initialContents, contentType, contentLocation});

                  mSOAPPart = (SOAPPart) mAttachments.getRootPart(); //If it can't support it, it wont have a root part.
                  }catch (InvocationTargetException ex) {
                      category.fatal(ex);
                      throw new RuntimeException(ex.getMessage());
                  }catch (InstantiationException ex) {
                      category.fatal(ex);
                      throw new RuntimeException(ex.getMessage());
                  }catch (IllegalAccessException ex) {
                      category.fatal(ex);
                      throw new RuntimeException(ex.getMessage());
                  }
          }

        if(null == mSOAPPart ){ //The stream was not determined by a more complex type so default to text/xml 
          mSOAPPart = new SOAPPart(this, initialContents, bodyInStream);
        }
     }
	
    /**
     * Get this message's SOAPPart.
	 * <p>
	 * Eventually, this should be generalized beyond just SOAP,
	 * but it's hard to know how to do that without necessitating
	 * a lot of casts in client code.  Refactoring keeps getting
	 * easier anyhow.
     */
    public SOAPPart getSOAPPart () {
        return mSOAPPart;
    }
            
    /**
     * Get the Attachments of this Message.
     * If this returns null, then NO ATTACHMENT SUPPORT EXISTS in this
     * configuration of Axis, and no attachment operations may be
     * performed.
     */
    public Attachments getAttachments () {
        return mAttachments;
    }

  public String getContentType() throws org.apache.axis.AxisFault {
    mSOAPPart.getAsBytes(); //Force serialization if it hasn't happend it. //Rick Rineholt fix this later.
    String ret= "text/xml; charset=utf-8";
    if(mAttachments != null && 0 != mAttachments.getAttachmentCount()){
        ret= mAttachments.getContentType();
    }
    return ret;
  }
  public int getContentLength() throws org.apache.axis.AxisFault{ //This will have to give way someday to HTTP Chunking but for now kludge.
      int ret= mSOAPPart.getAsBytes().length; //Force serialization if it hasn't happend it. //Rick Rineholt fix this later.
      if(mAttachments != null &&   0 < mAttachments.getAttachmentCount()){
          ret= mAttachments.getContentLength();
    }
    return ret;
  }


  public void writeContentToStream(java.io.OutputStream os){

    if(mAttachments == null || 0== mAttachments.getAttachmentCount()){ //Do it the old fashion way.
      try{
          os.write(mSOAPPart.getAsBytes());
      }catch( java.io.IOException e){
        System.err.println(e);
        e.printStackTrace();
      }
    }else{
      try{
          mAttachments.writeContentToStream(os);
      }catch(java.lang.Exception e){
        System.err.println(e);
        e.printStackTrace();
      }
    }
  }
  
}
