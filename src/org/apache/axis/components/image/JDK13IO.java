/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.utils.Messages;
import sun.awt.image.codec.JPEGImageEncoderImpl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * JDK1.3 based Image I/O
 *
 * @author <a href="mailto:butek@us.ibm.com">Russell Butek</a>
 */
public class JDK13IO extends Component implements ImageIO {
    /**
     * Save an image.
     * @param mimeType the mime-type of the format to save the image
     * @param image the image to save
     * @param filename the file to write to
     * @exception JimiException if an error prevents image encoding
     */
    public void saveImage(String mimeType, Image image, OutputStream os)
            throws Exception {

        BufferedImage rendImage = null;

        // Create a BufferedImage
        if (image instanceof BufferedImage) {
            rendImage = (BufferedImage) image;
        } else {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(image, 0);
            tracker.waitForAll();
            rendImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
            Graphics g = rendImage.createGraphics();
            g.drawImage(image, 0, 0, null);
        }

        // Write the image to the output stream
        if ("image/jpeg".equals(mimeType)) {
            JPEGImageEncoderImpl j = new JPEGImageEncoderImpl(os);
            j.encode(rendImage);
        }
        else {
            throw new IOException(Messages.getMessage("jpegOnly", mimeType));
        }
    } // saveImage

    /**
     * Load an Image.
     * @param in the stream to load the image
     * @return the Image
     */
    public Image loadImage(InputStream in) throws Exception {
        if (in.available() <= 0) {
            return null;
        }
        else {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return Toolkit.getDefaultToolkit().createImage(bytes);
        }
    } // loadImage
}

