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

package org.apache.axis.message;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.i18n.Messages;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.enum.Style;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.toJava.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

import java.util.Vector;

public class RPCElement extends SOAPBodyElement
{
    protected Vector params = new Vector();
    protected boolean needDeser = false;
    protected boolean elementIsFirstParam = false;
    OperationDesc [] operations = null;

    public RPCElement(String namespace,
                      String localName,
                      String prefix,
                      Attributes attributes,
                      DeserializationContext context,
                      OperationDesc [] operations) throws AxisFault
    {
        super(namespace, localName, prefix, attributes, context);

        // This came from parsing XML, so we need to deserialize it sometime
        needDeser = true;

        MessageContext msgContext = context.getMessageContext();

        // Obtain our possible operations
        if (operations == null && msgContext != null) {
            SOAPService service    = msgContext.getService();
            if (service != null) {
                ServiceDesc serviceDesc =
                        service.getInitializedServiceDesc(msgContext);
                
                String lc = Utils.xmlNameToJava(name);
                if (serviceDesc == null) {
                    AxisFault.makeFault(
                            new ClassNotFoundException(
                                    Messages.getMessage("noClassForService00",
                                                         lc)));
                }

                operations = serviceDesc.getOperationsByName(lc);
            }
        }

        if (operations != null && operations.length > 0) {
            // if we're document style, the top level element is important
            elementIsFirstParam = (operations[0].getStyle() == Style.DOCUMENT);
        }

        this.operations = operations;
    }

    public RPCElement(String namespace, String methodName, Object [] args)
    {
        this.setNamespaceURI(namespace);
        this.name = methodName;

        for (int i = 0; args != null && i < args.length; i++) {
            if (args[i] instanceof RPCParam) {
                addParam((RPCParam)args[i]);
            } else {
                String name = null;
                if (name == null) name = "arg" + i;
                addParam(new RPCParam(name, args[i]));
            }
        }
    }

    public RPCElement(String methodName)
    {
        this.name = methodName;
    }

    public String getMethodName()
    {
        return name;
    }

    public void setNeedDeser(boolean needDeser) {
        this.needDeser = needDeser;
    }

    public void deserialize() throws SAXException
    {
        needDeser = false;

        MessageContext msgContext = context.getMessageContext();

        // Figure out if we should be looking for out params or in params
        // (i.e. is this message a response?)
        Message msg = msgContext.getCurrentMessage();
        boolean isResponse = ((msg != null) &&
                              Message.RESPONSE.equals(msg.getMessageType()));

        // We're going to need this below, so create one.
        RPCHandler rpcHandler = new RPCHandler(this, isResponse);

        if (operations != null && !msgContext.isClient()) {
            int numParams = (getChildren() == null) ? 0 : getChildren().size();

            SAXException savedException = null;

            // We now have an array of all operations by this name.  Try to
            // find the right one.  For each matching operation which has an
            // equal number of "in" parameters, try deserializing.  If we
            // don't succeed for any of the candidates, punt.

            for (int i = 0; i < operations.length; i++) {
                OperationDesc operation = operations[i];
                if (operation.getNumInParams() >= numParams || 
                    elementIsFirstParam ||
                    operation.getStyle() == Style.WRAPPED) {
                    // If the Style is WRAPPED, the numParams may be inflated 
                    // as in the following case:
                    //  <getAttractions xmlns="urn:CityBBB">
                    //      <attname>Christmas</attname>
                    //      <attname>Xmas</attname>
                    //   </getAttractions>
                    //
                    //  for getAttractions(String[] attName)
                    //
                    // numParams will be 2 and and operation.getNumInParams=1

                    // Set the operation so the RPCHandler can get at it
                    rpcHandler.setOperation(operation);
                    try {
                        if (elementIsFirstParam && operation.getNumInParams() > 0) {
                            context.pushElementHandler(rpcHandler);
                            context.setCurElement(null);
                        } else {
                            context.pushElementHandler(
                                    new EnvelopeHandler(rpcHandler));
                            context.setCurElement(this);
                        }

                        publishToHandler((org.xml.sax.ContentHandler) context);

                        // Success!!  This is the right one...
                        msgContext.setOperation(operation);
                        return;
                    } catch (SAXException e) {
                        // If there was a problem, try the next one.
                        savedException = e;
                        params = new Vector();
                        continue;
                    }
                }
            }

            if (savedException != null) {
                throw savedException;
            } else {
                throw new SAXException(
                    Messages.getMessage("noSuchOperation", name));
            }
        }

        if (operations != null) {
            rpcHandler.setOperation(operations[0]);
        }

        if (elementIsFirstParam) {
            context.pushElementHandler(rpcHandler);
            context.setCurElement(null);
        } else {
            context.pushElementHandler(new EnvelopeHandler(rpcHandler));
            context.setCurElement(this);
        }

        publishToHandler((org.xml.sax.ContentHandler)context);
    }

    /** This gets the FIRST param whose name matches.
     * !!! Should it return more in the case of duplicates?
     */
    public RPCParam getParam(String name) throws SAXException
    {
        if (needDeser) {
            deserialize();
        }

        for (int i = 0; i < params.size(); i++) {
            RPCParam param = (RPCParam)params.elementAt(i);
            if (param.getName().equals(name))
                return param;
        }

        return null;
    }

    public Vector getParams() throws SAXException
    {
        if (needDeser) {
            deserialize();
        }

        return params;
    }

    public void addParam(RPCParam param)
    {
        param.setRPCCall(this);
        params.addElement(param);
    }

    protected void outputImpl(SerializationContext context) throws Exception
    {
        MessageContext msgContext = context.getMessageContext();
        boolean isRPC =
            (msgContext == null  ||
             msgContext.getOperationStyle() == Style.RPC  ||
             msgContext.getOperationStyle() == Style.WRAPPED);

        // When I have MIME and a no-param document WSDL, if I don't check
        // for no params here, the server chokes with "can't find Body".
        // because it will be looking for the enclosing element always
        // found in an RPC-style (and wrapped) request
        boolean noParams = params.size() == 0;

        if (isRPC || noParams) {
            // Set default namespace if appropriate (to avoid prefix mappings
            // in literal style).  Do this only if there is no encodingStyle.
            if (encodingStyle != null && encodingStyle.equals("")) {
                context.registerPrefixForURI("", getNamespaceURI());
            }
            context.startElement(new QName(namespaceURI,name), attributes);
        }

        for (int i = 0; i < params.size(); i++) {
            RPCParam param = (RPCParam)params.elementAt(i);
            if (!isRPC && encodingStyle.equals("")) {
                context.registerPrefixForURI("", param.getQName().getNamespaceURI());
            }
            param.serialize(context);
        }

        if (isRPC || noParams) {
            context.endElement();
        }
    }
}
