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
package org.apache.axis.utils;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisProperties;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class ByteArray
 */
public class ByteArray extends OutputStream {

    protected static double DEFAULT_CACHE_INCREMENT = 2.5;
    protected static int DEFAULT_RESIDENT_SIZE = 512 * 1024 * 1024; // 512 MB
    protected static boolean DEFAULT_ENABLE_BACKING_STORE = true;
    protected static int WORKING_BUFFER_SIZE = 8192;

    protected byte cache[] = null;
    protected int cache_fp = 0;
    protected double cache_increment = DEFAULT_CACHE_INCREMENT;
    protected int max_size = 0;
    protected File bs_handle = null;
    protected OutputStream bs_stream = null;
    protected long count = 0;
    protected boolean enableBackingStore = DEFAULT_ENABLE_BACKING_STORE;

    public boolean isEnableBackingStore() {
      return enableBackingStore;
    }

    public void setEnableBackingStore(boolean enableBackingStore) {
      this.enableBackingStore = enableBackingStore;
    }

    public static boolean isDEFAULT_ENABLE_BACKING_STORE() {
      return DEFAULT_ENABLE_BACKING_STORE;
    }

    public static void setDEFAULT_ENABLE_BACKING_STORE(boolean DEFAULT_ENABLE_BACKING_STORE) {
      ByteArray.DEFAULT_ENABLE_BACKING_STORE = DEFAULT_ENABLE_BACKING_STORE;
    }

    public static int getDEFAULT_RESIDENT_SIZE() {
      return DEFAULT_RESIDENT_SIZE;
    }

    public static void setDEFAULT_RESIDENT_SIZE(int DEFAULT_RESIDENT_SIZE) {
      ByteArray.DEFAULT_RESIDENT_SIZE = DEFAULT_RESIDENT_SIZE;
    }

    public static double getDEFAULT_CACHE_INCREMENT() {
      return DEFAULT_CACHE_INCREMENT;
    }

    public static void setDEFAULT_CACHE_INCREMENT(double DEFAULT_CACHE_INCREMENT) {
      ByteArray.DEFAULT_CACHE_INCREMENT = DEFAULT_CACHE_INCREMENT;
    }



    static {
      String value;

      value = AxisProperties.getProperty(AxisEngine.PROP_BYTE_BUFFER_CACHE_INCREMENT,
                                                ""+DEFAULT_CACHE_INCREMENT);
      DEFAULT_CACHE_INCREMENT=Double.parseDouble(value);

      value = AxisProperties.getProperty(AxisEngine.PROP_BYTE_BUFFER_RESIDENT_MAX_SIZE,
                                                ""+DEFAULT_RESIDENT_SIZE);
      DEFAULT_RESIDENT_SIZE=Integer.parseInt(value);

      value = AxisProperties.getProperty(AxisEngine.PROP_BYTE_BUFFER_WORK_BUFFER_SIZE,
                                                ""+WORKING_BUFFER_SIZE);
      WORKING_BUFFER_SIZE=Integer.parseInt(value);

      value = AxisProperties.getProperty(AxisEngine.PROP_BYTE_BUFFER_BACKING,
                                                ""+DEFAULT_ENABLE_BACKING_STORE);
      if (value.equalsIgnoreCase("true") ||
          value.equals("1") ||
          value.equalsIgnoreCase("yes") )
      {
        DEFAULT_ENABLE_BACKING_STORE=true;
      }
      else {
        DEFAULT_ENABLE_BACKING_STORE=false;
      }
    }

    /**
     * Constructor ByteArray
     */
    public ByteArray() {
        this(DEFAULT_RESIDENT_SIZE);
    }

    /**
     * Constructor ByteArray
     * 
     * @param max_resident_size 
     */
    public ByteArray(int max_resident_size) {
        this(0, max_resident_size);
    }

    /**
     * Constructor ByteArray
     * 
     * @param probable_size     
     * @param max_resident_size 
     */
    public ByteArray(int probable_size, int max_resident_size) {
        if (probable_size > max_resident_size) {
            probable_size = 0;
        }
        if (probable_size < WORKING_BUFFER_SIZE) {
            probable_size = WORKING_BUFFER_SIZE;
        }
        cache = new byte[probable_size];
        max_size = max_resident_size;
    }

    /**
     * Method write
     * 
     * @param bytes 
     * @throws IOException 
     */
    public void write(byte bytes[]) throws IOException {
        count += bytes.length;
        write(bytes, 0, bytes.length);
    }

    /**
     * Method write
     * 
     * @param bytes  
     * @param start  
     * @param length 
     * @throws IOException 
     */
    public void write(byte bytes[], int start, int length) throws IOException {
        count += length;
        if (cache != null) {
            increaseCapacity(length);
        }
        if (cache != null) {
            System.arraycopy(bytes, start, cache, cache_fp, length);
            cache_fp += length;
        } else if (bs_stream!=null) {
            bs_stream.write(bytes, start, length);
        }
        else {
          throw new IOException("ByteArray does not have a backing store!");
        }
    }

    /**
     * Method write
     * 
     * @param b 
     * @throws IOException 
     */
    public void write(int b) throws IOException {
        count += 1;
        if (cache != null) {
            increaseCapacity(1);
        }
        if (cache != null) {
            cache[cache_fp++] = (byte) b;
        } else if (bs_stream!=null) {
            bs_stream.write(b);
        }
        else {
          throw new IOException("ByteArray does not have a backing store!");
        }
    }

    /**
     * Method close
     * 
     * @throws IOException 
     */
    public void close() throws IOException {
        if (bs_stream != null) {
            bs_stream.close();
            bs_stream = null;
        }
    }

    /**
     * Method size
     * 
     * @return 
     */
    public long size() {
        return count;
    }

    /**
     * Method flush
     * 
     * @throws IOException 
     */
    public void flush() throws IOException {
        if (bs_stream != null) {
            bs_stream.flush();
        }
    }

    /**
     * Method increaseCapacity
     * 
     * @param count 
     * @throws IOException 
     */
    protected void increaseCapacity(int count) throws IOException {
        if (cache == null) {
            return;
        }
        int new_fp = cache_fp + count;
        if (new_fp < cache.length) {
            return;
        }

        if (new_fp < max_size) {
            grow(count);
        }
        else if (enableBackingStore) {
            switchToBackingStore();
        }
        else {
            throw new IOException("ByteArray can not increase capacity by "+count+
                " due to max size limit of "+max_size);
        }
    }

    /**
     * Method growMemCache
     * 
     * @param count 
     * @throws IOException 
     */
    protected void grow(int count) throws IOException {
        int new_fp = cache_fp + count;
        int new_size = (int) (cache.length * cache_increment);
        if (new_size < cache_fp + WORKING_BUFFER_SIZE) {
            new_size = cache_fp + WORKING_BUFFER_SIZE;
        }
        if (new_size < new_fp) {
            new_size = new_fp;
        }
        try {
            byte new_mem_cache[] = new byte[new_size];
            System.arraycopy(cache, 0, new_mem_cache, 0, cache_fp);
            cache = new_mem_cache;
        } catch (OutOfMemoryError e) {
            // Couldn't allocate a new, bigger vector!
            // That's fine, we'll just switch to backing-store mode.
            if (enableBackingStore) {
              switchToBackingStore();
            }
            else {
              throw new IOException("ByteArray exhausted memory: "+e.getMessage());
            }
        }
    }

    /**
     * Method discardBuffer
     */
    public synchronized void discardBuffer() {
        cache = null;
        cache_fp = 0;
        if (bs_stream != null) {
            try {
                bs_stream.close();
            } catch (IOException e) {
                // just ignore it...
            }
            bs_stream = null;
        }
        discardBackingStore();
    }

    /**
     * Method makeInputStream
     * 
     * @return 
     * @throws IOException           
     * @throws FileNotFoundException 
     */
    protected InputStream makeInputStream()
            throws IOException, FileNotFoundException {
        close();
        if (cache != null) {
            byte[] v = cache;
            int fp = cache_fp;
            return new ByteArrayInputStream(v, 0, fp);
        } else if (bs_handle != null) {
            return createBackingStoreInputStream();
        } else {
            return null;
        }
    }

    /**
     * Method finalize
     */
    protected void finalize() {
        discardBuffer();
    }

    /**
     * Method switchToBackingStore
     * 
     * @throws IOException 
     */
    protected void switchToBackingStore() throws IOException {
        bs_handle = File.createTempFile("Axis", ".msg");
        bs_handle.createNewFile();
        bs_handle.deleteOnExit();
        bs_stream = new FileOutputStream(bs_handle);
        if (cache_fp > 0) {
            bs_stream.write(cache, 0, cache_fp);
        }
        cache = null;
        cache_fp = 0;
    }


  /**
   *  Method getBackingStoreFileName
   *
   * @throws IOException
   */
  public String getBackingStoreFileName() throws IOException {
    String fileName = null;
    if (bs_handle!=null) {
      fileName=bs_handle.getCanonicalPath();
    }
    return fileName;
  }

    /**
     * Method discardBackingStore
     */
    protected void discardBackingStore() {
        if (bs_handle != null) {
            bs_handle.delete();
            bs_handle = null;
        }
    }

    /**
     * Method createBackingStoreInputStream
     * 
     * @return 
     * @throws FileNotFoundException 
     */
    protected InputStream createBackingStoreInputStream()
            throws FileNotFoundException {
        try {
            return new BufferedInputStream(
                    new FileInputStream(bs_handle.getCanonicalPath()));
        } catch (IOException e) {
            throw new FileNotFoundException(bs_handle.getAbsolutePath());
        }
    }

    /**
     * Method toByteArray
     * 
     * @return 
     * @throws IOException 
     */
    public byte[] toByteArray() throws IOException {
        InputStream inp = this.makeInputStream();
        byte[] buf = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        buf = new byte[WORKING_BUFFER_SIZE];
        int len;
        while ((len = inp.read(buf, 0, WORKING_BUFFER_SIZE)) != -1) {
            baos.write(buf, 0, len);
        }
        inp.close();
        discardBackingStore();
        return baos.toByteArray();
    }

    /**
     * Method writeTo
     * 
     * @param os 
     * @throws IOException 
     */
    public void writeTo(OutputStream os) throws IOException {
        InputStream inp = this.makeInputStream();
        byte[] buf = null;
        buf = new byte[WORKING_BUFFER_SIZE];
        int len;
        while ((len = inp.read(buf, 0, WORKING_BUFFER_SIZE)) != -1) {
            os.write(buf, 0, len);
        }
        inp.close();
        discardBackingStore();
    }
}
