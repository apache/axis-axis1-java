// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.format;

import java.io.*;
import java.util.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import javax.wsdl.xml.*;
import javax.wsdl.factory.*;
import org.w3c.dom.*;
import com.ibm.wsdl.*;
import com.ibm.wsdl.util.xml.*;

/**
 * Insert the type's description here.
 * Creation date: (5/21/2001 8:48:12 PM)
 * @author: Administrator
 */
public class FormatBindingSerializer
	implements javax.wsdl.extensions.ExtensionDeserializer, javax.wsdl.extensions.ExtensionSerializer, Serializable {
	/**
	 * ConnectorBindingSerializer constructor comment.
	 */
	public FormatBindingSerializer() {
		super();
	}
	/**
	  * 
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

		if (extension instanceof TypeMapping) {
			TypeMapping typeMapping = (TypeMapping) extension;
			pw.print("         <format:typeMapping");

			String style = typeMapping.getStyle();
			if (style != null)
				DOMUtils.printAttribute("style", style, pw);
			String encoding = typeMapping.getEncoding();
			if (encoding != null)
				DOMUtils.printAttribute("encoding", encoding, pw);
			pw.println(">");

			List maps = typeMapping.getMaps();
			Iterator iterator = maps.iterator();
			while (iterator.hasNext()) {
				TypeMap typeMap = (TypeMap) iterator.next();
				pw.print("            <format:typeMap");
				
				// Need definition to resolve namespace to prefix to do it properly
				QName elementName = typeMap.getElementName();
				if (elementName != null){
					String prefix = def.getPrefix(elementName.getNamespaceURI());
					DOMUtils.printAttribute("elementName", prefix + ":" + elementName.getLocalPart(), pw);
				}
				QName typeName = typeMap.getTypeName();
				if (typeName != null){
					String prefix = def.getPrefix(typeName.getNamespaceURI());
					DOMUtils.printAttribute("typeName", prefix + ":" + typeName.getLocalPart(), pw);
				}
				String formatType = typeMap.getFormatType();
				if (formatType != null)
					DOMUtils.printAttribute("formatType", formatType, pw);
				pw.println("/>");
				Boolean required = extension.getRequired();

				if (required != null) {
					DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED, required.toString(), def, pw);
				}

			}
			pw.println("         </format:typeMapping>");

		}
	}
	/**
	 * Registers the serializer.
	 */
	public void registerSerializer(ExtensionRegistry registry) {

		// Binding
		registry.registerSerializer(javax.wsdl.Binding.class, FormatBindingConstants.Q_ELEM_FORMAT_BINDING, this);

		registry.registerDeserializer(javax.wsdl.Binding.class, FormatBindingConstants.Q_ELEM_FORMAT_BINDING, this);

	}
	/**
	 * unmarshall method comment.
	 */
	public javax.wsdl.extensions.ExtensibilityElement unmarshall(
		Class parentPart,
		javax.wsdl.QName elementPart,
		org.w3c.dom.Element el,
		javax.wsdl.Definition def,
		javax.wsdl.extensions.ExtensionRegistry extReg)
		throws javax.wsdl.WSDLException {

		javax.wsdl.extensions.ExtensibilityElement returnValue = null;

		if (FormatBindingConstants.Q_ELEM_FORMAT_BINDING.equals(elementPart)) {
			TypeMapping typeMapping = new TypeMapping();

			String style = DOMUtils.getAttribute(el, "style");
			String encoding = DOMUtils.getAttribute(el, "encoding");
			String requiredStr = DOMUtils.getAttributeNS(el, Constants.NS_URI_WSDL, Constants.ATTR_REQUIRED);

			if (style != null) {
				typeMapping.setStyle(style);
			}
			if (encoding != null) {
				typeMapping.setEncoding(encoding);
			}

			Element tempEl = DOMUtils.getFirstChildElement(el);
			while (tempEl != null) {
				if (FormatBindingConstants.Q_ELEM_FORMAT_BINDING_MAP.matches(tempEl)) {
					TypeMap typeMap = new TypeMap();
					
					QName qElementName;
					QName qTypeName;
					WSDLFactory factory = WSDLFactory.newInstance("com.ibm.wsif.stub.WSIFPrivateWSDLFactoryImpl");
    				WSDLReader wsdlReader = factory.newWSDLReader();
					if (wsdlReader.getFeature(Constants.FEATURE_TOLERATE_UNQUALIFIED_QNAMES)) {
					  qElementName = DOMUtils.getQualifiedAttributeValue(tempEl,
					                                                     "elementName",
					                                                     "typeMap",
                                                                         def.getTargetNamespace(),
                                                                         false);
					  qTypeName = DOMUtils.getQualifiedAttributeValue(tempEl,
					                                                  "typeName",
					                                                  "typeMap",
					                                                  def.getTargetNamespace(),
					                                                  false);
					} else {
					  qElementName = DOMUtils.getQualifiedAttributeValue(tempEl,
					                                                     "elementName",
					                                                     "typeMap",
                                                                         false);
					  qTypeName = DOMUtils.getQualifiedAttributeValue(tempEl,"typeName", "typeMap", false);
					}

					String formatType = DOMUtils.getAttribute(tempEl, "formatType");
					if (qElementName != null) {
						typeMap.setElementName(qElementName);
					}
					if (qTypeName != null) {
						typeMap.setTypeName(qTypeName);
					}
					if (formatType != null) {
						typeMap.setFormatType(formatType);
					}
					typeMapping.addMap(typeMap);
				}
				tempEl = DOMUtils.getNextSiblingElement(tempEl);
			}
			return typeMapping;
		}
		return returnValue;
	}
}