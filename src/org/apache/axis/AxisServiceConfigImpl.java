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
package org.apache.axis;

/**
 * A simple implementation of AxisServiceConfig.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class AxisServiceConfigImpl implements AxisServiceConfig {
    // fixme: do we realy want these all held in a single string? should we be
    //  splitting this here, or in code that uses the config?
    private String methods;

    /**
     * Set the allowed method names.
     *
     * @param methods  a <code>String</code> containing a list of all allowed
     *              methods
     */
    public void setAllowedMethods(String methods)
    {
        this.methods = methods;
    }

    /** Get the allowed method names.
     *
     * @return a space-delimited list of method names which may be called
     *         via SOAP.
     */
    public String getAllowedMethods() {
        return methods;
    }
}
