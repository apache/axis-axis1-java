package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import java.lang.reflect.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.axis.*;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.AxisClassLoader;
import org.apache.axis.utils.cache.*;

public class RPCHandler extends SOAPHandler
{
    private final static boolean DEBUG_LOG = false;
    
    private RPCElement call;
    private RPCParam currentParam;

    protected Class  defaultParamTypes[] = null;
    
    public RPCHandler(RPCElement call)
        throws SAXException
    {
        this.call = call;
    }
    
    private void determineDefaultParams(String methodName,
                                        DeserializationContext context) {
        MessageContext msgContext = context.getMessageContext();
        Handler service    = msgContext.getServiceHandler();
        if (service == null) return;

        String  clsName    = (String) service.getOption( "className" );

        try {
            AxisClassLoader cl     = msgContext.getClassLoader();
            JavaClass       jc     = cl.lookup(clsName);
            Class           cls    = jc.getJavaClass();
            
            if (DEBUG_LOG) {
                System.err.println("Looking up method '" + methodName +
                                   "' in class " + clsName);
            }

            // try to find the method without knowing the number of
            // parameters.  If we are successful, we can make better
            // decisions about what deserializers to use for parameters
            Method method = jc.getMethod(methodName, -1);
            if (method != null) defaultParamTypes = method.getParameterTypes();
            
            // !!! This should be smart enough to deal with overloaded
            // methods - we should really be keeping a list of all of
            // the possibilities, then moving down the list to keep
            // matching as we deserialize params....
            //

            // in the future, we should add support for runtime information
            // from sources like WSDL, based on Handler.getDeploymentData();
        } catch (Exception e) {
            // oh well, we tried.
        }
    }

    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        /** Potential optimizations:
         * 
         * - Cache typeMappingRegistry
         * - Cache service description
         */
        ServiceDescription serviceDesc = context.getMessageContext().
                                                getServiceDescription();
        
        if (DEBUG_LOG) {
            System.err.println("In RPCHandler.onStartChild()");
        }
        
        Vector params = call.getParams();
        if (serviceDesc == null && params.isEmpty()) {
            determineDefaultParams(call.getMethodName(), context);
        }
        
        // This is a param.
        currentParam = new RPCParam(namespace, localName, null);
        call.addParam(currentParam);
        
        QName type = context.getTypeFromAttributes(namespace,
                                                   localName,
                                                   attributes);
        if (DEBUG_LOG) {
            System.err.println("Type from attrs was " + type);
        }
        
        // xsi:type always overrides everything else
        if (type == null) {
            // but if we don't find one, see if the ServiceDescription
            // might shed some light...
            if (serviceDesc != null) {
                String msgType = context.getEnvelope().getMessageType();
                type = serviceDesc.getParamTypeByName(msgType, localName);
                if (DEBUG_LOG) {
                    System.err.println("Type from service desc was " + type);
                }
            }
            
            // and if we still don't know, check the introspected types
            //
            // NOTE : We don't check params.isEmpty() here because we
            //        must have added at least one above...
            //
            if (type==null && defaultParamTypes!=null &&
                params.size()<=defaultParamTypes.length) {
                TypeMappingRegistry typeMap = context.
                                                  getTypeMappingRegistry();
                type = typeMap.getTypeQName(
                                         defaultParamTypes[params.size()-1]);
                if (DEBUG_LOG) {
                    System.err.println("Type from default parms was " + type);
                }
            }
        }
        
        Deserializer dser;
        if (type != null) {
            dser = context.getTypeMappingRegistry().getDeserializer(type);
        } else {
            dser = new Deserializer();
        }
        
        if (dser == null) {
            throw new SAXException("Deserializing param '" + localName +
                "' : Couldn't find deserializer for type " + type);
        }
        
        String isNil = attributes.getValue(Constants.URI_2001_SCHEMA_XSI, "nil");
        if (isNil == null || !isNil.equals("true")) {
            dser.registerValueTarget(
               new Deserializer.FieldTarget(currentParam, 
                   RPCParam.getValueField()));
        }
        
        if (DEBUG_LOG) {
            System.out.println("Out RPCHandler.onStartChild()");
        }
        return dser;
    }
    
    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        if (DEBUG_LOG) {
            System.out.println("Setting MessageContext property in " +
                               "RPCHandler.endElement().");
        }
        context.getMessageContext().setProperty("RPC", call);
    }
}
