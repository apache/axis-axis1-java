/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import java.util.Iterator;

/**
 * Detail Container implementation
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class Detail extends SOAPFaultElement implements javax.xml.soap.Detail {

    public Detail() {
    }

    /**
     * Creates a new <code>DetailEntry</code> object with the given
     * name and adds it to this <code>Detail</code> object.
     * @param   name a <code>Name</code> object identifying the new <code>DetailEntry</code> object
     * @return DetailEntry.
     * @throws SOAPException  thrown when there is a problem in adding a DetailEntry object to this Detail object.
     */
    public DetailEntry addDetailEntry(Name name) throws SOAPException {
        org.apache.axis.message.DetailEntry entry = new org.apache.axis.message.DetailEntry(name);
        addChildElement(entry);
        return entry;
    }

    /**
     * Gets a list of the detail entries in this <code>Detail</code> object.
     * @return  an <code>Iterator</code> object over the <code>DetailEntry</code>
     *        objects in this <code>Detail</code> object
     */
    public Iterator getDetailEntries() {
        return this.getChildElements();
    }
}
