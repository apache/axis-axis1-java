/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
 *    Apache Software Foundation (http://www.apache.org/)."
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
package org.apache.axis.utils;

import java.io.*;

/**
 * Class loader for JWS files.  There is one of these per JWS class, and
 * we keep a static Hashtable of them, indexed by class name.  When we want
 * to reload a JWS, we replace the ClassLoader for that class and let the
 * old one get GC'ed.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class JWSClassLoader extends ClassLoader {

    private String classFile = null;
    private String name = null;

    /**
     * Construct a JWSClassLoader with a class name, a parent ClassLoader,
     * and a filename of a .class file containing the bytecode for the class.
     * The constructor will load the bytecode, define the class, and register
     * this JWSClassLoader in the static registry.
     *
     * @param name the name of the class which will be created/loaded
     * @param cl the parent ClassLoader
     * @param classFile filename of the .class file
     * @exception FileNotFoundException
     * @exception IOException
     */
    public JWSClassLoader(String name, ClassLoader cl, String classFile)
        throws FileNotFoundException, IOException
    {
        super(cl);

        this.name = name + ".class";
        this.classFile = classFile;

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
        defineClass( name, data, 0, data.length );

        ClassUtils.setClassLoader(name,this);
    }

    /**
     * Overloaded getResourceAsStream() so we can be sure to return the
     * correct class file regardless of where it might live on our hard
     * drive.
     *
     * @param resourceName the resource to load (should be "classname.class")
     * @return an InputStream of the class bytes, or null
     */
    public InputStream getResourceAsStream(String resourceName) {
        try {
            if (resourceName.equals(name))
                return new FileInputStream( classFile );
        } catch (FileNotFoundException e) {
            // Fall through, return null.
        }
        return null;
    }
}
