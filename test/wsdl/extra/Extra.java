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
package test.wsdl.extra;

import java.util.Calendar;

/**
 * Extra class which is provided to Java2WSDL but not referenced in the service
 * class.  A complex type should be generated for the class in the WSDL
 */ 
public class Extra {
    private String item1;
    private int    item2;
    private Calendar stamp;

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public int getItem2() {
        return item2;
    }

    public void setItem2(int item2) {
        this.item2 = item2;
    }

    public Calendar getStamp() {
        return stamp;
    }

    public void setStamp(Calendar stamp) {
        this.stamp = stamp;
    }
}
