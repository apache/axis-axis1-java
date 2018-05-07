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

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

/**
 * {@link XMLHelper} implementation that uses {@link AxisXMLHelper}.
 * 
 * @author Andreas Veithen
 */
public class AxisXMLResource extends XMLResourceImpl {
    /**
     * Option to specify a namespace that will be ignored if it is used in an unqualified
     * <tt>xsd:QName</tt> literal.
     * <p>
     * Axis 1.4 incorrectly assumes that <tt>xsd:QName</tt> literals that have no prefix don't
     * belong to any namespace. This is incorrect: values of type <tt>xsd:QName</tt> are resolved in
     * the same way as element names. An unqualified literal therefore belongs to the default
     * namespace in scope where the <tt>xsd:QName</tt> appears.
     * <p>
     * Consider the following example:
     * 
     * <pre>
     * &lt;deployment xmlns="http://xml.apache.org/axis/wsdd/"
     *             xmlns:java="http://xml.apache.org/axis/wsdd/providers/java"&gt;
     *   &lt;transport name="http"&gt;
     *     &lt;requestFlow&gt;
     *       &lt;handler type="URLMapper"/&gt;
     *       &lt;handler type="java:org.apache.axis.handlers.http.HTTPAuthHandler"/&gt;
     *     &lt;/requestFlow&gt;
     *   &lt;/transport&gt;
     * &lt;/deployment&gt;
     * </pre>
     * 
     * If the <tt>type</tt> attribute is assumed to be of type <tt>xsd:QName</tt>, then the
     * <tt>URLMapper</tt> literal actually resolves to
     * <tt>{http://xml.apache.org/axis/wsdd/}URLMapper</tt>. However, Axis 1.4 incorrectly assumes
     * that it has no namespace because it is unprefixed.
     * <p>
     * This option allows to preserve compatibility with Axis 1.4 by specifying a namespace that
     * will be ignored if it is encountered in the resolution of a <tt>xsd:QName</tt> literal
     * without prefix.
     */
    public static final String OPTION_IGNORE_NAMESPACE_FOR_UNQUALIFIED_QNAME = "IGNORE_NAMESPACE_FOR_UNQUALIFIED_QNAME";
    
    protected XMLHelper createXMLHelper() {
        return new AxisXMLHelper(this);
    }
}
