package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.utils.AxisClassLoader;
import javax.xml.rpc.namespace.QName;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.lang.reflect.Method;
import java.util.Vector;

public class RPCHandler extends SOAPHandler
{
    static Category category =
            Category.getInstance(RPCHandler.class.getName());
    
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
            
            if (category.isDebugEnabled()) {
                category.debug("Looking up method '" + methodName +
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
        if (category.isDebugEnabled()) {
            category.debug("In RPCHandler.onStartChild()");
        }
        
        Vector params = call.getParams();
        if (params.isEmpty()) 
            determineDefaultParams(call.getMethodName(), context);
        
        // This is a param.
        currentParam = new RPCParam(namespace, localName, null);
        call.addParam(currentParam);
        
        QName type = context.getTypeFromAttributes(namespace,
                                                   localName,
                                                   attributes);
        if (category.isDebugEnabled()) {
            category.debug("Type from attrs was " + type);
        }
        
        // xsi:type always overrides everything else
        if (type == null) {
            // check the introspected types
            //
            // NOTE : We don't check params.isEmpty() here because we
            //        must have added at least one above...
            //
            if (type==null && defaultParamTypes!=null &&
                params.size()<=defaultParamTypes.length) {
                TypeMappingRegistry typeMap = context.getTypeMappingRegistry();
                int index = params.size()-1;
                if (index+1<defaultParamTypes.length)
                    if (defaultParamTypes[0]==MessageContext.class) index++;
                type = typeMap.getTypeQName(defaultParamTypes[index]);
                if (category.isDebugEnabled()) {
                    category.debug("Type from default parms was " + type);
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
        
        if (category.isDebugEnabled()) {
            category.debug("Out RPCHandler.onStartChild()");
        }
        return dser;
    }
    
    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug("Setting MessageContext property in " +
                               "RPCHandler.endElement().");
        }
        context.getMessageContext().setProperty("RPC", call);
    }
}
