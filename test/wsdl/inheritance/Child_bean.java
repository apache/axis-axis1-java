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
package test.wsdl.inheritance;

/**
 * This bean should be in the types section of the generated WSDL
 */ 
public class Child_bean extends Parent_bean {
    
    public String child_string;

    public String getChild_string() {
        return child_string;
    }

    public void setChild_string(String child_string) {
        this.child_string = child_string;
    }
}
