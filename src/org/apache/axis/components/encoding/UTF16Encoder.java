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
 * UTF-16 Encoder.
 *
 * @author <a href="mailto:jens@void.fm">Jens Schumann</a>
 *
 * @see <a href="http://encoding.org">encoding.org</a>
 * @see <a href="http://czyborra.com/utf/#UTF-16">UTF 16 explained</a>
 *
 */
class UTF16Encoder extends AbstractXMLEncoder {
    public String getEncoding() {
        return XMLEncoderFactory.ENCODING_UTF_16;
    }

    public boolean needsEncoding(char c) {
        return c > 0xFFFF;
    }

    public void appendEncoded(EncodedByteArray out, char c) {
        if (c > 0xFFFF) {
            out.append((0xD7C0 + (c >> 10)));
            out.append((0xDC00 | c & 0x3FF));
        }
    }
}
