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

package org.apache.axis.resolver.jws;

import org.apache.axis.Handler;
import org.apache.axis.resolver.Resolver;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.ResolverException;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.AxisClassLoader;
import sun.tools.javac.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * The JWSResolver checks to see if a JWS file has been compiled
 * and is up to date.  If not, it compiles it and makes sure that
 * it is properly loaded by the AxisClassLoader.  Once done, it
 * asks the top level resolver to resolve the compiled Java class 
 * as a handler.
 * 
 * @author James Snell (jasnell@us.ibm.com)
 */

public class JWSResolver implements Resolver {

    public Handler resolve(ResolverContext context) throws ResolverException {
        try {
            String jwsFile   = context.getKey();
            if (!jwsFile.substring(jwsFile.length() - 4).equals("jws")) return null;
            
            Runtime runtime = Runtime.getRuntime();
            
            String jFile     = jwsFile.substring(0, jwsFile.length()-3) + "java";
            String cFile     = jwsFile.substring(0, jwsFile.length()-3) + "class";
            File   f1        = new File(cFile);
            File   f2        = new File(jwsFile);
            String clsName = f2.getName();
            clsName = clsName.substring( 0, clsName.length()-4 );
            
            if (!f1.exists() ||
                (f2.lastModified() > f1.lastModified())) {
                // if the compiled JWS file does not exist, or
                // is out of date, we need to recompile
                FileReader fr = new FileReader(jwsFile);
                FileWriter fw = new FileWriter(jFile);
                char[] buf = new char[4096];
                int    rc ;
                while ( (rc = fr.read( buf, 0, 4095)) >= 0 )
                    fw.write( buf, 0, rc );
                fw.close();
                fr.close();
                
                // now we do the compile
                FileOutputStream fos = new FileOutputStream("jws.err");
                Main compiler = new Main(fos, "javac");
                String outdir = f1.getParent();
                String[] args = null;
                if (outdir == null) outdir = ".";
                args = new String[] {"-d", outdir, "-classpath", getDefaultClasspath(context), jFile };
                boolean result = compiler.compile( args );

                // delete the tempoary .java file
                (new File(jFile)).delete();
                
                // check the result of the compile
                if (!result) {
                    /* Delete the *class file - sometimes it gets created even */
                    /* when there are errors - so erase it so it doesn't       */
                    /* confuse us.                                             */
                    /***********************************************************/
                    (new File(cFile)).delete();
                    return null;
                }
                AxisClassLoader.removeClassLoader(clsName);
            }
            AxisClassLoader cl = AxisClassLoader.getClassLoader();
            if (!cl.isClassRegistered(clsName)) 
                cl.registerClass(clsName, cFile);
            ResolverContext rc = new ResolverContext("java:" + clsName);
            rc.setProperties(context.getProperties());
            return context.getResolver().resolve(rc);
        } catch (Exception e) {
            return null;
        }
    }
    
    private String getDefaultClasspath(ResolverContext context)
    {
        StringBuffer classpath = new StringBuffer();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        while(cl != null)
        {
            if(cl instanceof URLClassLoader)
            {
                URL[] urls = ((URLClassLoader) cl).getURLs();

                for(int i=0; (urls != null) && i < urls.length; i++)
                {
                    classpath.append(urls[i].getPath());
                    classpath.append(File.pathSeparatorChar);


                    // if its a jar extract Class-Path entries from manifest
                    File file = new File(urls[i].getFile());
                    if(file.isFile())
                    {
                        FileInputStream fis = null;

                        try
                        {
                            fis = new FileInputStream(file);

                            if(isJar(fis))
                            {
                                JarFile jar = new JarFile(file);
                                Manifest manifest = jar.getManifest();
                                if (manifest != null)
                                {
                                    Attributes attributes = manifest.
                                            getMainAttributes();
                                    if (attributes != null)
                                    {
                                        String s = attributes.
                           getValue(java.util.jar.Attributes.Name.CLASS_PATH);
                                        String base = file.getParent();

                                        if (s != null)
                                        {
                                            StringTokenizer st =
                                                  new StringTokenizer(s, " ");
                                            while(st.hasMoreTokens())
                                            {
                                                String t = st.nextToken();
                                                classpath.append(base +
                                                      File.separatorChar + t);
                                                classpath.append(
                                                      File.pathSeparatorChar);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        catch(IOException ioe)
                        {
                            if(fis != null)
                                try {
                                    fis.close();
                                } catch (IOException ioe2) {}
                        }
                    }
                }
            }

            cl = cl.getParent();
        }

        // Just to be safe (the above doesn't seem to return the webapp
        // classpath in all cases), manually do this:

        String webBase = (String)context.getProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION);
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

        // boot classpath isn't found in above search
        if(System.getProperty("sun.boot.class.path") != null)
        {
            classpath.append(System.getProperty("sun.boot.class.path"));
        }

        return classpath.toString();
    }
    
    // an exception or emptiness signifies not a jar
    public static boolean isJar(InputStream is)
    {
        try
        {
            JarInputStream jis = new JarInputStream(is);
            if(jis.getNextEntry() != null)
            {
                return true;
            }
        }
        catch(IOException ioe)
        {
        }

        return false;
    }
    
    /**
     * we're not going to allow caching because we need
     * to know if the JWS file has changed
     */ 
    public boolean getAllowCaching() { return false; }
    
}
