package org.apache.axis.message;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.rpc.namespace.QName;
import org.xml.sax.Attributes;

import java.util.Vector;

public class RPCElement extends SOAPBodyElement
{
    protected Vector params = new Vector();
    protected boolean needDeser = false;
    
    public RPCElement(String namespace, String localName, String prefix,
                      Attributes attributes, DeserializationContext context)
    {
        super(namespace, localName, prefix, attributes, context);

        // This came from parsing XML, so we need to deserialize it sometime
        needDeser = true;
    }
    
    public RPCElement(String namespace, String methodName,
                      Object [] args, ServiceDescription serviceDesc)
    {
        this.setNamespaceURI(namespace);
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
        this.name = methodName;
    }
    
    public String getMethodName()
    {
        return name;
    }
    
    public void deserialize() throws Exception
    {
        context.pushElementHandler(new EnvelopeHandler(new RPCHandler(this)));
        context.setCurElement(this);
        
        needDeser = false;
        
        publishToHandler(context);
    }
    
    /** This gets the FIRST param whose name matches.
     * !!! Should it return more in the case of duplicates?
     */
    public RPCParam getParam(String name)
    {
        if (needDeser) {
            try {
                deserialize();
            } catch (Exception e) {
                return null;
            }
        }
        
        for (int i = 0; i < params.size(); i++) {
            RPCParam param = (RPCParam)params.elementAt(i);
            if (param.getName().equals(name))
                return param;
        }
        
        return null;
    }
    
    public Vector getParams()
    {
        if (needDeser) {
            try {
                deserialize();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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
        context.startElement(new QName(namespaceURI,name),attributes);
        for (int i = 0; i < params.size(); i++) {
            ((RPCParam)params.elementAt(i)).serialize(context);
        }
        context.endElement();
    }
}
