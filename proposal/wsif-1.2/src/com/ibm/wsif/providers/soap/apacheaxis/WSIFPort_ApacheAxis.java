// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.

package com.ibm.wsif.providers.soap.apacheaxis;

import javax.wsdl.extensions.soap.*;
import com.ibm.wsdl.extensions.jms.*;
import com.ibm.wsif.WSIFException;
import com.ibm.wsif.WSIFMessage;
import com.ibm.wsif.providers.WSIFDynamicTypeMap;
import com.ibm.wsif.util.*;
import com.ibm.wsif.util.jms.*;
import com.ibm.wsif.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.wsdl.*;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.client.Transport;
//import org.apache.axis.transport.jms.MQJMSConstants;
//import org.apache.axis.transport.jms.MQJMSTransport;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;

// Referenced classes of package com.ibm.wsif.providers.soap.apacheaxis:
//            WSIFOperation_ApacheAxis

public class WSIFPort_ApacheAxis extends WSIFDefaultPort
{

	protected Map operationInstances;
	protected Port port;
	protected Definition definition;
	protected URL url=null;
	protected int transportcode;
  	protected Transport st;
	private static final boolean DEBUG = false;
	private static final int HTTP_TRANSPORT = 1;
	private static final int JMS_TRANSPORT = 2;
	private static final int MQJMS_TRANSPORT = 3;
	private static final String CONTEXT_FACTORY = "contextFactory";
	private static final String PROVIDER_URL = "providerURL";
	private static final String FACTORY = "factory";
	private static final String REPLY_QUEUE = "replyQueue";
	private static final String HOST = "host";
	private static final String CHANNEL = "channel";
	private static final String PORT = "port";
    private static final String CCSID = "ccsid";

    public WSIFPort_ApacheAxis(Definition definition1, 
                               Service service, 
                               Port port1, 
                               WSIFDynamicTypeMap wsifdynamictypemap)
        throws WSIFException
    {
        operationInstances = new HashMap();
        setDefinition(definition1);
        setPort(port1);
        
        JmsAddress jmsaddress = (JmsAddress) getExtElem(
            port1, JmsAddress.class, port1.getExtensibilityElements());
        SOAPAddress soapaddress = (SOAPAddress)getExtElem(
            port1, SOAPAddress.class, port1.getExtensibilityElements());
        
        if (soapaddress!=null && jmsaddress!=null) throw new WSIFException(
          "Both soap:address and jms:address cannot be specified for port "+port);

        if (soapaddress==null && jmsaddress==null) throw new WSIFException(
          "Either soap:address or jms:address must be specified for port "+port);

        boolean isNotHTTP = false;
        if(soapaddress != null)
        {
		  String s = soapaddress.getLocationURI();
          try
		  {
			url = new URL(s);
		  }
		  catch(MalformedURLException malformedurlexception)
		  {
			throw new WSIFException("could not set SOAP address to " + s, 
			                        malformedurlexception);
		  }
        }
        else
        {
          isNotHTTP=true;
        }        
        
        if(url == null && !isNotHTTP)
            throw new WSIFException("soap:address with location URI is required for " + port1);
        String s1 = null;
        Binding binding = port1.getBinding();
        SOAPBinding soapbinding = (SOAPBinding)getExtElem(binding, javax.wsdl.extensions.soap.SOAPBinding.class, binding.getExtensibilityElements());
        if(soapbinding != null)
        {
            s1 = soapbinding.getStyle();
            if(!"rpc".equals(s1))
                throw new WSIFException("unsupported style " + s1 + " for " + soapbinding);
            String s2 = soapbinding.getTransportURI();
            if("http://schemas.xmlsoap.org/soap/http".equals(s2)) {
				transportcode = HTTP_TRANSPORT;
				st = new HTTPTransport();
			} else if ("http://schemas.xmlsoap.org/soap/jms".equals(s2)) {
				transportcode = JMS_TRANSPORT;
				st = new WSIFJmsTransport();
//	HACK		} else if ("http://schemas.xmlsoap.org/soap/mqjms".equals(s2)) {
//	HACK			transportcode = MQJMS_TRANSPORT;
//	HACK			st = new MQJMSTransport();
			} else {
                throw new WSIFException("unsupported transport " + s2 + " for " + soapbinding);
			}
        }
        
        if (transportcode==JMS_TRANSPORT) 
        {
          WSIFJmsDestination jmsDestination = 
            new WSIFJmsDestination(WSIFJmsFinder.newFinder(jmsaddress), 
                                   jmsaddress.getJmsProvDestName(), 
                                   WSIFProperties.getSyncTimeout());
            
          ((WSIFJmsTransport)st).setDestination(jmsDestination);
        }
        
        if(s1 == null)
            s1 = "document";
        PortType porttype = binding.getPortType();
        List list = porttype.getOperations();
        Operation operation;
        WSIFOperation_ApacheAxis wsifoperation_apacheaxis;
        for(Iterator iterator = list.iterator(); 
            iterator.hasNext(); 
            setDynamicWSIFOperation(operation.getName(), 
                                    operation.getInput().getName(), 
                                    operation.getOutput()==null?null:operation.getOutput().getName(), 
                                    wsifoperation_apacheaxis))
        {
            operation = (Operation)iterator.next();
            String s3 = operation.getName();
            Input input = operation.getInput();
            Output output = operation.getOutput();
            if(input == null)
                throw new WSIFException("missing input message for operation " + s3);
            wsifoperation_apacheaxis = new WSIFOperation_ApacheAxis(this, operation, wsifdynamictypemap);
            BindingOperation bindingoperation = 
              binding.getBindingOperation(s3, input.getName(), output==null?null:output.getName());
            if(bindingoperation == null)
                throw new WSIFException("mising required in WSDL 1.1 binding operation for " + s3);
            SOAPOperation soapoperation = (SOAPOperation)getExtElem(bindingoperation, javax.wsdl.extensions.soap.SOAPOperation.class, bindingoperation.getExtensibilityElements());
            if(soapoperation == null)
                throw new WSIFException("soapAction must be specified in  required by WSDL 1.1 soap:operation binding for " + bindingoperation);
            String s4 = soapoperation.getSoapActionURI();
            wsifoperation_apacheaxis.setSoapActionURI(s4);
            String s5 = soapoperation.getStyle();
            if(s5 != null && !"rpc".equals(s5))
                throw new WSIFException("unsupported style " + s1 + " for operation " + s3);
            if(!"rpc".equals(s1))
                throw new WSIFException("default soap style must be rpc if operation " + s3 + " binding has not style attribute");
            BindingInput bindinginput = bindingoperation.getBindingInput();
            SOAPBody soapbody = (SOAPBody)getExtElem(bindinginput, javax.wsdl.extensions.soap.SOAPBody.class, bindinginput.getExtensibilityElements());
            if(soapbody != null)
            {
                String s6 = soapbody.getNamespaceURI();
                wsifoperation_apacheaxis.setInputNamespace(s6);
                String s7 = soapbody.getUse();
                if(!"encoded".equals(s7))
                    throw new WSIFException("unsupported use " + s7 + " in " + soapoperation);
                List list1 = soapbody.getEncodingStyles();
                if(list1 != null)
                {
                    list1.size();
                    wsifoperation_apacheaxis.setInputEncodingStyle((String)list1.get(0));
                }
                List list2 = soapbody.getParts();
                if(list2 != null)
                    wsifoperation_apacheaxis.setPartNames(list2);
            }

            SOAPHeader soapheader = (SOAPHeader)getExtElem(bindinginput, javax.wsdl.extensions.soap.SOAPHeader.class, bindinginput.getExtensibilityElements());
            if(soapheader != null)
                throw new WSIFException("not supported input soap:header " + soapheader);

            List inJmsAttrs = getExtElems(bindinginput, JmsAttribute.class, bindinginput.getExtensibilityElements());
            if (inJmsAttrs!=null && inJmsAttrs.size()>0)
            {
              if (st instanceof WSIFJmsTransport) wsifoperation_apacheaxis.setInputJmsAttributes(inJmsAttrs);
              else throw new WSIFException("jms:attributes found in non-jms binding");
            }
                
            JmsHeader jmsHeader = (JmsHeader)getExtElem(bindinginput,JmsHeader.class,bindinginput.getExtensibilityElements());
            if (jmsHeader!=null)
            {
              if (st instanceof WSIFJmsTransport) wsifoperation_apacheaxis.setJmsHeader(jmsHeader.getValue());
              else throw new WSIFException("jms:header found in non-jms binding");
            }

            BindingOutput bindingoutput = bindingoperation.getBindingOutput();
            if (bindingoutput!=null)
            {
              SOAPBody soapbody1 = (SOAPBody)getExtElem(bindingoutput, javax.wsdl.extensions.soap.SOAPBody.class, bindingoutput.getExtensibilityElements());
              if(soapbody1 != null)
              {
                String s8 = soapbody1.getUse();
                if(!"encoded".equals(s8))
                    throw new WSIFException("unsupported use " + s8 + " in " + soapoperation);
                List list3 = soapbody1.getParts();
                if(list3 != null && list3.size() > 0)
                    wsifoperation_apacheaxis.setReturnName((String)list3.get(0));
              }
              soapheader = (SOAPHeader)getExtElem(bindingoutput, javax.wsdl.extensions.soap.SOAPHeader.class, bindingoutput.getExtensibilityElements());
              if(soapheader != null)
                  throw new WSIFException("not supported output soap:header " + soapheader);
              for(Iterator iterator1 = bindingoperation.getBindingFaults().values().iterator(); iterator1.hasNext();)
              {
                BindingFault bindingfault = (BindingFault)iterator1.next();
                SOAPFault soapfault = (SOAPFault)getExtElem(bindingfault, javax.wsdl.extensions.soap.SOAPFault.class, bindingfault.getExtensibilityElements());
              }
              List outJmsAttrs = getExtElems(bindingoutput, JmsAttribute.class, bindingoutput.getExtensibilityElements());
              if (outJmsAttrs!=null && outJmsAttrs.size()>0)
              {
                if (st instanceof WSIFJmsTransport) wsifoperation_apacheaxis.setOutputJmsAttributes(outJmsAttrs);
                else throw new WSIFException("jms:attributes found in non-jms binding");
              }
            }

        }

    }

    /**
     * @deprecated
     */
    public boolean executeRequestResponseOperation(String s, WSIFMessage wsifmessage, WSIFMessage wsifmessage1, WSIFMessage wsifmessage2)
        throws WSIFException
    {
        WSIFOperation_ApacheAxis wsifoperation_apacheaxis = getDynamicWSIFOperation(s, wsifmessage.getName(), wsifmessage1.getName());
        if(wsifoperation_apacheaxis == null)
            throw new WSIFException("no operation named " + s + " is available in port " + port);
        else
            return wsifoperation_apacheaxis.invokeRequestResponseOperation(wsifmessage, wsifmessage1, wsifmessage2);
    }

    public Definition getDefinition()
    {
        return definition;
    }

    public WSIFOperation_ApacheAxis getDynamicWSIFOperation(String name, String inputName, String outputName)
    {
        WSIFOperation_ApacheAxis operation = (WSIFOperation_ApacheAxis)operationInstances.get(getKey(name, inputName, outputName));
	    if (operation == null)
	    {
	      	BindingOperation bindingOperationModel = 
	      	port.getBinding().getBindingOperation(name, inputName, outputName) ;
	      	
		    if(bindingOperationModel != null)
		    {
		    	// Only one operation matched in binding so find it in instances
		    	// from all the information that is available to us
		    	Iterator i = operationInstances.keySet().iterator();
		    	while (i.hasNext())
		    	{
		    		String key = (String) i.next();
		    		if ((outputName != null && key.endsWith(outputName))
		    			|| outputName == null)
		    		{
		    			String start = (inputName == null) ? name : name+":"+inputName;	
			    		if (key.startsWith(start))
			    		{
			    			if(operation != null)
			    			{
			    				// Duplicate operation found based on names!
			    				operation = null;
			    				break;
			    			}
			    			operation = (WSIFOperation_ApacheAxis) operationInstances.get(key);			    		
			    		}
		    		}
		    	}
		    }
	    }
	
	    return operation; 
    }

    public URL getEndPoint()
    {
        return url;
    }

    public Port getPort()
    {
        return port;
    }

	public Transport getAxisTransport() {
		return st;
	}

    public void setDefinition(Definition definition1)
    {
        definition = definition1;
    }

    public void setDynamicWSIFOperation(String s, String s1, String s2, WSIFOperation_ApacheAxis wsifoperation_apacheaxis)
    {
        operationInstances.put(getKey(s, s1, s2), wsifoperation_apacheaxis);
    }

    public WSIFOperation createOperation(String operationName) throws WSIFException {
      return createOperation(operationName, null, null);
    }
  
    public WSIFOperation createOperation(String operationName,
                                         String inputName,
                                         String outputName) throws WSIFException {
      WSIFOperation_ApacheAxis op=getDynamicWSIFOperation(
        operationName,inputName,outputName);
      if (op == null) {
        throw new WSIFException("Could not create operation: " + operationName
                                + ":" + inputName + ":" + outputName);
      }
      return op.copy();
  }
  
    public void setEndPoint(URL url1)
    {
        url = url1;
    }

    public void setPort(Port port1)
    {
        port = port1;
    }
    
    /**
     * Tests if this port supports asynchronous calls to operations.
     * 
     * @return true if the port is using a JMS transport, otherwise false
     */
    public boolean supportsAsync() {
       if ( st != null && JMS_TRANSPORT == transportcode ) {
          return true;
       } else {
       	  return false;
       }
    }
  
}
