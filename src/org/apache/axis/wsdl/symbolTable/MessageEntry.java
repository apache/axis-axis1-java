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
package org.apache.axis.wsdl.symbolTable;

import javax.wsdl.Message;

/**
 * This class represents a WSDL message.  It simply encompasses the WSDL4J Message object so it can
 * reside in the SymbolTable.
 */
public class MessageEntry extends SymTabEntry {

    /** Field message */
    private Message message;

    /**
     * Construct a MessageEntry from a WSDL4J Message object.
     * 
     * @param message 
     */
    public MessageEntry(Message message) {

        super(message.getQName());

        this.message = message;
    }    // ctor

    /**
     * Get this entry's Message object.
     * 
     * @return 
     */
    public Message getMessage() {
        return message;
    }    // getMessage
}    // class MessageEntry
