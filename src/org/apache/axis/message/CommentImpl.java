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

package org.apache.axis.message;

import javax.xml.soap.Text;

import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;

/**
 * Most of methods are inherited from TEXT, defined for its Interface Marker
 * only
 * 
 * @author Heejune Ahn (cityboy@tmax.co.kr)
 */

public class CommentImpl
    extends org.apache.axis.message.Text
    implements Text, Comment {

    public CommentImpl(String text) {
        super(text);
    }
    
    public boolean isComment() {
        return true;
    }

    public org.w3c.dom.Text splitText(int offset) throws DOMException {
        int length = textRep.getLength();
        // take the first part, and save the second part for new Text
        // length check and exception will be thrown here, no need to
        // duplicated check
        String tailData = textRep.substringData(offset, length);
        textRep.deleteData(offset, length);

        // insert the first part again as a new node
        Text tailText = new CommentImpl(tailData);
        org.w3c.dom.Node myParent = (org.w3c.dom.Node) getParentNode();
        if (myParent != null) {
            org.w3c.dom.NodeList brothers =
                (org.w3c.dom.NodeList) myParent.getChildNodes();
            for (int i = 0; i < brothers.getLength(); i++) {
                if (brothers.item(i).equals(this)) {
                    myParent.insertBefore(tailText, this);
                    return tailText;
                }
            }
        }
        return tailText;
    }

}
