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
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;

/**
 * @author dbyrne
 *
 * Created in response to AXIS-2088.  This class exposes a handful of static utility
 * methods that are used to manipulate backslash chars w/in the context of QName objects.
 * 
 */
public class BackslashUtil implements java.io.Serializable {

   /** 
     * @param suspectQName QName[local] that may contain unescaped backslashes
     * @return QName[local] w/ no backslashes
     */
	
	public static QName getQNameWithBackslashlessLocal(QName suspectQName) {
		String trustedString = null;

		// get a wsdl:service[@name] that we can trust
		trustedString = stripBackslashes(suspectQName.getLocalPart());
		return getQNameWithDifferentLocal(suspectQName, trustedString);
	}

   /** 
     * @param suspectQName QName[local] which may contain unescaped backslashes
     * @return QName[local] w/ escaped backslashes
     */
	
	public static QName getQNameWithBackslashedLocal(QName suspectQName) {
		String trustedString = null;

		// get a wsdl:service[@name] with safe backslashes
		trustedString = applyBackslashes(suspectQName.getLocalPart());
		return getQNameWithDifferentLocal(suspectQName, trustedString);
	}	
	
   /** 
    * Creates a copy of the supplied QName w/ the supplied local name  
    */	
	public static QName getQNameWithDifferentLocal(QName qName, String localName) {
		QName trustedQName = null;

		// recreate the QName, only w/ a local name we can trust.
		trustedQName = new QName(qName.getNamespaceURI(), localName, qName.getPrefix());
		return trustedQName;
	}
	
	/**
	 * Slave method for getQNameWithBackslashedLocal()
	 */
	public static String applyBackslashes(String string) {
		return transformBackslashes(string, false);
	}
	
	/**
	 * Slave method for getQNameWithBackslashlessLocal
	 */
	public static String stripBackslashes(String string) {
		return transformBackslashes(string, true);
	}
	
	/**
	 * Slave method for applyBackslashes & stripBackslashes .
	 * 
	 */
	public static String transformBackslashes(String string, boolean delete) {
		byte[] suspectBytes = null;
		StringBuffer stringBuffer = null;
		
		suspectBytes = string.getBytes();
		stringBuffer = new StringBuffer(string);
		
		for (int b = suspectBytes.length - 1; b >= 0; b--) {
			if (suspectBytes[b] == 92) {
				if(delete){
					stringBuffer.delete(b, b + 1);
				}else{
					stringBuffer.insert(b, "\\");
				}
			}
		}
		return stringBuffer.toString();
	}
}