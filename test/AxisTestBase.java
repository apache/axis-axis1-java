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


package test;

import org.custommonkey.xmlunit.XMLTestCase;

/**
 *  base test class for Axis test cases.
 * @author steve loughran
 */
public abstract class AxisTestBase extends XMLTestCase {

    public AxisTestBase(String s) {
        super(s);
    }

    /**
     * probe for a property being set in ant terms.
     * @param propertyname
     * @return true if the system property is set and set to true or yes
     */
    public static boolean isPropertyTrue(String propertyname) {
        String setting = System.getProperty(propertyname);
        return "true".equalsIgnoreCase(setting) ||
                "yes".equalsIgnoreCase(setting);
    }

    /**
     * test for the online tests being enabled
     * @return true if 'online' tests are allowed.
     */
    public static boolean isOnline() {
        return isPropertyTrue("test.functional.online");
    }
}
