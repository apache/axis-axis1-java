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
package org.apache.axis.wsdl.fromJava;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.Vector;
import java.util.HashMap;

import org.apache.axis.utils.JavapUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.Skeleton;

/**
 * ClassRep is the representation of a class used inside the Java2WSDL
 * emitter.  The information in the ClassRep can be changed by 
 * user provided code to affect the emitted wsdl file.
 *
 * If you wish to change the functionality (for example change the
 * getParameterNames(...) algorithm), here is one way to do it:
 *   1) Extend ClassRep class (MyClassRep) and override the desired methods.
 *   2) Extend the DefaultBuilderBeanClassRep and DefaultBuilderPortTypeClasses
 *      and provide new build(...) methods that construct MyClassRep objects.
 *   3) Extend the DefaultFactory class (MyFactory) so that it locates your new Builder classes.
 *   4) Provide MyFactory as an option when your invoke Java2WSDL.
 *            
 *             name  
 * ClassRep +-+---------> String
 *  | | | | | |
 *  | | | | | | isIntf
 *  | | | | | +---------> boolean
 *  | | | | | 
 *  | | | | | modifiers
 *  | | | | +-----------> int (use java.lang.reflect.Modifier to decode)
 *  | | | |  
 *  | | | | super 
 *  | | | +-------------> ClassRep
 *  | | |  
 *  | | | interfaces 
 *  | | +---------------> ClassRep(s)
 *  | |
 *  | | methods
 *  | +-----------------> MethodRep(s)
 *  |
 *  | fields
 *  +-------------------> FieldRep(s)
 *
 *            name
 *  MethodRep ----------> String
 *        | |
 *        | | return
 *        | +-----------> ParamRep
 *        |
 *        | params
 *        +-------------> ParamRep
 *
 *           name
 *  ParamRep -----------> String
 *      | |  
 *      | |  type
 *      | +-------------> Class
 *      |
 *      |   mode
 *      +---------------> int (in/out/inout)
 *
 *           name
 *  FieldRep -----------> String
 *       |
 *       | type
 *       +--------------> Class
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class ClassRep {
    
    private String   _name       = "";
    private boolean  _isInterface= false;
    private int      _modifiers  = 0;  // Use java.lang.reflect.Modifer to decode
    private ClassRep _super      = null;
    private Vector   _interfaces = new Vector();
    private Vector   _methods    = new Vector();
    private Vector   _fields     = new Vector();
    private HashMap  _fieldNames = new HashMap();
    private Vector   _stopList    = null;
    

    /**
     * Constructor
     * Create an empty ClassRep
     */ 
    public ClassRep() {
    }

    /**
     * Constructor
     * Create a default representation of ClassRep
     * @param cls Class to use to create default ClassRep
     * @param inhMethods if true, then the methods array will contain
     *                   methods declared and/or inherited else only
     *                   the declared methods are put in the list
     * @param stopList An optional vector of class names which if inhMethods
     *                    is true, will stop the inheritence search if encountered.
     * @param implClass  This is an optional parameter which is a 
     *                   class that implements or extends cls.  The
     *                   implClass is used to obtain parameter names.
     */ 
    public ClassRep(Class cls, boolean inhMethods, Vector stopList) {
        init(cls, inhMethods, stopList, null);
    }
    public ClassRep(Class cls, boolean inhMethods, Vector stopList, Class implClass) {
        init(cls, inhMethods, stopList, implClass);
    }
    protected void init(Class cls, boolean inhMethods, Vector stopList, Class implClass) {
        _name = cls.getName();
        _isInterface = cls.isInterface();
        _modifiers = cls.getModifiers();
        _stopList = stopList;

        // Get our parent class, avoid Object and any class on the stop list.
        Class superClass = cls.getSuperclass();
        if (isClassOk(superClass)) {
            _super = new ClassRep(superClass, inhMethods, _stopList);
        }
        
        // Add the interfaces
        for (int i=0; i < cls.getInterfaces().length; i++) {
            _interfaces.add(new ClassRep(cls.getInterfaces()[i], inhMethods, _stopList));
        }
        // Add the methods
        addMethods(cls, inhMethods, implClass);

        // Add the fields
        addFields(cls);
    }

    /**
     * Getters/Setters
     **/
    public String   getName()                { return _name; }
    public void     setName(String name)     { _name = name; }
    public boolean  isInterface()            { return _isInterface; }
    public void     setIsInterface(boolean b){ _isInterface = b; }
    public int      getModifiers()           { return _modifiers; }
    public void     setModifiers(int m)      { _modifiers = m; }
    public ClassRep getSuper()               { return _super; }
    public void     setSuper(ClassRep cr)    { _super = cr; }
    public Vector   getInterfaces()          { return _interfaces; }
    public void     setInterfaces(Vector v)  { _interfaces = v; }
    public Vector   getMethods()             { return _methods; }
    public void     setMethods(Vector v)     { _methods = v; }
    public Vector   getFields()              { return _fields; }
    public void     setFields(Vector v)      { _fields = v; }

    /**
     * Adds MethodReps to the ClassRep. 
     * @param cls the Class    
     * @param inhMethods if true, then the methods array will contain
     *                   methods declared and/or inherited else only
     *                   the declared methods are put in the list           
     * @param stopList An optional vector of class names which if inhMethods
     *                    is true, will stop the inheritence search if encountered.
     * @param implClass  This is an optional parameter which is a 
     *                   class that implements or extends cls.  The
     *                   implClass is used to obtain parameter names.            
     */ 
    protected void addMethods(Class cls, boolean inhMethods, Class implClass) {
        // Constructs a vector of all the public methods

        // walk class intheritance chain
        walkInheritanceChain(cls, inhMethods, implClass);

        // If we aren't doing inhertance, all done
        if (!inhMethods) {
            return;
        }
        // add methods from interfaces
        Class[] interfaces = cls.getInterfaces();
        for (int i=0; i < interfaces.length; i++) {
            walkInheritanceChain(interfaces[i], inhMethods, implClass);
        } 
        
        return;
    }

    /**
     * Return true if we should process this class
     */ 
    private boolean isClassOk(Class clazz) {
        if (clazz == null)
            return false;

        String name = clazz.getName();

        if (_stopList != null) {
            // Use the user provided list of classes to stop
            if (_stopList.contains(name))
                return false;
        } else {
            // if stop list not provided, default to java.* and javax.*
            if (name.startsWith("java.") || name.startsWith("javax."))
                return false;
        }
        
        // Didn't find a reason to reject this class
        return true;
    }


    /**
     * Iterate up the inheritance chain and construct the list of methods
     * Appends to the _methods class variable.
     */ 
    private void walkInheritanceChain(Class cls, boolean inhMethods, Class implClass) {
        Method[] m;
        Class currentClass = cls;
        while (isClassOk(currentClass)) {

            // get the methods in this class
            m = currentClass.getDeclaredMethods();

            // add each method in this class to the list
            for (int i=0; i < m.length; i++) {
                int mod = m[i].getModifiers();
                if (Modifier.isPublic(mod) &&
                        // Ignore the getParameterName method from the Skeleton class
                        (!m[i].getName().equals("getParameterName") ||
                        !(Skeleton.class).isAssignableFrom(m[i].getDeclaringClass()))) {
                    short[] modes = getParameterModes(m[i]);
                    Class[] types = getParameterTypes(m[i]);
                    _methods.add(new MethodRep(m[i], types, modes,
                                               getParameterNames(m[i], implClass, types)));
                }
            }
            
            // if we don't want inherited methods, don't walk the chain
            if (!inhMethods) {
                break;
            }
            
            // move up the inhertance chain
            currentClass = currentClass.getSuperclass();
        }
    }

    /**
     * Adds FieldReps to the ClassRep.
     * @param cls the Class    
     * A complexType component element will be generated for each FieldRep.
     * This implementation generates FieldReps for public data fields and
     * also for properties exposed by java bean accessor methods.
     */ 
    protected void addFields(Class cls) {

        // Constructs a FieldRep for every public field and
        // for every field that has JavaBean accessor methods
        for (int i=0; i < cls.getDeclaredFields().length; i++) {
            Field f = cls.getDeclaredFields()[i];
            int mod = f.getModifiers();
            if (Modifier.isPublic(mod) ||
                isJavaBeanNormal(cls, f.getName(), f.getType()) ||
                isJavaBeanIndexed(cls, f.getName(), f.getType())) {
                if (_fieldNames.get(f.getName().toLowerCase()) == null) {
                    FieldRep fr;
                    if (!isJavaBeanIndexed(cls, f.getName(), f.getType())) {
                        fr = new FieldRep(f);
                    } else {
                        fr = new FieldRep();
                        fr.setName(f.getName());
                        fr.setType(f.getType().getComponentType());
                        fr.setIndexed(true);
                    }
                    _fields.add(fr);
                    _fieldNames.put(f.getName().toLowerCase(), fr);
                }
            }
        }

        // Now add FieldReps for any remaining bean accessors.
        for (int i=0; i < cls.getDeclaredMethods().length; i++) {
            Method method = cls.getDeclaredMethods()[i];
            int mod = method.getModifiers();
            if (Modifier.isPublic(mod) &&
                (method.getName().startsWith("is") ||
                 method.getName().startsWith("get"))) {
                String name = method.getName();
                if (name.startsWith("is")) {
                    name = name.substring(2);
                } else {
                    name = name.substring(3);
                }
                Class type = method.getReturnType();
                if (_fieldNames.get(name.toLowerCase()) == null) {
                    if (isJavaBeanNormal(cls, name, type) ||
                            isJavaBeanIndexed(cls, name, type)) {
                        FieldRep fr = new FieldRep();
                        fr.setName(name);
                        if (!isJavaBeanIndexed(cls, name, type)) {
                            fr.setType(type);
                        } else {
                            fr.setType(type.getComponentType());
                            fr.setIndexed(true);
                        }
                        _fields.add(fr);
                        _fieldNames.put(name.toLowerCase(), fr);
                    }
                }
                
            }
        }
        return;
    }

    /**
     * Get the list of parameter types for the specified method.
     * This implementation uses the specified type unless it is a holder class,
     * in which case the held type is used.
     * @param method is the Method.                          
     * @return array of parameter types.                                      
     */ 
    protected Class[] getParameterTypes(Method method) {
        Class[] types = new Class[method.getParameterTypes().length];
        for (int i=0; i < method.getParameterTypes().length; i++) {
            Class type = method.getParameterTypes()[i];
            if (JavaUtils.getHolderValueType(type) != null) {
                types[i] = JavaUtils.getHolderValueType(type);
            } else {
                types[i] = type;
            }
        }
        return types;
    }

    /**
     * Get the list of parameter modes for the specified method.
     * This implementation assumes IN unless the type is a holder class
     * @param method is the Method.                          
     * @return array of parameter modes.                                      
     */ 
    protected short[] getParameterModes(Method method) {
        short[] modes = new short[method.getParameterTypes().length];
        for (int i=0; i < method.getParameterTypes().length; i++) {
            Class type = method.getParameterTypes()[i];
            if (JavaUtils.getHolderValueType(type) != null) {
                modes[i] = ParamRep.INOUT;
            } else {
                modes[i] = ParamRep.IN;
            }
        }
        return modes;
    }

    /**
     * Get the list of parameter names for the specified method.
     * This implementation uses Skeleton.getParameterNames or javap to get the parameter names
     * from the class file.  If parameter names are not available 
     * for the method (perhaps the method is in an interface), the
     * corresponding method in the implClass is queried.
     * @param method is the Method to search.                
     * @param implClass  If the first search fails, the corresponding  
     *                   Method in this class is searched.           
     * @param types  are the parameter types after converting Holders.
     * @return array of Strings which represent the return name followed by parameter names
     */ 
    protected String[] getParameterNames(Method method, Class implClass, Class[] types) {
        String[] paramNames = null;
        
        paramNames = getParameterNamesFromSkeleton(method);
        if (paramNames != null) {
            return paramNames;
        }
        
        paramNames = JavapUtils.getParameterNames(method); 
        
        // If failed, try getting a method of the impl class
        // It is possible that the impl class is a skeleton, thus the
        // method may have a different signature (no Holders).  This is
        // why the method search is done with two parameter lists.
        if (paramNames == null && implClass != null) {
            Method m = null;
            try {
                m = implClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            } catch (Exception e) {}
            if (m == null) { 
                try {
                    m = implClass.getMethod(method.getName(), method.getParameterTypes());
                } catch (Exception e) {}
            }
            if (m == null) { 
                try {
                    m = implClass.getDeclaredMethod(method.getName(), types);
                } catch (Exception e) {}
            }
            if (m == null) { 
                try {
                    m = implClass.getMethod(method.getName(), types);
                } catch (Exception e) {}
            }
            if (m != null) {
                paramNames = getParameterNamesFromSkeleton(m);
                if (paramNames != null) {
                    return paramNames;
                }
                paramNames = JavapUtils.getParameterNames(m); 
            }
        }            

        return paramNames;
    }

    /**
     * Get the list of parameter names for the specified method.
     * This implementation uses Skeleton.getParameterNames to get the parameter names
     * from the class file.  If parameter names are not available, returns null. 
     * @param method is the Method to search.                
     * @return array of Strings which represent the return name followed by parameter names
     */ 
    protected String[] getParameterNamesFromSkeleton(Method method) {
        String[] paramNames = null;
        Class cls = method.getDeclaringClass();
        Class skel = Skeleton.class;
        if (!cls.isInterface() && skel.isAssignableFrom(cls)) {
            try {
                // Use the getParameterNameStatic method so that we don't have to new up 
                // an object.
                Method getParameterName = cls.getMethod("getParameterNameStatic",
                                                         new Class [] {String.class, int.class});
                Skeleton skelObj = null;
                if (getParameterName == null) {
                    // Fall back to getting new instance
                    skelObj = (Skeleton) cls.newInstance();
                    getParameterName = cls.getMethod("getParameterName",
                                                     new Class [] {String.class, int.class});
                }

                int numNames = method.getParameterTypes().length + 1; // Parms + return
                paramNames = new String[numNames];
                for (int i=0; i < numNames; i++) {
                    paramNames[i] = (String) getParameterName.invoke(skelObj, 
                                                                     new Object[] {method.getName(), 
                                                                                   new Integer(i-1)});
                }
            } catch (Exception e) {
            }
        }
        return paramNames;
    }

    /**
     * Determines if the Property in the class has been compliant accessors. If so returns true,
     * else returns false
     * @param cls the Class
     * @param name is the name of the property
     * @param type is the type of the property
     * @return true if the Property has JavaBean style accessors
     */
    protected boolean isJavaBeanNormal(Class cls, String name, Class type) {
        if ((name == null) || (name.length() == 0))
            return false;
        
        try {
            String propName = name.substring(0,1).toUpperCase()
                + name.substring(1);
            String setter = "set" + propName;
            String getter = null;
            if (type.getName() == "boolean")
                getter = "is" + propName;
            else
                getter = "get" + propName;

            Method m = cls.getDeclaredMethod(setter, new Class[] {type});
            int mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }

            m = cls.getDeclaredMethod(getter, null);
            mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }       
        }
        catch (NoSuchMethodException ex) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the Property in the Class has bean compliant indexed accessors. If so returns true,
     * else returns false
     * @param cls the Class
     * @param name is the name of the property
     * @param type is the type of the property
     * @return true if the Property has JavaBean style accessors
     */
    protected boolean isJavaBeanIndexed(Class cls, String name, Class type) {
        // Must be an array
        if (!type.isArray())
            return false;

        try {
            String propName =  name.substring(0,1).toUpperCase()
                + name.substring(1);
            String setter = "set" + propName;
            String getter = null;
            if (type.getName().startsWith("boolean["))
                getter = "is" + propName;
            else
                getter = "get" + propName;

            Method m = type.getDeclaredMethod(setter, new Class[] {int.class, type.getComponentType()});
            int mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }

            m = type.getDeclaredMethod(getter, new Class[] {int.class});
            mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }       
        }
        catch (NoSuchMethodException ex) {
            return false;
        }
        return true;
    }

};
