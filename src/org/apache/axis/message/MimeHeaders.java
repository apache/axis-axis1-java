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

import javax.xml.soap.MimeHeader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

/**
 * wraps javax.xml.soap.MimeHeaders and implements java.io.Serializable interface 
 */
public class MimeHeaders extends javax.xml.soap.MimeHeaders
        implements java.io.Externalizable {
    public MimeHeaders() {
    }

    public MimeHeaders(javax.xml.soap.MimeHeaders h) {
        Iterator iterator = h.getAllHeaders();
        while (iterator.hasNext()) {
            MimeHeader hdr = (MimeHeader) iterator.next();
            addHeader(hdr.getName(), hdr.getValue());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            Object key = in.readObject();
            Object value = in.readObject();
            addHeader((String)key, (String)value);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(getHeaderSize());
        Iterator iterator = getAllHeaders();
        while (iterator.hasNext()) {
            MimeHeader hdr = (MimeHeader) iterator.next();
            out.writeObject(hdr.getName());
            out.writeObject(hdr.getValue());
        }
    }

    private int getHeaderSize() {
        int size = 0;
        Iterator iterator = getAllHeaders();
        while (iterator.hasNext()) {
            iterator.next();
            size++;
        }
        return size;
    }
}
