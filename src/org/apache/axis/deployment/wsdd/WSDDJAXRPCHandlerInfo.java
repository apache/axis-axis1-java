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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class WSDDJAXRPCHandlerInfo
    extends WSDDDeployableItem
{
    private String _classname;
    private QName[] _headers;
    private Map _map;
    
    /**
     * Default constructor
     */
    public WSDDJAXRPCHandlerInfo()
    {
    }

    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDJAXRPCHandlerInfo(Element e)
        throws WSDDException
    {
        super(e);
        
        String classnameStr = e.getAttribute(ATTR_CLASSNAME);
        if (classnameStr != null && !classnameStr.equals("")) {
            _classname = classnameStr;
        }
        else 
            throw new WSDDException(JavaUtils.getMessage("noClassNameAttr00"));
        
        Element[] elements = getChildElements(e, ELEM_WSDD_PARAM);
        if (elements.length != 0) {
            _map = new HashMap();
      
            // Load up the map
            for (int i = 0; i < elements.length; i++) {
                Element param = elements[i];
                String pname = param.getAttribute(ATTR_NAME);
                String value = param.getAttribute(ATTR_VALUE);
                parameters.put(pname, value); 
            }           
        }
        
        elements = getChildElements(e, ELEM_WSDD_JAXRPC_HEADER);
        if (elements.length != 0) {
            java.util.ArrayList headerList = new java.util.ArrayList();
            for (int i = 0; i < elements.length; i++) {
                Element qElem = elements[i];
                String headerStr = qElem.getAttribute(ATTR_QNAME);
                if (headerStr == null ||  headerStr.equals("")) 
                    throw new WSDDException(JavaUtils.getMessage("noValidHeader"));     

                QName headerQName = XMLUtils.getQNameFromString(headerStr, e);
                headerList.add(headerQName); 
            }
            QName[] headers = new QName[headerList.size()];
            _headers = (QName[]) headerList.toArray(headers);
        }       
    }

    protected QName getElementName()
    {
        return QNAME_JAXRPC_HANDLERINFO;
    }

    public String getHandlerClassName() {
        return _classname;
    }
    
    public void setHandlerClassName(String classname) {
        _classname = classname;
    }
    
    public Map getHandlerMap() {
        return _map;
    }
    
    public void setHandlerMap(Map map) {
        _map = map;
    }
    
    public QName[] getHeaders() {
        return _headers;
    }
    
    public void setHeaders(QName[] headers) {
        _headers = headers;
    }
    
    public void writeToContext(SerializationContext context)
        throws IOException
    {
        // GLT - FIX THIS UP
        AttributesImpl attrs = new AttributesImpl();
        QName name = getQName();
        if (name != null) {
            attrs.addAttribute("", ATTR_NAME, ATTR_NAME,
                               "CDATA", context.qName2String(name));
        }

        attrs.addAttribute("", ATTR_TYPE, ATTR_TYPE,
                           "CDATA", context.qName2String(getType()));
        context.startElement(WSDDConstants.QNAME_HANDLER, attrs);
        writeParamsToContext(context);
        context.endElement();
    }

}
