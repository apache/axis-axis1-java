/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Runtime state of the engine
 */
package org.apache.axis.context;

import org.apache.axis.description.OperationDescription;
import org.apache.axis.engine.AxisFault;
import org.apache.wsdl.WSDLConstants;

public class OperationContextFactory implements WSDLConstants {

    public static OperationContext createMEPContext(int mepURI,
                                                    OperationDescription axisOp, ServiceContext serviceContext)
            throws AxisFault {
        if (MEP_CONSTANT_IN_OUT == mepURI || MEP_CONSTANT_IN_ONLY == mepURI
                || MEP_CONSTANT_IN_OPTIONAL_OUT == mepURI
                || MEP_CONSTANT_ROBUST_IN_ONLY == mepURI
                || MEP_CONSTANT_OUT_ONLY == mepURI
                || MEP_CONSTANT_OUT_IN == mepURI
                || MEP_CONSTANT_OUT_OPTIONAL_IN == mepURI
                || MEP_CONSTANT_ROBUST_OUT_ONLY == mepURI) {
            return new OperationContext(axisOp, serviceContext);

        } else {
            throw new AxisFault("Cannot handle the MEP " + mepURI
                    + " for the current invocation of Operation ");
        }
    }

}