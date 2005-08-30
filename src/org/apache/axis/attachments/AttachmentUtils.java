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
package org.apache.axis.attachments;

import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.utils.Messages;

import javax.activation.DataHandler;

/**
 * This class allow access to the Jaf data handler in AttachmentPart.
 *
 * @author Rick Rineholt
 */
public class AttachmentUtils {

    private AttachmentUtils() {
    }
    // no one should create.

    /**
     * Obtain the DataHandler from the part.
     * @param part the part containing the Java Activiation Framework data source.
     * @return The Java activation data handler.
     *
     * @throws AxisFault
     */
    public static DataHandler getActivationDataHandler(Part part)
            throws AxisFault {

        if (null == part) {
            throw new AxisFault(Messages.getMessage("gotNullPart"));
        }

        if (!(part instanceof AttachmentPart)) {
            throw new AxisFault(
                    Messages.getMessage(
                            "unsupportedAttach", part.getClass().getName(),
                            AttachmentPart.class.getName()));
        }

        return ((AttachmentPart) part).getActivationDataHandler();
    }

    /**
     * Determine if an object is to be treated as an attchment.
     *
     * @param value the value that is to be determined if
     * its an attachment.
     *
     * @return True if value should be treated as an attchment.
     */
    public static boolean isAttachment(Object value) {

        if (null == value) {
            return false;
        }

        return value instanceof javax.activation.DataHandler;
    }
}
