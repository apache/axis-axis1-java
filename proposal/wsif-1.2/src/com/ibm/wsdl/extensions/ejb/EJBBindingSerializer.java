// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.ejb;

import java.io.*;
import org.w3c.dom.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import com.ibm.wsdl.*;
import com.ibm.wsdl.util.xml.*;
import com.ibm.wsdl.util.StringUtils;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class EJBBindingSerializer implements ExtensionSerializer, ExtensionDeserializer, Serializable
{
  public void marshall(
    Class parentType,
    QName elementType,
    javax.wsdl.extensions.ExtensibilityElement extension,
    java.io.PrintWriter pw,
    javax.wsdl.Definition def,
    javax.wsdl.extensions.ExtensionRegistry extReg)
    throws javax.wsdl.WSDLException
  {
    if (extension == null)
      return;

    if (extension instanceof EJBBinding)
    {
      EJBBinding ejbBinding = (EJBBinding) extension;
      pw.print("      <ejb:binding");

      Boolean required = extension.getRequired();
      if (required != null)
      {
        DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
      }

      pw.println("/>");
    }
    else if (extension instanceof EJBOperation)
    {
      EJBOperation ejbOperation = (EJBOperation) extension;
      pw.print("      <ejb:operation");

      if (ejbOperation.getMethodName() != null)
      {
        DOMUtils.printAttribute("methodName", ejbOperation.getMethodName(), pw);
      }

      if (ejbOperation.getEjbInterface() != null)
      {
        DOMUtils.printAttribute("interface", ejbOperation.getEjbInterface(), pw);
      }
      
      if (ejbOperation.getParameterOrder() != null)
      {
        DOMUtils.printAttribute("parameterOrder",
            StringUtils.getNMTokens(ejbOperation.getParameterOrder()), pw);
      }

      if (ejbOperation.getReturnPart() != null)
      {
        DOMUtils.printAttribute("returnPart", ejbOperation.getReturnPart(), pw);
      }

      Boolean required = extension.getRequired();
      if (required != null)
      {
        DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
      }

      pw.println("/>");
    }
    else if (extension instanceof EJBAddress)
    {
      EJBAddress ejbAddress = (EJBAddress) extension;
      pw.print("      <ejb:address");

      if (ejbAddress.getClassName() != null)
      {
        DOMUtils.printAttribute("className", ejbAddress.getClassName(), pw);
      }

      if (ejbAddress.getArchive() != null)
      {
        DOMUtils.printAttribute("archive", ejbAddress.getArchive(), pw);
      }

      if (ejbAddress.getClassLoader() != null)
      {
        DOMUtils.printAttribute("classLoader", ejbAddress.getClassLoader(), pw);
      }

      if (ejbAddress.getJndiName() != null)
      {
        DOMUtils.printAttribute("jndiName", ejbAddress.getJndiName(), pw);
      }

      Boolean required = extension.getRequired();
      if (required != null)
      {
        DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
      }

      pw.println("/>");
    }
  }
  /**
   * Registers the serializer.
   */
  public void registerSerializer(ExtensionRegistry registry)
  {
    // binding
    registry.registerSerializer(javax.wsdl.Binding.class, EJBBindingConstants.Q_ELEM_EJB_BINDING, this);
    registry.registerDeserializer(javax.wsdl.Binding.class, EJBBindingConstants.Q_ELEM_EJB_BINDING, this);
    registry.mapExtensionTypes(javax.wsdl.Binding.class,
															 EJBBindingConstants.Q_ELEM_EJB_BINDING,
															 EJBBinding.class);

    // operation
    registry.registerSerializer(javax.wsdl.BindingOperation.class, EJBBindingConstants.Q_ELEM_EJB_OPERATION, this);
    registry.registerDeserializer(javax.wsdl.BindingOperation.class, EJBBindingConstants.Q_ELEM_EJB_OPERATION, this);
    registry.mapExtensionTypes(javax.wsdl.BindingOperation.class,
															 EJBBindingConstants.Q_ELEM_EJB_OPERATION,
															 EJBOperation.class);

    // address
    registry.registerSerializer(javax.wsdl.Port.class, EJBBindingConstants.Q_ELEM_EJB_ADDRESS, this);
    registry.registerDeserializer(javax.wsdl.Port.class, EJBBindingConstants.Q_ELEM_EJB_ADDRESS, this);
    registry.mapExtensionTypes(javax.wsdl.Port.class,
															 EJBBindingConstants.Q_ELEM_EJB_ADDRESS,
															 EJBAddress.class);

  }
  public javax.wsdl.extensions.ExtensibilityElement unmarshall(
    Class parentType,
    javax.wsdl.QName elementType,
    org.w3c.dom.Element el,
    javax.wsdl.Definition def,
    javax.wsdl.extensions.ExtensionRegistry extReg)
    throws javax.wsdl.WSDLException
  {
	  
    javax.wsdl.extensions.ExtensibilityElement returnValue = null;

    if (EJBBindingConstants.Q_ELEM_EJB_BINDING.equals(elementType))
    {
      EJBBinding ejbBinding = new EJBBinding();

      return ejbBinding;
    }
    else if (EJBBindingConstants.Q_ELEM_EJB_OPERATION.equals(elementType))
    {
      EJBOperation ejbOperation = new EJBOperation();

      String methodName = DOMUtils.getAttribute(el, "methodName");

      if (methodName != null)
      {
        ejbOperation.setMethodName(methodName);
      }

      String ejbInterface = DOMUtils.getAttribute(el, "interface");
      if (ejbInterface != null)
      {
        ejbOperation.setEjbInterface(ejbInterface);
      }
      
      String parameterOrder = DOMUtils.getAttribute(el, "parameterOrder");
      if (parameterOrder != null)
      {
        ejbOperation.setParameterOrder(parameterOrder);
      }

      String returnPart = DOMUtils.getAttribute(el, "returnPart");
      if (returnPart != null)
      {
        ejbOperation.setReturnPart(returnPart);
      }

      return ejbOperation;
    }
    else if (EJBBindingConstants.Q_ELEM_EJB_ADDRESS.equals(elementType))
    {
      EJBAddress ejbAddress = new EJBAddress();

      String className = DOMUtils.getAttribute(el, "className");
      if (className != null)
      {
        ejbAddress.setClassName(className);
      }

      String archive = DOMUtils.getAttribute(el, "archive");
      if (archive != null)
      {
        ejbAddress.setArchive(archive);
      }

      String classLoader = DOMUtils.getAttribute(el, "classLoader");
      if (classLoader != null)
      {
        ejbAddress.setClassLoader(classLoader);
      }

      String jndiName = DOMUtils.getAttribute(el, "jndiName");
      if (jndiName != null)
      {
        ejbAddress.setJndiName(jndiName);
      }

      return ejbAddress;
    }

    return returnValue;
  }
}
