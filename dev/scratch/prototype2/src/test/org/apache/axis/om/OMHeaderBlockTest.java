/**
* Copyright 2001-2004 The Apache Software Foundation.
* <p/>
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* <p/>
* http://www.apache.org/licenses/LICENSE-2.0
* <p/>
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* <p/>
* Author: Eran Chinthaka - Lanka Software Foundation
* Date: Nov 2, 2004
* Time: 2:40:21 PM
*/
package org.apache.axis.om;

import java.util.Iterator;


public class OMHeaderBlockTest extends OMTestCase {

    SOAPHeader soapHeader;
    SOAPHeaderBlock soapHeaderElement;

    public OMHeaderBlockTest(String testName) {
        super(testName);
    }


    protected void setUp() throws Exception {
        super.setUp();
        soapHeader = soapEnvelope.getHeader();
        Iterator headerElementIter = soapHeader.examineAllHeaderBlocks();
        if (headerElementIter.hasNext()) {
            soapHeaderElement = (SOAPHeaderBlock) headerElementIter.next();
       }
    }


    public void testSetAndGetActor() {
        String newActorURI = "http://newActor.org";
        soapHeaderElement.setActor(newActorURI);
        System.out.println("ActorURI = " + soapHeaderElement.getActor());
        assertTrue("Actor was not properly set", soapHeaderElement.getActor().equalsIgnoreCase(newActorURI));
    }


    public void testSetAndGetMustUnderstand() {
        soapHeaderElement.setMustUnderstand(false);
        assertTrue("MustUnderstand was not properly set", !soapHeaderElement.getMustUnderstand());
    }

    public void testGetMustUnderstand() {
        //TODO Implement getMustUnderstand().
    }

}