package org.apache.axis.message.soap.codec;

import org.apache.axis.message.codec.*;
import org.apache.axis.message.soap.*;

public class SOAPCodecRegistry extends TypeCodecRegistry {
	
	public SOAPCodecRegistry() {		
		TypeCodec soapenc = new SOAPCodec();
		registerCodec(String.class, Constants.stringQName, soapenc);
		registerCodec(Boolean.class, Constants.booleanQName, soapenc);
		registerCodec(Double.class, Constants.doubleQName, soapenc);
		registerCodec(Float.class, Constants.floatQName, soapenc);
		registerCodec(Long.class, Constants.longQName, soapenc);
		registerCodec(Integer.class, Constants.intQName, soapenc);
		registerCodec(Short.class, Constants.shortQName, soapenc);
		registerCodec(Byte.class, Constants.byteQName, soapenc);
	}
	
}
