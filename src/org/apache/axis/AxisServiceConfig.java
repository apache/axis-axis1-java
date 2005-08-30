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

package org.apache.axis;

/** If a Java class which acts as the target for an Axis service
 * implements this interface, it may convey metadata about its
 * configuration to the Axis engine.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */
public interface AxisServiceConfig
{
    /** Get the allowed method names.
     * 
     * @return a space-delimited list of method names which may be called
     *         via SOAP.
     */
    public String getAllowedMethods();
}
