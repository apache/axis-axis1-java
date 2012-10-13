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
package test.soap;

import org.apache.axis.MessageContext;

/**
 * Trivial test service.
 */ 
public class TestService {
    /**
     * Calculate and return the length of the passed String.  If a
     * MessageContext property indicates that we've received a particular
     * header, then double the result before returning it.
     */ 
    public int countChars(String str)
    {
        int ret = str.length();
        MessageContext mc = MessageContext.getCurrentContext();
        if (mc.isPropertyTrue(TestHeaderAttrs.PROP_DOUBLEIT)) {
            ret = ret * 2;
        }
        return ret;
    }
}
