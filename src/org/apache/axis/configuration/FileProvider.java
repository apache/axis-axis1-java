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
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    protected Properties props = new Properties();

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

    public void configureEngine(AxisEngine engine) throws Exception
    {
        InputStream is;

        try {
            is = new FileInputStream(basepath + sep + filename);
        } catch (Exception e) {
            is = engine.getClass().getResourceAsStream(filename);
        }

        if (is == null) {
            throw new Exception(JavaUtils.getMessage("noconfig00"));
        }

        Document doc = XMLUtils.newDocument(is);

        Admin.processEngineConfig(doc, engine);

        loadProperties(engine);
    }

    public void writeEngineConfig(AxisEngine engine) throws Exception
    {
        Document doc = Admin.listConfig(engine);
        FileOutputStream fos = new FileOutputStream(basepath + sep + filename);
        XMLUtils.PrettyDocumentToStream(doc, fos);
        fos.close();
    }

    protected void loadProperties(AxisEngine engine)
    {
        /** Load properties 1st, so that debug level gets
        * set ASAP.
        */
        try {
            File propFile = new File("axis.properties");
            if (propFile.exists()) {
                FileInputStream propFileInputStream =
                                               new FileInputStream(propFile);
                props.load(propFileInputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String propVal;

        // Should we send XML declarations in our messages?
        // default is true, and currently the only accepted true value is
        // "true".
        propVal = props.getProperty(AxisEngine.PROP_XML_DECL, "true");
        engine.addOption(AxisEngine.PROP_XML_DECL, new Boolean(propVal.equals("true")));

        // Should we send multi-ref serializations in our messages?
        propVal = props.getProperty(AxisEngine.PROP_DOMULTIREFS, "true");
        engine.addOption(AxisEngine.PROP_DOMULTIREFS, new Boolean(propVal.equals("true")));

        // The admin password (if it hasn't been set, we're "unsafe", and
        // we shouldn't do anything in the admin but change it)
        propVal = props.getProperty(AxisEngine.PROP_PASSWORD);
        if (propVal != null) {
            engine.setAdminPassword(propVal);
        } else {
            engine.addOption(AxisEngine.PROP_PASSWORD, "admin");
        }
    }
}
