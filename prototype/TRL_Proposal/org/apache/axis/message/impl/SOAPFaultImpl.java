/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis.message.impl;

import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import org.apache.axis.message.SOAPFault;

import org.apache.axis.util.xml.DOMConverter;
import org.apache.axis.util.xml.DOMHandler;

/**
 * SOAPFaultImpl is an implementation of SOAPFault.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
final public class SOAPFaultImpl
    extends SOAPBodyEntryImpl
    implements SOAPFault
{
    /**
     * The useful number for "SOAP-ENV:VersionMismatch."
     */
    public static final int FAULT_CODE_TYPE_VERSION_MISMATCH = 1;
    /**
     * The useful number for "SOAP-ENV:MustUnderstand."
     */
    public static final int FAULT_CODE_TYPE_MUST_UNDERSTAND = 2;
    /**
     * The useful number for "SOAP-ENV:Client."
     */
    public static final int FAULT_CODE_TYPE_CLIENT = 3;
    /**
     * The useful number for "SOAP-ENV:Server."
     */
    public static final int FAULT_CODE_TYPE_SERVER = 4;
    /**
     * The useful number for "SOAP-ENV:Protocol."
     */
    public static final int FAULT_CODE_TYPE_PROTOCOL = 5;

    /**
     * The useful number for "faultcode."
     */
    public static final int TYPE_FAULT_CODE = 1;
    /**
     * The useful number for "faultstring."
     */
    public static final int TYPE_FAULT_STRING = 2;
    /**
     * The useful number for "faultactor."
     */
    public static final int TYPE_FAULT_ACTOR = 3;
    /**
     * The useful number for "detail."
     */
    public static final int TYPE_DETAIL = 4;

    private static final Hashtable tabFaultCode = new Hashtable();
    static {
        tabFaultCode.put(FAULT_CODE_VERSION_MISMATCH,
                         new Integer(FAULT_CODE_TYPE_VERSION_MISMATCH));
        tabFaultCode.put(FAULT_CODE_MUST_UNDERSTAND,
                         new Integer(FAULT_CODE_TYPE_MUST_UNDERSTAND));
        tabFaultCode.put(FAULT_CODE_CLIENT,
                         new Integer(FAULT_CODE_TYPE_CLIENT));
        tabFaultCode.put(FAULT_CODE_SERVER,
                         new Integer(FAULT_CODE_TYPE_SERVER));
        tabFaultCode.put(FAULT_CODE_PROTOCOL,
                         new Integer(FAULT_CODE_TYPE_PROTOCOL));
    }

    /**
     * Returns the number associated with the faultcode. About faultcode, please see <a target="SOAPSpec" href="http://www.w3.org/TR/SOAP/#_Toc478383499">"4.4.1 SOAP Fault Codes" in "Simple Object Access Protocol (SOAP) 1.1."</a>
     * @param faultCode The faultcode.
     * @return One of the nubmer in FAULT_CODE_TYPE_*.
     */
    public static int getFaultCode(String faultCode) {
        Integer integer;
        if ((integer = (Integer)tabFaultCode.get(faultCode)) == null)
            throw new IllegalArgumentException("Unknown faultCode: " + faultCode);
        return integer.intValue();
    }

    /**
     * Returns the faultcode associated with the number. About faultcode, please see <a target="SOAPSpec" href="http://www.w3.org/TR/SOAP/#_Toc478383499">"4.4.1 SOAP Fault Codes" in "Simple Object Access Protocol (SOAP) 1.1."</a>
     * @param faultCode One of the nubmer in FAULT_CODE_TYPE_*.
     * @return The faultcode.
     */
    public static String getFaultCode(int faultCode) {
        switch (faultCode) {
        case FAULT_CODE_TYPE_VERSION_MISMATCH:
            return FAULT_CODE_VERSION_MISMATCH;
        case FAULT_CODE_TYPE_MUST_UNDERSTAND:
            return FAULT_CODE_MUST_UNDERSTAND;
        case FAULT_CODE_TYPE_CLIENT:
            return FAULT_CODE_CLIENT;
        case FAULT_CODE_TYPE_SERVER:
            return FAULT_CODE_SERVER;
        case FAULT_CODE_TYPE_PROTOCOL:
            return FAULT_CODE_PROTOCOL;
        default:
            throw new IllegalArgumentException("Unknown faultCode: " + faultCode);
        }
    }

    private static final Hashtable tabField = new Hashtable();
    static {
        tabField.put(STRING_FAULT_CODE, new Integer(TYPE_FAULT_CODE));
        tabField.put(STRING_FAULT_STRING, new Integer(TYPE_FAULT_STRING));
        tabField.put(STRING_FAULT_ACTOR, new Integer(TYPE_FAULT_ACTOR));
        tabField.put(STRING_DETAIL, new Integer(TYPE_DETAIL));
    }

    /**
     * Returns the number associated with the subelement name of <code>&lt;SOAP-ENV:Fault&gt;</code>. For more details, please see <a target="SOAPSpec" href="http://www.w3.org/TR/SOAP/#_Toc478383507">"4.4 SOAP Fault" in "Simple Object Access Protocol (SOAP) 1.1."</a>
     * @param fieldName The subelement name.
     * @return The number.
     */
    public static int getFieldType(String fieldName) {
        Integer integer;
        if ((integer = (Integer)tabField.get(fieldName)) == null)
            throw new IllegalArgumentException("Unknown field name: " + fieldName);
        return integer.intValue();
    }

    /**
     * Returns the subelement name of <code>&lt;SOAP-ENV:Fault&gt;</code> associated with the number. For more details, please see target="SOAPSpec" <a href="http://www.w3.org/TR/SOAP/#_Toc478383507">"4.4 SOAP Fault" in "Simple Object Access Protocol (SOAP) 1.1."</a>
     * @param type One of the number in TYPE_*.
     * @return The subelement name.
     */
    public static String getFieldName(int type) {
        switch (type) {
        case TYPE_FAULT_CODE:
            return STRING_FAULT_CODE;
        case TYPE_FAULT_STRING:
            return STRING_FAULT_STRING;
        case TYPE_FAULT_ACTOR:
            return STRING_FAULT_ACTOR;
        case TYPE_DETAIL:
            return STRING_DETAIL;
        default:
            throw new IllegalArgumentException("Unknown field type: " + type);
        }
    }

    SOAPFaultImpl(Element entity) { super(entity); }

    SOAPFaultImpl(Document dom,
                  String faultCode,
                  String faultString,
                  String faultActor) {
        super(dom.createElementNS(Constants.URI_SOAP_ENV, Constants.NSPREFIX_SOAP_ENV+':'+Constants.ELEM_FAULT));
        setFaultCode(faultCode);
        setFaultString(faultString);
        setFaultActor(faultActor);
    }

    /*
    public SOAPFaultImpl(Document dom,
                         String faultCode,
                         String faultActor,
                         Object obj) {
        this(dom, faultCode, (obj instanceof Throwable ? ((Throwable)obj).getMessage() : obj.toString()), faultActor);
        try {
            setDetail(new Element[]{ new ObjectStore(obj, dom).getElement(), });
        } catch (IOException e) {
        }
    }
    */

    SOAPFaultImpl(Document dom,
                         String faultCode,
                         String faultString,
                         String faultActor,
                         Element[] detailElements) {
        this(dom, faultCode, faultString, faultActor);
        setDetail(detailElements);
    }

    /*
      public SOAPFaultImpl(Document dom,
                         String faultCode,
                         String faultString,
                         String faultActor,
                         String[] detailElements)
        throws SAXException
    {
        this(dom, faultCode, faultString, faultActor);
        if (detailElements != null && detailElements.length != 0) {
            Element[] elems = new Element[detailElements.length];
            for (int i = 0; i < elems.length; i++)
                if (detailElements[i] != null)
                    elems[i] = DOMConverter.toDOM(detailElements[i]).getDocumentElement();
            setDetail(elems);
        }
        }*/

    public void setFaultCode(String value) { setField(TYPE_FAULT_CODE, value); }
    public void setFaultString(String value) { setField(TYPE_FAULT_STRING, value); }
    public void setFaultActor(String value) { setField(TYPE_FAULT_ACTOR, value); }
    public void setDetail(Element[] detailElements) {
        if (detailElements != null && detailElements.length != 0) {
            Element detail = initField(TYPE_DETAIL);
            for (int i = 0; i < detailElements.length; i++)
                if (detailElements[i] != null)
                    detail.appendChild(detailElements[i]);
        }
    }

    public String getFaultCode() { return getField(TYPE_FAULT_CODE); }
    public String getFaultString() { return getField(TYPE_FAULT_STRING); }
    public String getFaultActor() { return getField(TYPE_FAULT_ACTOR); }
    public Element[] getDetail() {
        try {
            Vector buf = new Vector();
            NodeList list = getFieldElement(TYPE_DETAIL).getChildNodes();
            int length = list.getLength();
            for (int i = 0; i < length; i++)
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                    buf.addElement(list.item(i));
            Element[] elements = new Element[buf.size()];
            for (int i = 0; i < elements.length; i++)
                elements[i] = (Element)buf.elementAt(i);
            return elements;
        } catch (NoSuchElementException e) {
            return new Element[]{};
        }
    }

    public void removeFaultCode() { removeField(TYPE_FAULT_CODE); }
    public void removeFaultString() { removeField(TYPE_FAULT_STRING); }
    public void removeFaultActor() { removeField(TYPE_FAULT_ACTOR); }
    public void removeDetail() { removeField(TYPE_DETAIL); }

    /**
     * Initializes a child element of SOAP Fault by type.
     */
    private Element initField(int type) {
        Element fault = getDOMEntity();
        Document dom = fault.getOwnerDocument();
        Element field = dom.createElement(getFieldName(type));

        try {
            fault.replaceChild(field, getFieldElement(type));
        } catch (NoSuchElementException e) {
            fault.appendChild(field);
        }

        return field;
    }

    /**
     * Sets the value of a child element of SOAP Fault by type.
     */
    private void setField(int type, String value) {
        if (value != null && !"".equals(value))
            initField(type).appendChild(getDOMEntity().getOwnerDocument().createTextNode(value));
    }

    /**
     * Returns the value of a child element of SOAP Fault by type.
     */
    private String getField(int type) {
        try {
            return DOMHandler.getNodeValue(getFieldElement(type));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Removes a child element of SOAP Fault by type.
     */
    private void removeField(int type) {
        try {
            getDOMEntity().removeChild(getFieldElement(type));
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * Returns a child element of SOAP Fault by type.
     */
    private Element getFieldElement(int type) {
        return getElementByName(getFieldName(type));
    }

    /**
     * Returns a child element of SOAP Fault by name.
     */
    private Element getElementByName(String name)
        throws NoSuchElementException
    {
        Element entity;
        if ((entity = getDOMEntity()) == null)
            throw new NoSuchElementException();
        NodeList list = entity.getElementsByTagName(name);
        if (list.getLength() == 0)
            throw new NoSuchElementException();
        return (Element)list.item(0);
    }

    /*
    public void storeObject(Object object) throws IOException {
        setDetail(new Element[]{ new ObjectStore(object, getDOMEntity().getOwnerDocument()).getElement(), });
    }

    public Object loadObject() throws ClassNotFoundException {
        Element[] detail = getDetail();
        if (detail.length == 1)
            return new ObjectStore(detail[0]).loadObject();
        return new java.util.NoSuchElementException("Unexpected detail element count: " + detail.length);
        }

    }*/
}
