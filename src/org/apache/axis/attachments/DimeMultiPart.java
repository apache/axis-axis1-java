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

/**
 * @author Rick Rineholt 
 */

package org.apache.axis.attachments;





/**
 * This class hold all parts of a DIME multipart message. 
 */

public final class DimeMultiPart {
    static final long transSize = Integer.MAX_VALUE;
    static final byte CURRENT_VERSION = 1; //Anything above this we don't support.
    protected java.util.Vector parts = new java.util.Vector(); 
    public DimeMultiPart() {}
    public void addBodyPart(DimeBodyPart part) {
        parts.add(part);
    }

    public void write(java.io.OutputStream os)
       throws java.io.IOException {
        int size = parts.size();
        int last = size - 1;

        for (int i = 0; i < size; ++i)
            ((DimeBodyPart) parts.elementAt(i)).write(os,
                (byte) ((i == 0 ? DimeBodyPart.POSITION_FIRST : 
                 (byte) 0)
                    | (i == last ? DimeBodyPart.POSITION_LAST :
                     (byte) 0)), transSize);
    }

    public long getTransmissionSize() {
        long size = 0;

        for (int i = parts.size() - 1; i > -1; --i)
            size +=
            ((DimeBodyPart) parts.elementAt(i)).getTransmissionSize(
              transSize);

        return size;  
    }
}
