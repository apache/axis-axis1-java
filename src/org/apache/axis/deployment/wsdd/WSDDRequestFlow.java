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

import org.w3c.dom.Element;

import javax.xml.namespace.QName;


/**
 *
 */
public class WSDDRequestFlow
    extends WSDDChain
{
    /**
     * Default constructor
     */ 
    public WSDDRequestFlow()
    {
    }
    
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDRequestFlow(Element e)
        throws WSDDException
    {
        super(e);
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_REQFLOW;
    }
}
