package samples.transport ;

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

import java.io.* ;
import java.lang.Thread ;
import java.util.Date ;

import org.apache.axis.Message ;
import org.apache.axis.AxisFault ;
import org.apache.axis.MessageContext ;
import org.apache.axis.handlers.BasicHandler ;


/**
 * Just write the XML to a file called xml#.req and wait for
 * the result in a file called xml#.res
 *
 * Not thread-safe - just a dummy sample to show that we can indeed use
 * something other than HTTP as the transport.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

public class FileSender extends BasicHandler {
  static int nextNum = 1 ;

  public void invoke(MessageContext msgContext) throws AxisFault {
    Message  msg = msgContext.getRequestMessage();
    byte[]   buf = (byte[]) msg.getAsBytes();
    boolean timedOut = false;
    try {
      FileOutputStream fos = new FileOutputStream( "xml" + nextNum + ".req" );

      fos.write( buf );
      fos.close();
    }
    catch( Exception e ) {
      e.printStackTrace();
    }

    long timeout = Long.MAX_VALUE;
    if (msgContext.getTimeout()!=0)
      timeout=(new Date()).getTime()+msgContext.getTimeout();

    for (; timedOut == false;) {
      try {
        Thread.sleep( 100 );
        File file = new File( "xml" + nextNum + ".res" );

        if ((new Date().getTime())>=timeout)
            timedOut = true;

        if ( !file.exists() ) continue ;
        Thread.sleep( 100 );   // let the other side finish writing
        FileInputStream fis = new FileInputStream( "xml" + nextNum + ".res" );
        msg = new Message( fis );
        msg.getAsBytes();  // just flush the buffer
        fis.close();
         Thread.sleep( 100 );
        (new File("xml" + nextNum + ".res")).delete();
        msgContext.setResponseMessage( msg );
        break ;
      }
      catch( Exception e ) {
        // File not there - just loop
      }
    }
    nextNum++ ;
    if (timedOut)
        throw new AxisFault("timeout");

  }

  public void undo(MessageContext msgContext) {
  }
}
