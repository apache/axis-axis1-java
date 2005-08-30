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

package org.apache.axis.utils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.Constants;
import org.apache.commons.logging.Log;

import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.ListIterator;


public class WSDLUtils {
    protected static Log log =
        LogFactory.getLog(WSDLUtils.class.getName());

    /**
     * Return the endpoint address from a <soap:address location="..."> tag
     */
    public static String getAddressFromPort(Port p) {
        // Get the endpoint for a port
        List extensibilityList = p.getExtensibilityElements();
        for (ListIterator li = extensibilityList.listIterator(); li.hasNext();) {
            Object obj = li.next();
            if (obj instanceof SOAPAddress) {
                return ((SOAPAddress) obj).getLocationURI();
            } else if (obj instanceof UnknownExtensibilityElement){
                //TODO: After WSDL4J supports soap12, change this code
                UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement) obj;
                QName name = unkElement.getElementType();
                if(name.getNamespaceURI().equals(Constants.URI_WSDL12_SOAP) && 
                   name.getLocalPart().equals("address")) {
                    return unkElement.getElement().getAttribute("location");
                }
            }
        }
        // didn't find it
        return null;
    } // getAddressFromPort

} // class WSDLUtils
