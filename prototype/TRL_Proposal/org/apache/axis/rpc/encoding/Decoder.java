package org.apache.axis.rpc.encoding;

import org.w3c.dom.Element;

public interface Decoder {
    public Object[] decodeRoot(Class[] types, Element[] elems) 
        throws IllegalAccessException,
               InstantiationException,
               NoSuchFieldException,
               NoSuchTypeMappingException,
               MalformedEncodingException;
}
