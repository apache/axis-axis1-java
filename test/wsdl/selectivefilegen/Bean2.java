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
package test.wsdl.selectivefilegen;

/** This class provides an example of an externally generated bean class 
 * that will be excluded from WSDL to Java generation, but included when
 * the program is executed.
 
 @author Jim Stafford    jim.stafford@raba.com
*/
public class Bean2 {
    private String domain_;
    private String service_;
    public void setDomain(String domain)   { domain_ = domain; }
    public String getDomain()              { return domain_; }
    public void setService(String service) { service_ = service; }
    public String getService()             { return service_; }
}
