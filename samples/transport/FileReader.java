package samples.transport ;

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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

import org.apache.axis.Message ;
import org.apache.axis.AxisFault ;
import org.apache.axis.MessageContext ;
import org.apache.axis.handlers.BasicHandler ;
import org.apache.axis.server.AxisServer ;

/**
 * Waits for the XML to appear in a file called xml#.req and writes
 * the response in a file called xml#.res
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class FileReader extends Thread {
  static int      nextNum = 1 ;
  boolean  pleaseStop = false ;

  public void run() {
    String tmp = "" ;
    AxisServer  server = new AxisServer();
    server.init();

    while( !pleaseStop ) {
      try {
        Thread.sleep( 100 );
        File file = new File( "xml" + nextNum + ".req" );
        if ( !file.exists() ) continue ;
          
          // avoid race condition where file comes to exist but we were halted -- RobJ
          if (pleaseStop) continue;

        Thread.sleep( 100 );   // let the other side finish writing
        FileInputStream fis = new FileInputStream( file );
        int thisNum = nextNum++; // increment early to avoid infinite loops
        
        Message msg = new Message( fis );
        MessageContext  msgContext = new MessageContext(server);
        msgContext.setRequestMessage( msg );

        // SOAPAction hack
        byte[]  buf = new byte[50];
        fis.read( buf, 0, 50 );
        String action = new String( buf );
        msgContext.setTargetService( action.trim() );
        // end of hack

        try {
            server.invoke( msgContext );
            msg = msgContext.getResponseMessage();
        } catch (AxisFault af) {
            msg = new Message(af);
        } catch (Exception e) {
            msg = new Message(new AxisFault(e.toString()));
        }
        
        buf = (byte[]) msg.getAsBytes();
        FileOutputStream fos = new FileOutputStream( "xml" + thisNum + ".res" );
        fos.write( buf );
        fos.close();

        fis.close();
        file.delete();
      }
      catch( Exception e ) {
        if ( !(e instanceof FileNotFoundException) )
          e.printStackTrace();
      }
    }
      System.out.println("FileReader halted.");
  }

  public void halt() {
    pleaseStop = true ;
  }
}
