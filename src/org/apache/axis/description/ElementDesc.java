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
package org.apache.axis.description;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * An AttributeDesc is a FieldDesc for an Java field mapping to an
 * XML element
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ElementDesc extends FieldDesc implements Serializable {
    /** The minOccurs value from the schema */
    private int minOccurs = 1;
    /** The maxOccurs value from the schema */
    private int maxOccurs = 1;
    
    /** If this is an array, this holds the array type */
    private QName arrayType;
    
    public ElementDesc() {
        super(true);
    }

    public boolean isMinOccursZero() {
        return minOccurs == 0;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public QName getArrayType() {
        return arrayType;
    }

    public void setArrayType(QName arrayType) {
        this.arrayType = arrayType;
    }
}
