package org.apache.axis.message.soap.codec;

import org.apache.axis.message.codec.*;
import org.w3c.dom.*;

public class SOAPEncoder extends Encoder
{
	public SOAPEncoder() { super(new SOAPCodecRegistry()); }
	public SOAPEncoder(TypeCodecRegistry codecs) { super(codecs); }	
}
