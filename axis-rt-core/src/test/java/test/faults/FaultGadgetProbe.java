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
package test.faults;

/**
 * A stand-in for a dangerous "gadget" class (e.g. Spring's
 * ClassPathXmlApplicationContext) named by an inbound SOAP fault's
 * &lt;exceptionName&gt; element. It records whether its static initializer
 * runs (i.e. the class was loaded with initialization) and whether its
 * String constructor is invoked. It is deliberately not an AxisFault, so a
 * correctly hardened deserializer must never load-and-initialize or
 * construct it. See {@link TestFaultClassInstantiation}.
 */
public class FaultGadgetProbe {
    static {
        FaultGadgetState.staticInitRan = true;
    }

    public FaultGadgetProbe(String url) {
        FaultGadgetState.constructorRan = true;
    }
}
