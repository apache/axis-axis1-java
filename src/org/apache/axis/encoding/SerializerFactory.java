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
 * This interface describes the AXIS SerializerFactory.
 *
 * An Axis compliant Serializer Factory must provide one or more 
 * of the following methods:
 *
 * public static create(Class javaType, QName xmlType)
 * public <constructor>(Class javaType, QName xmlType)
 * public <constructor>()
 *
 * The deployment code will attempt to invoke these methods in the above order.
 * The xmlType, javaType arguments are filled in with the values supplied during the
 * deployment registration of the factory.
 */
public interface SerializerFactory extends javax.xml.rpc.encoding.SerializerFactory {
}


