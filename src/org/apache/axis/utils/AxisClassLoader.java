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

package org.apache.axis.utils ;

import org.apache.axis.utils.cache.JavaClass;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

/**
 * This allows us to specify that certain classes, the ones we register
 * using the registerClass method, should be loaded using this class
 * loader - all others use the system default one.
 * This was added so that the *.jws processor can reload classes
 * that have already been loaded once - when the java file changes.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class AxisClassLoader extends ClassLoader {
    static Hashtable classLoaders = new Hashtable();

    Hashtable classCache          = new Hashtable() ;

    public AxisClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    static synchronized public AxisClassLoader getClassLoader() {
        ClassLoader baseCL = Thread.currentThread().getContextClassLoader();
        AxisClassLoader cl = null ;
        cl = (AxisClassLoader) classLoaders.get( baseCL );
        if ( cl == null ) 
            classLoaders.put( baseCL, cl = new AxisClassLoader() );
        return( cl );
    }
    
    static public void removeClassLoader(String name) {
        if ( name != null )
            classLoaders.remove( name );
    }

    public void registerClass( String name, String classFile )
        throws FileNotFoundException, IOException
    {
        /* Load the class file the *.class file */
        /****************************************/
        FileInputStream       fis  = new FileInputStream( classFile );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        for(int i = 0; (i = fis.read(buf)) != -1; )
            baos.write(buf, 0, i);
        fis.close();
        baos.close();

        /* Create a new Class object from it */
        /*************************************/
        byte[] data = baos.toByteArray();
        Class  cls  = defineClass( name, data, 0, data.length );

        /* And finally register it */
        /***************************/
        registerClass( name, cls );
    }

    public synchronized void registerClass( String name, Class cls ) {
        /* And finally register it */
        /***************************/
        JavaClass oldClass = (JavaClass)classCache.get(name);
        if (oldClass!=null && oldClass.getJavaClass()==cls) return;
        classCache.put( name, new JavaClass(cls) );
    }

    public synchronized void deregisterClass( String name ) {
        /* Deregister the passed in className */
        /**************************************/
        classCache.remove( name);
    }

    public boolean isClassRegistered( String name ) {
        return( classCache != null && classCache.get(name) != null );
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        Object obj ;

        /* Check the classCache for the className - if there just return */
        /* the class - if not there use the default class loader.        */
        /*****************************************************************/
        if ( classCache != null ) {
            obj = classCache.get( name );
            if ( obj != null )
                return( ((JavaClass)obj).getJavaClass() );
        }
    
        Class cls = super.loadClass( name );
        registerClass( name, cls );
        return cls;
    }

    /**
     * Find the cached JavaClass entry for this class, creating one
     * if necessary.
     * @param className name of the class desired
     * @return JavaClass entry
     */
    public JavaClass lookup(String className) throws ClassNotFoundException {
        JavaClass jc = (JavaClass) classCache.get( className );
        if ( jc == null ) {
            loadClass( className );
            jc = (JavaClass) classCache.get( className );
        }

        return jc;
    }
};
