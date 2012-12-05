/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.model.util;

import javax.xml.namespace.QName;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;

/**
 * Custom {@link XMLHelper} that adds support for {@link QName}. This is necessary because we use an
 * EMF version compatible with Java 1.4 and that version doesn't support {@link QName} (because it's
 * not included in the JRE). Instead it uses its own class to represent QNames. Since Axis depends
 * on {@link QName} anyway, we add support for it here.
 * 
 * @author Andreas Veithen
 */
// TODO: this is actually not entirely correct; Axis may generate QNames that are not strictly valid (such as "ns:>fault") and they should be represented using a specific class
public class AxisXMLHelper extends XMLHelperImpl {
    public AxisXMLHelper(XMLResource resource) {
        super(resource);
    }

    protected String updateQNamePrefix(EFactory factory, EDataType dataType, Object value, boolean list) {
        if (!list && value instanceof QName) {
            QName qname = (QName)value;
            String namespace = qname.getNamespaceURI();
            if (namespace.length() == 0) {
                return qname.getLocalPart();
            } else {
                EPackage ePackage = extendedMetaData.getPackage(namespace);
                if (ePackage == null) {
                    ePackage = extendedMetaData.demandPackage(namespace);
                }
                String prefix = getPrefix(ePackage, true);
                if (!packages.containsKey(ePackage)) {
                    packages.put(ePackage, prefix);
                }
                return prefix + ":" + qname.getLocalPart();
            }
        } else {
            return super.updateQNamePrefix(factory, dataType, value, list);
        }
    }
}
