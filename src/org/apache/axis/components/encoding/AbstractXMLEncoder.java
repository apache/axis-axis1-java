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

import org.apache.axis.utils.Messages;

import java.io.UnsupportedEncodingException;

/**
 *
 * Abstract class for XML String encoders.
 *
 * The new encoding mechanism fixes the following bugs/issues:
 *   http://nagoya.apache.org/bugzilla/show_bug.cgi?id=15133
 *   http://nagoya.apache.org/bugzilla/show_bug.cgi?id=15494
 *   http://nagoya.apache.org/bugzilla/show_bug.cgi?id=19327
 *
 * @author <a href="mailto:jens@void.fm">Jens Schumann</a>
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 *
 */
public abstract class AbstractXMLEncoder implements XMLEncoder {
    private static final byte[] AMP = "&amp;".getBytes();
    private static final byte[] QUOTE = "&quot;".getBytes();
    private static final byte[] LESS = "&lt;".getBytes();
    private static final byte[] GREATER = "&gt;".getBytes();
    private static final byte[] LF = "\n".getBytes();
    private static final byte[] CR = "\r".getBytes();
    private static final byte[] TAB = "\t".getBytes();

    /**
     * Encode a string
     * @param xmlString string to be encoded
     * @return encoded string
     */
    public String encode(String xmlString) {
        if(xmlString == null) {
            return "";
        }
        char[] characters = xmlString.toCharArray();
        EncodedByteArray out = null;
        char character;

        for (int i = 0; i < characters.length; i++) {
            character = characters[i];
            switch (character) {
                // we don't care about single quotes since axis will
                // use double quotes anyway
                case '&':
                    if (out == null) {
                        out = getInitialByteArray(xmlString, i);
                    }
                    out.append(AMP);
                    break;
                case '"':
                    if (out == null) {
                        out = getInitialByteArray(xmlString, i);
                    }
                    out.append(QUOTE);
                    break;
                case '<':
                    if (out == null) {
                        out = getInitialByteArray(xmlString, i);
                    }
                    out.append(LESS);
                    break;
                case '>':
                    if (out == null) {
                        out = getInitialByteArray(xmlString, i);
                    }
                    out.append(GREATER);
                    break;
                case '\n':
                    if (out == null) {
                        out = getInitialByteArray(xmlString, i);
                    }
                    out.append(LF);
                    break;
                case '\r':
                    if (out == null) {
                        out = getInitialByteArray(xmlString, i);
                    }
                    out.append(CR);
                    break;
                case '\t':
                    if (out == null) {
                        out = getInitialByteArray(xmlString, i);
                    }
                    out.append(TAB);
                    break;
                default:
                    if (character < 0x20) {
                        throw new IllegalArgumentException(Messages.getMessage("invalidXmlCharacter00", Integer.toHexString(character), xmlString));
                    } else if (needsEncoding(character)) {
                        if (out == null) {
                            out = getInitialByteArray(xmlString, i);
                        }
                        appendEncoded(out, character);
                    } else {
                        if (out != null) {
                            out.append(character);
                        }
                    }
                    break;
            }
        }
        if (out == null) {
            return xmlString;
        }
        try {
            return out.toString(getEncoding());
        } catch (UnsupportedEncodingException e) {
            // we tested it ealier, should work.
            throw new IllegalStateException(Messages.getMessage("encodingDisappeared00", getEncoding()));
        }
    }

    public abstract String getEncoding();

    public abstract boolean needsEncoding(char c);

    public abstract void appendEncoded(EncodedByteArray out, char c);

    private EncodedByteArray getInitialByteArray(String aXmlString, int pos) {
        try {
            return new EncodedByteArray(aXmlString.getBytes(getEncoding()), 0, pos);
        } catch (UnsupportedEncodingException e) {
            // we tested it ealier, should work.
            throw new IllegalStateException(Messages.getMessage("encodingDisappeared00", getEncoding()));
        }
    }
}
