package org.apache.axis2.message.adapters;

import org.apache.axis2.message.SOAPDocument;

final public class ToStringAdapter {
    private ToStringAdapter() {}
    public static String convert(SOAPDocument message) {
        return message.toXML();
    }
}

