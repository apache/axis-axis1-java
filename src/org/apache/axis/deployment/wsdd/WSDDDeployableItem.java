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

import org.apache.axis.Handler;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.deployment.DeployableItem;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;


/**
 * WSDD DeployableItem complexType
 *
 */
public abstract class WSDDDeployableItem
    extends WSDDElement
    implements DeployableItem
{
    public static final int SCOPE_PER_ACCESS = 0;
    public static final int SCOPE_PER_REQUEST = 1;
    public static final int SCOPE_SINGLETON = 2;
    public static String [] scopeStrings = { "per-access",
                                             "per-request",
                                             "singleton" };
    
    /** Our parameters */
    LockableHashtable parameters;

    /** Our name */
    QName qname;
    
    /** Our type */
    QName type;
    
    /** Scope for this item (default is singleton) */
    int scope = SCOPE_SINGLETON;
    
    /** Placeholder for hanging on to singleton object */
    Handler singletonInstance = null;

    /**
     * Default constructor
     */ 
    public WSDDDeployableItem()
    {
    }
    
    /**
     *
     * @param e (Element) XXX
     * @param n (String) XXX
     * @throws WSDDException XXX
     */
    public WSDDDeployableItem(Element e)
        throws WSDDException
    {
        super(e);
        
        String name = e.getAttribute("name");
        if (name != null && !name.equals("")) {
//            qname = XMLUtils.getQNameFromString(name, e);
            qname = new QName("", name);
        }
        
        //!!! default namespace?
        
        String typeStr = e.getAttribute("type");
        if (typeStr != null && !typeStr.equals(""))
            type = XMLUtils.getQNameFromString(typeStr, e);
        
        // Figure out our scope - right now if a non-recognized scope
        // attribute appears, we will ignore it and use the default
        // scope.  Is this right, or should we throw an error?
        String scopeStr = e.getAttribute("scope");
        if (scopeStr != null) {
            for (int i = 0; i < scopeStrings.length; i++) {
                if (scopeStr.equals(scopeStrings[i])) {
                    scope = i;
                    break;
                }
            }
        }

        if (parameters == null)
            parameters = new LockableHashtable();
        
        // Load up our params
        Element [] paramElements = getChildElements(e, "parameter");
        for (int i = 0; i < paramElements.length; i++) {
            Element param = paramElements[i];
            String pname = param.getAttribute("name");
            String value = param.getAttribute("value");
            String locked = param.getAttribute("locked");
            parameters.put(pname, value, (locked != null &&
                                    locked.equalsIgnoreCase("true")));
        }
    }

    /**
     *
     * @param name XXX
     */
    public void setName(String name)
    {
        qname = new QName(null, name);
    }
    
    public void setQName(QName qname)
    {
        this.qname = qname;
    }

    /**
     *
     * @return XXX
     */
    public QName getQName()
    {
        return qname;
    }

    /**
     *
     * @return XXX
     */
    public QName getType()
    {
        return type;
    }

    /**
     *
     * @param type XXX
     */
    public void setType(QName type)
    {
        this.type = type;
    }

    /**
     * Set a parameter
     */ 
    public void setParameter(String name, String value)
    {
        if (parameters == null)
            parameters = new LockableHashtable();
        parameters.put(name, value);
    }
    
    /**
     * Get the value of one of our parameters
     */ 
    public String getParameter(String name)
    {
        if (name == null)
            return null;
        
        return (String)parameters.get(name);
    }
    
    /**
     * Returns the config parameters as a hashtable (lockable)
     * @return XXX
     */
    public LockableHashtable getParametersTable()
    {
        return parameters;
    }
    
    /**
     * Convenience method for using old deployment XML with WSDD.
     * This allows us to set the options directly after the Admin class
     * has parsed them out of the old format.
     */ 
    public void setOptionsHashtable(Hashtable hashtable)
    {
        if (hashtable == null)
            return;
        
        parameters = new LockableHashtable(hashtable);
    }
    
    public void writeParamsToContext(SerializationContext context)
        throws IOException
    {
        if (parameters == null)
            return;
        
        Set keys = parameters.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            String name = (String)i.next();
            AttributesImpl attrs = new AttributesImpl();
            
            attrs.addAttribute("", "name", "name", "CDATA", name);
            attrs.addAttribute("", "value", "value", "CDATA", 
                                   (String)parameters.get(name));
            if (parameters.isKeyLocked(name)) {
                attrs.addAttribute("", "locked", "locked", "CDATA", "true");
            }

            context.startElement(WSDDConstants.PARAM_QNAME, attrs);
            context.endElement();
        }
    }

    /**
     *
     * @param name XXX
     */
    public void removeParameter(String name)
    {
        // !!! FILL IN
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public final Handler getInstance(DeploymentRegistry registry)
        throws Exception
    {
        if (scope == SCOPE_SINGLETON) {
            synchronized (this) {
                if (singletonInstance == null)
                    singletonInstance = makeNewInstance(registry);
            }
            return singletonInstance;
        }
        
        return makeNewInstance(registry);
    }

    /**
     * Creates a new instance of this deployable.  if the
     * java class is not found, the registry is queried to
     * find a suitable item
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    protected Handler makeNewInstance(DeploymentRegistry registry)
        throws Exception
    {
        try {
            Class   c = getJavaClass();
            Handler h = null;

            if (c != null) {
                h = (Handler)createInstance(c);

                if (h != null) {
                    h.setOptions(getParametersTable());
                }
            } else {
                h = registry.getHandler(getType());
            }

            return h;
        }
        catch (Exception e) {
            throw e;
        }
    }

    /**
     *
     * @param _class XXX
     * @return XXX
     * @throws Exception XXX
     */
    Object createInstance(Class _class)
        throws Exception
    {
        return _class.newInstance();
    }

    /**
     *
     * @param type XXX
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getJavaClass()
        throws ClassNotFoundException
    {
        QName type = getType();
        if (type != null &&
                WSDDConstants.WSDD_JAVA.equals(type.getNamespaceURI())) {
            return Class.forName(type.getLocalPart());
        }
        return null;
    }
}
