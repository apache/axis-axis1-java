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
package org.apache.axis.components.encoding;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.logging.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for XMLEncoder
 * 
 * @author <a href="mailto:jens@void.fm">Jens Schumann</a>
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 */
public class XMLEncoderFactory {
    protected static Log log = LogFactory.getLog(XMLEncoderFactory.class.getName());
    
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String ENCODING_UTF_16 = "UTF-16";
    public static final String DEFAULT_ENCODING = ENCODING_UTF_8;
    
    private static Map encoderMap = new HashMap();
    private static final String PLUGABLE_PROVIDER_FILENAME = "org.apache.axis.components.encoding.XMLEncoder";

    static {
        encoderMap.put(ENCODING_UTF_8, new UTF8Encoder());
        encoderMap.put(ENCODING_UTF_16, new UTF16Encoder());
        try {
            loadPluggableEncoders();
        } catch (Throwable t){
            String msg=t + JavaUtils.LS + JavaUtils.stackToString(t);
            log.info(Messages.getMessage("exception01",msg));
        }
    }

    /** 
     * Returns the default encoder
     * @return
     */
    public static XMLEncoder getDefaultEncoder() {
        try {
            return getEncoder(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            //  as far I know J++ VMs will throw this exception
            throw new IllegalStateException(Messages.getMessage("unsupportedDefaultEncoding00", DEFAULT_ENCODING));
        }
    }

    /**
     * Returns the requested encoder
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    public static XMLEncoder getEncoder(String encoding) throws UnsupportedEncodingException {
        XMLEncoder encoder = (XMLEncoder) encoderMap.get(encoding.toUpperCase());
        if (encoder == null) {
            throw new UnsupportedEncodingException(Messages.getMessage("unsupportedEncoding00", encoding));
        }
        // test local encoding so we can ensure that getBytes()
        // will not fail later
        "test".getBytes(encoder.getEncoding());
        return encoder;
    }

    /**
     Look for file META-INF/services/org.apache.axis.components.encoding.XMLEncoder
     in all the JARS, get the classes listed in those files and add them to 
     encoders list if they are valid encoders. 

     Here is how the scheme would work.

     A company providing a new encoder will jar up their encoder related
     classes in a JAR file. The following file containing the name of the new 
     encoder class is also made part of this JAR file. 

     META-INF/services/org.apache.axis.components.encoding.XMLEncoder

     By making this JAR part of the webapp, the new encoder will be 
     automatically discovered. 
     */
    private static void loadPluggableEncoders() {
        ClassLoader clzLoader = XMLEncoder.class.getClassLoader();
        ClassLoaders loaders = new ClassLoaders();
        loaders.put(clzLoader);
        DiscoverServiceNames dsn = new DiscoverServiceNames(loaders);
        ResourceNameIterator iter = dsn.findResourceNames(PLUGABLE_PROVIDER_FILENAME);
        while (iter.hasNext()) {
            String className = iter.nextResourceName();
            try {
                Object o = Class.forName(className).newInstance();
                if (o instanceof XMLEncoder) {
                    XMLEncoder encoder = (XMLEncoder) o;
                    encoderMap.put(encoder.getEncoding(), encoder);
                }
            } catch (Exception e) {
                String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
                log.info(Messages.getMessage("exception01", msg));
                continue;
            }
        }
    }
}
