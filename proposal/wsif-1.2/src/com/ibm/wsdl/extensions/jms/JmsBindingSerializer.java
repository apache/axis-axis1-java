// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.jms;

import java.io.PrintWriter;
import java.io.Serializable;

import org.w3c.dom.*;

import javax.wsdl.*;
import javax.wsdl.extensions.*;

import com.ibm.wsdl.*;
import com.ibm.wsdl.util.xml.*;

/** 
 * WSDL Jms extension
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class JmsBindingSerializer
   implements ExtensionSerializer, ExtensionDeserializer, Serializable {

   /**
    * @see ExtensionSerializer#marshall(Class, QName, ExtensibilityElement, PrintWriter, Definition, ExtensionRegistry)
    */
   public void marshall(
      Class parentType,
      QName elementType,
      javax.wsdl.extensions.ExtensibilityElement extension,
      java.io.PrintWriter pw,
      javax.wsdl.Definition def,
      javax.wsdl.extensions.ExtensionRegistry extReg)
      throws javax.wsdl.WSDLException {

      if (extension == null)
         return;

      if (extension instanceof JmsBinding) {
         JmsBinding jmsBinding = (JmsBinding) extension;
         pw.print("      <jms:binding");

         /**
          * handle attributes
          */
         if (jmsBinding.getJmsMessageType()
            != JmsBindingConstants.MESSAGE_TYPE_NOTSET) {
            DOMUtils.printAttribute(
               "type",
               JmsBinding.JmsMessageTypeAsString(jmsBinding.getJmsMessageType()),
               pw);
         }

         Boolean required = extension.getRequired();
         if (required != null) {
            DOMUtils.printQualifiedAttribute(
               Constants.Q_ATTR_REQUIRED,
               required.toString(),
               def,
               pw);
         }

         pw.println("/>");

      }
      else if (extension instanceof JmsOperation) {
         JmsOperation jmsOperation = (JmsOperation) extension;
         pw.print("      <jms:operation");

         /**
          * handle attributes
          */
         Boolean required = extension.getRequired();
         if (required != null) {
            DOMUtils.printQualifiedAttribute(
               Constants.Q_ATTR_REQUIRED,
               required.toString(),
               def,
               pw);
         }

         pw.println("/>");
      }
      else if (extension instanceof JmsAddress) {
         JmsAddress jmsAddress = (JmsAddress) extension;
         pw.print("      <jms:address");

         /**
          * handle attributes
          */
         if (jmsAddress.getJmsVendorURL() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_JMS_VENDOR_URL,
                                    jmsAddress.getJmsVendorURL(), pw);
         }

         if (jmsAddress.getInitCxtFact() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_INIT_CXT_FACT,
                                    jmsAddress.getInitCxtFact(), pw);
         }

         if (jmsAddress.getJndiProvURL() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_JNDI_PROV_URL,
                                    jmsAddress.getJndiProvURL(), pw);
         }

         if (jmsAddress.getDestStyle() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_DEST_STYLE,
                                    jmsAddress.getDestStyle(), pw);
         }

         if (jmsAddress.getJndiConnFactName() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_JNDI_CONN_FACT_NAME,
                                    jmsAddress.getJndiConnFactName(), pw);
         }

         if (jmsAddress.getJndiDestName() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_JNDI_DEST_NAME,
                                    jmsAddress.getJndiDestName(), pw);
         }

         if (jmsAddress.getJmsProvDestName() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_JMS_PROV_DEST_NAME,
                                    jmsAddress.getJmsProvDestName(), pw);
         }

         if (jmsAddress.getJmsImplSpecURL() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_JMS_IMPL_SPEC_URL,
                                    jmsAddress.getJmsImplSpecURL(), pw);
         }

         Boolean required = extension.getRequired();
         if (required != null) {
            DOMUtils.printQualifiedAttribute(
               Constants.Q_ATTR_REQUIRED,
               required.toString(),
               def,
               pw);
         }

         pw.println("/>");
      }
      else if (extension instanceof JmsAttribute) {
         JmsAttribute jmsAttribute = (JmsAttribute) extension;
         pw.print("      <jms:attribute");

         /**
          * handle attributes 
          */
         if (jmsAttribute.getName() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_NAME, jmsAttribute.getName(), pw);
         }

         if (jmsAttribute.getPart() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_PART, jmsAttribute.getPart(), pw);
         }

         Boolean required = extension.getRequired();
         if (required != null) {
            DOMUtils.printQualifiedAttribute(
               Constants.Q_ATTR_REQUIRED,
               required.toString(),
               def,
               pw);
         }

         pw.println("/>");
      }
      else if (extension instanceof JmsHeader) {
         JmsHeader jmsHeader = (JmsHeader) extension;
         pw.print("      <jms:attribute");

         /**
          * handle attributes
          */
         if (jmsHeader.getValue() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_VALUE, jmsHeader.getValue(), pw);
         }

         Boolean required = extension.getRequired();
         if (required != null) {
            DOMUtils.printQualifiedAttribute(
               Constants.Q_ATTR_REQUIRED,
               required.toString(),
               def,
               pw);
         }

         pw.println("/>");
      }
      else if (extension instanceof JmsBody) {
      	 JmsBody jmsBody = (JmsBody) extension;
         pw.print("      <jms:body");

         /**
          * handle attributes
          */
         if (jmsBody.getParts() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_BODY_PARTS, jmsBody.getParts(), pw);
         }

         if (jmsBody.getUse() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_BODY_USE, jmsBody.getUse(), pw);
         }

		 if (jmsBody.getNameSpace() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_BODY_NS, jmsBody.getNameSpace(), pw);
         }

		 if (jmsBody.getEncodingStyle() != null) {
            DOMUtils.printAttribute(JmsBindingConstants.ELEM_JMS_BODY_ES, jmsBody.getEncodingStyle(), pw);
         }
 

         Boolean required = extension.getRequired();
         if (required != null) {
            DOMUtils.printQualifiedAttribute(
               Constants.Q_ATTR_REQUIRED,
               required.toString(),
               def,
               pw);
         }

         pw.println("/>");
      }
   }

   /**
    * Registers the serializer.
    */
   public void registerSerializer(ExtensionRegistry registry) {
      // binding	
      registry.registerSerializer(
         javax.wsdl.Binding.class,
         JmsBindingConstants.Q_ELEM_JMS_BINDING,
         this);
      registry.registerDeserializer(
         javax.wsdl.Binding.class,
         JmsBindingConstants.Q_ELEM_JMS_BINDING,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.Binding.class,
         JmsBindingConstants.Q_ELEM_JMS_BINDING,
         JmsBinding.class);

      // operation
      registry.registerSerializer(
         javax.wsdl.BindingOperation.class,
         JmsBindingConstants.Q_ELEM_JMS_OPERATION,
         this);
      registry.registerDeserializer(
         javax.wsdl.BindingOperation.class,
         JmsBindingConstants.Q_ELEM_JMS_OPERATION,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.BindingOperation.class,
         JmsBindingConstants.Q_ELEM_JMS_OPERATION,
         JmsOperation.class);

      // address
      registry.registerSerializer(
         javax.wsdl.Port.class,
         JmsBindingConstants.Q_ELEM_JMS_ADDRESS,
         this);
      registry.registerDeserializer(
         javax.wsdl.Port.class,
         JmsBindingConstants.Q_ELEM_JMS_ADDRESS,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.Port.class,
         JmsBindingConstants.Q_ELEM_JMS_ADDRESS,
         JmsAddress.class);

      // attribute
      registry.registerSerializer(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE,
         this);
      registry.registerDeserializer(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE,
         JmsAttribute.class);

      registry.registerSerializer(
         javax.wsdl.BindingOutput.class,
         JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE,
         this);
      registry.registerDeserializer(
         javax.wsdl.BindingOutput.class,
         JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.BindingOutput.class,
         JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE,
         JmsAttribute.class);

      // header - input only
      registry.registerSerializer(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_HEADER,
         this);
      registry.registerDeserializer(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_HEADER,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_HEADER,
         JmsHeader.class);

	  // body
      registry.registerSerializer(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_BODY,
         this);
      registry.registerDeserializer(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_BODY,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.BindingInput.class,
         JmsBindingConstants.Q_ELEM_JMS_BODY,
         JmsBody.class);

      registry.registerSerializer(
         javax.wsdl.BindingOutput.class,
         JmsBindingConstants.Q_ELEM_JMS_BODY,
         this);
      registry.registerDeserializer(
         javax.wsdl.BindingOutput.class,
         JmsBindingConstants.Q_ELEM_JMS_BODY,
         this);
      registry.mapExtensionTypes(
         javax.wsdl.BindingOutput.class,
         JmsBindingConstants.Q_ELEM_JMS_BODY,
         JmsBody.class);
   }

   /**
    * @see ExtensionDeserializer#unmarshall(Class, QName, Element, Definition, ExtensionRegistry)
    */
   public ExtensibilityElement unmarshall(
      Class parentType,
      QName elementType,
      Element el,
      Definition def,
      ExtensionRegistry extReg)
      throws WSDLException {

      ExtensibilityElement returnValue = null;

      if (JmsBindingConstants.Q_ELEM_JMS_BINDING.equals(elementType)) {
         JmsBinding jmsBinding = new JmsBinding();

         /**
         * handle binding attributes
         */
         String msgType =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_MESSAGE_TYPE);

         if (msgType.equals("ByteMessage")) {
            jmsBinding.setJmsMessageType(JmsBindingConstants.MESSAGE_TYPE_BYTEMESSAGE);
         }
         else if (msgType.equals("MapMessage")) {
            jmsBinding.setJmsMessageType(JmsBindingConstants.MESSAGE_TYPE_MAPMESSAGE);
         }
         else if (msgType.equals("ObjectMessage")) {
            jmsBinding.setJmsMessageType(JmsBindingConstants.MESSAGE_TYPE_OBJECTMESSAGE);
         }
         else if (msgType.equals("StreamMessage")) {
            jmsBinding.setJmsMessageType(JmsBindingConstants.MESSAGE_TYPE_STREAMMESSAGE);
         }
         else if (msgType.equals("TextMessage")) {
            jmsBinding.setJmsMessageType(JmsBindingConstants.MESSAGE_TYPE_TEXTMESSAGE);
         }
         else {
            jmsBinding.setJmsMessageType(JmsBindingConstants.MESSAGE_TYPE_NOTSET);
         }

         return jmsBinding;

      }
      else if (JmsBindingConstants.Q_ELEM_JMS_OPERATION.equals(elementType)) {
         JmsOperation jmsOperation = new JmsOperation();

         return jmsOperation;
      }
      else if (JmsBindingConstants.Q_ELEM_JMS_ADDRESS.equals(elementType)) {
         JmsAddress jmsAddress = new JmsAddress();

         /**
         * handle address attributes
         */
         String jmsVendorURL =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_JMS_VENDOR_URL);
         if (jmsVendorURL != null) {
            jmsAddress.setJmsVendorURL(jmsVendorURL);
         }

         String jmsImplSpecURL =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_JMS_IMPL_SPEC_URL);
         if (jmsImplSpecURL != null) {
            jmsAddress.setJmsImplSpecURL(jmsImplSpecURL);
         }

		 String initCxtFact =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_INIT_CXT_FACT);
         if (initCxtFact != null) {
            jmsAddress.setInitCxtFact(initCxtFact);
         }

		 String jndiProvURL =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_JNDI_PROV_URL);
         if (jndiProvURL != null) {
            jmsAddress.setJndiProvURL(jndiProvURL);
         }

		 String destStyle =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_DEST_STYLE);
         if (destStyle != null) {
            jmsAddress.setDestStyle(destStyle);
         }

         String jndiConnFactName =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_JNDI_CONN_FACT_NAME);
         if (jndiConnFactName != null) {
            jmsAddress.setJndiConnFactName(jndiConnFactName);
         }

         String jndiDestName =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_JNDI_DEST_NAME);
         if (jndiDestName != null) {
            jmsAddress.setJndiDestName(jndiDestName);
         }

         String jmsProvDestName =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_JMS_PROV_DEST_NAME);
         if (jmsProvDestName != null) {
            jmsAddress.setJmsProvDestName(jmsProvDestName);
         }

         return jmsAddress;
      }
      else if (JmsBindingConstants.Q_ELEM_JMS_ATTRIBUTE.equals(elementType)) {
         JmsAttribute jmsAttribute = new JmsAttribute();

         String jmsName =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_NAME);
         if (jmsName != null) {
            jmsAttribute.setName(jmsName);
         }

         String jmsPart =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_PART);
         if (jmsPart != null) {
            jmsAttribute.setPart(jmsPart);
         }

         return jmsAttribute;
      }
      else if (JmsBindingConstants.Q_ELEM_JMS_HEADER.equals(elementType)) {
         JmsHeader jmsHeader = new JmsHeader();

         String jmsValue =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_VALUE);
         if (jmsValue != null) {
            jmsHeader.setValue(jmsValue);
         }

         return jmsHeader;
      }
      else  if (JmsBindingConstants.Q_ELEM_JMS_BODY.equals(elementType)) {
      	 JmsBody jmsBody = new JmsBody();

         String jmsParts =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_BODY_PARTS);
         if (jmsParts != null) {
            jmsBody.setParts(jmsParts);
         }
		
		String jmsUse =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_BODY_USE);
         if (jmsUse != null) {
            jmsBody.setUse(jmsUse);
         }
		
		String jmsNS =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_BODY_NS);
         if (jmsNS != null) {
            jmsBody.setNameSpace(jmsNS);
         }
		
		String jmsES =
            DOMUtils.getAttribute(el, JmsBindingConstants.ELEM_JMS_BODY_ES);
         if (jmsES != null) {
            jmsBody.setEncodingStyle(jmsES);
         }
		
		return jmsBody;
      }

      return returnValue;
   }

}