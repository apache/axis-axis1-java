/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.message;

import org.apache.axis.InternalException;
import org.w3c.dom.DOMException;

/**
 * A representation of a node whose value is text. A <CODE>
 *   Text</CODE> object may represent text that is content or text
 *   that is a comment.
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 * @author Heejune Ahn      (cityboy@tmax.co.kr)
 */
public class Text extends MessageElement implements javax.xml.soap.Text {

    public Text(String s) {
        try {
            org.w3c.dom.Document doc = org.apache.axis.utils.XMLUtils.newDocument();
            textRep = doc.createTextNode(s);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new InternalException(e);
        }
    }

    /**
     * Retrieves whether this <CODE>Text</CODE> object
     * represents a comment.
     * @return  <CODE>true</CODE> if this <CODE>Text</CODE> object is
     *     a comment; <CODE>false</CODE> otherwise
     */
    public boolean isComment() {
        String temp = textRep.getNodeValue().trim();
        if(temp.startsWith("<!--") && temp.endsWith("-->"))
            return true;
        return false;
    }

    /**
     * Implementation of DOM TEXT Interface
     * *************************************************************
     */

    // Overriding the MessageElement Method, where it throws exeptions.
    public String getNodeValue() throws DOMException {
        return textRep.getNodeValue();
    }

    // Overriding the MessageElement Method, where it throws exeptions.
    public void setNodeValue(String nodeValue) throws DOMException{
        textRep.setNodeValue(nodeValue);
    }

    /**
     * Use the textRep, and convert it to org.apache.axis.Text
     * in order to keep the Axis SOAP strcture after operation
     *
     * This work would be easier if constructor, Text(org.w3c.dom.Text)
     * is defined
     *
     * @since SAAJ 1.2
     * @param offset
     * @return
     * @throws DOMException
     */
    public org.w3c.dom.Text splitText(int offset) throws DOMException
    {
        int length = textRep.getLength();
        // take the first part, and save the second part for new Text
        // length check and exception will be thrown here, no need to duplicated check
        String tailData = textRep.substringData(offset,length);
        textRep.deleteData(offset,length);

        // insert the first part again as a new node
        Text tailText = new Text(tailData);
        org.w3c.dom.Node myParent = (org.w3c.dom.Node)getParentNode();
        if(myParent != null){
            org.w3c.dom.NodeList brothers = (org.w3c.dom.NodeList)myParent.getChildNodes();
            for(int i = 0;i  < brothers.getLength(); i++){
                if(brothers.item(i).equals(this)){
                    myParent.insertBefore(tailText, this);
                    return tailText;
                }
            }
        }
        return tailText;
    }

    /**
     * @since SAAJ 1.2
     */
    public String getData() throws DOMException {
        return textRep.getData();
    }

    /**
     * @since SAAJ 1.2
     */
    public void setData(String data) throws DOMException  {
        textRep.setData(data);
    }

    /**
     * @since SAAJ 1.2
     *
     * @return
     */
    public int getLength(){
        return textRep.getLength();
    }

    /**
     * @since SAAJ 1.2
     * @param offset
     * @param count
     * @return
     * @throws DOMException
     */
    public String substringData(int offset, int count)throws DOMException {
        return textRep.substringData(offset,count);
    }

    /**
     *
     * @since SAAJ 1.2
     * @param arg
     * @throws DOMException
     */
    public void appendData(String arg) throws DOMException {
        textRep.appendData(arg);
    }

    /**
     * @since SAAJ 1.2
     * @param offset
     * @param arg
     * @throws DOMException
     */
    public void insertData(int offset,  String arg)throws DOMException {
        textRep.insertData(offset, arg);
    }

    /**
     * @since SAAJ 1.2
     * @param offset
     * @param count
     * @param arg
     * @throws DOMException
     */
    public void replaceData(int offset, int count, String arg) throws DOMException   {
        textRep.replaceData(offset, count, arg);
    }

    /**
     * @since SAAJ 1.2
     * @param offset
     * @param count
     * @throws DOMException
     */
    public void deleteData(int offset, int count) throws DOMException {
        textRep.deleteData(offset, count);
    }
}
