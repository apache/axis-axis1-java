package org.apache.axis.message.adapters;

import org.apache.axis.message.SOAPDocument;

final public class ToStringAdapter {
    public ToStringAdapter() {}
    public String convert(SOAPDocument message) {
        return message.toXML();
    }
}

