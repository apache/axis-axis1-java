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

import com.sun.jimi.core.Jimi;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * JIMI based Image I/O
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class JimiIO implements ImageIO {
    /**
     * Save an image.
     * @param id the mime-type of the format to save the image
     * @param image the image to save
     * @param filename the file to write to
     * @exception JimiException if an error prevents image encoding
     */

    public void saveImage(String id, Image image, OutputStream os)
            throws Exception {
        Jimi.putImage(id, image, os);
    }

    /**
     * Load an Image.
     * @param in the stream to load the image
     * @return the Image
     */
    public Image loadImage(InputStream in) throws Exception {
        return Jimi.getImage(in);
    }
}

