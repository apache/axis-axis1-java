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


package org.apache.axis.encoding;

/**
 * This interface describes the AXIS TypeMappingRegistry.
 */
public interface TypeMappingRegistry 
    extends javax.xml.rpc.encoding.TypeMappingRegistry {
    /**
     * delegate
     *
     * Changes the contained type mappings to delegate to 
     * their corresponding types in the secondary TMR.
     */
    public void delegate(TypeMappingRegistry secondaryTMR);
    
    /**
     * Obtain a type mapping for the given encoding style.  If no specific
     * mapping exists for this encoding style, we will create and register
     * one before returning it.
     * 
     * @param encodingStyle
     * @return a registered TypeMapping for the given encoding style
     */ 
    public TypeMapping getOrMakeTypeMapping(String encodingStyle);
}


