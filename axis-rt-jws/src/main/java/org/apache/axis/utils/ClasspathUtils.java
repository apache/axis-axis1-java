/*
 * ClasspathUtils.java
 * 
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
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
 * 
 */
package org.apache.axis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.axis.AxisProperties;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * Utility class for constructing the classpath
 */ 
public class ClasspathUtils {

    /**
     * Expand a directory path or list of directory paths (File.pathSeparator
     * delimited) into a list of file paths of all the jar files in those
     * directories.
     *
     * @param dirPaths The string containing the directory path or list of
     *                 directory paths.
     * @return The file paths of the jar files in the directories. This is an
     *         empty list if no files were found.
     */
    public static List<File> expandDirs(String dirPaths) {
        StringTokenizer st = new StringTokenizer(dirPaths, File.pathSeparator);
        List<File> files = new ArrayList<File>();
        while (st.hasMoreTokens()) {
            String d = st.nextToken();
            File dir = new File(d);
            if (dir.isDirectory()) {
                files.addAll(Arrays.asList(dir.listFiles(new JavaArchiveFilter())));
            }
        }
        return files;
    }

    /**
     * Check if this inputstream is a jar/zip
     * @param is
     * @return true if inputstream is a jar
     */ 
    public static boolean isJar(InputStream is) {
        try {
            JarInputStream jis = new JarInputStream(is);
            if (jis.getNextEntry() != null) {
                return true;
            }
        } catch (IOException ioe) {
        }
        return false;
    }

    /**
     * Get the default classpath from various thingies in the message context
     * @param msgContext
     * @return default classpath
     */ 
    public static List<File> getDefaultClasspath(MessageContext msgContext) {
        List<File> classpath = new ArrayList<File>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        fillClassPath(cl, classpath);

        // Just to be safe (the above doesn't seem to return the webapp
        // classpath in all cases), manually do this:

        String webBase = (String) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION);
        if (webBase != null) {
            classpath.add(new File(webBase, "classes"));
            try {
                String libBase = webBase + File.separatorChar + "lib";
                File libDir = new File(libBase);
                String[] jarFiles = libDir.list();
                for (int i = 0; i < jarFiles.length; i++) {
                    String jarFile = jarFiles[i];
                    if (jarFile.endsWith(".jar")) {
                        classpath.add(new File(libBase, jarFile));
                    }
                }
            } catch (Exception e) {
                // Oh well.  No big deal.
            }
        }

        // axis.ext.dirs can be used in any appserver
        getClassPathFromDirectoryProperty(classpath, "axis.ext.dirs");

        // classpath used by Jasper 
        getClassPathFromProperty(classpath, "org.apache.catalina.jsp_classpath");
        
        // websphere stuff.
        getClassPathFromProperty(classpath, "ws.ext.dirs");
        getClassPathFromProperty(classpath, "com.ibm.websphere.servlet.application.classpath");
        
        // java class path
        getClassPathFromProperty(classpath, "java.class.path");
        
        // Load jars from java external directory
        getClassPathFromDirectoryProperty(classpath, "java.ext.dirs");
        
        // boot classpath isn't found in above search
        getClassPathFromProperty(classpath, "sun.boot.class.path");
        
        return classpath;
    }

    /**
     * Add all files in the specified directory to the classpath
     * @param classpath
     * @param property
     */ 
    private static void getClassPathFromDirectoryProperty(List<File> classpath, String property) {
        String dirs = AxisProperties.getProperty(property);
        try {
            classpath.addAll(ClasspathUtils.expandDirs(dirs));
        } catch (Exception e) {
            // Oh well.  No big deal.
        }
    }

    /**
     * Add a classpath stored in a property.
     * @param classpath
     * @param property
     */ 
    private static void getClassPathFromProperty(List<File> classpath, String property) {
        String path = AxisProperties.getProperty(property);
        if (path != null) {
            for (String item : path.split(File.pathSeparator)) {
                classpath.add(new File(item));
            }
        }
    }

    /**
     * Walk the classloader hierarchy and add to the classpath
     * @param cl
     * @param classpath
     */
    private static void fillClassPath(ClassLoader cl, List<File> classpath) {
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader) cl).getURLs();
                for (int i = 0; (urls != null) && i < urls.length; i++) {
                    String path = urls[i].getPath();
                    //If it is a drive letter, adjust accordingly.
                    if (path.length() >= 3 && path.charAt(0) == '/' && path.charAt(2) == ':')
                        path = path.substring(1);
                    classpath.add(new File(URLDecoder.decode(path)));

                    // if its a jar extract Class-Path entries from manifest
                    File file = new File(urls[i].getFile());
                    if (file.isFile()) {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(file);
                            if (isJar(fis)) {
                                JarFile jar = new JarFile(file);
                                Manifest manifest = jar.getManifest();
                                if (manifest != null) {
                                    Attributes attributes = manifest.getMainAttributes();
                                    if (attributes != null) {
                                        String s = attributes.getValue(Attributes.Name.CLASS_PATH);
                                        String base = file.getParent();
                                        if (s != null) {
                                            StringTokenizer st = new StringTokenizer(s, " ");
                                            while (st.hasMoreTokens()) {
                                                String t = st.nextToken();
                                                classpath.add(new File(base, t));
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException ioe) {
                        } finally {
                            if (fis != null) {
                                try {
                                    fis.close();
                                } catch (IOException ioe2) {
                                }
                            }
                        }
                    }
                }
            }
            cl = cl.getParent();
        }
    }

    /**
     * Filter for zip/jar
     */ 
    private static class JavaArchiveFilter implements FileFilter {
        public boolean accept(File file) {
            String name = file.getName().toLowerCase();
            return (name.endsWith(".jar") || name.endsWith(".zip"));
        }
    }
}
