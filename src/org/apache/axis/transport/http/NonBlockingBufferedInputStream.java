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

package org.apache.axis.transport.http;

import java.io.IOException;
import java.io.InputStream; 

public class NonBlockingBufferedInputStream extends InputStream {

    // current stream to be processed
    private InputStream in;

    // maximum number of bytes allowed to be returned.
    private int remainingContent = Integer.MAX_VALUE;

    // Internal buffer for the input stream
    private byte[] buffer = new byte[4096];
    private int offset = 0;     // bytes before this offset have been processed
    private int numbytes = 0;   // number of valid bytes in this buffer

    /**
     * set the input stream to be used for subsequent reads
     * @param in the InputStream
     */
    public void setInputStream (InputStream in) {
        this.in = in;
        numbytes = 0;
        offset = 0;
        remainingContent = (in==null)? 0 : Integer.MAX_VALUE;
    }

    /**
     * set the maximum number of bytes allowed to be read from this input
     * stream.
     * @param value the Content Length
     */
    public void setContentLength (int value) {
        if (in != null) this.remainingContent = value - (numbytes-offset);
    }

    /**
     * Replenish the buffer with data from the input stream.  This is 
     * guaranteed to read atleast one byte or throw an exception.  When
     * possible, it will read up to the length of the buffer
     * the data is buffered for efficiency.
     * @return the byte read
     */
    private void refillBuffer() throws IOException {
        if (remainingContent <= 0 || in == null) return;

        // determine number of bytes to read
        numbytes = in.available();
        if (numbytes > remainingContent) numbytes=remainingContent;
        if (numbytes > buffer.length) numbytes=buffer.length;
        if (numbytes <= 0) numbytes = 1;

        // actually attempt to read those bytes
        numbytes = in.read(buffer, 0, numbytes);

        // update internal state to reflect this read
        remainingContent -= numbytes;
        offset = 0;
    }

    /**
     * Read a byte from the input stream, blocking if necessary.  Internally
     * the data is buffered for efficiency.
     * @return the byte read
     */
    public int read() throws IOException {
        if (in == null) return -1;
        if (offset >= numbytes) refillBuffer();
        if (offset >= numbytes) return -1;
        return buffer[offset++];
    }
    
    /**
     * Read bytes from the input stream.  This is guaranteed to return at 
     * least one byte or throw an exception.  When possible, it will return 
     * more bytes, up to the length of the array, as long as doing so would 
     * not require waiting on bytes from the input stream.
     * @param dest      byte array to read into
     * @return the number of bytes actually read
     */
    public int read(byte[] dest) throws IOException {
        return read(dest, 0, dest.length);
    }

    /**
     * Read a specified number of bytes from the input stream.  This is
     * guaranteed to return at least one byte or throw an execption.  When
     * possible, it will return more bytes, up to the length specified,
     * as long as doing so would not require waiting on bytes from the
     * input stream.
     * @param dest      byte array to read into
     * @param off       starting offset into the byte array
     * @param len       maximum number of bytes to read
     * @return the number of bytes actually read
     */
    public int read(byte[] dest, int off, int len) throws IOException {
        int ready = numbytes - offset;

        if (ready >= len) {
            System.arraycopy(buffer, offset, dest, off, len);
            offset += len;
            return len;
        } else if (ready>0) {
            System.arraycopy(buffer, offset, dest, off, ready);
            offset = numbytes;
            return ready;
        } else {
            if (in == null) return -1;
            refillBuffer();
            if (offset >= numbytes) return -1;
            return read(dest,off,len);
        }
    }
    
    /**
     * skip over (and discard) a specified number of bytes in this input
     * stream
     * @param len the number of bytes to be skipped
     * @return the action number of bytes skipped
     */
    public int skip(int len) throws IOException {
        int count = 0;
        while (len-->0 && read()>=0) count++;
        return count;
    }

    /**
     * return the number of bytes available to be read without blocking
     * @return the number of bytes
     */
    public int available() throws IOException {
        if (in == null) return 0;

        // return buffered + available from the stream
        return (numbytes-offset) + in.available();
    }

    /**
     * disassociate from the underlying input stream
     */
    public void close() throws IOException {
        setInputStream(null);
    }

    /**
     * Just like read except byte is not removed from the buffer. 
     * the data is buffered for efficiency.
     * Was added to support multiline http headers. ;-)
     * @return the byte read
     */
    public int peek() throws IOException {
        if (in == null) return -1;
        if (offset >= numbytes) refillBuffer();
        if (offset >= numbytes) return -1;
        return buffer[offset];
    }
}

