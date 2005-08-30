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
 * This class is the target of given to Java2WSDL.
 */ 
public class Baby extends Child {

    /**
     * This method should appear in the WSDL
     */ 
    public int baby_method(Baby_bean message) {
        return 42;
    }
    
    /**
     * This method is explicitly excluded from the WSDL
     */ 
    public String baby_excluded(int argument) {
        return "should never be called"; 
    }
}
