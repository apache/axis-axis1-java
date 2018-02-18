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

package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.utils.Messages;

import java.util.HashMap;

/**
 * The TypeMappingRegistry keeps track of the individual TypeMappings.
 * <p>
 * The TypeMappingRegistry for axis contains a default type mapping
 * that is set for either SOAP 1.1 or SOAP 1.2
 * The default type mapping is a singleton used for the entire
 * runtime and should not have anything new registered in it.
 * <p>
 * Instead the new TypeMappings for the deploy and service are
 * made in a separate TypeMapping which is identified by
 * the soap encoding.  These new TypeMappings delegate back to 
 * the default type mapping when information is not found.
 * <p>
 * So logically we have:
 * <pre>
 *         TMR
 *         | |  
 *         | +---------------&gt; DefaultTM 
 *         |                      ^
 *         |                      |
 *         +----&gt; TM --delegate---+
 * </pre>
 *
 * But in the implementation, the TMR references
 * "delegate" TypeMappings (TM') which then reference the actual TM's
 * <p>
 * So the picture is really:
 * <pre>
 *         TMR
 *         | |  
 *         | +-----------TM'------&gt; DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-&gt; TM ----+
 * </pre>
 *
 * This extra indirection is necessary because the user may want to 
 * change the default type mapping.  In such cases, the TMR
 * just needs to adjust the TM' for the DefaultTM, and all of the
 * other TMs will properly delegate to the new one.  Here's the picture:
 * <pre>
 *         TMR
 *         | |  
 *         | +-----------TM'--+     DefaultTM 
 *         |              ^   |
 *         |              |   +---&gt; New User Defined Default TM
 *         +-TM'-&gt; TM ----+
 * </pre>
 *
 * The other reason that it is necessary is when a deploy
 * has a TMR, and then TMR's are defined for the individual services
 * in such cases the delegate() method is invoked on the service
 * to delegate to the deploy TMR
 * <pre>
 *       Deploy TMR
 *         | |  
 *         | +-----------TM'------&gt; DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-&gt; TM ----+
 *
 *       Service TMR
 *         | |  
 *         | +-----------TM'------&gt; DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-&gt; TM ----+
 *
 *    ServiceTMR.delegate(DeployTMR)
 *
 *       Deploy TMR
 *         | |  
 *         | +------------TM'------&gt; DefaultTM 
 *         |              ^ ^ 
 *         |              | |
 *         +-TM'-&gt; TM ----+ |
 *           ^              |
 *   +-------+              |
 *   |                      |
 *   |   Service TMR        |
 *   |     | |              |
 *   |     | +----------TM'-+               
 *   |     |              
 *   |     |              
 *   |     +-TM'-&gt; TM +
 *   |                |
 *   +----------------+
 * </pre>
 *
 * So now the service uses the DefaultTM of the Deploy TMR, and
 * the Service TM properly delegates to the deploy's TM.  And
 * if either the deploy defaultTM or TMs change, the links are not broken.
 * 
 * @author James Snell (jasnell@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 * Re-written for JAX-RPC Compliance by
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class TypeMappingRegistryImpl implements TypeMappingRegistry { 
    
    private HashMap mapTM;          // Type Mappings keyed with Namespace URI
    private TypeMappingDelegate defaultDelTM;  // Delegate to default Type Mapping
    private boolean isDelegated = false;

    /**
     * Construct TypeMappingRegistry
     * @param tm
     */ 
    public TypeMappingRegistryImpl(TypeMappingImpl tm) {
        mapTM = new HashMap();
        defaultDelTM = new TypeMappingDelegate(tm);
//        TypeMappingDelegate del = new TypeMappingDelegate(new DefaultSOAPEncodingTypeMappingImpl());
//        register(Constants.URI_SOAP11_ENC, del);
    }

    /**
     * Construct TypeMappingRegistry
     */
    public TypeMappingRegistryImpl() {
        this(true);
    }

    public TypeMappingRegistryImpl(boolean registerDefaults) {
        mapTM = new HashMap();
        if (registerDefaults) {
            defaultDelTM = DefaultTypeMappingImpl.getSingletonDelegate();
            TypeMappingDelegate del = new TypeMappingDelegate(new DefaultSOAPEncodingTypeMappingImpl());
            register(Constants.URI_SOAP11_ENC, del);
        } else {
            defaultDelTM = new TypeMappingDelegate(TypeMappingDelegate.placeholder);
        }
    }

    /**
     * delegate
     *
     * Changes the contained type mappings to delegate to 
     * their corresponding types in the secondary TMR.
     */
    public void delegate(TypeMappingRegistry secondaryTMR) {

        if (isDelegated || secondaryTMR == null || secondaryTMR == this) {
            return;
        }

        isDelegated = true;
        String[]  keys = secondaryTMR.getRegisteredEncodingStyleURIs();
        TypeMappingDelegate otherDefault =
                ((TypeMappingRegistryImpl)secondaryTMR).defaultDelTM;
        if (keys != null) {
            for (int i=0; i < keys.length; i++) {
                try {
                    String nsURI = keys[i];
                    TypeMappingDelegate tm = (TypeMappingDelegate) mapTM.get(nsURI);
                    if (tm == null) {
                        tm = (TypeMappingDelegate)createTypeMapping();
                        tm.setSupportedEncodings(new String[] { nsURI });
                        register(nsURI, tm);
                    }

                    if (tm != null) {
                        // Get the secondaryTMR's TM'
                        TypeMappingDelegate del = (TypeMappingDelegate)
                            ((TypeMappingRegistryImpl)secondaryTMR).mapTM.get(nsURI);

                        while (del.next != null) {
                            TypeMappingDelegate nu = new TypeMappingDelegate(del.delegate);
                            tm.setNext(nu);

                            if (del.next == otherDefault) {
                                nu.setNext(defaultDelTM);
                                break;
                            }
                            del = del.next;
                            tm = nu;
                        }
                    }

                } catch (Exception e) {
                }
            }
        }
        // Change our defaultDelTM to delegate to the one in 
        // the secondaryTMR
        if (defaultDelTM.delegate != TypeMappingDelegate.placeholder) {
            defaultDelTM.setNext(otherDefault);
        } else {
            defaultDelTM.delegate = otherDefault.delegate;
        }
        
    }            
            


    /********* JAX-RPC Compliant Method Definitions *****************/
    
    /**
     * The method register adds a TypeMapping instance for a specific 
     * namespace                 
     *
     * @param namespaceURI 
     * @param mapping - TypeMapping for specific namespaces
     *
     * @return Previous TypeMapping associated with the specified namespaceURI,
     * or null if there was no TypeMapping associated with the specified namespaceURI
     *
     */
    public javax.xml.rpc.encoding.TypeMapping register(String namespaceURI,
                         javax.xml.rpc.encoding.TypeMapping mapping) {
//        namespaceURI = "";
        if (mapping == null || 
            !(mapping instanceof TypeMappingDelegate)) {
            throw new IllegalArgumentException(
                    Messages.getMessage("badTypeMapping"));
        } 
        if (namespaceURI == null) {
            throw new java.lang.IllegalArgumentException(
                    Messages.getMessage("nullNamespaceURI"));
        }

        TypeMappingDelegate del = (TypeMappingDelegate)mapping;
        TypeMappingDelegate old = (TypeMappingDelegate)mapTM.get(namespaceURI);
        if (old == null) {
            del.setNext(defaultDelTM);
        } else {
            del.setNext(old);
        }
        mapTM.put(namespaceURI, del);
        return old; // Needs works
    }
    
    /**
     * The method register adds a default TypeMapping instance.  If a specific
     * TypeMapping is not found, the default TypeMapping is used.  
     *
     * @param mapping - TypeMapping for specific type namespaces
     *
     * java.lang.IllegalArgumentException - 
     * if an invalid type mapping is specified or the delegate is already set
     */
    public void registerDefault(javax.xml.rpc.encoding.TypeMapping mapping) {
        if (mapping == null ||
            !(mapping instanceof TypeMappingDelegate)) {
            throw new IllegalArgumentException(
                    Messages.getMessage("badTypeMapping"));
        }

        /* Don't allow this call after the delegate() method since
         * the TMR's TypeMappings will be using the default type mapping
         * of the secondary TMR.
         */
        if (defaultDelTM.getNext() != null) {
            throw new IllegalArgumentException(
                    Messages.getMessage("defaultTypeMappingSet"));
        }

        defaultDelTM = (TypeMappingDelegate)mapping;
    }

    /**
     * Set up the default type mapping (and the SOAP encoding type mappings)
     * as per the passed "version" option.
     *
     * @param version
     */
    public void doRegisterFromVersion(String version) {
        if (version == null || version.equals("1.0") || version.equals("1.2")) {
            TypeMappingImpl.dotnet_soapenc_bugfix = false;
            // Do nothing, just register SOAPENC mapping
        } else if (version.equals("1.1")) {
            TypeMappingImpl.dotnet_soapenc_bugfix = true;
            // Do nothing, no SOAPENC mapping
            return;
        } else if (version.equals("1.3")) {
            // Reset the default TM to the JAXRPC version, then register SOAPENC
            defaultDelTM = new TypeMappingDelegate(
                    DefaultJAXRPC11TypeMappingImpl.getSingleton());
        } else {
            throw new RuntimeException(
                    Messages.getMessage("j2wBadTypeMapping00"));
        }
        registerSOAPENCDefault(
                new TypeMappingDelegate(DefaultSOAPEncodingTypeMappingImpl.
                                        getSingleton()));
    }
    /**
     * Force registration of the given mapping as the SOAPENC default mapping
     * @param mapping
     */
    private void registerSOAPENCDefault(TypeMappingDelegate mapping) {
        // This get a bit ugly as we do not want to just overwrite
        // an existing type mapping for SOAP encodings.  This happens
        // when {client,server}-config.wsdd defines a type mapping for
        // instance.
        if (!mapTM.containsKey(Constants.URI_SOAP11_ENC)) {
            mapTM.put(Constants.URI_SOAP11_ENC, mapping);
        } else {
            // We have to make sure the default type mapping is
            // at the end of the chain.
            // This is important if the default is switched to
            // the JAX_RPC 1.1 default type mapping!
            TypeMappingDelegate del =
                    (TypeMappingDelegate) mapTM.get(Constants.URI_SOAP11_ENC);
            while (del.getNext() != null && ! (del.delegate instanceof DefaultTypeMappingImpl)) {
                del = del.getNext();
            }
            del.setNext(defaultDelTM);
        }

        if (!mapTM.containsKey(Constants.URI_SOAP12_ENC)) {
            mapTM.put(Constants.URI_SOAP12_ENC, mapping);
        } else {
            // We have to make sure the default type mapping is
            // at the end of the chain.
            // This is important if the default is switched to
            // the JAX_RPC 1.1 default type mapping!
            TypeMappingDelegate del =
                    (TypeMappingDelegate) mapTM.get(Constants.URI_SOAP12_ENC);
            while (del.getNext() != null && ! (del.delegate instanceof DefaultTypeMappingImpl)) {
                del = del.getNext();
            }
            del.setNext(defaultDelTM);
        }
        
        // Just do this unconditionally in case we used mapping.
        // This is important if the default is switched to
        // the JAX_RPC 1.1 default type mapping!
        mapping.setNext(defaultDelTM);
    }

    /**
     * Gets the TypeMapping for the namespace.  If not found, the default
     * TypeMapping is returned.
     *
     * @param namespaceURI - The namespace URI of a Web Service
     * @return The registered TypeMapping 
     * (which may be the default TypeMapping) or null.
     */
    public javax.xml.rpc.encoding.TypeMapping 
        getTypeMapping(String namespaceURI) {
//        namespaceURI = "";
        TypeMapping del = (TypeMappingDelegate) mapTM.get(namespaceURI);
        if (del == null) {
            del = (TypeMapping)getDefaultTypeMapping();
        }
        return del;
    }

    /**
     * Obtain a type mapping for the given encodingStyle.  If no specific
     * mapping exists for this encodingStyle, we will create and register
     * one before returning it.
     * 
     * @param encodingStyle
     * @return a registered TypeMapping for the given encodingStyle
     */ 
    public TypeMapping getOrMakeTypeMapping(String encodingStyle) {
        TypeMappingDelegate del = (TypeMappingDelegate) mapTM.get(encodingStyle);
        if (del == null || del.delegate instanceof DefaultTypeMappingImpl) {
            del = (TypeMappingDelegate)createTypeMapping();
            del.setSupportedEncodings(new String[] {encodingStyle});
            register(encodingStyle, del);
        }
        return del;
    }

    /**
     * Unregisters the TypeMapping for the namespace.
     *
     * @param namespaceURI - The namespace URI
     * @return The registered TypeMapping .
     */
    public javax.xml.rpc.encoding.TypeMapping 
        unregisterTypeMapping(String namespaceURI) {
        return (TypeMappingDelegate)mapTM.remove(namespaceURI);
    }

    /**
     * Removes the TypeMapping for the namespace.
     *
     * @param mapping The type mapping to remove
     * @return true if found and removed
     */
    public boolean removeTypeMapping(
                                  javax.xml.rpc.encoding.TypeMapping mapping) {
        String[] ns = getRegisteredEncodingStyleURIs();
        boolean rc = false;
        for (int i=0; i < ns.length; i++) {
            if (getTypeMapping(ns[i]) == mapping) {
                rc = true;
                unregisterTypeMapping(ns[i]);
            }
        }
        return rc;
    }

    /**
     * Creates a new empty TypeMapping object for the specified
     * encoding style or XML schema namespace.
     *
     * @return An empty generic TypeMapping object
     */
    public javax.xml.rpc.encoding.TypeMapping createTypeMapping() {
        TypeMappingImpl impl = new TypeMappingImpl();
        TypeMappingDelegate del = new TypeMappingDelegate(impl);
        del.setNext(defaultDelTM);
        return del;
    }
        

    /**
     * Gets a list of namespace URIs registered with this TypeMappingRegistry.
     *
     * @return String[] containing names of all registered namespace URIs
     */
    public String[] getRegisteredEncodingStyleURIs() {
        java.util.Set s = mapTM.keySet(); 
        if (s != null) { 
            String[] rc = new String[s.size()];
            int i = 0;
            java.util.Iterator it = s.iterator();
            while(it.hasNext()) {
                rc[i++] = (String) it.next();
            }
            return rc;
        }
        return null;
    } 


    /**
     * Removes all TypeMappings and namespaceURIs from this TypeMappingRegistry.
     */
    public void clear() {
        mapTM.clear();
    }

    /**
     * Return the default TypeMapping
     * @return TypeMapping or null
     **/
    public javax.xml.rpc.encoding.TypeMapping getDefaultTypeMapping() {
        return defaultDelTM;
    }

}
