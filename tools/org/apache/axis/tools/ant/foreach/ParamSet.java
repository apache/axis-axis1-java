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

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Inner class stores sets of <param>s.
 * It can hold <fileset>s or <item>s or both.
 */
public class ParamSet {
    /**
     * Enumerated attribute with the values "file", "dir" and "both"
     * for the type attribute.
     */
    public static class FileDirBoth extends EnumeratedAttribute {
        public String[] getValues() {
            return new String[]{
                "file", "dir", "both"
            };
        }
    }

    /** Defaults to "file". */
    protected String type = "file";
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
            FileSet fileSet = (FileSet) enum.nextElement();
            File base = fileSet.getDir(project);
            DirectoryScanner scanner = fileSet.getDirectoryScanner(project);
            if (!"dir".equals(type)) {
                String[] files = getFiles(base, scanner);
                for (int j = 0; j < files.length; j++) {
                    values.addElement(files[j]);
                }
            }
            if (!"file".equals(type)) {
                String[] dirs = getDirs(base, scanner);
                for (int j = 0; j < dirs.length; j++) {
                    values.addElement(dirs[j]);
                }
            }
        }
        enum = items.elements();
        while (enum.hasMoreElements()) {
            ParamItem item = (ParamItem) enum.nextElement();
            values.addElement(item.getValue());
        }
        return values.elements();
    }

    /**
     * Shall the command work only on files, directories or both?
     */
    public void setType(FileDirBoth type) {
        this.type = type.getValue();
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
}
