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

 /* @author Rob Jellinghaus (robj@unrealities.com) */
 /* @author Rick Rineholt  */

package org.apache.axis.attachments;

import org.apache.axis.Message;
import org.apache.axis.Part;
import org.apache.axis.AxisFault;


/**
 * Implements the Attachment interface, via an actual Hashmap of actual
 * AttachmentParts.
 */

public class AttachmentsImpl implements Attachments {
    private Message msg;
    
    
    private java.util.HashMap attachments = new java.util.HashMap();

    protected org.apache.axis.SOAPPart soapPart= null; 
    /**
     * The actual stream to manage the multi-related input stream.
     */
    protected org.apache.axis.attachments.MultiPartRelatedInputStream mpartStream= null;

    /**
     * This is the content location as specified in SOAP with Attachments.
     * This maybe null if the message had no Content-Location specifed.
     */

    protected String contentLocation= null;
    
    /**
     * Construct one of these on a parent Message.
     * Should only ever be called by Message constructor!
     * @param msg the message associated 
     * @param initialContents should be anything but today only a stream is supported.
     * @param The mime content type of the stream for transports that provide it.
     */ 
    public AttachmentsImpl(Message msg, Object intialContents, String contentType,
       String contentLocation) throws org.apache.axis.AxisFault {
       if(contentLocation != null){
           contentLocation= contentLocation.trim();
           if(contentLocation.length()==0) contentLocation= null;
       }
       this.contentLocation= contentLocation;

      this.msg= msg;
      if(contentType  != null) {
          if(contentType.equals(org.apache.axis.Message.MIME_UNKNOWN)){
          //Process the input stream for headers to determine the mime type.
          //TODO
          }
          else{
            java.util.StringTokenizer st = new java.util.StringTokenizer(contentType, " \t;");
             if(st.hasMoreTokens()) {
                 String mimetype= st.nextToken();
                 if(mimetype.equalsIgnoreCase(org.apache.axis.Message.MIME_MULTIPART_RELATED)){
                   mpartStream= new org.apache.axis.attachments.MultiPartRelatedInputStream(contentType,
                     (java.io.InputStream)intialContents);
                   if(null == contentLocation){ //If the content location is not specified as
                                            //of the main message use the SOAP content location.
                       contentLocation= mpartStream.getContentLocation();

                       if(contentLocation != null){
                           contentLocation= contentLocation.trim();
                           if(contentLocation.length()==0) contentLocation= null;
                       }
                   }
          
                  soapPart= new org.apache.axis.SOAPPart(msg, mpartStream, false); 
                 }
                 else if(mimetype.equalsIgnoreCase(org.apache.axis.Message.MIME_APPLICATION_DIME)){ //do nothing today.
                   //is= new DIMEInputStreamManager(is);
                 }
             }
           }
      }
    }

    /**
     * Create an attachment part with a buried JAF data handler.
     */
    public Part createAttachmentPart(Object datahandler ) throws org.apache.axis.AxisFault{
       if(!( datahandler  instanceof javax.activation.DataHandler )){
            throw new org.apache.axis.AxisFault( "Unsupported attachment type \"" + datahandler.getClass().getName()
             + "\" only supporting \"" + javax.activation.DataHandler.class.getName() +"\".");
         }
        Part ret= new AttachmentPart(msg, (javax.activation.DataHandler)datahandler);
        attachments.put(ret.getContentId(), ret); 
        return ret;
    }

    /**
     * Add the collection of parts. 
     */
    public void setAttachmentParts( java.util.Collection parts) throws org.apache.axis.AxisFault{
       attachments.clear();
       if(parts != null && !parts.isEmpty()){
           for(java.util.Iterator i= parts.iterator(); i.hasNext();){
               Part part= (Part ) i.next();    
               if(null != part){
                   part.setMessage(msg);
                   attachments.put(part.getContentId(),  part);
               }
           }
       }
    }

    /**
     * This method should look at a refernce and determine if it is a CID: or url
     * to look for attachment.
     * @param  The reference in the xml that referers to an attachment.
     * @return The part associated with the attachment.
     */ 
    public Part getAttachmentByReference(String reference) throws org.apache.axis.AxisFault {
        if(null == reference ) return null;
        reference= reference.trim();
        if(0== reference.length()) return null;

        String[]id= null;
        String referenceLC= reference.toLowerCase();
        if(!referenceLC.startsWith("cid:") && null != contentLocation){
            String  fqreference= contentLocation; 
            if(!fqreference.endsWith("/")) fqreference += "/";
            if(reference.startsWith("/")) fqreference += reference.substring(1); 
            else fqreference += reference;
            id= new String[]{reference, fqreference };
        }else{
            id= new String[]{reference};
        }
        Part ret= (AttachmentPart)attachments.get(id);
        if(ret == null && mpartStream != null ){
          //We need to still check if this coming in the input stream;
          ret= mpartStream.getAttachmentByReference(id); 
        }
        return  ret;
    }
    
    /**
     * This method will return all attachments as a collection. 
     * 
     * @return A collection of attachments. 
     */ 
    public java.util.Collection getAttachments() throws org.apache.axis.AxisFault{
        java.util.Collection ret= new java.util.LinkedList();

        if(null != mpartStream){
            java.util.Collection mc= mpartStream.getAttachments();
            ret= new java.util.LinkedList( mc); // make a copy.
        }

       return ret;
    }

    /**
     * From the complex stream return the root part. 
     * Today this is SOAP.
     */ 
    public Part getRootPart(){
      return soapPart;
    }

    public javax.mail.internet.MimeMultipart multipart= null; 
    
    /**
     * Get the content length of the stream. 
     */ 
    public int getContentLength() throws org.apache.axis.AxisFault {
      try{
        return (int) org.apache.axis.attachments.MimeUtils.getContentLength( multipart !=null ? multipart :
        (multipart= org.apache.axis.attachments.MimeUtils.createMP(msg.getSOAPPart().getAsString(), attachments  )));
      }  
      catch(Exception e){
          throw AxisFault.makeFault(e);
      }
    }

    /**
     * Write the content to the stream. 
     */ 
    public void writeContentToStream(java.io.OutputStream os) throws org.apache.axis.AxisFault {
         org.apache.axis.attachments.MimeUtils.writeToMultiPartStream(os, multipart !=null ? multipart :
      (multipart= org.apache.axis.attachments.MimeUtils.createMP(msg.getSOAPPart().getAsString(), attachments  )));
    }
    /**
     * Gets the content type for the whole stream.
     */
    public String getContentType()throws org.apache.axis.AxisFault {
       return org.apache.axis.attachments.MimeUtils.getContentType( multipart !=null ? multipart :
      (multipart= org.apache.axis.attachments.MimeUtils.createMP(msg.getSOAPPart().getAsString(), attachments  )));
    }

    /**
     *This is the number of attachments.
     **/
    public int getAttachmentCount(){
       return attachments.size(); 
    }

    /**
     * Determine if an object is to be treated as an attchment. 
     *
     * @param value the value that is to be determined if
     * its an attachment.
     *
     * @return True if value should be treated as an attchment. 
     */

    public boolean isAttachment( Object value){
      return AttachmentUtils.isAttachment(value);
    }
}
