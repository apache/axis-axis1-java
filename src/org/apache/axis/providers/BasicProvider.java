/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.providers;

import java.util.Hashtable;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

/**
 * This class has one way of keeping track of the
 * operations declared for a particular service
 * provider.  I'm not exactly married to this though.
 */
public abstract class BasicProvider extends BasicHandler {
    
    public static final String OPTION_WSDL_PORTTYPE = "wsdlPortType";
    public static final String OPTION_WSDL_SERVICEELEMENT = "wsdlServiceElement";
    public static final String OPTION_WSDL_SERVICEPORT = "wsdlServicePort";
    public static final String OPTION_WSDL_TARGETNAMESPACE = "wsdlTargetNamespace";
    public static final String OPTION_WSDL_INPUTSCHEMA = "wsdlInputSchema";

    protected static Log log =
            LogFactory.getLog(BasicProvider.class.getName());

    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
            LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);
    
    /**
     * This method returns a ServiceDesc that contains the correct 
     * implimentation class. 
     */ 
    public abstract void initServiceDesc(SOAPService service,
                                         MessageContext msgContext)
            throws AxisFault;
    
    public void addOperation(String name, QName qname) {
        Hashtable operations = (Hashtable)getOption("Operations");
        if (operations == null) {
            operations = new Hashtable();
            setOption("Operations", operations);
        }
        operations.put(qname, name);
    }
    
    public String getOperationName(QName qname) {
        Hashtable operations = (Hashtable)getOption("Operations");
        if (operations == null) return null;
        return (String)operations.get(qname);
    }
    
    public QName[] getOperationQNames() {
        Hashtable operations = (Hashtable)getOption("Operations");
        if (operations == null) return null;
        Object[] keys = operations.keySet().toArray();
        QName[] qnames = new QName[keys.length];
        System.arraycopy(keys,0,qnames,0,keys.length);
        return qnames;
    }
    
    public String[] getOperationNames() {
        Hashtable operations = (Hashtable)getOption("Operations");
        if (operations == null) return null;
        Object[] values = operations.values().toArray();
        String[] names = new String[values.length];
        System.arraycopy(values,0,names,0,values.length);
        return names;
    }
    
    /**
     * Generate the WSDL for this service.
     *
     * Put in the "WSDL" property of the message context
     * as a org.w3c.dom.Document
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled())
            log.debug("Enter: BasicProvider::generateWSDL (" + this +")");

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        SOAPService service = msgContext.getService();
        
        ServiceDesc serviceDesc = service.getInitializedServiceDesc(msgContext);

        // Calculate the appropriate namespaces for the WSDL we're going
        // to put out.
        //
        // If we've been explicitly told which namespaces to use, respect
        // that.  If not:
        //
        // The "interface namespace" should be either:
        // 1) The namespace of the ServiceDesc
        // 2) The transport URL (if there's no ServiceDesc ns)

        try {
            // Location URL is whatever is explicitly set in the MC
            String locationUrl = msgContext.getStrProp(MessageContext.WSDLGEN_SERV_LOC_URL);

            if (locationUrl == null) {
                // If nothing, try what's explicitly set in the ServiceDesc
                locationUrl = serviceDesc.getEndpointURL();
            }

            if (locationUrl == null) {
                // If nothing, use the actual transport URL
                locationUrl = msgContext.getStrProp(MessageContext.TRANS_URL);
            }

            // Interface namespace is whatever is explicitly set
            String interfaceNamespace = msgContext.getStrProp(MessageContext.WSDLGEN_INTFNAMESPACE);

            if (interfaceNamespace == null) {
                // If nothing, use the default namespace of the ServiceDesc
                interfaceNamespace = serviceDesc.getDefaultNamespace();
            }

            if (interfaceNamespace == null) {
                // If nothing still, use the location URL determined above
                interfaceNamespace = locationUrl;
            }

            //Do we want to do this?
            //
            //          if (locationUrl == null) {
            //              locationUrl = url;
            //          } else {
            //              try {
            //                  URL urlURL = new URL(url);
            //                  URL locationURL = new URL(locationUrl);
            //                  URL urlTemp = new URL(urlURL.getProtocol(),
            //                          locationURL.getHost(),
            //                          locationURL.getPort(),
            //                          urlURL.getFile());
            //                  interfaceNamespace += urlURL.getFile();
            //                  locationUrl = urlTemp.toString();
            //              } catch (Exception e) {
            //                  locationUrl = url;
            //                  interfaceNamespace = url;
            //              }
            //          }

            Emitter emitter = new Emitter();

            // This seems like a good idea, but in fact isn't because the
            // emitter will figure out a reasonable name (<classname>Service)
            // for the WSDL service element name.  We provide the 'alias'
            // setting to explicitly set this name. See bug 13262 for more info.
            //emitter.setServiceElementName(serviceDesc.getName());

            // service alias may be provided if exact naming is required,
            // otherwise Axis will name it according to the implementing class name
            String alias = (String) service.getOption("alias");
            if (alias != null)
                emitter.setServiceElementName(alias);

            // Set style/use
            emitter.setStyle(serviceDesc.getStyle());
            emitter.setUse(serviceDesc.getUse());

            if (serviceDesc instanceof ServiceDesc) {
                emitter.setClsSmart(((JavaServiceDesc)serviceDesc).getImplClass(),
                                    locationUrl);
            }

            // If a wsdl target namespace was provided, use the targetNamespace.
            // Otherwise use the interfaceNamespace constructed above.
            String targetNamespace = (String) service.getOption(OPTION_WSDL_TARGETNAMESPACE);
            if (targetNamespace == null || targetNamespace.length() == 0) {
                targetNamespace = interfaceNamespace;
            }
            emitter.setIntfNamespace(targetNamespace);

            emitter.setLocationUrl(locationUrl);
            emitter.setServiceDesc(serviceDesc);
            emitter.setTypeMapping((TypeMapping) msgContext.getTypeMappingRegistry().getTypeMapping(serviceDesc.getUse().getEncoding()));
            emitter.setDefaultTypeMapping((TypeMapping) msgContext.getTypeMappingRegistry().getDefaultTypeMapping());

            String wsdlPortType = (String) service.getOption(OPTION_WSDL_PORTTYPE);
            String wsdlServiceElement = (String) service.getOption(OPTION_WSDL_SERVICEELEMENT);
            String wsdlServicePort = (String) service.getOption(OPTION_WSDL_SERVICEPORT);

            if (wsdlPortType != null && wsdlPortType.length() > 0) {
                emitter.setPortTypeName(wsdlPortType);
            }
            if (wsdlServiceElement != null && wsdlServiceElement.length() > 0) {
                emitter.setServiceElementName(wsdlServiceElement);
            }
            if (wsdlServicePort != null && wsdlServicePort.length() > 0) {
                emitter.setServicePortName(wsdlServicePort);
            }

            String wsdlInputSchema = (String) service.getOption(OPTION_WSDL_INPUTSCHEMA);
            if (null != wsdlInputSchema && wsdlInputSchema.length() > 0) {
                emitter.setInputSchema(wsdlInputSchema);
            }

            Document doc = emitter.emit(Emitter.MODE_ALL);

            msgContext.setProperty("WSDL", doc);
        } catch (NoClassDefFoundError e) {
            entLog.info(Messages.getMessage("toAxisFault00"), e);
            throw new AxisFault(e.toString(), e);
        } catch (Exception e) {
            entLog.info(Messages.getMessage("toAxisFault00"), e);
            throw AxisFault.makeFault(e);
        }

        if (log.isDebugEnabled())
            log.debug("Exit: BasicProvider::generateWSDL (" + this +")");
    }
}
