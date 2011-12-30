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

package org.apache.axis;

/**
 * Base interface for doing an action to Handlers with a MessageContext.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public interface HandlerIterationStrategy {
    /**
     * Visit a handler with a message context.
     *
     * @param handler       the <code>Handler</code> to apply to the context
     * @param msgContext    the <code>MessageContext</code> used
     * @throws AxisFault    if this visit encountered an error
     */
    public void visit(Handler handler, MessageContext msgContext)
        throws AxisFault;
}
