// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.java;

import java.io.*;
import org.w3c.dom.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import com.ibm.wsdl.*;
import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.util.xml.*;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class JavaBindingSerializer implements ExtensionSerializer, ExtensionDeserializer, Serializable
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

    // CHANGE HERE: Adjust with unmarshall() !!!

    if (extension == null)
      return;

    if (extension instanceof JavaBinding)
    {
      JavaBinding javaBinding = (JavaBinding) extension;
      pw.print("      <java:binding");

      Boolean required = extension.getRequired();
      if (required != null)
      {
        DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
      }

      pw.println("/>");
    }
    else if (extension instanceof JavaOperation)
    {
      JavaOperation javaOperation = (JavaOperation) extension;
      pw.print("      <java:operation");

      if (javaOperation.getMethodName() != null)
      {
        DOMUtils.printAttribute("methodName", javaOperation.getMethodName(), pw);
      }

      if (javaOperation.getMethodType() != null)
      {
        DOMUtils.printAttribute("methodType", javaOperation.getMethodType(), pw);
      }

      if (javaOperation.getParameterOrder() != null)
      {
        DOMUtils.printAttribute("parameterOrder",
            StringUtils.getNMTokens(javaOperation.getParameterOrder()), pw);
      }

      if (javaOperation.getReturnPart() != null)
      {
        DOMUtils.printAttribute("returnPart", javaOperation.getReturnPart(), pw);
      }

      Boolean required = extension.getRequired();
      if (required != null)
      {
        DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
      }

      pw.println("/>");
    }
    else if (extension instanceof JavaAddress)
    {
      JavaAddress javaAddress = (JavaAddress) extension;
      pw.print("      <java:address");

      if (javaAddress.getClassName() != null)
      {
        DOMUtils.printAttribute("className", javaAddress.getClassName(), pw);
      }

      if (javaAddress.getClassPath() != null)
      {
        DOMUtils.printAttribute("classPath", javaAddress.getClassPath(), pw);
      }

      if (javaAddress.getClassLoader() != null)
      {
        DOMUtils.printAttribute("classLoader", javaAddress.getClassLoader(), pw);
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
    registry.registerSerializer(javax.wsdl.Binding.class, JavaBindingConstants.Q_ELEM_JAVA_BINDING, this);
    registry.registerDeserializer(javax.wsdl.Binding.class, JavaBindingConstants.Q_ELEM_JAVA_BINDING, this);
	registry.mapExtensionTypes(javax.wsdl.Binding.class, JavaBindingConstants.Q_ELEM_JAVA_BINDING,JavaBinding.class);

    // operation
    registry.registerSerializer(javax.wsdl.BindingOperation.class,  JavaBindingConstants.Q_ELEM_JAVA_OPERATION, this);
    registry.registerDeserializer(javax.wsdl.BindingOperation.class, JavaBindingConstants.Q_ELEM_JAVA_OPERATION, this);
    registry.mapExtensionTypes(javax.wsdl.BindingOperation.class, JavaBindingConstants.Q_ELEM_JAVA_OPERATION, JavaOperation.class);
    // address
    registry.registerSerializer(javax.wsdl.Port.class, JavaBindingConstants.Q_ELEM_JAVA_ADDRESS, this);
    registry.registerDeserializer(javax.wsdl.Port.class, JavaBindingConstants.Q_ELEM_JAVA_ADDRESS, this);
    registry.mapExtensionTypes(javax.wsdl.Port.class, JavaBindingConstants.Q_ELEM_JAVA_ADDRESS, JavaAddress.class);

  }
  
  public javax.wsdl.extensions.ExtensibilityElement unmarshall(
    Class parentType,
    javax.wsdl.QName elementType,
    org.w3c.dom.Element el,
    javax.wsdl.Definition def,
    javax.wsdl.extensions.ExtensionRegistry extReg)
    throws javax.wsdl.WSDLException
  {
	  // CHANGE HERE: Use only one temp string ...
	  
    javax.wsdl.extensions.ExtensibilityElement returnValue = null;

    if (JavaBindingConstants.Q_ELEM_JAVA_BINDING.equals(elementType))
    {
      JavaBinding javaBinding = new JavaBinding();

      return javaBinding;
    }
    else if (JavaBindingConstants.Q_ELEM_JAVA_OPERATION.equals(elementType))
    {
      JavaOperation javaOperation = new JavaOperation();

      String methodName = DOMUtils.getAttribute(el, "methodName");
      //String requiredStr = DOMUtils.getAttributeNS(el, Constants.NS_URI_WSDL, Constants.ATTR_REQUIRED);
      if (methodName != null)
      {
        javaOperation.setMethodName(methodName);
      }

      String methodType = DOMUtils.getAttribute(el, "methodType");
      if (methodType != null)
      {
        javaOperation.setMethodType(methodType);
      }
      
      String parameterOrder = DOMUtils.getAttribute(el, "parameterOrder");
      if (parameterOrder != null)
      {
        javaOperation.setParameterOrder(parameterOrder);
      }

      String returnPart = DOMUtils.getAttribute(el, "returnPart");
      if (returnPart != null)
      {
        javaOperation.setReturnPart(returnPart);
      }
      
      return javaOperation;
    }
    else if (JavaBindingConstants.Q_ELEM_JAVA_ADDRESS.equals(elementType))
    {
      JavaAddress javaAddress = new JavaAddress();

      String className = DOMUtils.getAttribute(el, "className");
      if (className != null)
      {
        javaAddress.setClassName(className);
      }

      String classPath = DOMUtils.getAttribute(el, "classPath");
      if (classPath != null)
      {
        javaAddress.setClassPath(classPath);
      }

      String classLoader = DOMUtils.getAttribute(el, "classLoader");
      if (classLoader != null)
      {
        javaAddress.setClassLoader(classLoader);
      }

      return javaAddress;
    }

    return returnValue;
  }
}