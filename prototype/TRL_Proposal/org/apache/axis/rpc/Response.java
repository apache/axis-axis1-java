package org.apache.axis.rpc;

import java.io.NotSerializableException;
import org.w3c.dom.Document;
import org.apache.axis.message.SOAPDocument;
import org.apache.axis.message.SOAPBodyEntry;
import org.apache.axis.rpc.encoding.Encoder;
import org.apache.axis.rpc.encoding.Decoder;
import org.apache.axis.rpc.encoding.NoSuchTypeMappingException;
import org.apache.axis.rpc.encoding.MalformedEncodingException;

final public class Response extends Message {
    public Response(SOAPDocument doc) { super(doc); }

    public Response(String name, Document fac) { super(name, fac); }

    public Response(String name,
                    Parameter returnValue,
                    Encoder enc,
                    Document fac)
        throws NoSuchTypeMappingException, NotSerializableException
    {
        super(name,
              (returnValue == null ? null : new Parameter[]{ returnValue, }),
              enc,
              fac);
    }

    public SOAPBodyEntry getStruct() { return super.getStruct(); }
    public String getStructName() { return super.getStructName(); }
    public Object getReturnValue(Decoder dec, Class type)
        throws MalformedEncodingException,
               NoSuchTypeMappingException,
               NoSuchFieldException,
               InstantiationException,
               IllegalAccessException
    {
        Object[] values = getParameters(dec, new Class[]{ type, });
        if (values.length == 0)
            return null;
        return values[0];
    }
}
