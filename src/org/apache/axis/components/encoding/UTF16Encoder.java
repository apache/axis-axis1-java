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

import org.apache.axis.i18n.Messages;

import java.io.IOException;
import java.io.Writer;

/**
 * UTF-16 Encoder.
 * 
 * @author <a href="mailto:jens@void.fm">Jens Schumann</a>
 * @see <a href="http://encoding.org">encoding.org</a>
 * @see <a href="http://czyborra.com/utf/#UTF-16">UTF 16 explained</a>
 */
class UTF16Encoder extends AbstractXMLEncoder {
    /**
     * gets the encoding supported by this encoder
     * @return string
     */
    public String getEncoding() {
        return XMLEncoderFactory.ENCODING_UTF_16;
    }

    /**
     * write the encoded version of a given string
     * 
     * @param writer    writer to write this string to
     * @param xmlString string to be encoded
     */
    public void writeEncoded(Writer writer, String xmlString)
            throws IOException {
        if (xmlString == null) {
            return;
        }
        char[] characters = xmlString.toCharArray();
        char character;
        for (int i = 0; i < characters.length; i++) {
            character = characters[i];
            switch (character) {
                // we don't care about single quotes since axis will
                // use double quotes anyway
                case '&':
                    writer.write(AMP);
                    break;
                case '"':
                    writer.write(QUOTE);
                    break;
                case '<':
                    writer.write(LESS);
                    break;
                case '>':
                    writer.write(GREATER);
                    break;
                case '\n':
                    writer.write(LF);
                    break;
                case '\r':
                    writer.write(CR);
                    break;
                case '\t':
                    writer.write(TAB);
                    break;
                default:
                    if (character < 0x20) {
                        throw new IllegalArgumentException(Messages.getMessage(
                                "invalidXmlCharacter00",
                                Integer.toHexString(character),
                                xmlString));
                    } else if (character > 0xFFFF) {
                        writer.write((0xD7C0 + (character >> 10)));
                        writer.write((0xDC00 | character & 0x3FF));
                    } else {
                        writer.write(character);
                    }
                    break;
            }
        }
    }
}
