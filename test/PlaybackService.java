/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
