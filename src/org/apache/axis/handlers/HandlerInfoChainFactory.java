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

package org.apache.axis.handlers;

import javax.xml.rpc.handler.HandlerChain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HandlerInfoChainFactory {

    protected List handlerInfos = new ArrayList();
    protected String[] _roles = null;

    public HandlerInfoChainFactory() {
    }

    public HandlerInfoChainFactory(List handlerInfos) {
        this.handlerInfos = handlerInfos;
    }

    public List getHandlerInfos() {
        return this.handlerInfos;
    }

    public HandlerChain createHandlerChain() {
        HandlerChain hc = new HandlerChainImpl(handlerInfos);
        hc.setRoles(getRoles());
        return hc;
        
    }
    
    public String[] getRoles() {
        return _roles;
    }
    
    public void setRoles(String[] roles) {
        _roles = roles;
    }
    
    public void init(Map map) {
        // DO SOMETHING WITH THIS
    }    
}

