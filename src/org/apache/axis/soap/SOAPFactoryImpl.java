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
package org.apache.axis.soap;

import org.apache.axis.message.Detail;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * SOAP Element Factory implementation
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class SOAPFactoryImpl extends javax.xml.soap.SOAPFactory {
    /**
     * Create a <CODE>SOAPElement</CODE> object initialized with
     * the given <CODE>Name</CODE> object.
     * @param   name a <CODE>Name</CODE> object with
     *     the XML name for the new element
     * @return the new <CODE>SOAPElement</CODE> object that was
     *     created
     * @throws  SOAPException if there is an error in
     *     creating the <CODE>SOAPElement</CODE> object
     */
    public SOAPElement createElement(Name name) throws SOAPException {
        return new MessageElement(name);
    }

    /**
     * Create a <CODE>SOAPElement</CODE> object initialized with
     * the given local name.
     * @param   localName a <CODE>String</CODE> giving
     *     the local name for the new element
     * @return the new <CODE>SOAPElement</CODE> object that was
     *     created
     * @throws  SOAPException if there is an error in
     *     creating the <CODE>SOAPElement</CODE> object
     */
    public SOAPElement createElement(String localName) throws SOAPException {
        return new MessageElement("",localName);
    }

    /**
     * Create a new <CODE>SOAPElement</CODE> object with the
     * given local name, prefix and uri.
     * @param   localName a <CODE>String</CODE> giving
     *     the local name for the new element
     * @param   prefix the prefix for this <CODE>
     *     SOAPElement</CODE>
     * @param   uri a <CODE>String</CODE> giving the
     *     URI of the namespace to which the new element
     *     belongs
     * @return the new <CODE>SOAPElement</CODE> object that was
     *     created
     * @throws  SOAPException if there is an error in
     *     creating the <CODE>SOAPElement</CODE> object
     */
    public SOAPElement createElement(
            String localName, String prefix, String uri) throws SOAPException {
        return new MessageElement(localName, prefix, uri);
    }

    public javax.xml.soap.Detail createDetail()
            throws SOAPException {
        return new Detail();
    }

    public Name createName(String localName, String prefix, String uri)
            throws SOAPException {
        return new PrefixedQName(uri,localName,prefix);
    }

    public Name createName(String localName)
            throws SOAPException {
        return new PrefixedQName("",localName,"");
    }
}
