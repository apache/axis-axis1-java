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

import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MimeMultipartDataSource implements DataSource {
    public static final String CONTENT_TYPE = "multipart/mixed";

    private final String name;
    private final String contentType;
    private byte[] data;
    private ByteArrayOutputStream os;

    public MimeMultipartDataSource(String name, MimeMultipart data) {
        this.name = name;
        this.contentType = data == null ? CONTENT_TYPE : data.getContentType();
        os = new ByteArrayOutputStream();
        try {
            if (data != null) {
                data.writeTo(os);
            }
        }
        catch (Exception e) {
            // Is this sufficient?
        }
    } // ctor

    public String getName() {
        return name;
    } // getName

    public String getContentType() {
        return contentType;
    } // getContentType

    public InputStream getInputStream() throws IOException {
        if (os.size() != 0) {
            data = os.toByteArray();
            os.reset();
        }
        return new ByteArrayInputStream(data == null ? new byte[0] : data);
    } // getInputStream

    public OutputStream getOutputStream() throws IOException {
        if (os.size() != 0) {
            data = os.toByteArray();
            os.reset();
        }
        return os;
    } // getOutputStream
} // class MimeMultipartDataSource
