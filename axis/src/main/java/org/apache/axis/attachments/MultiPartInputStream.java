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

package org.apache.axis.attachments;


import org.apache.axis.Part;


/**
 * This simulates the multipart stream.
 *
 * @author Rick Rineholt
 */
public abstract class MultiPartInputStream extends
  java.io.FilterInputStream {

    MultiPartInputStream(java.io.InputStream is) {
        super(is);
    }

    public abstract Part getAttachmentByReference(final String[] id)
     throws org.apache.axis.AxisFault;

    public abstract java.util.Collection getAttachments()
      throws org.apache.axis.AxisFault;

    /**
     * Return the content location.
     * @return the Content-Location of the stream.
     *   Null if no content-location specified.
     */
    public abstract String getContentLocation();

    /**
     * Return the content id of the stream.
     * 
     * @return the Content-Location of the stream.
     *   Null if no content-location specified.
     */
    public abstract String getContentId();

}
