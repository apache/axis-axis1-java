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

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.Vector;

/**
 * A simple implementation for Nodelist Support in AXIS
 * 
 * @author Heejune Ahn (cityboy@tmax.co.kr)
 *  
 */

class NodeListImpl implements NodeList {
    Vector mNodes;

    /**
     * Constructor and Setter is intensionally made package access only.
     *  
     */
    NodeListImpl() {
        mNodes = new Vector();
    }

    NodeListImpl(Vector nodes) {
        mNodes = nodes;
    }

    void addNode(org.w3c.dom.Node node) {
        mNodes.add(node);
    }

    void addNodeList(org.w3c.dom.NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            mNodes.add(nodes.item(i));
        }
    }

    /**
     * Interface Implemented
     * 
     * @param index
     * @return
     */
    public Node item(int index) {
        if (mNodes != null && mNodes.size() > index) {
            return (Node) mNodes.get(index);
        } else {
            return null;
        }
    }

    public int getLength() {
        return mNodes.size();
    }

}
