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

/* @author Rob Jellinghaus (robj@unrealities.com) */
/* @author Rick Rineholt */
package org.apache.axis.attachments;

import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.SOAPPart;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
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

    /** Field attachments.           */
    private HashMap attachments = new java.util.HashMap();

    /** Field orderedAttachments.           */
    private LinkedList orderedAttachments = new LinkedList();

    /** Field soapPart.           */
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
     * The HashMap for DataHandler Managements.
     */
	private HashMap stackDataHandler = new HashMap();

    /**
     * Construct one of these on a parent Message.
     * Should only ever be called by Message constructor!
     *
     * @param intialContents should be anything but today only a stream is
     *        supported.
     * @param contentType The mime content type of the stream for transports
     *        that provide it.
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
     * @param reference The reference that referers to an attachment.
     *
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
     * @return Part old attachment with the same Content-ID, or null.
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

    public Part createAttachmentPart(Object datahandler)
            throws org.apache.axis.AxisFault {

        // Searching for the same attachements
    	Integer key = new Integer(datahandler.hashCode());
    	if (stackDataHandler.containsKey(key)) {
        	return (Part)stackDataHandler.get(key);
        }

        multipart = null;

        dimemultipart = null;

        mergeinAttachments();

        if (!(datahandler instanceof javax.activation.DataHandler)) {
            throw new org.apache.axis.AxisFault(
                    Messages.getMessage(
                            "unsupportedAttach", datahandler.getClass().getName(),
                            javax.activation.DataHandler.class.getName()));
        }

        Part ret =
                new AttachmentPart((javax.activation.DataHandler) datahandler);

        addAttachmentPart(ret);

        // Store the current DataHandler with its key
    	stackDataHandler.put(key, ret);

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
                Object part = i.next();

                if (null != part) {
                    if(part instanceof Part)
                        addAttachmentPart((Part)part);
                    else
                        createAttachmentPart(part);
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
     * @param reference The reference in the xml that referers to an attachment.
     *
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
     * @return the root <code>Part</code>
     */
    public Part getRootPart() {
        return soapPart;
    }

    public void setRootPart(Part newRoot) {

        try {
            this.soapPart = (SOAPPart) newRoot;
            multipart = null;
            dimemultipart = null;
        } catch (ClassCastException e) {
            throw new ClassCastException(Messages.getMessage("onlySOAPParts"));
        }
    }

    /** multipart , cached entries for the stream of attachment that are going to be sent.   */
    javax.mail.internet.MimeMultipart multipart = null;
    DimeMultiPart dimemultipart = null;

    /**
     * Get the content length of the stream.
     *
     * @return the content length of the stream
     *
     * @throws org.apache.axis.AxisFault
     */
    public long getContentLength() throws org.apache.axis.AxisFault {

        mergeinAttachments();

        int sendtype= this.sendtype == SEND_TYPE_NOTSET ? SEND_TYPE_DEFAULT :   this.sendtype;

        try {
              if(sendtype == SEND_TYPE_MIME)
                 return org.apache.axis.attachments.MimeUtils.getContentLength(
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
     * @return a DIME part
     *
     * @throws org.apache.axis.AxisFault if the part could not be built
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
     * @return the content type for the whole stream
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
     * @return the number of attachments
     */
    public int getAttachmentCount() {

        try {
            mergeinAttachments();

            // force a serialization of the message so that
            // any attachments will be added
            soapPart.getAsBytes();
            
            return orderedAttachments.size();
        } catch (AxisFault e) {
            log.warn(Messages.getMessage("exception00"),e);
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
            stackDataHandler.clear();
        } catch (AxisFault af){
            log.warn(Messages.getMessage("exception00"),af);
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
        java.util.Iterator iterator = GetAttachmentsIterator();
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
     * get an iterator over all attachments. This
     * @return iterator of Part Objects; some of which may be
     * AttachmentPart instances
     * @see org.apache.axis.Part
     * @see AttachmentPart
     */
    private java.util.Iterator GetAttachmentsIterator() {
        java.util.Iterator iterator = attachments.values().iterator();
        return iterator;
    }

    /**
     * Create a new attachment Part in this Message.
     * Will actually, and always, return an AttachmentPart.
     *
     * @return a new attachment Part
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

    /**
     * dispose of the attachments and their files; do not use the object
     * after making this call.
     */

    public void dispose() {
        java.util.Iterator iterator = GetAttachmentsIterator();
        while (iterator.hasNext()) {
            Part part = (Part) iterator.next();
            if (part instanceof AttachmentPart) {
                AttachmentPart apart=(AttachmentPart)part;
                apart.dispose();
            }
        }

    }

    // consider type-safe e-num here?
    /**
     * Determine how an object typically sent as attachments are to
     * be represented. Currently, MIME DIME and NONE are reccognised.
     *
     * @param value  a String representing a sending type, treated in a
     *              case-insensetive manner
     * @return an <code>int</code> send type code
     */
    public static int getSendType(String value) {
        if (value.equalsIgnoreCase("MIME")) return SEND_TYPE_MIME;
        if (value.equalsIgnoreCase("DIME")) return SEND_TYPE_DIME;
        if (value.equalsIgnoreCase("NONE")) return SEND_TYPE_NONE;
        return SEND_TYPE_NOTSET;
    }

    /**
     * For a given sendType value, return a string representation.
     *
     * @param value  a type code integer
     * @return a <code>String</code> representation of <code>value</code>
     */
    public static String getSendTypeString(int value) {
        if (value == SEND_TYPE_MIME) {
            return "MIME";
        }
        if (value == SEND_TYPE_DIME) {
            return "DIME";
        }
        if (value == SEND_TYPE_NONE) {
            return "NONE";
        }
        return null;
    }
}
