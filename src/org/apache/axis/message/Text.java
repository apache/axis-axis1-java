/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
