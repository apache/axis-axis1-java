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
 * A simple type with several elements for testing serialization
 */
public class SuperBean  {
	
	// Definition of three elements
	private String zero;
	private String one;
	private String two;
	
	/**
	 * Returns the one.
	 * @return String
	 */
	public String getOne() {
		return one;
	}

	/**
	 * Returns the two.
	 * @return String
	 */
	public String getTwo() {
		return two;
	}

	/**
	 * Returns the zero.
	 * @return String
	 */
	public String getZero() {
		return zero;
	}

	/**
	 * Sets the one.
	 * @param one The one to set
	 */
	public void setOne(String one) {
		this.one = one;
	}

	/**
	 * Sets the two.
	 * @param two The two to set
	 */
	public void setTwo(String two) {
		this.two = two;
	}

	/**
	 * Sets the zero.
	 * @param zero The zero to set
	 */
	public void setZero(String zero) {
		this.zero = zero;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
		new org.apache.axis.description.TypeDesc(SuperBean.class);

	static {
		org.apache.axis.description.FieldDesc field = new org.apache.axis.description.ElementDesc();
		field.setFieldName("zero");
		field.setXmlName(new javax.xml.namespace.QName("", "zero"));
		field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		typeDesc.addFieldDesc(field);
		field = new org.apache.axis.description.ElementDesc();
		field.setFieldName("one");
		field.setXmlName(new javax.xml.namespace.QName("", "one"));
		field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		typeDesc.addFieldDesc(field);
		field = new org.apache.axis.description.ElementDesc();
		field.setFieldName("two");
		field.setXmlName(new javax.xml.namespace.QName("", "two"));
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