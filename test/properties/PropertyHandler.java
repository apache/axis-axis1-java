/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package test.properties;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

/**
 * Combination of two functions for this test.
 *
 * 1) When invoked as a Handler, save the value of the PROP_NAME property
 *    into a field so the test can look at it later.  (used to test client-
 *    side)
 *
 * 2) When used as a back-end service, return the value of the PROP_NAME
 *    property (used to test server-side)
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class PropertyHandler extends BasicHandler {
    private String propVal;

    public void invoke(MessageContext msgContext) throws AxisFault {
        // Get the "normal" property value, and save it away.
        propVal = msgContext.getStrProp(TestScopedProperties.PROP_NAME);

        // Set the "override" property directly in the MC.
        msgContext.setProperty(TestScopedProperties.OVERRIDE_NAME,
                               TestScopedProperties.OVERRIDE_VALUE);
    }

    public String getPropVal() {
        return propVal;
    }

    public void setPropVal(String propVal) {
        this.propVal = propVal;
    }

    public String testScopedProperty() throws Exception {
        MessageContext context = MessageContext.getCurrentContext();
        String propVal = context.getStrProp(TestScopedProperties.PROP_NAME);
        return propVal;
    }

    public String testOverrideProperty() throws Exception {
        MessageContext context = MessageContext.getCurrentContext();
        String propVal = context.getStrProp(TestScopedProperties.OVERRIDE_NAME);
        return propVal;
    }
}
