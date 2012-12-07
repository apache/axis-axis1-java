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
package org.apache.axis.model.wsdd;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.model.util.AxisXMLResource;
import org.apache.axis.model.wsdd.impl.WSDDPackageImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;
import org.xml.sax.InputSource;

public final class WSDDUtil {
    private WSDDUtil() {}

    public static Deployment load(InputSource is) throws IOException {
        WSDDPackageImpl.eINSTANCE.eClass();
        AxisXMLResource resource = new AxisXMLResource();
        Map options = new HashMap();
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, ExtendedMetaData.INSTANCE);
        options.put(AxisXMLResource.OPTION_IGNORE_NAMESPACE_FOR_UNQUALIFIED_QNAME, WSDDPackageImpl.eNS_URI);
        resource.load(is, options);
        return (Deployment)resource.getContents().get(0);        
    }
    
    public static void save(Deployment deployment, OutputStream out) throws IOException {
        AxisXMLResource resource = new AxisXMLResource();
        XMLProcessor processor = new XMLProcessor();
        resource.getContents().add(deployment);
        Map options = new HashMap();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        processor.save(out, resource, options);        
    }
    
    public static void save(Deployment deployment, Writer writer) throws IOException {
        AxisXMLResource resource = new AxisXMLResource();
        XMLProcessor processor = new XMLProcessor();
        resource.getContents().add(deployment);
        Map options = new HashMap();
        options.put(XMLResource.OPTION_DECLARE_XML, Boolean.FALSE);
        processor.save(writer, resource, options);        
    }
}
