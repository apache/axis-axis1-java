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

package test.encoding;

/**
 * A type used for testing serialization of inherited elements
 */
public class DerivatedBean extends SuperBean {

	private String three;
	
	private String four;

	/**
	 * Returns the four.
	 * @return String
	 */
	public String getFour() {
		return four;
	}

	/**
	 * Returns the three.
	 * @return String
	 */
	public String getThree() {
		return three;
	}

	/**
	 * Sets the four.
	 * @param four The four to set
	 */
	public void setFour(String four) {
		this.four = four;
	}

	/**
	 * Sets the three.
	 * @param three The three to set
	 */
	public void setThree(String three) {
		this.three = three;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
		new org.apache.axis.description.TypeDesc(DerivatedBean.class);

	static {
		org.apache.axis.description.FieldDesc field = new org.apache.axis.description.ElementDesc();
		field.setFieldName("three");
		field.setXmlName(new javax.xml.namespace.QName("", "three"));
		field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		typeDesc.addFieldDesc(field);
		field = new org.apache.axis.description.ElementDesc();
		field.setFieldName("four");
		field.setXmlName(new javax.xml.namespace.QName("", "four"));
		field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		typeDesc.addFieldDesc(field);
	};

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}


}
