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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Java;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

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
 *
 * @author <a href="mailto:tpv@spamcop.net">Tim Vernum</a>
 * @author Davanum Srinivas
 */
public class ForeachTask extends Task {
    private Ant callee;
    private Java callee2;
    private String subTarget;
    private Vector params;
    private Hashtable properties;
    // must match the default value of Ant#inheritAll
    private boolean inheritAll = true;
    // must match the default value of Ant#inheritRefs
    private boolean inheritRefs = false;
    private boolean fork = false;
    private boolean verbose = false;

    public ForeachTask() {
        params = new Vector();
        properties = new Hashtable();
    }

    public void init() {
    }

    /**
     * If true, pass all properties to the new Ant project.
     * Defaults to true.
     */
    public void setInheritAll(boolean inherit) {
       inheritAll = inherit;
    }

    /**
     * If true, pass all references to the new Ant project.
     * Defaults to false
     * @param inheritRefs new value
     */
    public void setInheritRefs(boolean inheritRefs) {
        this.inheritRefs = inheritRefs;
    }

    /**
     * Target to execute, required.
     */
    public void setTarget(String target) {
        subTarget = target;
    }

    /**
     * If true, forks the ant invocation.
     *
     * @param f "true|false|on|off|yes|no"
     */
    public void setFork(boolean f) {
        fork = f;
    }

    /**
     * Enable verbose output when signing
     * ; optional: default false
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    public ParamSet createParam() {
        ParamSet param = new ParamSet();
        params.addElement(param);
        return param;
    }

    private void buildProperty(String propName, String propValue) {
        properties.put(propName, propValue);
    }

    private void executeTarget() {
        if (subTarget == null) {
            throw new BuildException("Attribute target is required.",
                                     getLocation());
        }
        if(fork) {
            executeForkedAntTask();
        } else {
            executeAntTask();
        }
    }

    private void executeForkedAntTask() {
        if (callee == null) {
            callee2 = (Java) getProject().createTask("java");
            callee2.setOwningTarget(getOwningTarget());
            callee2.setTaskName(getTaskName());
            callee2.setLocation(getLocation());
            callee2.setClassname("org.apache.tools.ant.Main");
            callee2.setAppend(true);
            callee2.setFork(true);
        }
        String systemClassPath = System.getProperty("java.class.path");
        callee2.setClasspath(new Path(getProject(), systemClassPath));
        String args = "-buildfile " + properties.get("file");
        Commandline.Argument arguments = callee2.createArg();
        arguments.setLine(args);
        if (verbose) {
            callee2.createArg().setValue("-verbose");
        }
        if (callee2.executeJava() != 0) {
            throw new BuildException("Execution of ANT Task failed");
        }
    }

    private void executeAntTask() {
        if (callee == null) {
            callee = (Ant) getProject().createTask("ant");
            callee.setOwningTarget(getOwningTarget());
            callee.setTaskName(getTaskName());
            callee.setLocation(getLocation());
            callee.init();
        }

        callee.setAntfile(getProject().getProperty("ant.file"));
        callee.setTarget(subTarget);
        callee.setInheritAll(inheritAll);
        callee.setInheritRefs(inheritRefs);
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String val = (String) properties.get(key);
            Property prop = callee.createProperty();
            prop.setName(key);
            prop.setValue(val);
        }
        callee.execute();
        System.gc();
        System.gc();
        System.gc();
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
            ParamSet paramSet = (ParamSet) params.elementAt(paramNumber);
            Enumeration values = paramSet.getValues(project);
            while (values.hasMoreElements()) {
                String val = (String) values.nextElement();
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
}
