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
/* @author Rick Rineholt */
package org.apache.axis.attachments;

import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.SOAPPart;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Implements the Attachment interface, via an actual Hashmap of actual
 * AttachmentParts.
 */
public class AttachmentsImpl implements Attachments {
    protected static Log log =
            LogFactory.getLog(AttachmentsImpl.class.getName());

    /** Field attachments           */
    private HashMap attachments = new java.util.HashMap();

    /** Field orderedAttachments           */
    private LinkedList orderedAttachments = new LinkedList();

    /** Field soapPart           */
    protected SOAPPart soapPart = null;

    /**
     * The actual stream to manage the multi-related input stream.
     */
    protected MultiPartInputStream  mpartStream =
            null;
    /**
     * The form of the attachments, whether MIME or DIME.
     */
    protected int sendtype= Attachments.SEND_TYPE_NOTSET;

    /**
     * This is the content location as specified in SOAP with Attachments.
     * This maybe null if the message had no Content-Location specifed.
     */
    protected String contentLocation = null;

    /**
     * Construct one of these on a parent Message.
     * Should only ever be called by Message constructor!
     * @param initialContents should be anything but today only a stream is
     *        supported.
     * @param The mime content type of the stream for transports that provide
     *        it.
     *
     * @param intialContents
     * @param contentType
     * @param contentLocation
     *
     * @throws org.apache.axis.AxisFault
     */
    public AttachmentsImpl(
            Object intialContents, String contentType, String contentLocation)
            throws org.apache.axis.AxisFault {

        if (contentLocation != null) {
            contentLocation = contentLocation.trim();

            if (contentLocation.length() == 0) {
                contentLocation = null;
            }
        }

        this.contentLocation = contentLocation;

        if (contentType != null) {
            if (contentType.equals(org.apache.axis.Message.MIME_UNKNOWN)) {

            } else {
                java.util.StringTokenizer st =
                        new java.util.StringTokenizer(contentType, " \t;");

                if (st.hasMoreTokens()) {
                    String mimetype = st.nextToken();

                    if (mimetype.equalsIgnoreCase(
                            org.apache.axis.Message.MIME_MULTIPART_RELATED)) {
                        sendtype=  SEND_TYPE_MIME;     
                        mpartStream =
                                new org.apache.axis.attachments.MultiPartRelatedInputStream(
                                        contentType,
                                        (java.io.InputStream) intialContents);

                        if (null == contentLocation) {

                            // If the content location is not specified as
                            // of the main message use the SOAP content location.
                            contentLocation = mpartStream.getContentLocation();

                            if (contentLocation != null) {
                                contentLocation = contentLocation.trim();

                                if (contentLocation.length() == 0) {
                                    contentLocation = null;
                                }
                            }
                        }

                        soapPart = new org.apache.axis.SOAPPart(null,
                                mpartStream,
                                false);
                     } else if (mimetype.equalsIgnoreCase(org.apache.axis.Message.MIME_APPLICATION_DIME)) { 
                         try{
                            mpartStream=
                             new MultiPartDimeInputStream( (java.io.InputStream) intialContents);
                             soapPart = new org.apache.axis.SOAPPart(null, mpartStream, false);
                         }catch(Exception e){ throw org.apache.axis.AxisFault.makeFault(e);}
                         sendtype=  SEND_TYPE_DIME;     
                    }
                }
            }
        }
    }

    /**
     * Copies attachment references from the multipartStream to local list.
     * Done only once per object creation.
     *
     * @throws AxisFault
     */
    private void mergeinAttachments() throws AxisFault {

        if (mpartStream != null) {
            Collection atts = mpartStream.getAttachments();

            if(contentLocation == null)
                contentLocation= mpartStream.getContentLocation();

            mpartStream = null;

            setAttachmentParts(atts);
        }
    }

    /**
     * This method uses getAttacmentByReference() to look for attachment.
     *     If attachment has been found, it will be removed from the list, and
     *     returned to the user.
     * @param  The reference that referers to an attachment.
     *
     * @param reference
     * @return The part associated with the removed attachment, or null.
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part removeAttachmentPart(String reference)
            throws org.apache.axis.AxisFault {

        multipart = null;
        
        dimemultipart = null;

        mergeinAttachments();

        Part removedPart = getAttachmentByReference(reference);

        if (removedPart != null) {
            attachments.remove(removedPart.getContentId());
            attachments.remove(removedPart.getContentLocation());
            orderedAttachments.remove(removedPart);
        }

        return removedPart;
    }

    /**
     * Adds an existing attachment to this list.
     * Note: Passed part will be bound to this message.
     * @param newPart new part to add
     * @returns Part old attachment with the same Content-ID, or null.
     *
     * @return
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part addAttachmentPart(Part newPart)
            throws org.apache.axis.AxisFault {


        multipart = null;
        dimemultipart = null;

        mergeinAttachments();

        Part oldPart = (Part) attachments.put(newPart.getContentId(), newPart);

        if (oldPart != null) {
            orderedAttachments.remove(oldPart);
            attachments.remove(oldPart.getContentLocation());
        }

        orderedAttachments.add(newPart);

        if (newPart.getContentLocation() != null) {
            attachments.put(newPart.getContentLocation(), newPart);
        }

        return oldPart;
    }

    /**
     * Create an attachment part with a buried JAF data handler.
     *
     * @param datahandler
     *
     * @return
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part createAttachmentPart(Object datahandler)
            throws org.apache.axis.AxisFault {

        multipart = null;

        dimemultipart = null;

        mergeinAttachments();

        if (!(datahandler instanceof javax.activation.DataHandler)) {
            throw new org.apache.axis.AxisFault(
                    JavaUtils.getMessage(
                            "unsupportedAttach", datahandler.getClass().getName(),
                            javax.activation.DataHandler.class.getName()));
        }

        Part ret =
                new AttachmentPart((javax.activation.DataHandler) datahandler);

        addAttachmentPart(ret);

        return ret;
    }

    /**
     * Add the collection of parts.
     *
     * @param parts
     *
     * @throws org.apache.axis.AxisFault
     */
    public void setAttachmentParts(java.util.Collection parts)
            throws org.apache.axis.AxisFault {

        removeAllAttachments();

        if ((parts != null) && !parts.isEmpty()) {
            for (java.util.Iterator i = parts.iterator(); i.hasNext();) {
                Part part = (Part) i.next();

                if (null != part) {
                    addAttachmentPart(part);
                }
            }
        }
    }

    /**
     * This method should look at a refernce and determine if it is a CID: or
     * url to look for attachment.
     *     <br>
     *     Note: if Content-Id or Content-Location headers have changed by outside
     *     code, lookup will not return proper values. In order to change these
     *     values attachment should be removed, then added again.
     * @param  The reference in the xml that referers to an attachment.
     *
     * @param reference
     * @return The part associated with the attachment.
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part getAttachmentByReference(String reference)
            throws org.apache.axis.AxisFault {

        if (null == reference) {
            return null;
        }

        reference = reference.trim();

        if (0 == reference.length()) {
            return null;
        }

        mergeinAttachments();

        //This search will pickit up if its fully qualified location or if it's a content-id
        // that is not prefixed by the cid.

        Part ret = (Part) attachments.get(reference);
        if( null != ret) return ret;


        if (!reference.startsWith(Attachments.CIDprefix) && (null != contentLocation)) {
            //Not a content-id check to see if its a relative location id.

                String fqreference = contentLocation;

                if (!fqreference.endsWith("/")) {
                    fqreference += "/";
                }

                if (reference.startsWith("/")) {
                    fqreference += reference.substring(1);
                } else {
                    fqreference += reference;
                }

                // lets see if we can get it as Content-Location
                ret = (AttachmentPart) attachments.get(fqreference);
        }

        if( null == ret && reference.startsWith(Attachments.CIDprefix)){ 
             //This is a content-id lets see if we have it.
                ret = (Part) attachments.get( reference.substring(4));
        }

        return ret;
    }

    /**
     * This method will return all attachments as a collection.
     *
     * @return A collection of attachments.
     *
     * @throws org.apache.axis.AxisFault
     */
    public java.util.Collection getAttachments()
            throws org.apache.axis.AxisFault {

        mergeinAttachments();

        return new LinkedList(orderedAttachments);
    }

    /**
     * From the complex stream return the root part.
     * Today this is SOAP.
     *
     * @return
     */
    public Part getRootPart() {
        return soapPart;
    }

    /**
     * Sets the root part of this multipart block
     *
     * @param newRoot
     */
    public void setRootPart(Part newRoot) {

        try {
            this.soapPart = (SOAPPart) newRoot;
            multipart = null;
            dimemultipart = null;
        } catch (ClassCastException e) {
            throw new ClassCastException(JavaUtils.getMessage("onlySOAPParts"));
        }
    }

    /** multipart , cached entries for the stream of attachment that are going to be sent.   */
    javax.mail.internet.MimeMultipart multipart = null;
    DimeMultiPart dimemultipart = null;

    /**
     * Get the content length of the stream.
     *
     * @return
     *
     * @throws org.apache.axis.AxisFault
     */
    public long getContentLength() throws org.apache.axis.AxisFault {

        mergeinAttachments();

        int sendtype= this.sendtype == SEND_TYPE_NOTSET ? SEND_TYPE_DEFAULT :   this.sendtype;       

        try {
              if(sendtype == SEND_TYPE_MIME)
                 return (int)org.apache.axis.attachments.MimeUtils.getContentLength(
                                multipart != null ? multipart : (multipart = org.apache.axis.attachments.MimeUtils.createMP(soapPart.getAsString(), orderedAttachments)));
              else if (sendtype == SEND_TYPE_DIME)return createDimeMessage().getTransmissionSize();
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
        return 0;
    }

    /**
     * Creates the DIME message 
     *
     * @return
     *
     * @throws org.apache.axis.AxisFault
     */
    protected DimeMultiPart createDimeMessage() throws org.apache.axis.AxisFault{
        int sendtype= this.sendtype == SEND_TYPE_NOTSET ? SEND_TYPE_DEFAULT :   this.sendtype;       
        if (sendtype == SEND_TYPE_DIME){
           if(dimemultipart== null){

              dimemultipart= new DimeMultiPart(); 
              dimemultipart.addBodyPart(new DimeBodyPart(
                soapPart.getAsBytes(), DimeTypeNameFormat.URI,
                "http://schemas.xmlsoap.org/soap/envelope/",
                  "uuid:714C6C40-4531-442E-A498-3AC614200295"));

              for( java.util.Iterator i= orderedAttachments.iterator();
                i.hasNext(); ){
                AttachmentPart part= (AttachmentPart)i.next();
                    DataHandler dh= AttachmentUtils.
                      getActivationDataHandler(part);
                    dimemultipart.addBodyPart(new 
                      DimeBodyPart(dh,part.getContentId()));
              }
            }
        }
        return dimemultipart;
      }

    /**
     * Write the content to the stream.
     *
     * @param os
     *
     * @throws org.apache.axis.AxisFault
     */
    public void writeContentToStream(java.io.OutputStream os)
            throws org.apache.axis.AxisFault {
        int sendtype= this.sendtype == SEND_TYPE_NOTSET ?
                          SEND_TYPE_DEFAULT : this.sendtype;       
        try{    

        mergeinAttachments();
        if(sendtype == SEND_TYPE_MIME){        
        org.apache.axis.attachments.MimeUtils.writeToMultiPartStream(os,
                (multipart != null)
                ? multipart
                : (multipart =
                   org.apache.axis.attachments.MimeUtils.createMP(
                        soapPart.getAsString(), orderedAttachments)));

        for (java.util.Iterator i = orderedAttachments.iterator();
             i.hasNext();) {
            AttachmentPart part = (AttachmentPart) i.next();
            DataHandler dh =
                    AttachmentUtils.getActivationDataHandler(part);
            DataSource ds = dh.getDataSource();

            if ((ds != null) && (ds instanceof ManagedMemoryDataSource)) {
                ((ManagedMemoryDataSource) ds).delete();
            }
        }
        }else if (sendtype == SEND_TYPE_DIME)createDimeMessage().write(os);
       }catch(Exception e){ throw org.apache.axis.AxisFault.makeFault(e);}
    }

    /**
     * Gets the content type for the whole stream.
     *
     * @return
     *
     * @throws org.apache.axis.AxisFault
     */
    public String getContentType() throws org.apache.axis.AxisFault {

        mergeinAttachments();

        int sendtype= this.sendtype == SEND_TYPE_NOTSET ? SEND_TYPE_DEFAULT :
               this.sendtype;       
        if(sendtype == SEND_TYPE_MIME)        
          return org.apache.axis.attachments.MimeUtils.getContentType((multipart
                  != null)
                  ? multipart
                  : (multipart =
                  org.apache.axis.attachments.MimeUtils.createMP(
                          soapPart.getAsString(),
                          orderedAttachments)));
        else return org.apache.axis.Message.MIME_APPLICATION_DIME;        
    }

    /**
     * This is the number of attachments.
     *
     * @return
     */
    public int getAttachmentCount() {

        try {
            mergeinAttachments();

            return orderedAttachments.size();
        } catch (AxisFault e) {
        }

        return 0;
    }

    /**
     * Determine if an object is to be treated as an attchment.
     *
     * @param value the value that is to be determined if
     * its an attachment.
     *
     * @return True if value should be treated as an attchment.
     */
    public boolean isAttachment(Object value) {
        return AttachmentUtils.isAttachment(value);
    }

    /**
     * Removes all <CODE>AttachmentPart</CODE> objects that have
     *   been added to this <CODE>SOAPMessage</CODE> object.
     *
     *   <P>This method does not touch the SOAP part.</P>
     */
    public void removeAllAttachments() {
        try {
            multipart = null;
            dimemultipart = null;
            mergeinAttachments();
            attachments.clear();
            orderedAttachments.clear();
        } catch (AxisFault af){
            log.warn(JavaUtils.getMessage("exception00"));
        }
    }

    /**
     * Retrieves all the <CODE>AttachmentPart</CODE> objects
     * that have header entries that match the specified headers.
     * Note that a returned attachment could have headers in
     * addition to those specified.
     * @param   headers a <CODE>MimeHeaders</CODE>
     *     object containing the MIME headers for which to
     *     search
     * @return an iterator over all attachments that have a header
     *     that matches one of the given headers
     */
    public java.util.Iterator getAttachments(
            javax.xml.soap.MimeHeaders headers) {
        java.util.Vector vecParts = new java.util.Vector();
        java.util.Iterator iterator = attachments.values().iterator();
        while(iterator.hasNext()){
            Part part = (Part) iterator.next();
            if(part instanceof AttachmentPart){
                if(((AttachmentPart)part).matches(headers)){
                    vecParts.add(part);
                }
            }
        }
        return vecParts.iterator();
    }

    /**
     * Create a new attachment Part in this Message.
     * Will actually, and always, return an AttachmentPart.
     *
     * @return
     *
     * @throws org.apache.axis.AxisFault
     */
    public Part createAttachmentPart() throws org.apache.axis.AxisFault {
        return new AttachmentPart();
    }

    public void setSendType( int sendtype){
      if( sendtype < 1)
        throw new IllegalArgumentException("");
      if( sendtype > SEND_TYPE_MAX )
        throw new IllegalArgumentException("");
      this.sendtype= sendtype;  
    }

    public int getSendType(){
      return sendtype;
    }
}
