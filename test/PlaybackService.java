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
package test;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A trivial service which simply echoes back a desired SOAP message.  This
 * is useful for testing, as we can simulate responses from particular packages,
 * bugs, etc.  This should be deployed with provider="Handler".
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class PlaybackService extends BasicHandler {
    /**
     * Get the filename which contains the response message.  Looks in
     * the MessageContext/service/engine for a "responseFile" property, and
     * if found simply returns that value.  Otherwise defaults to
     * "response.xml" in the current directory of the server.
     * 
     * This mechanism can be configured in two ways.  First, anyone can set
     * the "responseFile" property based on the message contents, etc.  As long
     * as this happens earlier in the handler chain, the value will be picked
     * up and used here.  Second, this class can be subclassed and this
     * method overriden to do the right thing.
     * 
     * @param context the current MessageContext
     * @return the filename containing the canned response
     */ 
    protected String getFilename(MessageContext context) {
        String filename = context.getStrProp("responseFile");
        if (filename == null) {
            filename = "response.xml";
        }
        return filename;
    }
    
    public void invoke(MessageContext context) throws AxisFault {
        try {
            FileInputStream stream = new FileInputStream(getFilename(context));
            context.setResponseMessage(new Message(stream));
        } catch (FileNotFoundException e) {
            throw AxisFault.makeFault(e);
        }
    }
}
