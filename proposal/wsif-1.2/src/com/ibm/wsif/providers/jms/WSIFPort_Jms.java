// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.jms;

import java.io.Serializable;
import java.util.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
 
import com.ibm.wsif.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.providers.*;
import com.ibm.wsdl.extensions.format.*;
import com.ibm.wsdl.extensions.jms.*;

/**
 * Jms WSIF port
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class WSIFPort_Jms extends WSIFDefaultPort implements Serializable {

   private final static boolean DEBUG = true; //false;

   private Definition fieldDefinition = null;
   private Port fieldPortModel = null;

   private JmsAddress fieldObjectReference = null; // 'physical connection'

   protected Map operationInstances = new HashMap();

   /**
    * ctor
    */
   public WSIFPort_Jms(Definition def, Port port, WSIFDynamicTypeMap typeMap) {

      TraceLogger.getGeneralTraceLogger().entry(
         new Object[] { def.getQName(), port.getName(), typeMap });

      fieldDefinition = def;
      fieldPortModel = port;

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
       *
       * NOTE: fault processing is not yet implemented.
       */
   public boolean executeRequestResponseOperation(
      String operationName,
      WSIFMessage input,
      WSIFMessage output,
      WSIFMessage fault)
      throws WSIFException {

      // find correct operation
      WSIFOperation_Jms operation =
         getDynamicWSIFOperation(operationName, input.getName(), output.getName());
      if (operation == null) {
         throw new WSIFException(
            "No operation named '"
               + operationName
               + "' found in port "
               + fieldPortModel.getName());
      }

      return operation.executeRequestResponseOperation(input, output, fault);
   }

   /**
    * @see WSIFPort#createOperation(String)
    */
   public WSIFOperation createOperation(String operationName)
      throws WSIFException {
      return createOperation(operationName, null, null);
   }
  
   /**
    * @see WSIFPort#createOperation(String, String, String)
    */
   public WSIFOperation createOperation(
      String operationName,
      String inputName,
      String outputName)
      throws WSIFException {

      WSIFOperation_Jms op =
         getDynamicWSIFOperation(operationName, inputName, outputName);
      if (op == null) {
         throw new WSIFException(
            "Could not create operation: "
               + operationName
               + ":"
               + inputName
               + ":"
               + outputName);
      }
      return op.copy();
   }

   /**
    * get/set WSIF operation
    */
   public WSIFOperation_Jms getDynamicWSIFOperation(
      String name,
      String inputName,
      String outputName)
      throws WSIFException {

      WSIFOperation_Jms operation =
         (WSIFOperation_Jms) operationInstances.get(getKey(name, inputName, outputName));

      if (operation == null) {
         BindingOperation bindingOperationModel =
            fieldPortModel.getBinding().getBindingOperation(name, inputName, outputName);

         if (bindingOperationModel != null) {
            operation = new WSIFOperation_Jms(fieldPortModel, bindingOperationModel, this);
            setDynamicWSIFOperation(name, inputName, outputName, operation);
         }
      }

      return operation;
   }

   // WSIF: keep list of operations available in this port
   public void setDynamicWSIFOperation(
      String name,
      String inputName,
      String outputName,
      WSIFOperation_Jms value) {

      operationInstances.put(getKey(name, inputName, outputName), value);
   }

   
  /**
   * Tests if this port supports asynchronous/synchronous 
   * 	calls to operations.
   * 
   */
   public boolean supportsSync() {
     return false;
   }	
  
   public boolean supportsAsync() {
     return true;
   }
   
   /**
    * accessor/mutators
    */
   public Definition getDefinition() {
      return fieldDefinition;
   }

   public void setDefinition(Definition value) {
      fieldDefinition = value;
   }

   public Port getPortModel() {
      return fieldPortModel;
   }

   public void setPortModel(Port value) {
      fieldPortModel = value;
   }

   public JmsAddress getObjectReference() throws WSIFException {
      if (fieldObjectReference == null) {

         try {
            ExtensibilityElement portExtension =
               (ExtensibilityElement) fieldPortModel.getExtensibilityElements().get(0);

            if (portExtension == null) {
               throw new WSIFException("Jms missing port extension");
            }

            fieldObjectReference = (JmsAddress) portExtension;

         }
         catch (Exception ex) {
            throw new WSIFException(
               "Could not create object of class '???todo??? " + "'",
               ex);
         }
      }
      return fieldObjectReference;
   }

   public void setObjectReference(JmsAddress newObjectReference) {
      fieldObjectReference = newObjectReference;
   }

   /**
    * helper
    */

   public String toString() {
      String buff = new String(super.toString() + ":\n");

      buff += "definition:" + fieldDefinition.getQName();
      buff += "\nportModel:" + fieldPortModel.getName();

      buff += " operationInstances: size:" + operationInstances.size();
      Iterator it = operationInstances.keySet().iterator();
      int i = 0;
      while (it.hasNext()) {
         String key = (String) it.next();
         WSIFOperation_Jms woj = (WSIFOperation_Jms) operationInstances.get(key);
         buff += "\noperationInstances["
            + i
            + "]:"
            + key
            + " "
            + woj.toShallowString()
            + " ";
         i++;
      }

      return buff;
   }

   public String toShallowString() {
      return super.toString();
   } 
}
