package org.apache.axis.message;

import java.util.*;
import org.xml.sax.*;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.utils.QName;

public class RPCElement extends SOAPBodyElement
{
    protected String methodName;    
    protected Vector params = new Vector();
    
    public RPCElement(String namespace, String localName,
                      Attributes attributes, DeserializationContext context)
    {
        super(namespace, localName, attributes, context);
        methodName = localName;
    }
    
    public RPCElement(String namespace, String methodName,
                      Object [] args, ServiceDescription serviceDesc)
    {
        this.setNamespaceURI(namespace);
        this.methodName = methodName;
        this.name = methodName;
        
        for (int i = 0; args != null && i < args.length; i++) {
            if (args[i] instanceof RPCParam) {
                addParam((RPCParam)args[i]);
            } else {
                String name = null;
                if (serviceDesc != null)
                    name = serviceDesc.getInputParamNameByPos(i);
                if (name == null) name = "arg" + i;
                addParam(new RPCParam(name, args[i]));
            }
        }
    }
    
    public RPCElement(String namespace, String methodName,
                      Object [] args)
    {
        this(namespace, methodName, args, null);
    }
    
    public RPCElement(String methodName)
    {
        this.methodName = methodName;
    }
    
    public String getMethodName()
    {
        return methodName;
    }
    
    /** This gets the FIRST param whose name matches.
     * !!! Should it return more in the case of duplicates?
     */
    public RPCParam getParam(String name)
    {
        for (int i = 0; i < params.size(); i++) {
            RPCParam param = (RPCParam)params.elementAt(i);
            if (param.getName().equals(name))
                return param;
        }
        
        return null;
    }
    
    public Vector getParams()
    {
        return params;
    }
    
    public void addParam(RPCParam param)
    {
        param.setRPCCall(this);
        params.addElement(param);
    }

    protected void outputImpl(SerializationContext context) throws Exception
    {
        context.startElement(new QName(namespaceURI,name),attributes);
        for (int i = 0; i < params.size(); i++) {
            ((RPCParam)params.elementAt(i)).serialize(context);
        }
        context.endElement();
    }
}
