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

/**
 * 
 *  UUIDGen adopted from the juddi project
 *  (http://sourceforge.net/projects/juddi/)
 * 
 */

package test.components;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;

public class TestUUID extends TestCase {

    public TestUUID(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestUUID.class);
    }

    public void testUUID() {
        long startTime = 0;
        long endTime = 0;
        UUIDGen uuidgen = null;

        uuidgen = UUIDGenFactory.getUUIDGen();
        startTime = System.currentTimeMillis();
        for (int i = 1; i <= 10; ++i) {
            String u = uuidgen.nextUUID();
            System.out.println(i + ":  " + u);
        }
        endTime = System.currentTimeMillis();
        System.out.println("UUIDGen took " + (endTime - startTime) + " milliseconds");
    }
}
