/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
    private static final byte[] LF = "\r".getBytes();
    private static final byte[] CR = "\n".getBytes();
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
        return (out == null) ? xmlString : out.toString();
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
