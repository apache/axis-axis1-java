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
        encoderMap.put(ENCODING_UTF_8.toLowerCase(), new UTF8Encoder());
        encoderMap.put(ENCODING_UTF_16.toLowerCase(), new UTF16Encoder());
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
        XMLEncoder encoder = (XMLEncoder) encoderMap.get(encoding);
        if (encoder == null) {
            throw new UnsupportedEncodingException(Messages.getMessage("unsupportedEncoding00", encoding));
        }
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
                    encoderMap.put(encoder.getEncoding().toLowerCase(), encoder);
                }
            } catch (Exception e) {
                String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
                log.info(Messages.getMessage("exception01", msg));
                continue;
            }
        }
    }
}
