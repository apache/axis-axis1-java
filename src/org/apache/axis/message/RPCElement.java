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

package org.apache.axis.message;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.wsdl.toJava.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class RPCElement extends SOAPBodyElement
{
    protected Vector params = new Vector();
    protected boolean needDeser = false;
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
                addParam(new RPCParam(namespace, name, args[i]));
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
        SOAPConstants soapConstants = msgContext.getSOAPConstants();

        boolean isResponse = ((msg != null) &&
                              Message.RESPONSE.equals(msg.getMessageType()));

        // We're going to need this below, so create one.
        RPCHandler rpcHandler = new RPCHandler(this, isResponse);

        if (operations != null) {
            int numParams = (getChildren() == null) ? 0 : getChildren().size();

            SAXException savedException = null;

            // By default, accept missing parameters as nulls, and
            // allow the message context to override.
            boolean acceptMissingParams = msgContext.isPropertyTrue(
                    MessageContext.ACCEPTMISSINGPARAMS,
                    true);

            // We now have an array of all operations by this name.  Try to
            // find the right one.  For each matching operation which has an
            // equal number of "in" parameters, try deserializing.  If we
            // don't succeed for any of the candidates, punt.

            for (int i = 0; i < operations.length; i++) {
                OperationDesc operation = operations[i];

                // See if any information is coming from a header
                boolean needHeaderProcessing =
                    needHeaderProcessing(operation, isResponse);

                // Make a quick check to determine if the operation
                // could be a match.
                //  1) The element is the first param, DOCUMENT, (i.e.
                //     don't know the operation name or the number
                //     of params, so try all operations).
                //  or (2) Style is literal
                //     If the Style is LITERAL, the numParams may be inflated
                //     as in the following case:
                //     <getAttractions xmlns="urn:CityBBB">
                //         <attname>Christmas</attname>
                //         <attname>Xmas</attname>
                //     </getAttractions>
                //   for getAttractions(String[] attName)
                //   numParams will be 2 and and operation.getNumInParams=1
                //  or (3) Number of expected params is
                //         >= num params in message
                if (operation.getStyle() == Style.DOCUMENT ||
                    operation.getStyle() == Style.WRAPPED ||
                    operation.getUse() == Use.LITERAL ||
                    (acceptMissingParams ?
                        (operation.getNumInParams() >= numParams) :
                        (operation.getNumInParams() == numParams))) {

                    boolean isEncoded = operation.getUse() == Use.ENCODED;
                    rpcHandler.setOperation(operation);
                    try {
                        // If no operation name and more than one
                        // parameter is expected, don't
                        // wrap the rpcHandler in an EnvelopeHandler.
                         if ( ( msgContext.isClient() &&
                               operation.getStyle() == Style.DOCUMENT ) ||
                            ( !msgContext.isClient() &&
                               operation.getStyle() == Style.DOCUMENT &&
                               operation.getNumInParams() > 0 ) ) {                            context.pushElementHandler(rpcHandler);
                            context.setCurElement(null);
                        } else {
                            context.pushElementHandler(
                                    new EnvelopeHandler(rpcHandler));
                            context.setCurElement(this);
                        }

                        publishToHandler((org.xml.sax.ContentHandler) context);

                        // If parameter values are located in headers,
                        // get the information and publish the header
                        // elements to the rpc handler.
                        if (needHeaderProcessing) {
                            processHeaders(operation, isResponse,
                                           context, rpcHandler);
                        }

                        // Check if the RPCParam's value match the signature of the
                        // param in the operation.
                        boolean match = true;
                        for ( int j = 0 ; j < params.size() && match ; j++ ) {
                            RPCParam rpcParam = (RPCParam)params.get(j);
                            Object value = rpcParam.getValue();

                            // first check the type on the paramter
                            ParameterDesc paramDesc = rpcParam.getParamDesc();

                            // if we found some type info try to make sure the value type is
                            // correct.  For instance, if we deserialized a xsd:dateTime in
                            // to a Calendar and the service takes a Date, we need to convert
                            if (paramDesc != null && paramDesc.getJavaType() != null) {

                                // Get the type in the signature (java type or its holder)
                                Class sigType = paramDesc.getJavaType();
                                if(!JavaUtils.isConvertable(value, sigType, isEncoded))
                                    match = false;
                            }
                        }
                        // This is not the right operation, try the next one.
                        if(!match) {
                            params = new Vector();
                            continue;
                        }

                        // Success!!  This is the right one...
                        msgContext.setOperation(operation);
                        return;
                    } catch (SAXException e) {
                        // If there was a problem, try the next one.
                        savedException = e;
                        params = new Vector();
                        continue;
                    }  catch (AxisFault e) {
                        // Thrown by getHeadersByName...
                        // If there was a problem, try the next one.
                        savedException = new SAXException(e);
                        params = new Vector();
                        continue;
                    }
                }
            }

            // If we're SOAP 1.2, getting to this point means bad arguments.
            if (!msgContext.isClient() && soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, "string", null, null);
                fault.addFaultSubCode(Constants.FAULT_SUBCODE_BADARGS);
                throw new SAXException(fault);
            }

            if (savedException != null) {
                throw savedException;
            } else if (!msgContext.isClient()) {
                QName faultCode = new QName(Constants.FAULT_SERVER_USER);
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS)
                    faultCode = Constants.FAULT_SOAP12_SENDER;
                AxisFault fault = new AxisFault(faultCode,
                    null, Messages.getMessage("noSuchOperation", name), null, null, null);

                throw new SAXException(fault);
            }
        }

        if (operations != null) {
            rpcHandler.setOperation(operations[0]);
        }

        // Same logic as above.  Don't wrap rpcHandler
        // if there is no operation wrapper in the message
        if (operations != null && operations.length > 0 &&
            (operations[0].getStyle() == Style.DOCUMENT)) {
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
        boolean hasOperationElement =
            (msgContext == null  ||
             msgContext.getOperationStyle() == Style.RPC ||
             msgContext.getOperationStyle() == Style.WRAPPED);

        // When I have MIME and a no-param document WSDL, if I don't check
        // for no params here, the server chokes with "can't find Body".
        // because it will be looking for the enclosing element always
        // found in an RPC-style (and wrapped) request
        boolean noParams = params.size() == 0;

        if (hasOperationElement || noParams) {
            // Set default namespace if appropriate (to avoid prefix mappings
            // in literal style).  Do this only if there is no encodingStyle.
            if (encodingStyle != null && encodingStyle.equals("")) {
                context.registerPrefixForURI("", getNamespaceURI());
            }
            context.startElement(new QName(getNamespaceURI(), name), attributes);
        }

        for (int i = 0; i < params.size(); i++) {
            RPCParam param = (RPCParam)params.elementAt(i);
            if (!hasOperationElement && encodingStyle != null && encodingStyle.equals("")) {
                context.registerPrefixForURI("", param.getQName().getNamespaceURI());
            }
            param.serialize(context);
        }

        if (hasOperationElement || noParams) {
            context.endElement();
        }
    }

    /**
     * needHeaderProcessing
     * @param operation OperationDesc
     * @param isResponse boolean indicates if request or response message
     * @return true if the operation description indicates parameters/results
     * are located in the soap header.
     */
    private boolean needHeaderProcessing(OperationDesc operation,
                                         boolean isResponse) {

        // Search parameters/return to see if any indicate
        // that instance data is contained in the header.
        ArrayList paramDescs = operation.getParameters();
        if (paramDescs != null) {
            for (int j=0; j<paramDescs.size(); j++) {
                ParameterDesc paramDesc =
                    (ParameterDesc) paramDescs.get(j);
                if ((!isResponse && paramDesc.isInHeader()) ||
                    (isResponse && paramDesc.isOutHeader())) {
                    return true;
                }
            }
        }
        if (isResponse &&
            operation.getReturnParamDesc() != null &&
            operation.getReturnParamDesc().isOutHeader()) {
            return true;
        }
        return false;
    }

    /**
     * needHeaderProcessing
     * @param operation OperationDesc
     * @param isResponse boolean indicates if request or response message
     * @param context DeserializationContext
     * @param handler RPCHandler used to deserialize parameters
     * are located in the soap header.
     */
    private void processHeaders(OperationDesc operation,
                                boolean isResponse,
                                DeserializationContext context,
                                RPCHandler handler)
        throws AxisFault, SAXException
    {
        // Inform handler that subsequent elements come from
        // the header
        try {
            handler.setHeaderElement(true);
            // Get the soap envelope
            SOAPElement envelope = getParentElement();
            while (envelope != null &&
                   !(envelope instanceof SOAPEnvelope)) {
                envelope = envelope.getParentElement();
            }
            if (envelope == null)
                return;

            // Find parameters that have instance
            // data in the header.
            ArrayList paramDescs = operation.getParameters();
            if (paramDescs != null) {
                for (int j=0; j<paramDescs.size(); j++) {
                    ParameterDesc paramDesc =
                        (ParameterDesc) paramDescs.get(j);
                    if ((!isResponse && paramDesc.isInHeader()) ||
                        (isResponse && paramDesc.isOutHeader())) {
                        // Get the headers that match the parameter's
                        // QName
                        Enumeration headers = ((SOAPEnvelope) envelope).
                            getHeadersByName(
                                 paramDesc.getQName().getNamespaceURI(),
                                 paramDesc.getQName().getLocalPart(),
                                 true);
                        // Publish each of the found elements to the
                        // handler.  The pushElementHandler and
                        // setCurElement calls are necessary to
                        // have the message element recognized as a
                        // child of the RPCElement.
                        while(headers != null &&
                              headers.hasMoreElements()) {
                            context.pushElementHandler(handler);
                            context.setCurElement(null);
                            ((MessageElement) headers.nextElement()).
                                publishToHandler(
                                   (org.xml.sax.ContentHandler)context);
                        }
                    }
                }
            }

            // Now do the same processing for the return parameter.
            if (isResponse &&
                operation.getReturnParamDesc() != null &&
                operation.getReturnParamDesc().isOutHeader()) {
                ParameterDesc paramDesc = operation.getReturnParamDesc();
                Enumeration headers =
                    ((SOAPEnvelope) envelope).
                    getHeadersByName(
                        paramDesc.getQName().getNamespaceURI(),
                        paramDesc.getQName().getLocalPart(),
                        true);
                while(headers != null &&
                      headers.hasMoreElements()) {
                    context.pushElementHandler(handler);
                    context.setCurElement(null);

                    ((MessageElement) headers.nextElement()).
                        publishToHandler((org.xml.sax.ContentHandler)context);
                }
            }
        } finally {
            handler.setHeaderElement(false);
        }
    }
}
