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

package org.apache.axis.utils.cache;

import java.lang.reflect.Method;
import java.util.Vector;

/**
 * A simple cache of previously loaded methods
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class JavaMethod {

    // The list of the methods in the given class with the given name.
    private Method[] methods = null;

    /**
     * Create a cache entry for this java.lang.Class
     * @param jc java.lang.Class which will be searched for methods
     * @param name name of the method
     */
    public JavaMethod(Class jc, String name) {
        Method[] methods = jc.getMethods();
        Vector workinglist = new Vector();

        // scan for matching names, saving the match if it is unique,
        // otherwise accumulating a list
        for (int i=0; i<methods.length; i++) {
            if (methods[i].getName().equals(name)) {
                workinglist.addElement(methods[i]);
            }
        }

        // If a list was found, convert it into an array
        if (workinglist.size() > 0) {
            this.methods = new Method[workinglist.size()];
            workinglist.copyInto(this.methods);
        }
    }
    
    /**
     * Lookup a method based on name.  This method returns an array just in
     * case there is more than one.
     * @param name name of method
     */
    public Method[] getMethod() {
        return methods;
    }
};
