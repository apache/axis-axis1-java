/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.components.image;

import org.apache.axis.AxisProperties;
import org.apache.axis.utils.ClassUtils;

import org.apache.commons.discovery.tools.SPInterface;

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

