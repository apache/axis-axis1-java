package org.apache.axis.rpc;

import java.io.NotSerializableException;
import org.w3c.dom.Document;
import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.SOAPBodyEntry;
import org.apache.axis.rpc.encoding.Encoder;
import org.apache.axis.rpc.encoding.Decoder;
import org.apache.axis.rpc.encoding.NoSuchTypeMappingException;
import org.apache.axis.rpc.encoding.MalformedEncodingException;

final public class Call extends Message {
    public Call(SOAPDocument doc) { super(doc); }

    public Call(String methodName,
                Parameter[] params,
                Encoder enc,
                Document fac)
        throws NoSuchTypeMappingException, NotSerializableException
    {
        super(methodName, params, enc, fac);
    }

    public SOAPBodyEntry getMethod() {
        return getStruct();
    }

    public String getMethodName() {
        return getStructName();
    }

    public Object[] getParameters(Decoder dec, Class[] types)
        throws MalformedEncodingException,
               NoSuchTypeMappingException,
               NoSuchFieldException,
               InstantiationException,
               IllegalAccessException
    {
        return super.getParameters(dec, types);
    }
}
