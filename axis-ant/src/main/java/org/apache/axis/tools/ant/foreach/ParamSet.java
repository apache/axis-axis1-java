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
 * 
 * @author <a href="mailto:tpv@spamcop.net">Tim Vernum</a>
 * @author Davanum Srinivas
 * @author Richard A. Sitze
 */
public class ParamSet {
    public static final String TYPE_FILE = "file".intern();
    public static final String TYPE_DIR  = "dir".intern();
    public static final String TYPE_BOTH = "both".intern();

    /**
     * Enumerated attribute with the values "file", "dir" and "both"
     * for the type attribute.
     */
    public static class FileDirBoth extends EnumeratedAttribute {
        public String[] getValues() {
            return new String[]{
                TYPE_FILE, TYPE_DIR, TYPE_BOTH
            };
        }
    }

    /** Defaults to "file". */
    protected String type = TYPE_FILE;
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
            if (TYPE_DIR != type) {
                String[] files = getFiles(base, scanner);
                for (int j = 0; j < files.length; j++) {
                    File f = new File(base, files[j]);
                    values.addElement(f.getAbsolutePath());
                }
            }
            if (TYPE_FILE != type) {
                String[] dirs = getDirs(base, scanner);
                for (int j = 0; j < dirs.length; j++) {
                    File f = new File(base, dirs[j]);
                    values.addElement(f.getAbsolutePath());
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
        this.type = type.getValue().intern();
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
