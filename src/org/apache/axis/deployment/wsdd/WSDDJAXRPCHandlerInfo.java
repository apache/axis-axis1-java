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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 *
 */
public class WSDDJAXRPCHandlerInfo
    extends WSDDElement
{
    private String _classname;
    private Map _map;
    private QName[] _headers;
    
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
            throw new WSDDException(Messages.getMessage("noClassNameAttr00"));
        
        Element[] elements = getChildElements(e, ELEM_WSDD_PARAM);
        if (elements.length != 0) {
            _map = new HashMap();
      
            // Load up the map
            for (int i = 0; i < elements.length; i++) {
                Element param = elements[i];
                String pname = param.getAttribute(ATTR_NAME);
                String value = param.getAttribute(ATTR_VALUE);
                _map.put(pname, value);
            }           
        }
        
        elements = getChildElements(e, ELEM_WSDD_JAXRPC_HEADER);
        if (elements.length != 0) {
            java.util.ArrayList headerList = new java.util.ArrayList();
            for (int i = 0; i < elements.length; i++) {
                Element qElem = elements[i];
                String headerStr = qElem.getAttribute(ATTR_QNAME);
                if (headerStr == null ||  headerStr.equals("")) 
                    throw new WSDDException(Messages.getMessage("noValidHeader"));     

                QName headerQName = XMLUtils.getQNameFromString(headerStr, qElem);
                if (headerQName != null) 
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
    AttributesImpl attrs = new AttributesImpl();
    attrs.addAttribute("", ATTR_CLASSNAME, ATTR_CLASSNAME,
                   "CDATA", _classname);
    context.startElement(WSDDConstants.QNAME_JAXRPC_HANDLERINFO, attrs);

    Map ht =  _map;
    if (ht != null) {
        Set keys= ht.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            String value = (String) ht.get(name);
            attrs = new AttributesImpl();
            attrs.addAttribute("",ATTR_NAME, ATTR_NAME, "CDATA", name);
            attrs.addAttribute("",ATTR_VALUE, ATTR_VALUE, "CDATA", value);
            context.startElement(WSDDConstants.QNAME_PARAM,attrs);
            context.endElement();
        }
    }

    if (_headers != null) {
        for (int i=0 ; i < _headers.length ; i++) {
        QName qname = _headers[i];
        attrs = new AttributesImpl();
        attrs.addAttribute("",ATTR_QNAME,ATTR_QNAME,"CDATA",context.qName2String(qname));
        context.startElement(WSDDConstants.QNAME_JAXRPC_HEADER,attrs);
        context.endElement();
        }
    }

    context.endElement();
    }

}
