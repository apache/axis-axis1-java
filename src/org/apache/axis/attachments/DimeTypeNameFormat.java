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

/**
 * @author Rick Rineholt 
 */

package org.apache.axis.attachments;


import org.apache.axis.utils.Messages;


/**
 * This class is a single part for DIME mulitpart message. 
 */


public final class DimeTypeNameFormat {
    private byte format = 0; 
    private DimeTypeNameFormat() {}
    private DimeTypeNameFormat(byte f) {
        format = f;
    }
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

    public int hashCode() {
        return (int) format;
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
