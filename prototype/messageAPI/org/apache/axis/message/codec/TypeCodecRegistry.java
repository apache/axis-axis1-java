package org.apache.axis.message.codec;

import java.util.Hashtable;
import org.apache.axis.util.xml.QName;
import org.apache.axis.util.StringUtils;

public class TypeCodecRegistry
{
	private Hashtable xml2java = new Hashtable();
	private Hashtable java2xml = new Hashtable();
	
	public void registerCodec(Class javaType, QName elementType, TypeCodec codec) {
		if (javaType != null && elementType != null && codec != null) {
			xml2java.put(elementType, codec);
			java2xml.put(getClassName(javaType), codec);
		}
	}
	
	public TypeCodec queryCodec(Class javaType) throws IllegalArgumentException {
		TypeCodec codec = (TypeCodec)java2xml.get(getClassName(javaType));
		if (codec != null)
			return codec;
		else {
			codec = (TypeCodec)java2xml.get(null);
			if (codec != null)
				return codec;
			else {
				throw new IllegalArgumentException("No Codec Registered For " + getClassName(javaType));
			}
		}
	}
	
	public TypeCodec queryCodec(QName elementType) throws IllegalArgumentException  {
		TypeCodec codec = (TypeCodec)xml2java.get(elementType);
		if (codec != null)
			return codec;
		else {
			codec = (TypeCodec)xml2java.get(null);
			if (codec != null)
				return codec;
			else {
				throw new IllegalArgumentException("No Codec Registered For " + elementType);
			}
		}
	}
	
	private static String getClassName(Class javaType) {
		return javaType != null ? StringUtils.getClassName(javaType) : "null";
	}
}
