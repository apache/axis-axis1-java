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

package org.apache.axis.handlers ;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A simple Handler which logs the request and response messages to either
 * the console or a specified file (default "axis.log").
 *
 * To use this, deploy it either in both the request and response flows
 * (global, service, or transport) or in just the response flow.  If deployed
 * in both places, you'll also get an elapsed time indication, which can be
 * handy for debugging.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class LogHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(LogHandler.class.getName());

    long start = -1;
    private boolean writeToConsole = false;
    private String filename = "axis.log";

    public void init() {
        super.init();

        Object opt = this.getOption("LogHandler.writeToConsole");
        if (opt != null && opt instanceof String &&
                "true".equalsIgnoreCase((String)opt))
            writeToConsole = true;

        opt = this.getOption("LogHandler.fileName");
        if (opt != null && opt instanceof String)
            filename = (String)opt;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug("Enter: LogHandler::invoke");
        if (msgContext.getPastPivot() == false) {
           start = System.currentTimeMillis();
        } else {
            logMessages(msgContext);
        }
        log.debug("Exit: LogHandler::invoke");
    }

    private void logMessages(MessageContext msgContext) throws AxisFault {
        try {
            PrintWriter writer   = null;

            writer = getWriter(msgContext);

            Message inMsg = msgContext.getRequestMessage();
            Message outMsg = msgContext.getResponseMessage();

            writer.println( "=======================================================" );
            if (start != -1) {
                writer.println( "= " + Messages.getMessage("elapsed00",
                       "" + (System.currentTimeMillis() - start)));
            }
            writer.println( "= " + Messages.getMessage("inMsg00",
                   (inMsg == null ? "null" : inMsg.getSOAPPartAsString())));
            writer.println( "= " + Messages.getMessage("outMsg00",
                   (outMsg == null ? "null" : outMsg.getSOAPPartAsString())));
            writer.println( "=======================================================" );

            //START FIX: http://nagoya.apache.org/bugzilla/show_bug.cgi?id=16646
            if (!writeToConsole) {
              writer.close();
            }
            //END FIX: http://nagoya.apache.org/bugzilla/show_bug.cgi?id=16646

        } catch( Exception e ) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
    }

    private PrintWriter getWriter(MessageContext msgContext) throws IOException {
        PrintWriter writer;

        // Allow config info to control where we write.
        if (writeToConsole) {
            // Writing to the console
            writer = new PrintWriter(System.out);
        } else {
            // Writing to a file.
            if (filename == null) {
                filename = "axis.log";
            }
            writer = new PrintWriter(new FileWriter( filename, true ));
        }
        return writer;
    }


    public void onFault(MessageContext msgContext) {
        try {
            logMessages(msgContext);
        } catch (AxisFault axisFault) {
            log.error(Messages.getMessage("exception00"), axisFault);
        }
    }
};
