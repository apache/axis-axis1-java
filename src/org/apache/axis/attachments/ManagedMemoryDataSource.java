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


/**
 * @author Rick Rineholt 
 */

/**
 * This class allows small attachments to be cached in memory, while large ones are
 * cached out.  It implements a Java Activiation Data source interface.
 * TODO TODO TODO need to delete cached out data sources after a service ends.
 */

public class ManagedMemoryDataSource implements  javax.activation.DataSource {
    protected String contentType = "application/octet-stream"; //Is the default.
    java.io.InputStream ss = null; //The incoming source stream.
    public static final  int MAX_MEMORY_DISK_CACHED = -1;
    protected int maxCached = 16  * 1024;  //max in memory cached. Default.
                                        //If set the file the disk is cached to.
    protected java.io.File diskCacheFile = null; 
    
                             //Memory is allocated in these size chunks.
    public static final int READ_CHUNK_SZ = 32 * 1024 ;

    //Should not be called; 
    protected ManagedMemoryDataSource () {
    }

    /**
     * Create a new boundary stream;
     * @param ss is the source input stream that is used to create this data source.. 
     * @param readbufsz lets you have some control over the amount of buffering.
     * @param maxCached  This is the max memory that is to be used to cache the data.
     * @param contentType the mime type for this data stream.
     *   by buffering you can some effiency in searching.
     */
    public ManagedMemoryDataSource(java.io.InputStream ss, int maxCached,
                               String contentType) throws java.io.IOException {
        this (ss, maxCached, contentType, false);
    }

    /**
     * Create a new boundary stream;
     * @param ss is the source input stream that is used to create this data source.. 
     * @param readbufsz lets you have some control over the amount of buffering.
     * @param maxCached  This is the max memory that is to be used to cache the data.
     * @param contentType the mime type for this data stream.
     *   by buffering you can some effiency in searching.
     * @param readall if true will read in the whole source.
     */
    public ManagedMemoryDataSource(java.io.InputStream ss, int maxCached,
              String contentType,  boolean readall) throws java.io.IOException {
        this.ss = ss;
        this.maxCached = maxCached;
        if (null != contentType && contentType.length() != 0 ) this.contentType = contentType;
        if ( maxCached < MAX_MEMORY_DISK_CACHED)
            throw  new IllegalArgumentException(" maxcached value is bad: " +
                                                                   maxCached);
        //for now read all in to disk.
        if ( readall) {
            byte[] readbuffer = new byte[READ_CHUNK_SZ];
            int read = 0;

            do {
                read = ss.read(readbuffer);
                if (read > 0) write(readbuffer, read);
            }
            while ( read > -1);
            close();
        }
    }

    /*  javax.activation.Interface DataSource implementation */

    /**
     * This method returns the MIME type of the data in the form of a string. 
     * @return The mime type.
     */
    public java.lang.String getContentType() {
        return contentType;
    }

    /**
     *This method returns an InputStream representing the the data and throws the appropriate exception if it can not do so. 
     *@return the java.io.InputStream for the data source.
     */
    public synchronized java.io.InputStream getInputStream()
        throws java.io.IOException {
        if (memorybuflist == null) {
            return  new java.io.FileInputStream(diskCacheFile);
        }
        else
            return new Instream(); //Return the memory held stream.
    }

    /**
     * This will flush any memory source to disk and
     * provide the name of the file if desired.
     * Return the name of the file of the stream.
     */
    public java.lang.String getName() {
        String ret= null;
        try{
            flushToDisk();
            if(diskCacheFile != null)
               ret= diskCacheFile.getAbsolutePath(); 
        }catch( Exception e){
                diskCacheFile= null;
        }        
        return ret;
    }

    /** 
     *This method returns an OutputStream where the data can be written and throws the appropriate exception if it can not do so. 
     * NOT SUPPORTED, not need for axis, data sources are create by constructors.
     *
     */
    public java.io.OutputStream getOutputStream() throws java.io.IOException {
        return null;
    }

    protected java.util.LinkedList memorybuflist = new java.util.LinkedList(); //The linked list to hold the in memory buffers.
    protected byte[] currentMemoryBuf = null; //Hold the last memory buffer.
    protected int currentMemoryBufSz = 0; //The number of bytes written to the above buffer.
    protected int totalsz = 0;  //The total size in bytes in this data source. 
    protected java.io.BufferedOutputStream cachediskstream = null; //This is the cached disk stream.
                              //If true the source input stream is now closed. 
    protected boolean closed = false; 

    /**
     * Write bytes to the stream.
     * @param data all bytes of this array are written to the stream.
     */
    protected void write( byte[] data) throws java.io.IOException {
        write(data, data.length);
    }



    /**
     * This method is a low level write.
     * Note it is designed to in the future to allow streaming to both memory
     *  AND to disk simultaneously.
     */

    protected synchronized  void  write( byte[] data, int length)
      throws java.io.IOException {
        if (closed) throw new java.io.IOException("Stream closed stream.");
        int writesz = length;
        int byteswritten = 0;

        if ( null != memorybuflist && totalsz + writesz > maxCached ) { //Cache to disk.

            if (null == cachediskstream)   //Need to create a disk cache
                flushToDisk();
        }

        if ( memorybuflist != null) { //Can write to memory.
            do {
                if ( null == currentMemoryBuf) {
                    currentMemoryBuf = new byte[READ_CHUNK_SZ];
                    currentMemoryBufSz = 0;
                    memorybuflist.add(currentMemoryBuf);
                }
                //bytes to write is the min. between the remaining bytes and what is left in this buffer.
                int bytes2write = Math.min( (writesz - byteswritten),
                          (currentMemoryBuf.length - currentMemoryBufSz));

                //copy the data.
                System.arraycopy(data, byteswritten, currentMemoryBuf,
                   currentMemoryBufSz, bytes2write);

                byteswritten += bytes2write;
                currentMemoryBufSz += bytes2write;

                if (byteswritten < writesz) { //only get more if we really need it.
                    currentMemoryBuf = new byte[READ_CHUNK_SZ];
                    currentMemoryBufSz = 0;
                    memorybuflist.add(currentMemoryBuf); //add it to the chain.
                }
            } 
            while (byteswritten < writesz);
        }

        if (null != cachediskstream) { //Write to the out going stream.
            cachediskstream.write(data, 0, length);
        }

        totalsz += writesz;
        return;
    }

    /**
     * This method is a low level write.
     * Close the stream. 
     */
    protected synchronized  void close() throws java.io.IOException {
        if (!closed) {
            closed = true; //Markit as closed.
            if (null != cachediskstream) { //close the disk cache.
                cachediskstream.close();
                cachediskstream = null;
            }
            if (null != memorybuflist) {  //There is a memory buffer.

                if (currentMemoryBufSz > 0){
                    byte[] tmp = new byte[currentMemoryBufSz]; //Get the last buffer and make it the sizeof the actual data.
                    System.arraycopy(currentMemoryBuf, 0, tmp, 0,
                       currentMemoryBufSz);
                    memorybuflist.set( memorybuflist.size() - 1, tmp);  //Now replace the last buffer with this size.
                 }else{
                    memorybuflist.remove( memorybuflist.size() - 1);  //Now replace the last buffer with this size.
                 }

                currentMemoryBuf = null; //No need for this anymore.
            }
        }
    }

    /**
     * Routine to flush data to disk if is in memory. 
     */

    protected void flushToDisk() throws java.io.IOException, java.io.FileNotFoundException {
       java.util.LinkedList ml=memorybuflist;
       memorybuflist =null;

       if( ml != null){
           if (null == cachediskstream) { //Need to create a disk cache
                diskCacheFile = java.io.File.createTempFile("Axis", "axis"); //Create a temporary file. TODO allow location to be configurable.
            //    diskCacheFile.deleteOnExit(); //Insurance it goes.
                cachediskstream = new java.io.BufferedOutputStream(
                     new java.io.FileOutputStream(diskCacheFile));
                int listsz = ml.size();
              
                //Write out the entire memory held store to disk.
                for (java.util.Iterator it = ml.iterator();
                       it.hasNext(); ) {
                    byte[] rbuf = (byte[]) it.next();
                    int bwrite = listsz-- == 0 ? currentMemoryBufSz :
                       rbuf.length;

                    cachediskstream.write(rbuf, 0, bwrite);
                    if(closed){
                        cachediskstream.close();
                        cachediskstream= null;
                    }
                }
                ml= null;
            }
        }
    }

    /** Inner class to handle getting an input stream to this data source
     *  Handles creating an input stream to the source.
     */
    private class Instream extends java.io.InputStream {
        protected int bread = 0; //bytes read
        java.io.FileInputStream fin = null;  //The real stream.
        int currentIndex = 0;  //The position in the list were we are reading from.
        byte[] currentBuf = null; //the buffer we are currently reading from.
        int currentBufPos = 0; //The current position in there.

        public int available() throws java.io.IOException {
            return totalsz - bread;
        }

        /**
         * Read a byte from the stream.
         * @param byte to read or -1 if no more data.
         */
        public int read() throws java.io.IOException {
            synchronized (ManagedMemoryDataSource.this){
              byte[]retb = new byte[1];
              int br = read(retb, 0, 1);

              if (br == -1) return -1;
              return retb[0];
            }
        }
        /**
         * Not supported.
         */
        public boolean markSupported() {
            return false;
        }

        /**
         * Skip bytes in the stream.
         * @param the number of bytes to skip.
         */

        public long skip(long skipped) throws java.io.IOException {
            synchronized (ManagedMemoryDataSource.this){
                if ( skipped < 1) return 0; //nothing to skip.

                skipped = Math.min(skipped, totalsz - bread);//only skip what we've read. 
                if(skipped == 0) return 0;
                java.util.List ml = memorybuflist; //hold the memory list.
                int bwritten = 0;

                if ( ml != null) {
                    if ( null == currentBuf ) { //get the buffer we need to read from.
                        currentBuf = (byte[]) ml.get(currentIndex);
                        currentBufPos = 0; //start reading from the begining.
                    }
                    do {
                        long bcopy = Math.min(currentBuf.length - currentBufPos,
                        skipped - bwritten);

                        bwritten += bcopy;
                        currentBufPos += bcopy;
                        if (bwritten < skipped) {
                            currentBuf = (byte[]) ml.get(++currentIndex);
                            currentBufPos = 0;
                        }
                    }
                    while ( bwritten < skipped);
                }
                if (null != fin) fin.skip(skipped);
                bread += skipped;
                return skipped;
            }
        }

        /**
         * Read from the stream. 
         * @param b the data buffer to write to. 
         * @param off the offset in the buffer to write to
         * @param len the number of bytes to write to the buffer.
         */

        public int read(byte[] b, int off, int len) throws java.io.IOException {
            if (b == null) throw new NullPointerException(
                 "input buffer is null");
            if (off < 0)  throw new IndexOutOfBoundsException
                ("Offset is negative: " + off);
            if (len < 0)  throw new IndexOutOfBoundsException("Length: " + len);
            if (len + off > b.length) throw new IndexOutOfBoundsException(
                     "Write beyond buffer");
            if (len == 0) return 0;

            synchronized(ManagedMemoryDataSource.this){
                if (closed && bread == totalsz) return -1;
                len = Math.min(len, totalsz - bread); //Only return the number of bytes in the data store that is left.
                java.util.List ml = memorybuflist;

                int bwritten = 0;

                if ( ml != null) {
                    if ( null == currentBuf ) { //Get the buffer we need to read from.
                        currentBuf = (byte[]) ml.get(currentIndex);
                        currentBufPos = 0; //New buffer start from the begining.
                    }
                    do {
                        //The bytes to copy, the minimum of the bytes left in this buffer or bytes remaining. 
                        int bcopy = Math.min(currentBuf.length - currentBufPos, len - bwritten);

                        //Copy the data.
                        System.arraycopy(currentBuf, currentBufPos , b,
                            off + bwritten , bcopy );
                        bwritten += bcopy;
                        currentBufPos += bcopy;

                        if (bwritten < len) { //Get the next buffer.
                            currentBuf = (byte[]) ml.get(++currentIndex);
                            currentBufPos = 0;
                        }
                    }
                    while ( bwritten < len);
                }

                if (bwritten == 0 && null != diskCacheFile) {
                    if (null != fin ) { //we are no reading from disk.
                        fin = new java.io.FileInputStream( diskCacheFile);
                        fin.skip(bread); //Skip what we've read so far.
                    }

                    if(cachediskstream  != null){
                     cachediskstream.flush();  
                    }

                    bwritten = fin.read(b, len, off);
                }
                if ( bwritten > 0) bread += bwritten;
                return bwritten;
            }
        }

    }//endof innerclass Instream 


    //Used to test.
    public static void main( String arg[]) { //test
        try {
            String readFile = arg[0];
            String writeFile = arg[1];
            java.io.FileInputStream ss = new java.io.FileInputStream(readFile);

            ManagedMemoryDataSource ms = new ManagedMemoryDataSource( ss,
             1024 * 1024,  "foo/data",  true);
            javax.activation.DataHandler dh = new javax.activation.DataHandler
                      (ms);
            java.io.InputStream is = dh.getInputStream();
            java.io.FileOutputStream fo =
                 new java.io.FileOutputStream(writeFile);
            byte[] buf = new byte[512];
            int read = 0;

            do {
                read = is.read(buf);
                if (read > 0) fo.write(buf, 0, read);

            }
            while (read > -1);
            fo.close();
            is.close();

        }
        catch ( java.lang.Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
