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

/**
 * Detail Entry implementation
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class DetailEntry extends MessageElement implements javax.xml.soap.DetailEntry {
    public DetailEntry(javax.xml.soap.Name name){
        super(name);
    }

    public javax.xml.soap.SOAPElement addTextNode(String text) throws javax.xml.soap.SOAPException {
        javax.xml.soap.SOAPElement element = super.addTextNode(text);
        org.apache.axis.message.Detail detail = (org.apache.axis.message.Detail)this.getParentElement();
        org.apache.axis.AxisFault fault = detail.getFault();
        fault.addFaultDetailString(text);
        return element;
    }
}
