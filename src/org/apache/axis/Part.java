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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * A MIME component of an Axis Message.
 * <p>
 * Loosely based on javax.xml.soap.SOAPPart and javax.xml.soap.AttachmentPart.
 * (Why don't <i>they</i> have a Part superclass?)
 * <p>
 * In general, all methods of Part and its subclasses are (or should
 * be) implemented as efficiently as possible.  No buffering is done
 * unless necessary.  (Look-ahead (and hence buffering) is necessary
 * when calculating content length.)
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Rick Rineholt
 */

import org.apache.axis.transport.http.HTTPConstants;

public abstract class Part {

    private Message msg;
    private Hashtable headers = new Hashtable();
    private String contentId;
    private String contentLocation;
    
    /**
     * Fill in the Message field.  (Of course this can only be called by
     * subclass constructors since Part itself is abstract.)
     */
    public Part (Message parent) {
        msg = parent;
        addMimeHeader(HTTPConstants.HEADER_CONTENT_ID , getNewContentIdValue());

    }

    /**
     * Add the specified MIME header, as per JAXM.
     */
    public void addMimeHeader (String header, String value) {

        if(null == header) throw new IllegalArgumentException
            ("Header may not be null!");

        header= header.trim();    

        if(header.length() == 0)
            throw new IllegalArgumentException ("Header may not be empty!");

        if(null == value) throw new IllegalArgumentException
            ("Header value may not be null!");

        headers.put(header.toLowerCase(), value);
    }

    /**
     * Get the specified MIME header.
     */
    public String getMimeHeader (String header) {
        return (String) headers.get(header.toLowerCase());
    }
    
    /**
     * Get the Message for this Part.
     */
    public Message getMessage () {
        return msg;
    }

    /**
     * Set the Message for this Part.
     */
    public void setMessage (Message msg) {
        this.msg= msg;
    }
    
    /**
     * Total size in bytes (of all content and headers, as encoded).
    public abstract int getSize();
     */

    /**
     * Content location.
     */
    public String getContentLocation() {
        return getMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION);
    }

    /**
     * Set content location.
     */
    public void setContentLocation(String loc) {
        addMimeHeader(HTTPConstants.HEADER_CONTENT_LOCATION, loc);
    }

    /**
     * Content ID.
     */
    public String getContentId() {
        String ret= getMimeHeader(HTTPConstants.HEADER_CONTENT_ID);
        //Do not let the contentID ever be empty.
        if(ret == null){
            ret=getNewContentIdValue();
            addMimeHeader(HTTPConstants.HEADER_CONTENT_ID , ret);
        }
        ret= ret.trim();
        if(ret.length() ==0){
            ret=getNewContentIdValue();
            addMimeHeader(HTTPConstants.HEADER_CONTENT_ID , ret);
        }
        return ret;
    }


    /**
     * Get all headers that match 
     */
    public java.util.Iterator getMatchingMimeHeaders( final String[] match){
        java.util.LinkedList retList= new java.util.LinkedList();
        if(null != match && 0 != match.length ){
            for(int i= match.length-1 ; i > -1 ; --i){
                    if(match[i] != null){
                    retList.add( headers.get(match[i].toLowerCase())); 
                }
            }
        }
        return retList.iterator();
    }

    /**
     * Get all headers that do not match 
     */
    public java.util.Iterator getNonMatchingMimeHeaders( final String[] match){
        java.util.LinkedList retList= new java.util.LinkedList(headers.keySet());
        if(null != match && 0 != match.length && !headers.isEmpty()){
            for(int i= match.length-1 ; i > -1 ; --i){
                    if(match[i] != null){
                        String remItem= match[i].toLowerCase();
                        if(headers.containsKey(remItem)){
                            retList.remove(remItem); 
                    }
                }
            }
        }
        return retList.iterator();
    }

    /**
     * Content type.
     */
    public abstract String getContentType();


    static String thisHost = null;

    private static int count = (int) (Math.random() * 100);

    public static String getNewContentIdValue() {
        int lcount;

        synchronized (org.apache.axis.Part.class  ) {
            lcount = ++count;
        }
        if (null == thisHost) {
            try {
                thisHost = java.net.InetAddress.getLocalHost().getHostName();
            } 
            catch (java.net.UnknownHostException e) {
                System.err.println("exception:" + e);
                thisHost = "localhost";
                e.printStackTrace();
            }
        }

        StringBuffer s = new StringBuffer();

        // Unique string is <hashcode>.<currentTime>.apache-soap.<hostname>
        s.append("cid:").append( lcount).append(s.hashCode()).append('.').append(System.currentTimeMillis()).append(".AXIS@").append(thisHost);
        return s.toString();
    }
}

