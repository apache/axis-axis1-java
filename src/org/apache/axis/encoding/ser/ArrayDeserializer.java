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

package org.apache.axis.encoding.ser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.Constants;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
import java.util.List;
import java.util.StringTokenizer;
import java.beans.IntrospectionException;


/**
 * An ArrayDeserializer handles deserializing SOAP
 * arrays.
 * 
 * Some code borrowed from ApacheSOAP - thanks to Matt Duftler!
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 * 
 * Multi-reference stuff:
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class ArrayDeserializer extends DeserializerImpl implements Deserializer  {

    static Category category =
            Category.getInstance(ArrayDeserializer.class.getName());

    public QName arrayType = null;
    public int curIndex = 0;
    QName arrayItemType;
    int length;
    ArrayList mDimLength = null;  // If set, array of multi-dim lengths 
    ArrayList mDimFactor = null;  // If set, array of factors for multi-dim arrays
    HashSet waiting = new HashSet();  // List of indices waiting for completion


    /**
     * This method is invoked after startElement when the element requires
     * deserialization (i.e. the element is not an href and the value is not nil.)
     * DeserializerImpl provides default behavior, which simply
     * involves obtaining a correct Deserializer and plugging its handler.
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param qName is the prefixed qname of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void onStartElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", "ArrayDeserializer.startElement()"));
        }

        QName arrayTypeValue = context.getQNameFromString(
                      Constants.getValue(attributes,
                                         Constants.URI_CURRENT_SOAP_ENC,
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
            arrayItemType = new QName(Constants.URI_CURRENT_SOAP_ENC, "Array");
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
                    componentType = context.getTypeMapping().
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
        
        String offset = Constants.getValue(attributes,
                                         Constants.URI_CURRENT_SOAP_ENC,
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
            category.debug(JavaUtils.getMessage("exit00", "ArrayDeserializer.startElement()"));
        }
    }
    

    /**
     * onStartChild is called on each child element.
     * @param namespace is the namespace of the child element
     * @param localName is the local name of the child element
     * @param prefix is the prefix used on the name of the child element
     * @param attributes are the attributes of the child element
     * @param context is the deserialization context.
     * @return is a Deserializer to use to deserialize a child (must be
     * a derived class of SOAPHandler) or null if no deserialization should
     * be performed.
     */
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", "ArrayDeserializer.onStartChild()"));
        }
        
        if (attributes != null) {
            String pos =
                Constants.getValue(attributes,
                                   Constants.URI_CURRENT_SOAP_ENC,
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
            if (context.isNil(attributes)) {
                setValue(null, new Integer(curIndex++));
                return null;
            }
        }
        
        QName itemType = context.getTypeFromAttributes(namespace,
                                                       localName,
                                                       attributes);
        if (itemType == null)
            itemType = arrayItemType;
        
        Deserializer dSer = context.getDeserializerForType(itemType);
        if (dSer == null) {
            dSer = new DeserializerImpl();  
        }

        // Register the callback value target, and
        // keep track of this index so we know when it has been set.
        dSer.registerValueTarget(new DeserializerTarget(this, new Integer(curIndex)));
        waiting.add(new Integer(curIndex));

        curIndex++;
        
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", "ArrayDeserializer.onStartChild()"));
        }
        return (SOAPHandler) dSer;
    }

    /** 
     * Need to wait for all indices to be set.
     */
    public boolean componentsReady() {
        return (waiting.size() == 0);
    }

    /**
     * set is called during deserialization to assign
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
    public void setValue(Object value, Object hint) throws SAXException
    { 
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("gotValue00", "ArrayDeserializer", "[" + hint +
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

    /**
     * During processing, the Array Deserializer stores the array in 
     * an ArrayListExtension class.  This class contains all of the
     * normal function of an ArrayList, plus it keeps a list of the
     * converted array values.  This class is essential to support
     * arrays that are multi-referenced.
     **/
    public class ArrayListExtension extends ArrayList implements JavaUtils.ConvertCache {
        private HashMap table = null;
        
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
                table = new HashMap();
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
   
}
