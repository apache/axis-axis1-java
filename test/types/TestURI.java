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

package test.types;

import junit.framework.TestCase;
import org.apache.axis.types.URI;

/**
 * Test validation of types.Day
 */
public class TestURI extends TestCase {

    public TestURI(String name) {
        super(name);
    }

    /**
     * Bug AXIS-814
     */
    public void testAxis814() throws Exception {
        URI uri1 = new URI("urn:foobar");
        URI uri2 = new URI("urn:foobar");
        assertEquals(uri1,uri2);
        assertEquals(uri1.hashCode(),uri2.hashCode());
    }
}
