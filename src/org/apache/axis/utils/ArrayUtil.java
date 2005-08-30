/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

public class ArrayUtil {
    private static class ArrayInfo {
        public Class componentType;
        public Class arrayType;
        public int dimension;       
    }
        
    public static class NonConvertable {
        public NonConvertable() { }            
    }
    
    /** An object indicating that the conversion is not possible */
    public static final NonConvertable NON_CONVERTABLE = new NonConvertable();
    
    /**
     * Convert ArrayOfT to T[].
     * @param obj        the object of type ArrayOfT to convert
     * @param arrayType  the destination array type
     * @return returns   the converted array object. 
     *                   If not convertable the original obj argument is returned.
     *                   If the obj is not type of ArrayOfT or the value is null, null is returned.
     */
    public static Object convertObjectToArray(Object obj, Class arrayType) {
        try {            
            ArrayInfo arri = new ArrayInfo();
            boolean rc = internalIsConvertable(obj.getClass(), arri, arrayType);
            if (rc == false) {
                return obj;
            }
                  
            BeanPropertyDescriptor pd = null;                   
            pd = getArrayComponentPD(obj.getClass());           
            if (pd == null) {
                return NON_CONVERTABLE;
            }
            Object comp = pd.get(obj);
            if (comp == null) {
                return null;
            }
            int arraylen = 0;
            if (comp.getClass().isArray()) {
                arraylen = Array.getLength(comp);
            } else {                
                return comp;
            }                       
                        
            int[] dims = new int[arri.dimension];
            dims[0] = arraylen;         
            Object targetArray = Array.newInstance(arri.componentType, dims);
            
            for (int i = 0; i < arraylen; i++) {
                Object subarray = Array.get(comp, i);
                Class subarrayClass = arrayType.getComponentType();
                Array.set(targetArray, i, convertObjectToArray(subarray, subarrayClass)); 
            }                        
            return targetArray;
        } catch (InvocationTargetException e) {         
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }       
        
        return null;
    }
    
    
    /**
     * Check if the clazz(perhaps ArrayOfT class) can be converted to T[].  
     * @param clazz      a class of ArrayOfT
     * @param arrayType  an array class (T[]) 
     * @return           true if converable, false if not
     */
    public static boolean isConvertable(Class clazz, Class arrayType) {
        ArrayInfo arrInfo = new ArrayInfo();
        return internalIsConvertable(clazz, arrInfo, arrayType);
    }
    
    /**
     * Check if the clazz(perhaps ArrayOfT class) can be converted to T[].  
     * @param clazz      a class of ArrayOfT
     * @param arri       convert result information
     * @param arrayType  an array class (T[]) 
     * @return           true if converable, false if not
     */
    private static boolean internalIsConvertable(Class clazz, ArrayInfo arri, Class arrayType) {
        BeanPropertyDescriptor pd = null, oldPd = null;
        if (!arrayType.isArray())
            return false;

        Class destArrCompType = arrayType.getComponentType();       
        Class src = clazz;
        int depth = 0;
        
        while (true) {
            pd = getArrayComponentPD(src);
            if (pd == null)
                break;
            depth++;            
            src = pd.getType();
            oldPd = pd;          
            if (destArrCompType.isAssignableFrom(src)) 
                break;                  
        }
        
        if (depth == 0 || oldPd.getType() == null) {           
            return false;
        }
                
        arri.componentType = oldPd.getType();
        arri.dimension = depth;
        
        Class componentType = oldPd.getType();
        int[] dims = new int[depth];
        Object array = Array.newInstance(componentType, dims);      
        arri.arrayType = array.getClass();
        
        if (arrayType.isAssignableFrom(arri.arrayType))
            return true;
        else
            return false;
    }
    
    /**
     * Gets the BeanPropertyDescriptor of ArrayOfT class's array member.
     * @param clazz a class of perhaps ArrayOfT type.
     * @return the BeanPropertyDescriptor. If the clazz is not type of ArrayOfT, null is returned.
     */
    private static BeanPropertyDescriptor getArrayComponentPD(Class clazz) {
        BeanPropertyDescriptor bpd = null;
        int count = 0;
        Class cls = clazz;
        while (cls != null && cls.getName() != null && !cls.getName().equals("java.lang.Object")) {                 
            BeanPropertyDescriptor bpds[] = BeanUtils.getPd(clazz);
            for (int i = 0; i < bpds.length; i++) {             
                BeanPropertyDescriptor pd = bpds[i];
                if (pd.isReadable() && pd.isWriteable() && pd.isIndexed()) {                    
                    count++;
                    if (count >= 2)
                        return null;
                    else
                        bpd = pd;
                }
            }
            cls = cls.getSuperclass();
        }
        
        if (count == 1) {
            return bpd;
        }
        else 
            return null;
    }   
    
    /**
     * Gets the dimension of arrayType
     * @param arrayType an array class
     * @return the dimension
     */
    public static int getArrayDimension(Class arrayType) {      
        if (!arrayType.isArray())
            return 0;
        int dim = 0;
        Class compType = arrayType;
        do {
            dim++;
            arrayType = compType;           
            compType = arrayType.getComponentType();
        } while (compType.isArray());
        
        return dim;             
    }
    
    private static Object createNewInstance(Class cls) throws InstantiationException, IllegalAccessException {
        Object obj = null;
        if (!cls.isPrimitive()) 
            obj = cls.newInstance();
        else {
            if (boolean.class.isAssignableFrom(cls)) 
                obj = new Boolean(false);
            else if (byte.class.isAssignableFrom(cls)) 
                obj = new Byte((byte)0);
            else if (char.class.isAssignableFrom(cls))
                obj = new Character('\u0000');
            else if (short.class.isAssignableFrom(cls))
                obj = new Short((short)0);
            else if (int.class.isAssignableFrom(cls))
                obj = new Integer(0);
            else if (long.class.isAssignableFrom(cls))
                obj = new Long(0L);
            else if (float.class.isAssignableFrom(cls))
                obj = new Float(0.0F);
            else if (double.class.isAssignableFrom(cls))
                obj = new Double(0.0D);
        }
        
        return obj;
    }
    
    /**
     * Convert an array object of which type is T[] to ArrayOfT class.
     * @param array     the array object
     * @param destClass the destination class
     * @return the object of type destClass if convertable, null if not.
     */
    public static Object convertArrayToObject(Object array, Class destClass) {        
        int dim = getArrayDimension(array.getClass());          
        if (dim == 0) {
            return null;
        }
        
        Object dest = null;
        
        try {           
            // create the destArray
            int arraylen = Array.getLength(array);
            Object destArray = null;
            Class destComp = null;
            if (!destClass.isArray()) {
                dest = destClass.newInstance();
                BeanPropertyDescriptor pd = getArrayComponentPD(destClass);
                if (pd == null)
                    return null;
            
                destComp = pd.getType();            
                destArray = Array.newInstance(destComp, arraylen);
                pd.set(dest, destArray);               
            } else {
                destComp = destClass.getComponentType();                               
                dest = Array.newInstance(destComp, arraylen);
                destArray = dest;
            }
            
            // iniialize the destArray
            for (int i = 0; i < arraylen; i++) {
                Array.set(destArray, i, createNewInstance(destComp));
            }                               
            
            // set the destArray 
            for (int i = 0; i < arraylen; i++) {
                Object comp = Array.get(array, i);

                if(comp == null)
                    continue;

                if (comp.getClass().isArray()) {
                    Class cls = Array.get(destArray, i).getClass();
                    Array.set(destArray, i, convertArrayToObject(comp, cls));
                }
                else {
                    Array.set(destArray, i, comp);                 
                }
            }
        } catch (IllegalAccessException ignore) {
            return null;
        } catch (InvocationTargetException ignore) {            
            return null;
        } catch (InstantiationException ignore) {
            return null;
        }
        
        return dest;
    }
}
