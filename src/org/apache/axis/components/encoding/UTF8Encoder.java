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
package org.apache.axis.components.encoding;

/**
 * UTF-8 Encoder.
 *
 * @author <a href="mailto:jens@void.fm">Jens Schumann</a>
 *
 * @see <a href="http://encoding.org">encoding.org</a>
 * @see <a href="http://czyborra.com/utf/#UTF-8">UTF 8 explained</a>
 *
 */
class UTF8Encoder extends AbstractXMLEncoder {
    public String getEncoding() {
        return XMLEncoderFactory.ENCODING_UTF_8;
    }

    public boolean needsEncoding(char c) {
        return c > 0x7F;
    }

    public void appendEncoded(EncodedByteArray out, char c) {
        if (c < 0x80) {
            out.append(c);
        } else if (c < 0x800) {
            out.append((0xC0 | c >> 6));
            out.append((0x80 | c & 0x3F));
        } else if (c < 0x10000) {
            out.append((0xE0 | c >> 12));
            out.append((0x80 | c >> 6 & 0x3F));
            out.append((0x80 | c & 0x3F));
        } else if (c < 0x200000) {
            out.append((0xF0 | c >> 18));
            out.append((0x80 | c >> 12 & 0x3F));
            out.append((0x80 | c >> 6 & 0x3F));
            out.append((0x80 | c & 0x3F));
        }
    }
}
