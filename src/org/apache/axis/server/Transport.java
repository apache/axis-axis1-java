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

package org.apache.axis.server;

import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Transport is a targeted chain that knows it's a transport.
 *
 * This is purely for deployment naming at this point.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class Transport extends SimpleTargetedChain
{
    protected static Log log =
        LogFactory.getLog(Transport.class.getName());

}
