/*
 *  The Apache Software License, Version 1.1
 *
 *  Copyright (c) 2002 The Apache Software Foundation.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution, if
 *  any, must include the following acknowlegement:
 *  "This product includes software developed by the
 *  Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowlegement may appear in the software itself,
 *  if and wherever such third-party acknowlegements normally appear.
 *
 *  4. The names "The Jakarta Project", "Ant", and "Apache Software
 *  Foundation" must not be used to endorse or promote products derived
 *  from this software without prior written permission. For written
 *  permission, please contact apache@apache.org.
 *
 *  5. Products derived from this software may not be called "Apache"
 *  nor may "Apache" appear in their names without prior written
 *  permission of the Apache Group.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of the Apache Software Foundation.  For more
 *  information on the Apache Software Foundation, please see
 *  <http://www.apache.org/>.
 */

package org.apache.axis.tools.ant.wsdl;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;

import java.util.HashMap;
import java.util.Properties;
import java.util.Enumeration;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Used for nested package definitions.
 */
public class NamespaceMapping implements Mapper {


    private String namespace = null;
    private String packageName = null;
    private File mappingFile;

    /**
     * pass in the namespace to map to
     */
    public NamespaceMapping() {
    }

    /**
     * the namespace in the WSDL. Required.
     * @param value new uri of the mapping
     */
    public void setNamespace(String value) {
        namespace = value;
    }

    /**
     * the Java package to bind to. Required.
     * @param value java package name
     */
    public void setPackage(String value) {
        packageName = value;
    }

    /**
     * name of a property file that contains mappings in
     * package=namespace format
     * @param file
     */
    public void setFile(File file) {
        mappingFile = file;
    }

    /**
     * map a namespace to a package
     * @param owner owning project component (For logging)
     * @param map map to assign to
     * @param packName package name
     * @param nspace namespace
     */
    protected void map(ProjectComponent owner,
                       HashMap map,
                       String packName,
                       String nspace) {
        owner.log("mapping "+nspace+" to "+packName, Project.MSG_VERBOSE);
        map.put(nspace, packName);
    }
    /**
     * validate the option set
     */
    private void validate() {
        if (mappingFile != null) {
            if (namespace != null || packageName != null) {
                throw new BuildException(
                        "Namespace or Package cannot be used with a File attribute");
            }
        } else {
            if (namespace == null) {
                throw new BuildException("namespace must be defined");
            }
            if (packageName == null) {
                throw new BuildException("package must be defined");
            }
        }
    }

    /**
     * Load a mapping file and save it to the map
     * @param owner owner component
     * @param map target map file
     * @throws BuildException if an IOException needed swallowing
     */
    protected void mapFile(ProjectComponent owner, HashMap map) throws BuildException {
        Properties props = loadMappingPropertiesFile();
        Enumeration keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String uri = props.getProperty(key);
            map(owner, map, key, uri);
        }
    }

    /**
     * load a file containing properties
     * @return a properties file with zero or more mappings
     * @throws BuildException if the load failed
     */
    private Properties loadMappingPropertiesFile() throws BuildException {
        Properties props = new Properties();
        FileInputStream instr = null;
        try {
            instr = new FileInputStream(mappingFile);
            props.load(new BufferedInputStream(instr));
        } catch (IOException e) {
            throw new BuildException("Failed to load " + mappingFile, e);
        } finally {
            if (instr != null) {
                try {
                    instr.close();
                } catch (IOException e) {
                }
            }
        }
        return props;
    }


    /**
     * execute the mapping
     * @param owner owner object
     * @param map map to map to
     * @throws BuildException in case of emergency
     */
    public void execute(ProjectComponent owner, HashMap map) throws BuildException {
        validate();
        if (mappingFile != null) {
            mapFile(owner, map);
        } else {
            map(owner, map, packageName, namespace);
        }
    }


}