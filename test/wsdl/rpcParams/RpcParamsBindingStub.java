/**
 * RpcParamsBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.rpcParams;

import java.util.Enumeration;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;
import javax.xml.namespace.QName;

import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.client.Stub;

public class RpcParamsBindingStub extends Stub implements RpcParamsTest {
    private Vector cachedSerClasses = new Vector();
    private Vector cachedSerQNames = new Vector();
    private Vector cachedSerFactories = new Vector();
    private Vector cachedDeserFactories = new Vector();

    public RpcParamsBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public RpcParamsBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public RpcParamsBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
            Class cls;
            QName qName;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            qName = new QName("urn:rpcParams.wsdl.test", "EchoStruct");
            cachedSerQNames.add(qName);
            cls = EchoStruct.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    private org.apache.axis.client.Call createCall() throws RemoteException {
        try {
            org.apache.axis.client.Call _call =
                    (org.apache.axis.client.Call) super.service.createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        Class cls = (Class) cachedSerClasses.get(i);
                        QName qName =
                                (QName) cachedSerQNames.get(i);
                        Class sf = (Class)
                                 cachedSerFactories.get(i);
                        Class df = (Class)
                                 cachedDeserFactories.get(i);
                        _call.registerTypeMapping(cls, qName, sf, df, false);
                    }
                }
            }
            return _call;
        }
        catch (Throwable t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", t);
        }
    }

    /**
     * Send the given request to the server, ommitting any null parameters.
     */
    public EchoStruct echo(String first, String second) throws RemoteException {
        EchoStruct result;
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }

        // create the operation... omit any null parameters
        OperationDesc operation = new OperationDesc();
        operation.setName("echo");
        if (first != null)
            operation.addParameter(new QName("", "first"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
        if (second != null)
            operation.addParameter(new QName("", "second"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
        operation.setReturnType(new QName("urn:rpcParams.wsdl.test", "EchoStruct"));
        operation.setReturnClass(EchoStruct.class);
        operation.setReturnQName(new QName("", "echoReturn"));
        operation.setStyle(org.apache.axis.enum.Style.RPC);
        operation.setUse(org.apache.axis.enum.Use.ENCODED);

        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(operation);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("urn:rpcParams.wsdl.test", "echo"));

        setRequestHeaders(_call);
        setAttachments(_call);

        // add the params we're actually going to send, again ommitting nulls
        List params = new ArrayList(2);
        if (first != null)
            params.add(first);
        if (second != null)
            params.add(second);

        Object _resp = _call.invoke(params.toArray());

        if (_resp instanceof RemoteException) {
            throw (RemoteException)_resp;
        }
        else {
            getResponseHeaders(_call);
            extractAttachments(_call);
            try {
                result = (EchoStruct) _resp;
            } catch (Exception _exception) {
                result = (EchoStruct) org.apache.axis.utils.JavaUtils.convert(_resp, EchoStruct.class);
            }
        }
        return result;
    }

    /**
     * The same as echo, but reverse the order of parameters we send.
     */
    public EchoStruct echoReverse(String first, String second) throws RemoteException {
        EchoStruct result;
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }

        // create the operation... omit any null parameters
        OperationDesc operation = new OperationDesc();
        operation.setName("echo");
        if (second != null)
            operation.addParameter(new QName("", "second"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
        if (first != null)
            operation.addParameter(new QName("", "first"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
        operation.setReturnType(new QName("urn:rpcParams.wsdl.test", "EchoStruct"));
        operation.setReturnClass(EchoStruct.class);
        operation.setReturnQName(new QName("", "echoReturn"));
        operation.setStyle(org.apache.axis.enum.Style.RPC);
        operation.setUse(org.apache.axis.enum.Use.ENCODED);

        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(operation);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new QName("urn:rpcParams.wsdl.test", "echo"));

        setRequestHeaders(_call);
        setAttachments(_call);

        // add the params we're actually going to send, again ommitting nulls
        List params = new ArrayList(2);
        if (second != null)
            params.add(second);
        if (first != null)
            params.add(first);

        Object _resp = _call.invoke(params.toArray());

        if (_resp instanceof RemoteException) {
            throw (RemoteException)_resp;
        }
        else {
            getResponseHeaders(_call);
            extractAttachments(_call);
            try {
                result = (EchoStruct) _resp;
            } catch (Exception _exception) {
                result = (EchoStruct) org.apache.axis.utils.JavaUtils.convert(_resp, EchoStruct.class);
            }
        }
        return result;
    }

}
