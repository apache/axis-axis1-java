/*
 * Copyright 1999,2004 The Apache Software Foundation.
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
package org.apache.axis.tools.ant.foreach;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Call a target foreach entry in a set of parameters based on a fileset.
 * <p>
 * <i>For Axis development; there is no support or stability associated 
 *  with this task</i> 
 *  <pre>
 *    &lt;target name=&quot;target1&quot;&gt;
 *      &lt;foreach target=&quot;target2&quot;&gt;
 *        &lt;param name=&quot;param1&quot;&gt;
 *            &lt;fileset refid=&quot;fset1&quot;/&gt;
 *        &lt;/param&gt;
 *        &lt;param name=&quot;param2&quot;&gt;
 *          &lt;item value=&quot;jar&quot; /&gt;
 *          &lt;item value=&quot;zip&quot; /&gt;
 *        &lt;/param&gt;
 *       &lt;/foreach&gt;
 *    &lt;/target&gt;
 *
 *    &lt;target name=&quot;target2&quot;&gt;
 *      &lt;echo message=&quot;prop is ${param1}.${param2}&quot; /&gt;
 *    &lt;/target&gt;  
 * </pre>
 * <br>
 * Really this just a wrapper around "AntCall"
 * <br>
 * Added a "type" attribute that works precisely like its equivalent
 * in <code>ExecuteOn</code>.  It allows the user
 * to specify whether directories, files, or both directories and files
 * from the filesets are included as entries in the parameter set.
 * @ant.task category="axis" 
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
        /* if (callee2 == null) { */
            callee2 = (Java) getProject().createTask("java");
            callee2.setOwningTarget(getOwningTarget());
            callee2.setTaskName(getTaskName());
            callee2.setLocation(getLocation());
            callee2.setClassname("org.apache.tools.ant.Main");
            callee2.setAppend(true);
            callee2.setFork(true);
            callee2.createJvmarg().setValue("-Xbootclasspath/p:" + System.getProperty("sun.boot.class.path"));
        /* }                      */
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
        /* if (callee == null) { */
            callee = (Ant) getProject().createTask("ant");
            callee.setOwningTarget(getOwningTarget());
            callee.setTaskName(getTaskName());
            callee.init();
        /* }                     */

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
