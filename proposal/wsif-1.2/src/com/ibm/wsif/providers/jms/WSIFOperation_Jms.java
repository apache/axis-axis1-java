// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.jms;

import java.io.Serializable;
import java.util.*;

import javax.jms.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;

import com.ibm.wsdl.extensions.format.*;
import com.ibm.wsdl.extensions.jms.*;
import com.ibm.wsif.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.util.jms.*;
import com.ibm.wsif.logging.*;

/**
 * WSIF Jms provider
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class WSIFOperation_Jms
   extends WSIFDefaultOperation
   implements WSIFOperation {

   private final static boolean DEBUG = false;

   protected javax.wsdl.Port fieldBasePort;
   protected javax.wsdl.BindingOperation fieldBindingOperation;
   protected WSIFPort_Jms fieldJmsPort;

   protected JmsBinding fieldJmsBinding; // my binding extensions   	
   protected Operation fieldOperation;

   protected int fieldJmsMessageType = JmsBindingConstants.MESSAGE_TYPE_NOTSET;

   // input message 	
   protected String fieldInputMessageName;
   protected HashMap fieldInputJmsAttributes;
   protected JmsBody fieldInputBody;

   // output message
   protected String fieldOutputMessageName;
   protected HashMap fieldOutputJmsAttributes;
   protected JmsBody fieldOutputBody;

   /**
    * ctor
    */
   public WSIFOperation_Jms(
      javax.wsdl.Port basePort,
      BindingOperation bindingOperation,
      WSIFPort_Jms jmsPort)
      throws WSIFException {

      TraceLogger.getGeneralTraceLogger().entry(
         new Object[] {
            basePort.getName(),
            bindingOperation.getName(),
            jmsPort.toShallowString()});

      fieldBasePort = basePort;
      fieldBindingOperation = bindingOperation;
      fieldJmsPort = jmsPort;

      TraceLogger.getGeneralTraceLogger().exit();
   }

   /**
   * Create a new copy of this object. This is not a clone, since
   * it does not copy the referenced objects as well.
   */
   public WSIFOperation_Jms copy() throws WSIFException {

      return new WSIFOperation_Jms(
         fieldBasePort,
         fieldBindingOperation,
         fieldJmsPort);
   }

   /**
    * executeRequestResponseOperation(WSIFMessage, WSIFMessage, WSIFMessage)
    * 
    *		synchronous execution is NOT supported for JMS	
    * 
    */
   public boolean executeRequestResponseOperation(
      WSIFMessage input,
      WSIFMessage output,
      WSIFMessage fault)
      throws WSIFException {

      //@ake      System.err.println("fake to executeInputOnlyOperation ..");
      //@ake      executeInputOnlyOperation(input);
      //@ake      throw new WSIFException("executeRequestResponseOperation(WSIFMessage, WSIFMessage, WSIFMessage) not supported for jms");

      return false;
   }

   /**
    * executeInputOnlyOperation(WSIFMessage)
    * WSDL transmission primitive Notification (fire&forget)
    */
   public void executeInputOnlyOperation(WSIFMessage input) throws WSIFException {

      executeRequestResponseAsync(input, null);

   }

   /**
    * executeRequestResponseAsync(WSIFMessage, WSIFResponseHandler)
    * 
    * @param input   input message to send to the operation
    * @param handler   the response handler that will be notified 
    *        when the asynchronous response becomes available.
    *
    * @return the correlation ID or the request. The correlation ID
    *         is used to associate the request with the WSIFOperation.
    *
    * @exception WSIFException if something goes wrong.
    */
   public Serializable executeRequestResponseAsync(
      WSIFMessage input,
      WSIFResponseHandler handler)
      throws WSIFException {

      //	 	workflow does not need WSIFResponseHandler!

      TraceLogger.getGeneralTraceLogger().entry(new Object[] { input });

      String correlId = null;

      try {

         getOperation();

         // send the jms message	  
         correlId = sendJmsMessage(input);
      }
      catch (Exception ex) {
         // Log message
         MessageLogger messageLog =
            MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
         messageLog.message(
            WSIFConstants.TYPE_ERROR,
            "WSIF.0005E",
            new Object[] { "Jms", fieldBindingOperation.getName()});
         messageLog.destroy();

         throw new WSIFException(
            this +" : Could not invoke '" + fieldBindingOperation.getName() + "'",
            ex);
      }

      TraceLogger.getGeneralTraceLogger().exit();

      return correlId;
   }

   /**
    * workflow specific processAsyncResponse(Object, WSIFMessage, WSIFMessage)
    */
   public void procssAsyncResponse( //@ake todo fix typo
      Object responseObject,
      WSIFMessage output,
      WSIFMessage fault)
      throws WSIFException {

      TraceLogger.getGeneralTraceLogger().entry(new Object[] { output });

      try {

         getOperation();

         // set output message name
         output.setName(fieldBindingOperation.getBindingOutput().getName());

         receiveJmsMessage(responseObject, output, fault);

      }
      catch (Exception ex) {
         // Log message
         MessageLogger messageLog =
            MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
         messageLog.message(
            WSIFConstants.TYPE_ERROR,
            "WSIF.0005E",
            new Object[] { "Jms", fieldBindingOperation.getName()});
         messageLog.destroy();

         throw new WSIFException(
            this +" : Could not invoke '" + fieldBindingOperation.getName() + "'",
            ex);
      }

      TraceLogger.getGeneralTraceLogger().exit();

      return;
   }

   /**
    * fireAsyncResponse is called when a response has been received
    * for a previous executeRequestResponseAsync call.
    * @param response   an Object representing the response
    */

   public void fireAsyncResponse(Object response) throws WSIFException {

      //	  workflow does not need this yet! use processAsyncResponse
   }

   /**
   * WSIFOperation supports asyncronus requests,
   * @return true 
   */
   public boolean isAsyncSupported() {
      return true;
   }

   //
   // helper
   //

   /**
    * send jms message
    */
   private String sendJmsMessage(WSIFMessage input) throws WSIFException {

      String correlId = null;

      /**
       * Get the part name of the input to send
       */
      Part part = null;
      String partName = null;

      if (fieldInputBody != null) {
         partName = fieldInputBody.getParts();
         part = fieldOperation.getInput().getMessage().getPart(partName);

         if (part == null) {
            throw new WSIFException(
               this +" message partname '" + partName + "' not found!");
         }
      }
      else {
         // no part specified - take first of message
         part = getFirstMessagePart(fieldOperation.getInput().getMessage());
         if (part == null) {
            throw new WSIFException(this +" no message partname found!");
         }
         partName = part.getName();
      }

      /**
       * create jms destination
       **/
      WSIFJmsDestination jmsDest = 
        new WSIFJmsDestination(WSIFJmsFinder.newFinder(fieldJmsPort.getObjectReference()), 
                               fieldJmsPort.getObjectReference().getJmsProvDestName(), 
                               WSIFProperties.getSyncTimeout());

      /**
       * set the additionl Jms attributes
       */
      if (fieldInputJmsAttributes != null) {
         setJmsAttributes(input, fieldInputJmsAttributes, jmsDest);
      }

      if (DEBUG) {
         System.err.println("inputMessage partName1: " + partName);

         System.err.println(
            "send JmsMessageType: "
               + JmsBinding.JmsMessageTypeAsString(fieldJmsMessageType));
      }

      /**
       * send the message
       */

      Object partValue = input.getObjectPart(partName);
      if (partValue == null) {
         throw new WSIFException(this +"message partname '" + partName + "' not set!");

      }

      if (fieldJmsMessageType == JmsBindingConstants.MESSAGE_TYPE_OBJECTMESSAGE) {
         // serialize the inputmessage

         Serializable partValueSer = (Serializable) partValue;

         correlId = jmsDest.send(partValueSer, null);
         // interface contract: exchange parts!            
      }
      else if (fieldJmsMessageType == JmsBindingConstants.MESSAGE_TYPE_TEXTMESSAGE) {
         // build text string            
         correlId = jmsDest.send(partValue.toString(), null);
      }
      else {
         throw new WSIFException(
            this
               + "Jms message type not supported: "
               + JmsBinding.JmsMessageTypeAsString(fieldJmsMessageType));
      }

      return correlId;

   }

   /**
    * receivesend jms message
    */
   private void receiveJmsMessage(
      Object responseObject,
      WSIFMessage output,
      WSIFMessage fault)
      throws WSIFException {

      /**
       * set output message parts
       */

      // get the output part
      Part part = null;
      String partName = null;
      QName partTypeName = null;

      if (fieldOutputBody != null) {
         partName = fieldOutputBody.getParts();
         part = fieldOperation.getOutput().getMessage().getPart(partName);

         if (part == null) {
            throw new WSIFException(this +"message partname '" + partName + "' not found!");
         }
      }
      else {
         // no part specified - take first of message
         part = getFirstMessagePart(fieldOperation.getOutput().getMessage());
         if (part == null) {
            throw new WSIFException(this +"no message partname found!");
         }
         partName = part.getName();
      }

      partTypeName = part.getTypeName();

      if (responseObject != null) {
         //
         // set output message from response object
         //
         if (fieldJmsMessageType == JmsBindingConstants.MESSAGE_TYPE_OBJECTMESSAGE) {
         	// object jms message
         	output.setObjectPart(part.getName(), responseObject);
         }	
      	 else if (fieldJmsMessageType == JmsBindingConstants.MESSAGE_TYPE_TEXTMESSAGE) {
      	 	// jms text message
            if (partTypeName.getLocalPart().equals("byte")) {
            	// just use first!
            	byte[] val = responseObject.toString().getBytes();            
            	output.setBytePart(part.getName(), val[0]);
            }
         	else if (partTypeName.getLocalPart().equals("char")) {
         		// just use first!
            	char val = responseObject.toString().charAt(0);            
            	output.setCharPart(part.getName(), val);
         	}
         	else if (partTypeName.getLocalPart().equals("float")) {
           		float val = Float.parseFloat(responseObject.toString());            
            	output.setFloatPart(part.getName(), val);
         	}
         	else if (partTypeName.getLocalPart().equals("double")) {
            	double val = Double.parseDouble(responseObject.toString());           
            	output.setDoublePart(part.getName(), val);
         	}
         	else if (partTypeName.getLocalPart().equals("long")) {
            	long val = Long.parseLong(responseObject.toString());
            	output.setLongPart(part.getName(), val);
         	}
         	else if (partTypeName.getLocalPart().equals("int")) {
            	int val = Integer.parseInt(responseObject.toString());            
            	output.setIntPart(part.getName(), val);
         	}
         	else if (partTypeName.getLocalPart().equals("short")) {
            	short val = Short.parseShort(responseObject.toString());            
            	output.setShortPart(part.getName(), val);
         	}
         	else {
         		output.setObjectPart(part.getName(), responseObject);            
         	}
      	 } // jms text message
         else {
           throw new WSIFException(this
               + "Jms message type not supported: "
               + JmsBinding.JmsMessageTypeAsString(fieldJmsMessageType));
         }
         
      }
      else {
      	throw new WSIFException(this + "jms message empty (no payload)");
      }
   }

   /**
    * Get first part of a message 
    */
   private Part getFirstMessagePart(javax.wsdl.Message msg) throws WSIFException {

      // Get input message part name, only one part is supported
      Part part1 = null;
      List inputPartsList = msg.getOrderedParts(null);

      if (inputPartsList.size() > 0) {
         //         	System.out.println(inputPartsList.toString());

         Object[] parts = inputPartsList.toArray();
         part1 = (Part) parts[0]; // only the first is supported.
      }
      else {
         throw new WSIFException("expected message to have at least on part");

      }
      return part1;
   }

   /**
    * set the specified jms attributes
    */
   private void setJmsAttributes(
      WSIFMessage msg,
      HashMap attr,
      WSIFJmsDestination jmsDest)
      throws WSIFException {

      Iterator iter = msg.getPartNames();
      while (iter.hasNext()) {
         String partName = (String) iter.next();

         String jmsAttribute = (String) attr.get(partName);

         if (jmsAttribute != null) {
            jmsDest.setAttribute(jmsAttribute, msg.getObjectPart(partName));
         }
      }
   }

   /**
    * get the specified operation (w/ input and output message)
    */
   private Operation getOperation() throws Exception {

      if (fieldOperation == null) {
         // <input> and <output> tags in binding operations are not mandatory
         // so deal with null BindingInputs or BindingOutputs
         try {
            fieldInputMessageName = fieldBindingOperation.getBindingInput().getName();
         }
         catch (NullPointerException e) {
            fieldInputMessageName = null;
         }

         try {
            fieldOutputMessageName = fieldBindingOperation.getBindingOutput().getName();
         }
         catch (NullPointerException e) {
            fieldOutputMessageName = null;
         }

         // Get operation
         fieldOperation =
            fieldBasePort.getBinding().getPortType().getOperation(
               fieldBindingOperation.getName(),
               fieldInputMessageName,
               fieldOutputMessageName);

         //      
         // Jms binding extension
         //	
         Iterator bindingIterator =
            fieldBasePort.getBinding().getExtensibilityElements().iterator();

         while (bindingIterator.hasNext()) {
            try {
               ExtensibilityElement ele = (ExtensibilityElement) bindingIterator.next();
               if (ele.getElementType().equals(JmsBindingConstants.Q_ELEM_JMS_BINDING)) {
                  fieldJmsBinding = (JmsBinding) ele;
                  fieldJmsMessageType = fieldJmsBinding.getJmsMessageType();
               }

            }
            catch (ClassCastException exn) {
               //todo
            }
         }

         //
         // Jms body extension
         //
         Iterator bodyIterator;

         // input message parts  
         if (fieldBindingOperation.getBindingInput() != null) {
            bodyIterator =
               fieldBindingOperation.getBindingInput().getExtensibilityElements().iterator();

            while (bodyIterator.hasNext()) {
               ExtensibilityElement ele = (ExtensibilityElement) bodyIterator.next();
               if (ele instanceof JmsBody) {
                  fieldInputBody = (JmsBody) ele;
               }
            }

            // input message jms attributes	
            fieldInputJmsAttributes =
               makeJmsAttributeMap(
                  fieldBindingOperation.getBindingInput().getExtensibilityElements());
         }

         // output message parts 
         if (fieldBindingOperation.getBindingOutput() != null) {
            bodyIterator =
               fieldBindingOperation.getBindingOutput().getExtensibilityElements().iterator();

            while (bodyIterator.hasNext()) {
               ExtensibilityElement ele = (ExtensibilityElement) bodyIterator.next();
               if (ele instanceof JmsBody) {
                  fieldOutputBody = (JmsBody) ele;
               }
            }

            // output message jms attributs 
            fieldOutputJmsAttributes =
               makeJmsAttributeMap(
                  fieldBindingOperation.getBindingOutput().getExtensibilityElements());
         }
      }

      if (fieldOperation == null) {
         throw new WSIFException(
            "Unable to resolve Jms binding for operation '"
               + fieldBindingOperation.getName()
               + ":"
               + fieldInputMessageName
               + ":"
               + fieldOutputMessageName
               + "'");
      }

      /*         
            System.out.println("basePortName: " + fieldBasePort.getName());
            System.out.println(
               "basePortName-porttype: "
                  + fieldBasePort.getBinding().getPortType().getQName());
      
            System.out.println(
               "bindingOperationModel: " + fieldBindingOperation.getName());
            System.out.println(
               "operation: " + bindingOperation.getOperation().getName());
            System.out.println(
               "operation-inputname: "
                  + bindingOperation.getOperation().getInput().getName());
            System.out.println(
               "operation-outputname: "
                  + bindingOperation.getOperation().getOutput().getName());      
      
            Map inputParts =
               bindingOperation.getOperation().getInput().getMessage().getParts();
            System.out.println("operation-input-message-parts: " + inputParts.size());
      */

      //      if (DEBUG)
      //         System.err.println(toString());

      return fieldOperation;
   }
   
   /*   
     if (typeMapping == null) {
        throw new WSIFException("Definition does not contain TypeMapping");
     }
   
     // Build the hashmap 
     bindingIterator = typeMapping.getMaps().iterator();
     while (bindingIterator.hasNext()) {
        TypeMap typeMap = (TypeMap) bindingIterator.next();
        this.fieldTypeMaps.put(typeMap.getTypeName(), typeMap.getFormatType());
     }
   
     // Build the parts list
     if (fieldBindingOperationModel.getBindingOutput().getName() == null) {
        operation =
           fieldBasePort.getBinding().getPortType().getOperation(
              fieldBindingOperationModel.getName(),
              fieldBindingOperationModel.getBindingInput().getName(),
              null);
     }
     else {
   
        operation =
           fieldBasePort.getBinding().getPortType().getOperation(
              fieldBindingOperationModel.getName(),
              fieldBindingOperationModel.getBindingInput().getName(),
              fieldBindingOperationModel.getBindingOutput().getName());
     }
    
   }
   
   
   */
   public String toString() {

      String buff = new String(super.toString() + ":\n");
      buff += "basePort:" + fieldBasePort.getName();
      buff += " wsifPort_Jms:" + fieldJmsPort.toShallowString();
      buff += " bindingOperation:" + fieldBindingOperation.getName();
      buff += " JmsBinding:" + fieldJmsBinding;
      buff += " Operation:" + fieldOperation;
      buff += " JmsMessageType:"
         + JmsBinding.JmsMessageTypeAsString(fieldJmsMessageType);

      buff += " inputMessageName:" + fieldInputMessageName;
      buff += " outputMessageName:" + fieldOutputMessageName;

      /*      	
      		if (fieldInParameterNames == null) {
      			buff += " inParameterNames:null";
      		} else {
      			buff += " inParameterNames: size:" + fieldInParameterNames.length;
      			for (int i = 0; i < fieldInParameterNames.length; i++)
      				buff += " inParameterNames[" + i + "]:" + fieldInParameterNames[i];
      		}
      
      		if (fieldOutParameterNames == null) {
      			buff += " outParameterNames:null";
      		} else {
      			buff += " outParameterNames: size:" + fieldOutParameterNames.size();
      			Iterator it = fieldOutParameterNames.keySet().iterator();
      			int i = 0;
      			while (it.hasNext()) {
      				String key = (String) it.next();
      				buff += " outParameterNames["
      					+ i
      					+ "]:"
      					+ key
      					+ " "
      					+ fieldOutParameterNames.get(key);
      				i++;
      			}
      		}
      
      		if (fieldFaultMessageInfos == null) {
      			buff += " faultMessageInfos:null";
      		} else {
      			Iterator it = fieldFaultMessageInfos.keySet().iterator();
      			int i = 0;
      			while (it.hasNext()) {
      				String key = (String) it.next();
      				buff += " faultMessageInfos["
      					+ i
      					+ "]:"
      					+ key
      					+ " "
      					+ fieldFaultMessageInfos.get(key);
      				i++;
      			}
      		}
      */

      /*	
      			if (fieldTypeMaps == null) {
      				buff += " faultTypeMaps:null";
      			} else {
      				Iterator it = fieldTypeMaps.keySet().iterator();
      				int i = 0;
      				while (it.hasNext()) {
      					QName key = (QName) it.next();
      					buff += " typeMaps[" + i + "]:" + key + " " + fieldTypeMaps.get(key);
      					i++;
      				}
      			}
      	*/
      buff += "\n";
      return buff;
   }

   public String toShallowString() {
      return super.toString();
   }

}