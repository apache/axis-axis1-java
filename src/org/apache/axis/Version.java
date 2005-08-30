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

import org.apache.axis.client.Call;
import org.apache.axis.utils.Messages;

/**
 * Little utility to get the version and build date of the axis.jar.
 *
 * The messages referenced here are automatically kept up-to-date by the
 * build.xml.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class Version {
    /**
     * Get the version of this AXIS.
     *
     * @return the version of this axis
     */
    public static String getVersion()
    {
        return Messages.getMessage("axisVersion") + "\n" +
               Messages.getMessage("builtOn");
    }

    /**
     *  Returns the Axis Version number and build date.
     *  <p>
     *  Example output: 1.1 Jul 08, 2003 (09:00:12 EDT)
     *
     * @return the full version of this axis
     **/
    public static String getVersionText()
    {
        return Messages.getMessage("axisVersionRaw") + " " + Messages.getMessage("axisBuiltOnRaw");
    }

    /**
     * Entry point.
     * <p>
     * Calling this with no arguments returns the version of the client-side
     * axis.jar.  Passing a URL which points to a remote Axis server will
     * attempt to retrieve the version of the server via a SOAP call.
     */
    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println(getVersion());
        else
            try {
                Call call = new Call(args[0]);
                String result = (String)call.invoke("Version", "getVersion",
                                                    null);
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
