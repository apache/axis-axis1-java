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

package org.apache.axis.attachments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Rick Rineholt 
 */

 /**
  * This class takes the input stream and turns it multiple streams. 
  */
public class BoundaryDelimitedStream extends java.io.FilterInputStream {
    static Log log =
            LogFactory.getLog(BoundaryDelimitedStream.class.getName());

    protected byte[] boundary = null;
    int boundaryLen = 0;  //The boundary length.
    int boundaryBufLen = 0; //The boundary length plus crlf. 
    java.io.InputStream is = null; //The source input stream.
    boolean closed = true; //The stream has been closed.
    boolean eos = false;  //eof has been detected.
    boolean theEnd = false; //There are no more streams left.

    int readbufsz = 0; //Minimum to read at one time.
    byte[] readbuf = null; //The buffer we are reading.
    int readBufPos = 0;  //Where we have read so far in the stream.
    int readBufEnd = 0;  //The number of bytes in array.
    protected static final int BOUNDARY_NOT_FOUND = Integer.MAX_VALUE;
                                   // Where in the stream a boundary is located.
    int boundaryPos = BOUNDARY_NOT_FOUND;

    static int streamCount= 0; //number of streams produced.
    protected synchronized static int newStreamNo(){

     log.debug("New boundary stream no:" + (streamCount +1));
        return ++streamCount;
    }
    protected int streamNo=-1; //Keeps track of stream

    static boolean isDebugEnabled= false;

    /**
     * Gets the next stream. From the previous using the same buffer size to read.
     * @return the boundary delmited stream. Null if there are no more streams.
     */
    public synchronized BoundaryDelimitedStream getNextStream() {
        return getNextStream(readbufsz);
    }

    /**
     * Gets the next stream. From the previous using  new buffer reading size.
     * @return the boundary delmited stream. Null if there are no more streams.
     */
    protected synchronized BoundaryDelimitedStream getNextStream(int readbufsz) {
        BoundaryDelimitedStream ret = null;

        if ( !theEnd ) {
          //Create an new boundary stream  that comes after this one.
            ret = new BoundaryDelimitedStream(this, readbufsz);
        }
        return ret;
    }
    /**
      * Constructor to create the next stream from the previous one.
      */
    protected BoundaryDelimitedStream(BoundaryDelimitedStream prev,
      int readbufsz ) {
        super (prev.is);

        streamNo= newStreamNo();

        boundary = prev.boundary;
        boundaryLen = prev.boundaryLen;
        boundaryBufLen = prev.boundaryBufLen;
        skip= prev.skip;
        is = prev.is;
        closed = false; //The new one is not closed.
        eos = false;  //Its not at th EOS.
        readbufsz = prev.readbufsz;
        readbuf = prev.readbuf;
        //Move past the old boundary.
        readBufPos = prev.readBufPos + boundaryBufLen; 
        readBufEnd = prev.readBufEnd;
        //find the new boundary.
        boundaryPos = boundaryPosition( readbuf, readBufPos, readBufEnd-1);
        prev.theEnd = theEnd; //The stream.
    }

    /**
     * Create a new boundary stream;
     * @param boundary is the boundary that separates the individual streams.
     * @param readbufsz lets you have some control over the amount of buffering.
     *   by buffering you can some effiency in searching.
     */
     BoundaryDelimitedStream( java.io.InputStream is, byte[] boundary,
      int readbufsz) throws org.apache.axis.AxisFault {
        super (is);
        isDebugEnabled= log.isDebugEnabled();
        streamNo= newStreamNo();
        closed = false;
        this.is = is;
        this.boundary = boundary;
        this.boundaryLen = boundary.length;
        this.boundaryBufLen = boundaryLen + 2;
          //allways leave room for at least a 2x boundary
          //Most mime boundaries are 40 bytes or so.
        this.readbufsz = Math.max( (boundaryBufLen) * 2, readbufsz); 
    }
     
    /**
     * Read from the boundary delimited stream.
     * @param b is the array to read into.
     * @param off is the offset 
     * @return the number of bytes read. -1 if endof stream.
     */

    public synchronized int read(byte[] b, final int off, final int len)
                                                throws java.io.IOException {
        if (closed) throw new java.io.IOException("Stream closed.");
        if (eos) return -1;

        if (readbuf == null) { //Allocate the buffer.
            readbuf = new byte[Math.max(len, readbufsz ) ];
            readBufEnd = is.read(readbuf);
            if( readBufEnd < 0) throw new java.io.IOException( "End of stream encountered before final boundary marker."); 
            readBufPos = 0;
                                                       //Finds the boundary pos.
            boundaryPos = boundaryPosition( readbuf, 0, readBufEnd);
        }
        int bwritten = 0; //Number of bytes written.

         //read and copy bytes in.
        do {    //Always allow to have a boundary length left in the buffer.
            int bcopy = Math.min(readBufEnd - readBufPos - boundaryBufLen,
              len - bwritten);
                                                   //never go past the boundary.
            bcopy = Math.min(bcopy, boundaryPos - readBufPos); 

            if (bcopy > 0) {
                System.arraycopy(readbuf, readBufPos, b, off + bwritten, bcopy);
                bwritten += bcopy;
                readBufPos += bcopy;
            }
            if (readBufPos == boundaryPos) {
                eos = true; //hit the boundary so it the end of the stream.
                log.debug("Boundary stream no:" + streamNo + " is at end of stream");
            }
            else if ( bwritten < len) { //need to get more data.
                byte[]dstbuf = readbuf;

                if ( readbuf.length < len) dstbuf = new byte[len];
                int movecnt = readBufEnd - readBufPos;

                //copy what was left over.
                System.arraycopy(readbuf, readBufPos, dstbuf, 0, movecnt);
                //Read in the new data.
                int readcnt = is.read(dstbuf, movecnt, dstbuf.length - movecnt);
                
                if( readcnt < 0) throw new java.io.IOException( "End of stream encountered before final boundary marker."); 

                readBufEnd = readcnt + movecnt;
                readbuf = dstbuf;
                readBufPos = 0; //start at the begining.
                                      //just move the boundary by what we moved
                if (BOUNDARY_NOT_FOUND != boundaryPos ) boundaryPos -= movecnt;
                else boundaryPos = boundaryPosition( readbuf, readBufPos,
                                 readBufEnd-1); //See if the boundary is now there.
            }

        }
        //read till we get the amount or the stream is finished.
        while ( !eos && bwritten < len );

        if ( log.isDebugEnabled()) {
            if (bwritten  > 0) {
                byte tb[] = new byte[bwritten];

                System.arraycopy(b, off, tb, 0, bwritten);
                log.debug("Read " + bwritten +
                " from BoundaryDelimitedStream:"+ streamNo+"\"" + 
                new String(tb) + "\"");
                
//    System.err.println("Read " + bwritten +
//                " from BoundaryDelimitedStream:"+ streamNo+"\"" + 
//                new String(tb) + "\"");
            }
        }

        return bwritten;
    }

    /**
     * Read from the boundary delimited stream.
     * @param b is the array to read into. Read as much as possible 
     *   into the size of this array.
     * @return the number of bytes read. -1 if endof stream.
     */
    public int read(byte[] b) throws java.io.IOException {
        return read(b, 0, b.length);
    }

    /**
     * Read from the boundary delimited stream.
     * @return The byte read, or -1 if endof stream.
     */

    public int read() throws java.io.IOException {
        byte[] b = new byte[1];  //quick and dirty. //for now
        int read = read(b);

        if ( read < 0 ) return -1;
        else return b[0];
    }

    /**
     * Closes the stream.
     */
    public synchronized void close() throws java.io.IOException {
        if (closed) return;
        log.debug("Boundary stream no:" + streamNo + " is closed");
        closed = true; //mark it closed.
        if (!eos) { //We need get this off the stream.
                                //Easy way to flush through the stream;
            byte[] readrest = new byte[1024 * 16];
            int bread = 0;

            do {
                bread = read(readrest);
            }
            while ( bread > -1 );

        }
    }

    /**
     * Read from the boundary delimited stream.
     * @return The position of the boundary. Detects the end of the source stream.
     * 
     */
    int boundaryPosition( byte[] searchbuf, int start, int end) {

        int foundAt = boundarySearch(searchbuf, start, end);
                                               //First find the boundary marker

        if (BOUNDARY_NOT_FOUND != foundAt) { //Something was found.

            //If the marker has a "--" at the end then this is the last boundary.
            if ( searchbuf[foundAt + boundaryLen] == '-' && 
                                searchbuf[foundAt + boundaryLen + 1 ] == '-' ) {
                theEnd = true;
            }
            else if ( searchbuf[foundAt + boundaryLen] != 13 ||
                                  searchbuf[foundAt + boundaryLen + 1 ] != 10 ) {
           //If there really was no crlf at then end then this is not a boundary.
                foundAt = BOUNDARY_NOT_FOUND;
            }
        }
        return foundAt;
    }

  /* The below uses a standard textbook Boyer-Moore pattern search.*/
   

   private int[] skip= null;
   private int boundarySearch(final byte[]text,final int start, final int end ) {
//System.err.println(">>>>" + start + "," + end);   

       int i, j, k;
      
       if(null == skip){
           skip= new int[256];
           java.util.Arrays.fill(skip, boundaryLen);   
           for( k=0; k<boundaryLen-1; k++ ) skip[boundary[k]] = boundaryLen-k-1;
       }


       for( k=start + boundaryLen-1; k <=end; k += skip[text[k] & (0xff)] ) {
// System.err.println(">>>>" + k);   
//printarry(text, k-boundaryLen+1, end);
            for( j=boundaryLen-1, i=k; j>=0 && text[i] == boundary[j]; j-- ) i--;
            if( j == (-1) ) return i+1;
       }

// System.err.println(">>>> not found" );   
       return BOUNDARY_NOT_FOUND;
   }

public static void printarry( byte[] b, int start , int end){
                byte tb[] = new byte[end-start];

                System.arraycopy(b, start, tb, 0, end-start);
                
    System.err.println("\"" + new String(tb) + "\"");
    
}

}
