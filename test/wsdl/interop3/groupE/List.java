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

package test.wsdl.interop3.groupE;
 
/**
 * Test linked list used by the WSDLInteropTestDocLitService.
 *
 * @author Glyn Normington <glyn@apache.org>
 */
public class List {

    // items of the structure.  String permits nulls.
    private int varInt;
    private String varString;
    private List child;

    /**
     * null constructor
     */
    public List() {}

    /**
     * convenience constructor that sets all of the fields
     */
    public List(int i, String s, List c) {
        this.varInt = i;
        this.varString = s;
        this.child = c;
    }

    /**
     * bean getter for VarInt
     */
    public int getVarInt() {
        return varInt;
    }

    /**
     * bean setter for VarInt
     */
    public void setVarInt(int varInt) {
        this.varInt = varInt;
    }

    /**
     * bean getter for VarString
     */
    public String getVarString() {
        return varString;
    }

    /**
     * bean setter for VarString
     */
    public void setVarString(String varString) {
        this.varString = varString;
    }

    /**
     * bean getter for Child
     */
    public List getChild() {
        return child;
    }

    /**
     * bean setter for Child
     */
    public void setChild(List c) {
        this.child = c;
    }

    /**
     * Equality comparison.  
     */
    public boolean equals(Object object) {
        if (!(object instanceof List)) return false;

        List that = (List)object;

        if (this.varInt != that.varInt) return false;

        if (this.varString == null) {
            if (that.varString != null) return false;
        } else {
            if (!this.varString.equals(that.varString)) return false;
        }

        if (this.child == null) {
            if (that.child != null) return false;
        } else {
            if (!this.child.equals(that.child)) return false;
        }

        return true;
    }

    /**
     * Printable representation
     */
    public String toString() {
        return "{" + varInt + ", \"" + varString + "\", " + 
            (child == null ? null :child.toString()) + "}";
    }
}
