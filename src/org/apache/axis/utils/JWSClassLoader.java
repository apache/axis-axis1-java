/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package org.apache.axis.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
