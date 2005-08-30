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

package test.concurrency;

/**
 * An Axis service to test application scope.  There should be exactly one
 * instance of this class, if we end up with more then application scope
 * isn't working correctly.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestService {
    private static Object lock = new Object();

    private static TestService singleton = null;
    public static final String MESSAGE = "Hi there, come here often?";

    public TestService() throws Exception {
        synchronized (lock) {
            if (singleton != null) {
                // We're not the first/only one, so throw an Exception!
                throw new Exception("Multiple instances of TestService created!");
            }

            singleton = this;
        }
    }

    public String hello() {
        return MESSAGE;
    }
}
