package org.apache.axis.message.soap.codec;

import org.apache.axis.message.codec.*;
import org.w3c.dom.*;

public class SOAPDecoder extends Decoder 
{
	public SOAPDecoder() { super(new SOAPCodecRegistry(), true); }
	public SOAPDecoder(boolean useExplicitTyping) { super(new SOAPCodecRegistry(), useExplicitTyping); }
	public SOAPDecoder(TypeCodecRegistry codecs) { super(codecs, true); }
	public SOAPDecoder(TypeCodecRegistry codecs, boolean useExplicitTyping) { super(codecs, useExplicitTyping); }
}
