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
 * Test structure used by the WSDLInteropTestDocLitService.
 * Note: this implementation does not allow null values for varInt or varFloat.
 *
 * @author Glyn Normington <glyn@apache.org>
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class SOAPStruct {

    // items of the structure.  String permits nulls.
    private float varFloat;
    private int varInt;
    private String varString;

    /**
     * null constructor
     */
    public SOAPStruct() {}

    /**
     * convenience constructor that sets all of the fields
     */
    public SOAPStruct(float f, int i, String s) {
        this.varFloat = f;
        this.varInt = i;
        this.varString = s;
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
     * bean getter for VarFloat
     */
    public float getVarFloat() {
        return varFloat;
    }

    /**
     * bean setter for VarFloat
     */
    public void setVarFloat(float varFloat) {
        this.varFloat=varFloat;
    }

    /**
     * Equality comparison.  
     */
    public boolean equals(Object object) {
        if (!(object instanceof SOAPStruct)) return false;

        SOAPStruct that = (SOAPStruct)object;

        if (this.varInt != that.varInt) return false;

        if (this.varFloat != that.varFloat) return false;

        if (this.varString == null) {
            if (that.varString != null) return false;
        } else {
            if (!this.varString.equals(that.varString)) return false;
        }

        return true;
    }

    /**
     * Printable representation
     */
    public String toString() {
        return "{" + varInt + ", \"" + varString + "\", " + varFloat + "}";
    }
}
