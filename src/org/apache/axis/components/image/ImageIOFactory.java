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

package org.apache.axis.components.image;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

/**
 * This class implements a factory to instantiate an ImageIO component.
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class ImageIOFactory {
    protected static Log log =
            LogFactory.getLog(ImageIOFactory.class.getName());

    static {
        AxisProperties.setClassOverrideProperty(ImageIO.class, "axis.ImageIO");

        // If the imageIO is not configured look for the following:
        // 1.  Try the JDK 1.4 classes
        // 2.  Try the JIMI classes
        // 3.  If all else fails, try the JDK 1.3 classes
        AxisProperties.setClassDefaults(ImageIO.class,
                                        new String [] {
                                             "org.apache.axis.components.image.MerlinIO",
                                             "org.apache.axis.components.image.JimiIO",
                                             "org.apache.axis.components.image.JDK13IO",
                                        });
    }

    /**
     * Get the ImageIO implementation.  This method follows a precedence:
     * 1.  Use the class defined by the System property axis.ImageIO.
     * 2.  If that isn't set, try instantiating MerlinIO, the JDK 1.4 ImageIO implementation.
     * 3.  If that fails try JimiIO, the JIMI ImageIO implementation.
     * 4.  If that fails, instantiate the limited JDK13IO implementation.
     */
    public static ImageIO getImageIO() {
        ImageIO imageIO = (ImageIO)AxisProperties.newInstance(ImageIO.class);
        
        /**
         * This shouldn't be needed, but seems to be a common feel-good:
         */
        if (imageIO == null) {
            imageIO = new JDK13IO();
        }

        log.debug("axis.ImageIO: " + imageIO.getClass().getName());
        return imageIO;
    }
}

