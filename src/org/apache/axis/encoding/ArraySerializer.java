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
package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

/** An ArraySerializer handles serializing and deserializing SOAP
 * arrays.
 * 
 * Some code borrowed from ApacheSOAP - thanks to Matt Duftler!
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 * 
 * Multi-reference stuff:
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */

public class ArraySerializer extends Deserializer
    implements ValueReceiver, Serializer
{
    static Category category =
            Category.getInstance(ArraySerializer.class.getName());

    static Hashtable primitives = new Hashtable();
    static {
        primitives.put(Character.class, Character.TYPE);
        primitives.put(Byte.class, Byte.TYPE);
        primitives.put(Short.class, Short.TYPE);
        primitives.put(Integer.class, Integer.TYPE);
        primitives.put(Long.class, Long.TYPE);
        primitives.put(Float.class, Float.TYPE);
        primitives.put(Double.class, Double.TYPE);
    }

    public static class Factory implements DeserializerFactory {
        public Deserializer getDeserializer(Class cls) {
            return new ArraySerializer();
        }
    }
    public static DeserializerFactory factory = new Factory();
    
    public QName arrayType = null;
    public int curIndex = 0;
    QName arrayItemType;
    int length;
    ArrayList mDimLength = null;  // If set, array of multi-dim lengths 
    ArrayList mDimFactor = null;  // If set, array of factors for multi-dim arrays
    HashSet waiting = new HashSet();  // List of indices waiting for completion

    /**
     * During processing, the Array Deserializer stores the array in 
     * an ArrayListExtension class.  This class contains all of the
     * normal function of an ArrayList, plus it keeps a list of the
     * converted array values.  This class is essential to support
     * arrays that are multi-referenced.
     **/
    public class ArrayListExtension extends ArrayList {
        private Hashtable table = null;
        
        /**
         * Constructors
         */
        ArrayListExtension() {
            super();
        }
        ArrayListExtension(int length) {
            super(length);
        }
        /**
         * Store converted value
         **/
        public void setConvertedValue(Class cls, Object value) {
            if (table == null)
                table = new Hashtable();
            table.put(cls, value);
        }
        /**
         * Get previously converted value
         **/
        public Object getConvertedValue(Class cls) {
            if (table == null)
                return null;
            return table.get(cls);
        }
    }
   
    public void onStartElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", "ArraySerializer.startElement()"));
        }

        QName arrayTypeValue = context.getQNameFromString(
                                  attributes.getValue(Constants.URI_SOAP_ENC,
                                                   Constants.ATTR_ARRAY_TYPE));
        if (arrayTypeValue == null)
            throw new SAXException(JavaUtils.getMessage("noArrayType00"));
        
        String arrayTypeValueNamespaceURI = arrayTypeValue.getNamespaceURI();
        String arrayTypeValueLocalPart = arrayTypeValue.getLocalPart();
        int leftBracketIndex = arrayTypeValueLocalPart.lastIndexOf('[');
        int rightBracketIndex = arrayTypeValueLocalPart.lastIndexOf(']');

        if (leftBracketIndex == -1
            || rightBracketIndex == -1
            || rightBracketIndex < leftBracketIndex)
        {
            throw new IllegalArgumentException(
                    JavaUtils.getMessage("badArrayType00", "" + arrayTypeValue));
        }

        Class componentType = null;
        String componentTypeName =
                        arrayTypeValueLocalPart.substring(0, leftBracketIndex);
        
        if (componentTypeName.endsWith("]"))
        {
            // If the componentTypeName is an array, use soap_enc:Array
            // with a componentType of ArrayList.class
            arrayItemType = new QName(Constants.URI_SOAP_ENC, "Array");
            componentType = ArrayList.class;
        }
        else
            arrayItemType = new QName(arrayTypeValueNamespaceURI,
                                      componentTypeName);

        String lengthStr =
                       arrayTypeValueLocalPart.substring(leftBracketIndex + 1,
                                                         rightBracketIndex);
        
        if (lengthStr.length() > 0)
        {
            try
            {
                StringTokenizer tokenizer = new StringTokenizer(lengthStr, "[],");

                length = Integer.parseInt(tokenizer.nextToken());

                if (tokenizer.hasMoreTokens())
                    {
                        // If the array is passed as a multi-dimensional array
                        // (i.e. int[2][3]) then store all of the mult-dim lengths.
                        // The valueReady method uses this array to set the
                        // proper mult-dim element.
                        mDimLength = new ArrayList();
                        mDimLength.add(new Integer(length));
                        
                        while(tokenizer.hasMoreTokens()) {
                            mDimLength.add(new Integer(Integer.parseInt(tokenizer.nextToken())));
                        }
                    }


                // If the componentType was not already determined to be an 
                // array, go and get it.
                if (componentType == null)
                    componentType = context.getTypeMappingRegistry().
                                              getClassForQName(arrayItemType);
                
                if (componentType == null)
                    throw new SAXException(
                            JavaUtils.getMessage("noComponent00",  "" + arrayItemType));
                
                
                // Create an ArrayListExtension class to store the ArrayList
                // plus converted objects.
                ArrayList list = new ArrayListExtension(length);
                // ArrayList lacks a setSize(), so...
                for (int i = 0; i < length; i++) {
                    list.add(null);
                }
                value = list;

            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException(
                        JavaUtils.getMessage("badInteger00", lengthStr));
            }
        }
        else
        {
            // asize with no integers: size must be determined by inspection
            // of the actual members.
            value = new ArrayListExtension();
        }
        
        String offset = attributes.getValue(Constants.URI_SOAP_ENC,
                                            Constants.ATTR_OFFSET);
        if (offset != null) {
            leftBracketIndex = offset.lastIndexOf('[');
            rightBracketIndex = offset.lastIndexOf(']');

            if (leftBracketIndex == -1
                || rightBracketIndex == -1
                || rightBracketIndex < leftBracketIndex)
            {
                throw new SAXException(
                        JavaUtils.getMessage("badOffset00", offset));
            }

            curIndex = convertToIndex(offset.substring(leftBracketIndex + 1,rightBracketIndex),
                                      "badOffset00");
        }
        
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", "ArraySerializer.startElement()"));
        }
    }
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", "ArraySerializer.onStartChild()"));
        }
        
        if (attributes != null) {
            String pos = attributes.getValue(Constants.URI_SOAP_ENC,
                                             Constants.ATTR_POSITION);
            if (pos != null) {
                int leftBracketIndex = pos.lastIndexOf('[');
                int rightBracketIndex = pos.lastIndexOf(']');

                if (leftBracketIndex == -1
                    || rightBracketIndex == -1
                    || rightBracketIndex < leftBracketIndex)
                {
                    throw new SAXException(
                            JavaUtils.getMessage("badPosition00", pos));
                }
                
                curIndex = convertToIndex(pos.substring(leftBracketIndex + 1,rightBracketIndex),
                                          "badPosition00");
            }

            // If the xsi:nil attribute, set the value to null and return since
            // there is nothing to deserialize.
            String nil = null;
            for (int i=0; i<Constants.URIS_SCHEMA_XSI.length && nil==null; i++)
                nil = attributes.getValue(Constants.URIS_SCHEMA_XSI[i], "nil");
            if (nil != null && nil.equals("true")) {
              valueReady(null, new Integer(curIndex++));
              return null;
            }
        }
        
        QName itemType = context.getTypeFromAttributes(namespace,
                                                       localName,
                                                       attributes);
        if (itemType == null)
            itemType = arrayItemType;
        
        Deserializer dSer = context.getTypeMappingRegistry().
                                        getDeserializer(itemType);
        // Register the call back and indicate to wait for 
        // index to be set before calling completing
        dSer.registerCallback(this, new Integer(curIndex));
        waiting.add(new Integer(curIndex));

        curIndex++;
        
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", "ArraySerializer.onStartChild()"));
        }
        return dSer;
    }

    /** 
     * Need to wait for all indices to be set.
     */
    protected boolean componentsReady() {
        return (waiting.size() == 0);
    }

    /**
     * valueReady is called during deserialization to assign
     * the Object value to the array position indicated by hint.
     * The hint is always a single Integer.  If the array being
     * deserialized is a multi-dimensional array, the hint is 
     * converted into a series of indices to set the correct
     * nested position.
     * The array deserializer always deserializes into
     * an ArrayList, which is converted and copied into the
     * actual array after completion (by valueComplete).
     * It is important to wait until all indices have been 
     * processed before invoking valueComplete.
     * @param value value of the array element
     * @param hint index of the array element (Integer)
     **/
    public void valueReady(Object value, Object hint) throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("gotValue00", "ArraySerializer", "[" + hint +
                               "] = " + value));
        }
        ArrayList list = (ArrayList)this.value;
        int offset = ((Integer)hint).intValue();

        if (this.mDimLength == null) {
            // Normal Case: Set the element in the list
            // grow the list if necessary to accomodate the new member
            while (list.size() <= offset) {
                list.add(null);
            }

            list.set(offset, value);
        } else {
            // Multi-Dim Array case:  Need to find the nested ArrayList
            // and set the proper element.

            // Convert the offset into a series of indices
            ArrayList mDimIndex = toMultiIndex(offset);

            // Get/Create the nested ArrayList
            for(int i=0; i < mDimLength.size(); i++) {
                int length = ((Integer)mDimLength.get(i)).intValue();
                int index  = ((Integer)mDimIndex.get(i)).intValue();
                while (list.size() < length) {
                    list.add(null);
                }
                // If not the last dimension, get the nested ArrayList
                // Else set the value
                if (i < mDimLength.size()-1) {
                    if (list.get(index) == null) {
                        list.set(index, new ArrayList());
                    }
                    list = (ArrayList) list.get(index);                    
                } else {
                    list.set(index, value);
                }
            }
        }
        // If all indices are accounted for, the array is complete.
        waiting.remove(hint);
        if (isEnded && waiting.size()==0) {
            valueComplete();
        }
    }
    
    /**
     * Converts the given string to an index.
     * Assumes the string consists of a brackets surrounding comma 
     * separated digits.  For example "[2]" or [2,3]".
     * The routine returns a single index.
     * For example "[2]" returns 2.
     * For example "[2,3]" depends on the size of the multiple dimensions.
     *   if the dimensions are "[3,5]" then 13 is returned (2*5) + 3.
     * @param string representing index text
     * @param exceptKey exception message key
     * @return index 
     */
    private int convertToIndex(String text, String exceptKey) throws SAXException {
        StringTokenizer tokenizer = new StringTokenizer(text, "[],");
        int index = 0;
        try {
            if (mDimLength == null) {
                // Normal Case: Single dimension
                index = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens())
                    throw new SAXException(JavaUtils.getMessage(exceptKey, text));
            }
            else {
                // Multiple Dimensions: 
                int dim = -1;
                ArrayList work = new ArrayList();
                while(tokenizer.hasMoreTokens()) {
                    // Problem if the number of dimensions specified exceeds
                    // the number of dimensions of arrayType
                    dim++;
                    if (dim >= mDimLength.size())
                        throw new SAXException(JavaUtils.getMessage(exceptKey, text));

                    // Get the next token and convert to integer
                    int workIndex = Integer.parseInt(tokenizer.nextToken());

                    // Problem if the index is out of range.                     
                    if (workIndex < 0 || 
                        workIndex >= ((Integer)mDimLength.get(dim)).intValue())
                        throw new SAXException(JavaUtils.getMessage(exceptKey, text));
                    
                    work.add(new Integer(workIndex));
                }
                index = toSingleIndex(work); // Convert to single index
            }
        } catch (SAXException e) {
            throw e;
        } catch (Exception e) {
            throw new SAXException(JavaUtils.getMessage(exceptKey, text));
        }
        return index;
    } 

    /**
     * Converts single index to list of multiple indices.
     * @param single index
     * @return list of multiple indices or null if not multiple indices.
     */
    private ArrayList toMultiIndex(int single) {
        if (mDimLength == null) 
            return null;

        // Calculate the index factors if not already known
        if (mDimFactor == null) {
            mDimFactor = new ArrayList();
            for (int i=0; i < mDimLength.size(); i++) {
                int factor = 1;
                for (int j=i+1; j<mDimLength.size(); j++) {
                    factor *= ((Integer)mDimLength.get(j)).intValue();
                }
                mDimFactor.add(new Integer(factor));
            }
        }

        ArrayList rc = new ArrayList();
        for (int i=0; i < mDimLength.size(); i++) {
            int factor = ((Integer)mDimFactor.get(i)).intValue();
            rc.add(new Integer(single / factor));
            single = single % factor;
        }
        return rc;
    }

    /**
     * Converts multiple index to single index.
     * @param Array list of multiple indices
     * @return single index
     */
    private int toSingleIndex(ArrayList indexArray) {
        if (mDimLength == null || indexArray == null) 
            return -1;

        // Calculate the index factors if not already known
        if (mDimFactor == null) {
            mDimFactor = new ArrayList();
            for (int i=0; i < mDimLength.size(); i++) {
                int factor = 1;
                for (int j=i+1; j<mDimLength.size(); j++) {
                    factor *= ((Integer)mDimLength.get(j)).intValue();
                }
                mDimFactor.add(new Integer(factor));
            }
        }

        int single = 0;
        for (int i=0; i < indexArray.size(); i++) {
            single += ((Integer)mDimFactor.get(i)).intValue()*((Integer)indexArray.get(i)).intValue();
        }
        return single;
    }


    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value == null)
            throw new IOException(JavaUtils.getMessage("cantDoNullArray00"));
        
        Class cls = value.getClass();
        List list = null;
        
        if (!cls.isArray()) {
            if (!(value instanceof List)) {
                throw new IOException(
                        JavaUtils.getMessage("cantSerialize00", cls.getName()));
            }
            list = (List)value;
        }
        
        Class componentType;
        if (list == null) {
            componentType = cls.getComponentType();
        } else {
            if (list.isEmpty()) {
                componentType = Object.class;
            } else {
                componentType = list.get(0).getClass();
            }
        }

        // Check to see if componentType is also an array.
        // If so, set the componentType to the most nested non-array 
        // componentType.  Increase the dims string by "[]"
        // each time through the loop.  
        // Note from Rich Scheuerle:
        //    This won't handle Lists of Lists or
        //    arrays of Lists....only arrays of arrays.
        String dims = "";
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
            dims += "[]";
        }

       
        QName componentQName = context.getQNameForClass(componentType);
        if (componentQName == null)
            throw new IOException(
                    JavaUtils.getMessage("noType00", componentType.getName()));
        String prefix = context.getPrefixForURI(componentQName.getNamespaceURI());
        String compType = prefix + ":" + componentQName.getLocalPart();
        int len = (list == null) ? Array.getLength(value) : list.size();

        String arrayType = compType + dims + "[" + len + "]";
        
        
        // Discover whether array can be serialized directly as a two-dimensional
        // array (i.e. arrayType=int[2,3]) versus an array of arrays.
        // Benefits:
        //   - Less text passed on the wire.
        //   - Easier to read wire format
        //   - Tests the deserialization of multi-dimensional arrays.
        // Drawbacks:
        //   - Is not safe!  It is possible that the arrays are multiply
        //     referenced.  Transforming into a 2-dim array will cause the 
        //     multi-referenced information to be lost.  Plus there is no
        //     way to determine whether the arrays are multi-referenced.
        //     Thus the code is currently disabled (see enable2Dim below).
        //   
        // In the future this code may be enabled for cases that we know
        // are safe.
        // (More complicated processing is necessary for 3-dim arrays, etc.
        // This initial support is mainly used to test deserialization.)
        //
        int dim2Len = -1;
        boolean enable2Dim = false;  // Disabled
        if (enable2Dim && !dims.equals("")) {
            if (cls.isArray() && len > 0) {
                boolean okay = true;
                // Make sure all of the component arrays are the same size
                for (int i=0; i < len && okay; i++) {
                
                    Object elementValue = Array.get(value, i);
                    if (elementValue == null)
                        okay = false;
                    else if (dim2Len < 0) {
                        dim2Len = Array.getLength(elementValue);
                        if (dim2Len <= 0) {
                            okay = false;
                        }
                    } else if (dim2Len != Array.getLength(elementValue)) {
                        okay = false;
                    }
                }
                // Update the arrayType to use mult-dim array encoding
                if (okay) {
                    dims = dims.substring(0, dims.length()-2);
                    arrayType = compType + dims + "[" + len + "," + dim2Len + "]";
                } else {
                    dim2Len = -1;
                }
            }
        }
        
        Attributes attrs = attributes;
        
        if (attributes != null &&
            attributes.getIndex(Constants.URI_SOAP_ENC,
                                Constants.ATTR_ARRAY_TYPE) == -1) {
            String encprefix = context.getPrefixForURI(Constants.URI_SOAP_ENC);
            AttributesImpl attrImpl = new AttributesImpl(attributes);
            attrImpl.addAttribute(Constants.URI_SOAP_ENC, 
                                  Constants.ATTR_ARRAY_TYPE,
                                  encprefix + ":arrayType",
                                  "CDATA",
                                  arrayType);
            attrs = attrImpl;
        }
        
        context.startElement(name, attrs);

        if (dim2Len < 0) {
            // Normal case, serialize each array element
            for (int index = 0; index < len; index++)
                context.serialize(new QName("","item"), null,
                                  (list == null) ? Array.get(value, index) :
                                  list.get(index));
        } else {
            // Serialize as a 2 dimensional array
            for (int index = 0; index < len; index++) {
                for (int index2 = 0; index2 < dim2Len; index2++) {
                   context.serialize(new QName("","item"), null,
                                     Array.get(Array.get(value, index), index2));
                }
            }
        }

        
        context.endElement();
    }
}
