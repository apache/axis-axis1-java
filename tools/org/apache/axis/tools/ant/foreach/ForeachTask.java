/*
* The Apache Software License, Version 1.1
*
* Copyright (c) 1999 The Apache Software Foundation.  All rights
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
* 3. The end-user documentation included with the redistribution, if
*    any, must include the following acknowlegement:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowlegement may appear in the software itself,
*    if and wherever such third-party acknowlegements normally appear.
*
* 4. The names "The Jakarta Project", "Ant", and "Apache Software
*    Foundation" must not be used to endorse or promote products derived
*    from this software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache"
*    nor may "Apache" appear in their names without prior written
*    permission of the Apache Group.
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
package org.apache.axis.tools.ant.foreach;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
/**
 * Call a target foreach entry in a set of parameters based on a fileset.
 *  <pre>
 *    <target name="target1">
 *      <foreach target="target2">
 *        <param name="param1">
 *            <fileset refid="fset1"/>
 *        </param>
 *        <param name="param2">
 *          <item value="jar" />
 *          <item value="zip" />
 *        </param>
 *       </foreach>
 *    </target>
 *
 *    <target name="target2">
 *      <echo message="prop is ${param1}.${param2}" />
 *    </target>
 * </pre>
 * <br>
 * Really this just a wrapper around "AntCall"
 * <br>
 * Added a "type" attribute that works precisely like its equivalent
 * in <code>ExecuteOn</code>.  It allows the user
 * to specify whether directories, files, or both directories and files
 * from the filesets are included as entries in the parameter set.
 * @author <a href="mailto:tpv@spamcop.net">Tim Vernum</a>
 */
public class ForeachTask extends Task {
	/** Defaults to "file". */
    protected String type = "file";
    private String subTarget;
    private Vector params;
    private Hashtable properties;
    /**
     * Enumerated attribute with the values "file", "dir" and "both"
     * for the type attribute.
     */
    public static class FileDirBoth extends EnumeratedAttribute {
        public String[] getValues() {
            return new String[] {
                "file", "dir", "both"
            };
        }
    }
    /**
     * Inner class stores <item>s with <param> lists
     */
    public class ParamItem {
        private String value;
        public void setValue(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
    }
    /**
     * Inner class stores sets of <param>s.
     * It can hold <fileset>s or <item>s or both.
     */
    public class ParamSet {
        private Vector filesets;
        private Vector items;
        private String name;
        public ParamSet() {
            filesets = new Vector();
            items = new Vector();
        }
        public void addFileset(FileSet fileset) {
            filesets.addElement(fileset);
        }
        public ParamItem createItem() {
            ParamItem item = new ParamItem();
            items.addElement(item);
            return item;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public Enumeration getValues(Project project) {
            /* As an arbitrary rule, this will return filesets first,
            and then <item>s. The ordering of the buildfile is
            not guaranteed. */
            Vector values = new Vector();
            Enumeration enum = filesets.elements();
            while (enum.hasMoreElements()) {
                FileSet fileSet          = (FileSet)enum.nextElement();
                File base                = fileSet.getDir(project);
                DirectoryScanner scanner = fileSet.getDirectoryScanner(project);
                if (! "dir".equals(type)) {
                    String[] files = getFiles(base, scanner);
                    for (int j = 0; j < files.length; j++) {
                        values.addElement(files[j]);
                    }
                }
                if (! "file".equals(type)) {
                    String[] dirs = getDirs(base, scanner);
                    for (int j = 0; j < dirs.length; j++) {
                        values.addElement(dirs[j]);
                    }
                }
            }
            enum = items.elements();
            while (enum.hasMoreElements()) {
                ParamItem item = (ParamItem)enum.nextElement();
                values.addElement(item.getValue());
            }
            return values.elements();
        }
    }
    public ForeachTask() {
        params = new Vector();
        properties = new Hashtable();
    }
    public void init() {
    }
    private void buildProperty(String propName, String propValue) {
        properties.put(propName, propValue);
    }
    private void executeTarget() {
        /* The "callee" has to be created each time in order to make
        the properties mutable. */
        CallTarget callee;
        callee = (CallTarget)project.createTask("antcall");
        callee.init();
        callee.setTarget(subTarget);
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String val = (String)properties.get(key);
            Property prop = callee.createParam();
            prop.setName(key);
            prop.setValue(val);
        }
        callee.execute();
    }
    /**
     * This method is used to recursively iterate through
     * each parameter set.
     * It ends up being something like:
     * <pre>
      *    for( i=0; i< params[0].size ; i++ )
      *       for( j=0; j < params[1].size ; j++ )
      *          for( k=0; k < params[2].size ; k++ )
      *             executeTarget( params[0][i], params[1][j] , params[2][k] ) ;
      * </pre>
     */
    private void executeParameters(int paramNumber) {
        if (paramNumber == params.size()) {
            executeTarget();
        } else {
            ParamSet paramSet = (ParamSet)params.elementAt(paramNumber);
            Enumeration values = paramSet.getValues(project);
            while (values.hasMoreElements()) {
                String val = (String)values.nextElement();
                buildProperty(paramSet.getName(), val);
                executeParameters(paramNumber + 1);
            }
        }
    }
    public void execute() {
        if (subTarget == null) {
            throw new BuildException("Attribute target is required.", location);
        }
        executeParameters(0);
    }
    public ParamSet createParam() {
        ParamSet param = new ParamSet();
        params.addElement(param);
        return param;
    }
    public void setTarget(String target) {
        subTarget = target;
    }
    /**
     * Return the list of files from this DirectoryScanner that should
     * be included on the command line.
     */
    protected String[] getFiles(File basedir, DirectoryScanner ds) {
        return ds.getIncludedFiles();
    }
    /**
     * Return the list of Directories from this DirectoryScanner that
     * should be included on the command line.
     */
    protected String[] getDirs(File basedir, DirectoryScanner ds) {
        return ds.getIncludedDirectories();
    }
    /**
     * Shall the command work only on files, directories or both?
     */
    public void setType(FileDirBoth type) {
        this.type = type.getValue();
    }
}
