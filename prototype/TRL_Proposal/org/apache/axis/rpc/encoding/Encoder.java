package org.apache.axis.rpc.encoding;

import java.io.NotSerializableException;
import org.w3c.dom.Element;

public interface Encoder {
    public Element[] getEncodedObjects();
    public Element encodeRoot(Class type, String name, Object value)
        throws NotSerializableException, NoSuchTypeMappingException;
}

