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

 package org.apache.axis.configuration;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationProvider;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

/**
 * A simple ConfigurationProvider that uses the Admin class to read +
 * write XML files.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class FileProvider implements ConfigurationProvider
{
    protected String sep = System.getProperty("file.separator");

    String basepath = ".";
    String filename;
    
    InputStream myInputStream = null;
    
    // Should we search the classpath for the file if we don't find it in
    // the specified location?
    boolean searchClasspath = true;

    /**
     * Constructor which accesses a file in the current directory of the
     * engine.
     */
    public FileProvider(String filename)
    {
        this.filename = filename;
    }

    /**
     * Constructor which accesses a file relative to a specific base
     * path.
     */
    public FileProvider(String basepath, String filename)
    {
        this.basepath = basepath;
        this.filename = filename;
    }
    
    /**
     * Constructor which takes an input stream directly.
     * Note: The configuration will be read-only in this case!
     */ 
    public FileProvider(InputStream is)
    {
        myInputStream = is;
    }
    
    /**
     * Determine whether or not we will look for a "server-config.wsdd" file
     * on the classpath if we don't find it in the specified location.
     * 
     * @param searchClasspath true if we should search the classpath
     */ 
    public void setSearchClasspath(boolean searchClasspath)
    {
        this.searchClasspath = searchClasspath;
    }

    public void configureEngine(AxisEngine engine) throws Exception
    {
        if (myInputStream == null) {
            try {
                myInputStream = new FileInputStream(basepath + sep + filename);
            } catch (Exception e) {
                if (searchClasspath) {
                    myInputStream = engine.
                                    getClass().getResourceAsStream(filename);
                }
            }
        }
        
        if (myInputStream == null) {
            throw new Exception("No engine configuration file - aborting!");
        }

        WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(myInputStream));
        engine.deployWSDD(doc);
        
        myInputStream = null;
    }

    /**
     * Save the engine configuration.  In case there's a problem, we
     * write it to a string before saving it out to the actual file so
     * we don't screw up the file.
     */ 
    public void writeEngineConfig(AxisEngine engine) throws Exception
    {
        // If there's no filename then we must have created this with just
        // an InputStream - in which case the config stuff is read-only
        if ( filename == null ) return ;

        Document doc = Admin.listConfig(engine);
        StringWriter writer = new StringWriter();
        XMLUtils.DocumentToWriter(doc, writer);
        writer.close();
        FileOutputStream fos = new FileOutputStream(basepath + sep + filename);
        fos.write(writer.getBuffer().toString().getBytes());
        fos.close();
    }
}
