package org.apache.axis.message.api;

import org.apache.axis.message.codec.*;

public interface RPCElement extends MessageElement 
{
	public String getMethodName();
	public String getMethodNamespaceURI();
	public MessageElementEntry setMethodName(String name, String namespaceURI);
	public Object[] getArgs();
	public Object getArg(String name);
	public Object getArg(String name, TypeCodec codec);
	public MessageElementEntry addArg(String name, String namespaceURI, Object value);
	public MessageElementEntry addArg(String name, String namespaceURI, Object value, TypeCodec codec);
}
