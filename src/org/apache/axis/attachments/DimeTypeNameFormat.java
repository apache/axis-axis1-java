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

/**
 * @author Rick Rineholt 
 */

package org.apache.axis.attachments;


import org.apache.axis.components.i18n.Messages;
import org.apache.axis.utils.JavaUtils;


/**
 * This class is a single part for DIME mulitpart message. 
 */


public final class DimeTypeNameFormat {
    private byte format = 0; 
    private DimeTypeNameFormat() {}; 
    private DimeTypeNameFormat(byte f) {
        format = f;
    }; 
    //Current type values.
    static final byte NOCHANGE_VALUE = 0x00; // indicates the type is unchanged from the previous record (used for chunking)
    static final byte MIME_VALUE = 0x01; //indicates the type is specified as a MIME media-type
    static final byte URI_VALUE = 0x02; // indicates the type is specified as an absolute URI
    static final byte UNKNOWN_VALUE = 0x03; // indicates the type is not specified
    static final byte NODATA_VALUE = 0x04; // indicates the record has no payload

    static final DimeTypeNameFormat NOCHANGE =
      new DimeTypeNameFormat(NOCHANGE_VALUE);

    public static final DimeTypeNameFormat MIME=
      new DimeTypeNameFormat(MIME_VALUE);

    public static final DimeTypeNameFormat URI=
      new DimeTypeNameFormat(URI_VALUE);

    public static final DimeTypeNameFormat UNKNOWN=
      new DimeTypeNameFormat(UNKNOWN_VALUE);

    static final DimeTypeNameFormat NODATA=
      new DimeTypeNameFormat(NODATA_VALUE);

    private static String[] toEnglish = {"NOCHANGE", "MIME", "URI",
      "UNKNOWN", "NODATA"};
    private static DimeTypeNameFormat[] fromByte = {NOCHANGE, MIME,
      URI, UNKNOWN, NODATA};

    public final String toString() {
        return toEnglish[format];
    }

    public final byte toByte() {
        return format;
    }

    public final boolean equals(final Object x) {
        if (x == null)  {
            return false;
        }
        if (!(x instanceof DimeTypeNameFormat)) {
            return false;
        }
        return ((DimeTypeNameFormat) x).format == this.format;
    }

    public static DimeTypeNameFormat parseByte(byte x) {
        if (x < 0 || x > fromByte.length) {
            throw new IllegalArgumentException(Messages.getMessage(
                        "attach.DimeStreamBadType", "" + x));
        }
        return fromByte[x];
    }

    public static DimeTypeNameFormat parseByte(Byte x) {
        return parseByte(x.byteValue());
    }
}
