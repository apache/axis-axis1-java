/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.handlers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.compiler.Compiler;
import org.apache.axis.components.compiler.CompilerError;
import org.apache.axis.components.compiler.CompilerFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.enum.Scope;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JWSClassLoader;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** A <code>JWSHandler</code> sets the target service and JWS filename
 * in the context depending on the JWS configuration and the target URL.
 *
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class JWSHandler extends BasicHandler
{
    protected static Log log =
        LogFactory.getLog(JWSHandler.class.getName());

    public final String OPTION_JWS_FILE_EXTENSION = "extension";
    public final String DEFAULT_JWS_FILE_EXTENSION = Constants.JWS_DEFAULT_FILE_EXTENSION;

    protected static HashMap soapServices = new HashMap();

    /**
     * Just set up the service, the inner service will do the rest...
     */ 
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: JWSHandler::invoke");
        }

        try {
            setupService(msgContext);
        } catch (Exception e) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
    }
    
    /**
     * If our path ends in the right file extension (*.jws), handle all the
     * work necessary to compile the source file if it needs it, and set
     * up the "proxy" RPC service surrounding it as the MessageContext's
     * active service.
     *
     */ 
    protected void setupService(MessageContext msgContext) throws Exception {
        // FORCE the targetService to be JWS if the URL is right.
        String realpath = msgContext.getStrProp(Constants.MC_REALPATH);
        String extension = (String)getOption(OPTION_JWS_FILE_EXTENSION);
        if (extension == null) extension = DEFAULT_JWS_FILE_EXTENSION;
        
        if ((realpath!=null) && (realpath.endsWith(extension))) {
            /* Grab the *.jws filename from the context - should have been */
            /* placed there by another handler (ie. HTTPActionHandler)     */
            /***************************************************************/
            String   jwsFile = realpath;
            String rel = msgContext.getStrProp(Constants.MC_RELATIVE_PATH);
            if (rel.charAt(0) == '/') {
                rel = rel.substring(1);
            }
            
            int lastSlash = rel.lastIndexOf('/');
            String dir = null;
            
            if (lastSlash > 0) {
                dir = rel.substring(0, lastSlash);
            }
            
            String file = rel.substring(lastSlash + 1);
            
            String outdir = msgContext.getStrProp( Constants.MC_JWS_CLASSDIR );
            if ( outdir == null ) outdir = "." ;
            
            // Build matching directory structure under the output
            // directory.  In other words, if we have:
            //    /webroot/jws1/Foo.jws
            //
            // That will be compiled to:
            //    .../jwsOutputDirectory/jws1/Foo.class
            if (dir != null) {
                outdir = outdir + File.separator + dir;
            }
            
            // Confirm output directory exists.  If not, create it IF we're
            // allowed to.
            // !!! TODO: add a switch to control this.
            File outDirectory = new File(outdir);
            if (!outDirectory.exists()) {
                outDirectory.mkdirs();
            }
            
            if (log.isDebugEnabled())
                log.debug("jwsFile: " + jwsFile );
            
            String   jFile   = outdir + File.separator + file.substring(0, file.length()-3) +
                    "java" ;
            String   cFile   = outdir + File.separator + file.substring(0, file.length()-3) +
                    "class" ;
            
            if (log.isDebugEnabled()) {
                log.debug("jFile: " + jFile );
                log.debug("cFile: " + cFile );
                log.debug("outdir: " + outdir);
            }
            
            File  f1 = new File( cFile );
            File  f2 = new File( jwsFile );
            
            /* Get the class */
            /*****************/
            String clsName = null ;
            //clsName = msgContext.getStrProp(Constants.MC_RELATIVE_PATH);
            if ( clsName == null ) clsName = f2.getName();
            if ( clsName != null && clsName.charAt(0) == '/' )
                clsName = clsName.substring(1);
            
            clsName = clsName.substring( 0, clsName.length()-4 );
            clsName = clsName.replace('/', '.');
            
            if (log.isDebugEnabled())
                log.debug("ClsName: " + clsName );
            
            /* Check to see if we need to recompile */
            /****************************************/
            if ( !f1.exists() || f2.lastModified() > f1.lastModified() ) {
                /* If the class file doesn't exist, or it's older than the */
                /* java file then recompile the java file.                 */
                /* Start by copying the *.jws file to *.java               */
                /***********************************************************/
                log.debug(Messages.getMessage("compiling00", jwsFile) );
                log.debug(Messages.getMessage("copy00", jwsFile, jFile) );
                FileReader fr = new FileReader( jwsFile );
                FileWriter fw = new FileWriter( jFile );
                char[] buf = new char[4096];
                int    rc ;
                while ( (rc = fr.read( buf, 0, 4095)) >= 0 )
                    fw.write( buf, 0, rc );
                fw.close();
                fr.close();
                
                /* Now run javac on the *.java file */
                /************************************/
                log.debug("javac " + jFile );
                // Process proc = rt.exec( "javac " + jFile );
                // proc.waitFor();
                Compiler          compiler = CompilerFactory.getCompiler();
                
                compiler.setClasspath(getDefaultClasspath(msgContext));
                compiler.setDestination(outdir);
                compiler.addFile(jFile);
                
                boolean result   = compiler.compile();
                
                /* Delete the temporary *.java file and check return code */
                /**********************************************************/
                (new File(jFile)).delete();
                
                if ( !result ) {
                    /* Delete the *class file - sometimes it gets created even */
                    /* when there are errors - so erase it so it doesn't       */
                    /* confuse us.                                             */
                    /***********************************************************/
                    (new File(cFile)).delete();
                    
                    Document doc = XMLUtils.newDocument();
                    
                    Element         root = doc.createElementNS("", "Errors");
                    StringBuffer message = new StringBuffer("Error compiling ");
                    message.append(jFile);
                    message.append(":\n");
                    
                    List errors = compiler.getErrors();
                    int count = errors.size();
                    for (int i = 0; i < count; i++) {
                        CompilerError error = (CompilerError) errors.get(i);
                        if (i > 0) message.append("\n");
                        message.append("Line ");
                        message.append(error.getStartLine());
                        message.append(", column ");
                        message.append(error.getStartColumn());
                        message.append(": ");
                        message.append(error.getMessage());
                    }
                    root.appendChild( doc.createTextNode( message.toString() ) );
                    throw new AxisFault( "Server.compileError",
                                         Messages.getMessage("badCompile00", jFile),
                                         null, new Element[] { root } );
                }
                ClassUtils.removeClassLoader( clsName );
                // And clean out the cached service.
                soapServices.remove(clsName);
            }
            
            ClassLoader cl = ClassUtils.getClassLoader(clsName);
            if (cl == null) {
                cl = new JWSClassLoader(clsName,
                                        msgContext.getClassLoader(),
                                        cFile);
            }
            
            msgContext.setClassLoader(cl);
            
            /* Create a new RPCProvider - this will be the "service"   */
            /* that we invoke.                                                */
            /******************************************************************/
            // Cache the rpc service created to handle the class.  The cache
            // is based on class name, so only one .jws/.jwr class can be active
            // in the system at a time.
            SOAPService rpc = (SOAPService)soapServices.get(clsName);
            if (rpc == null) {
                rpc = new SOAPService(new RPCProvider());
                rpc.setName(clsName);
                rpc.setOption(RPCProvider.OPTION_CLASSNAME, clsName );
                rpc.setEngine(msgContext.getAxisEngine());
                
                // Support specification of "allowedMethods" as a parameter.
                String allowed = (String)getOption(RPCProvider.OPTION_ALLOWEDMETHODS);
                if (allowed == null) allowed = "*";
                rpc.setOption(RPCProvider.OPTION_ALLOWEDMETHODS, allowed);
                // Take the setting for the scope option from the handler
                // parameter named "scope"
                String scope = (String)getOption(RPCProvider.OPTION_SCOPE);
                if (scope == null) scope = Scope.DEFAULT.getName();
                rpc.setOption(RPCProvider.OPTION_SCOPE, scope);
                
                rpc.getInitializedServiceDesc(msgContext);
                
                soapServices.put(clsName, rpc);                
            }
            
            // Set engine, which hooks up type mappings.
            rpc.setEngine(msgContext.getAxisEngine());
            
            rpc.init();   // ??

            // OK, this is now the destination service!
            msgContext.setService( rpc );
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: JWSHandler::invoke");
        }
    }
    
    private String getDefaultClasspath(MessageContext msgContext)
    {
        StringBuffer classpath = new StringBuffer();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        fillClassPath(cl, classpath);

        // Just to be safe (the above doesn't seem to return the webapp
        // classpath in all cases), manually do this:

        String webBase = (String)msgContext.getProperty(
                                         HTTPConstants.MC_HTTP_SERVLETLOCATION);
        if (webBase != null) {
            classpath.append(webBase + File.separatorChar + "classes" +
                             File.pathSeparatorChar);
            try {
                String libBase = webBase + File.separatorChar + "lib";
                File libDir = new File(libBase);
                String [] jarFiles = libDir.list();
                for (int i = 0; i < jarFiles.length; i++) {
                    String jarFile = jarFiles[i];
                    if (jarFile.endsWith(".jar")) {
                        classpath.append(libBase +
                                         File.separatorChar +
                                         jarFile +
                                         File.pathSeparatorChar);
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

        return classpath.toString();
    }

    private void getClassPathFromDirectoryProperty(StringBuffer classpath, String property) {
        String dirs = AxisProperties.getProperty(property);
        String path = null;
        try {
            path = expandDirs(dirs);
        } catch (Exception e) {
            // Oh well.  No big deal.
        }
        if( path!= null) {
            classpath.append(path);
            classpath.append(File.pathSeparatorChar);
        }
    }

    private void getClassPathFromProperty(StringBuffer classpath, String property) {
        String path = AxisProperties.getProperty(property);
        if( path  != null) {
            classpath.append(path);
            classpath.append(File.pathSeparatorChar);
        }
    }

    /**
     * Walk the classloader hierarchy and add to the classpath
     * 
     * @param cl
     * @param classpath
     */
    private void fillClassPath(ClassLoader cl, StringBuffer classpath) {
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader) cl).getURLs();
                for (int i = 0; (urls != null) && i < urls.length; i++) {
                    String path = urls[i].getPath();
                    //If it is a drive letter, adjust accordingly.
                    if (path.length() >= 3 && path.charAt(0) == '/' && path.charAt(2) == ':')
                        path = path.substring(1);
                    classpath.append(URLDecoder.decode(path));
                    classpath.append(File.pathSeparatorChar);

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
                                                classpath.append(base + File.separatorChar + t);
                                                classpath.append(File.pathSeparatorChar);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException ioe) {
                            if (fis != null)
                                try {
                                    fis.close();
                                } catch (IOException ioe2) {
                                }
                        }
                    }
                }
            }
            cl = cl.getParent();
        }
    }

    /**
     * Expand a directory path or list of directory paths (File.pathSeparator
     * delimited) into a list of file paths of all the jar files in those
     * directories.
     *
     * @param dirPaths The string containing the directory path or list of
     * 		directory paths.
     * @return The file paths of the jar files in the directories. This is an
     *		empty string if no files were found, and is terminated by an
     *		additional pathSeparator in all other cases.
     */
    private String expandDirs(String dirPaths) {
        StringTokenizer st = new StringTokenizer(dirPaths, File.pathSeparator);
        StringBuffer buffer = new StringBuffer();
        while (st.hasMoreTokens()) {
            String d = st.nextToken();
            File dir = new File(d);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles(new JavaArchiveFilter());
                for (int i = 0; i < files.length; i++) {
                    buffer.append(files[i]).append(File.pathSeparator);
                }
            }
        }
        return buffer.toString();
    }

    // an exception or emptiness signifies not a jar
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

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        try {
            setupService(msgContext);
        } catch (Exception e) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
    }

    class JavaArchiveFilter implements FileFilter {
        public boolean accept(File file) {
            String name = file.getName().toLowerCase();
            return (name.endsWith(".jar") || name.endsWith(".zip"));
        }
    }
}
