/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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