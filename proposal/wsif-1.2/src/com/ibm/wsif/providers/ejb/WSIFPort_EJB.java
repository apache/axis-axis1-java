// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.ejb;

import java.lang.reflect.*; 
import javax.naming.*;
import java.util.*;
import javax.ejb.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import com.ibm.wsdl.extensions.ejb.*;
import com.ibm.wsdl.extensions.format.*;
import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.providers.*;
import com.ibm.wsif.util.*;
/**
 * EJB WSIF Port.
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 * Partially based on WSIFPort_ApacheSOAP from Alekander Slominski,
 * Paul Fremantle, Sanjiva Weerawarana and Matthew J. Duftler
 */
public class WSIFPort_EJB
    extends com.ibm.wsif.util.WSIFDefaultPort
    implements java.io.Serializable // is this the right place for caching?
{
    private javax.wsdl.Definition fieldDefinition = null;
    private javax.wsdl.Port fieldPortModel = null;

    private EJBHome fieldEjbHome = null; // 'factory for physical connection'
    private EJBObject fieldEjbObject = null; // 'physical connection'

    protected Map operationInstances = new HashMap();

    public WSIFPort_EJB(Definition def, Port port, WSIFDynamicTypeMap typeMap) {
    	
        TraceLogger.getGeneralTraceLogger().entry(
          new Object[] {def.getQName(),port.getName(),typeMap});
          
        fieldDefinition = def;
        fieldPortModel = port;
        // System.out.println(def.getQName());
        TraceLogger.getGeneralTraceLogger().exit();
    }
    /**
      * Execute a request-response operation. The signature allows for
      * input, output and fault messages. WSDL in fact allows one to
      * describe the set of possible faults an operation may result
      * in, however, only one fault can occur at any one time.
      *
      * @param op name of operation to execute
      * @param input input message to send to the operation
      * @param output an empty message which will be filled in if
      *        the operation invocation succeeds. If it does not
      *        succeed, the contents of this message are undefined.
      *        (This is a return value of this method.)
      * @param fault an empty message which will be filled in if
      *        the operation invocation fails. If it succeeds, the
      *        contents of this message are undefined. (This is a
      *        return value of this method.)
      *
      * @return true or false indicating whether a fault message was
      *         generated or not. The truth value indicates whether
      *         the output or fault message has useful information.
      *
      * @exception WSIFException if something goes wrong.
      * @deprecated
      * NOTE: fault processing is not yet implemented.
      */

    public boolean executeRequestResponseOperation(
        String operationName,
        WSIFMessage input,
        WSIFMessage output,
        WSIFMessage fault)
        throws WSIFException {
        	
        // find correct operation
        WSIFOperation_EJB operation =
            getDynamicWSIFOperation(operationName, input.getName(), output.getName());
        if (operation == null) {
            throw new WSIFException(
                "No operation named '"
                    + operationName
                    + "' found in port "
                    + fieldPortModel.getName());
        }

        // and invoke it
        //return operation.invokeRequestResponseOperation(input, output, fault);
        return operation.executeRequestResponseOperation(input, output, fault);
    }

    /**
    * Execute an input only operation. The signature allows for
    * only an input message. 
    * 
    * @param op name of operation to execute
    * @param input input message to send to the operation
    * @exception WSIFException if something goes wrong.
    * @deprecated
    */
    public void executeInputOnlyOperation(String operationName, WSIFMessage input)
        throws WSIFException {
        // find correct operation
        WSIFOperation_EJB operation =
            getDynamicWSIFOperation(operationName, input.getName(), null);
        if (operation == null) {
            throw new WSIFException(
                "No operation named '"
                    + operationName
                    + "' found in port "
                    + fieldPortModel.getName());
        }

        // and invoke it
        operation.executeInputOnlyOperation(input);
    }

    public Definition getDefinition() {
        return fieldDefinition;
    }

    public WSIFOperation_EJB getDynamicWSIFOperation(
        String name,
        String inputName,
        String outputName)
        throws WSIFException {
        WSIFOperation_EJB operation =
            (WSIFOperation_EJB) operationInstances.get(getKey(name, inputName, outputName));

        if (operation == null) {
            BindingOperation bindingOperationModel =
                fieldPortModel.getBinding().getBindingOperation(name, inputName, outputName);

            if (bindingOperationModel != null) {
                operation = new WSIFOperation_EJB(fieldPortModel, bindingOperationModel, this);
                setDynamicWSIFOperation(name, inputName, outputName, operation);
            }
        }

        return operation;
    }

    public EJBHome getEjbHome() throws WSIFException {
        if (fieldEjbHome == null) {
            EJBAddress address = null;

            try {
                ExtensibilityElement portExtension =
                    (ExtensibilityElement) fieldPortModel.getExtensibilityElements().get(0);

                if (portExtension == null) {
                    throw new WSIFException("missing port extension");
                }

                address = (EJBAddress) portExtension;
				
				Hashtable hash = new Hashtable();
				// Lookup from an authoritative source
				hash.put(InitialContext.AUTHORITATIVE, "true");
				InitialContext initContext;
				try {
                	initContext = new InitialContext(hash);
                	fieldEjbHome = (EJBHome) initContext.lookup(address.getJndiName());
				}
				catch (NoInitialContextException e) {
					// Attempt to use Websphere default settings
					try {
						Class testClass = Class.forName("com.ibm.websphere.naming.WsnInitialContextFactory", true, Thread.currentThread().getContextClassLoader());
						hash.put("java.naming.factory.initial","com.ibm.websphere.naming.WsnInitialContextFactory");
						hash.put("java.naming.provider.url","iiop://localhost:900/");
						initContext = null;
			           	initContext = new InitialContext(hash);
			           	fieldEjbHome = (EJBHome) initContext.lookup(address.getJndiName());
					}
					catch (Exception ex)
					{
						// throw the original exception
						throw e;
					}						
				}
				
                //fieldEjbHome = (EJBHome) initContext.lookup(address.getJndiName());
            } catch (Exception ex) {
                throw new WSIFException(
                    "Could not find EJB home '" + address.getJndiName() + "'",
                    ex);
            }
        }
        return fieldEjbHome;
    }

    public EJBObject getEjbObject() throws WSIFException {
        if (fieldEjbObject == null) {
            EJBHome ejbHome = getEjbHome();
            try {
                Method createMethod = ejbHome.getClass().getDeclaredMethod("create", null);
                fieldEjbObject = (EJBObject) createMethod.invoke(ejbHome, null);
            } catch (Exception ex) {
                throw new WSIFException(
                    "Could not create instance for home '" + ejbHome + "'",
                    ex);
            }
        }
        return fieldEjbObject;
    }

    public Port getPortModel() {
        return fieldPortModel;
    }

    public void setDefinition(Definition value) {
        fieldDefinition = value;
    }

    // WSIF: keep list of operations available in this port
    public void setDynamicWSIFOperation(
        String name,
        String inputName,
        String outputName,
        WSIFOperation_EJB value) {
        operationInstances.put(getKey(name, inputName, outputName), value);
    }

    public void setEjbHome(EJBHome newEjbHome) {
        fieldEjbHome = newEjbHome;
    }

    public void setEjbObject(EJBObject newEjbObject) {
        fieldEjbObject = newEjbObject;
    }

    public void setPortModel(Port value) {
        fieldPortModel = value;
    }
    
    public WSIFOperation createOperation(String operationName) throws WSIFException {
      return createOperation(operationName, null, null);
    }
  
    public WSIFOperation createOperation(String operationName,
                                         String inputName,
                                         String outputName) throws WSIFException {
      WSIFOperation_EJB op=getDynamicWSIFOperation(
        operationName,inputName,outputName);
      if (op == null) {
        throw new WSIFException("Could not create operation: " + operationName
                                + ":" + inputName + ":" + outputName);
  	  }
      return op.copy();
    }
    
    public String toString() {
   	  String buff=new String(super.toString()+":\n");
   	 
  	  buff += "definition:"+fieldDefinition.getQName();
  	  buff += "\nportModel:"+fieldPortModel.getName();
  	  buff += "\nejbHome:"+fieldEjbHome;
  	  buff += "\nejbObject"+fieldEjbObject;
  	  
  	  buff += " operationInstances: size:"+operationInstances.size();
  	  Iterator it=operationInstances.keySet().iterator();
  	  int i=0;
  	  while (it.hasNext()) {
  		  String key=(String)it.next();
  		  WSIFOperation_EJB woejb=(WSIFOperation_EJB)operationInstances.get(key);
  		  buff += "\noperationInstances["+i+"]:"+key+" "+woejb.toShallowString()+" ";
  		  i++;
  	  }
  	
  	  return buff;
   }
  
   public String toShallowString() { return super.toString(); }
}
