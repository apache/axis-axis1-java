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
package org.apache.axis.wsdl.symbolTable;

import java.util.Map;
import java.util.Vector;

/**
 * This class simply collects all the parameter or message data for an operation into one place.
 */
public class Parameters {

    // This vector contains instances of the Parameter class

    /** Field list */
    public Vector list = new Vector();

    // The return info is stored as a Parameter.

    /** Field returnParam */
    public Parameter returnParam = null;

    // A map of the faults

    /** Field faults */
    public Map faults = null;

    // The signature that the interface and the stub will use

    /** Field signature */
    public String signature = null;

    // The numbers of the respective parameters

    /** Field inputs */
    public int inputs = 0;

    /** Field inouts */
    public int inouts = 0;

    /** Field outputs */
    public int outputs = 0;

    /**
     * Method toString
     * 
     * @return 
     */
    public String toString() {

        return "\nreturnParam = " + returnParam + "\nfaults = " + faults
                + "\nsignature = " + signature
                + "\n(inputs, inouts, outputs) = (" + inputs + ", " + inouts
                + ", " + outputs + ")" + "\nlist = " + list;
    }    // toString
}    // class Parameters
