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
package org.apache.axis.transport.http;


import java.io.IOException;
import java.io.InputStream;


/**
 *
 * @author Rick Rineholt 
 */

 /**
  * The ONLY reason for this is so we can clean up sockets quicker/cleaner.
  */


public class SocketInputStream extends java.io.FilterInputStream {
    protected volatile boolean closed = false;
    java.net.Socket socket= null;

    private SocketInputStream() {
        super(null);
    }


    public SocketInputStream(InputStream is, java.net.Socket socket) {
        super(is);
        this.socket= socket;
    }

    public void close() throws IOException {
       synchronized(this){
       if(closed) return;
       closed= true;
       }
       in.close();
       in= null;
       socket.close();
       socket= null;
    }
}
