// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.soap.apachesoap;

import com.ibm.jrom.*;
import com.ibm.jrom.factory.*;
import com.ibm.jrom.util.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;
import org.apache.soap.util.*;
import org.apache.soap.util.xml.*;
import org.apache.soap.*;
import org.apache.soap.encoding.*;
import org.apache.soap.rpc.*;
import org.apache.soap.encoding.soapenc.SoapEncUtils;
/**
 * A <code>JROMSerializer</code> can be used to serialize and deserialize
 * <em>JROM Values</em> using the <code>SOAP-ENC</code> encoding style.
 * 
 * @author Rania Y. Khalaf (rkhalaf@us.ibm.com)
 */
public class JROMSerializer implements Serializer, Deserializer{
  
  private SOAPMappingRegistry simpleTypeSMR;
  private JROMComplexValue parent = null;

  /**
   * Serialize JROMValues. For JROMSimpleValues, the internal JAVA value is extracted and
   * is then serialized using the built-in schema to SOAP mappings and deserializers. 
   * For JROMComplexValues, a new element is formed with the name as its type 
   * attribute, and its elements subsequently serialized and set as child elements
   */
  public void marshall(String inScopeEncStyle, Class javaType, Object src,
                       Object context, Writer sink, NSStack nsStack,
                       XMLJavaMappingRegistry xjmr, SOAPContext ctx)
    throws IllegalArgumentException, IOException {
    QName elementType;
    
    nsStack.pushScope();
    JROMValue jromEl = (JROMValue)src;
    String elNameLP = jromEl.getNameLocalPart();
    String elNameNS = jromEl.getNameNamespace();
    String elName = (elNameLP != null)
                    ? elNameLP
                    : (context != null )? context.toString() : "";
    QName qname = new QName(elNameNS, elName);

         
    sink.write(StringUtils.lineSeparator);
    
    //serialize simple values by getting their type and value
    // and sending them through the Parameter Serializer
    if(jromEl.getJROMType() != JROMType.JROM_COMPLEX_VALUE) {
        Object jromVal = ConvertorUtils.getSimpleJROMObject(jromEl);
        //special case coz jrom uses javax.wsdl.QName and 
        //soap uses org.apache.soap.QName
        if(jromEl.getJROMType() == JROMType.JROM_QNAME_VALUE)
          jromVal = toSoapQName((javax.wsdl.QName)jromVal);
        Parameter p = new Parameter(elName,
                                    jromVal.getClass(),
                                    jromVal, null);
        xjmr.marshall(inScopeEncStyle, Parameter.class, p, null,
                      sink, nsStack, ctx);
        sink.write(StringUtils.lineSeparator);
      }
    
    else {
        //serialize complex elements
        //generate the header

		JROMComplexValue cv = (JROMComplexValue)jromEl;
		elementType = new QName(cv.getTypeNamespace(),cv.getTypeLocalPart());

        generateStructureHeader(inScopeEncStyle,
                                elementType,
                                context,
                                sink,
                                nsStack,
                                xjmr);
        sink.write(StringUtils.lineSeparator);

		JROMValue childName;
		Iterator elNames = ((JROMComplexValue)jromEl).getElementValues();

        while(elNames.hasNext()) {
            childName = (JROMValue) elNames.next();
        
            marshall(inScopeEncStyle, javaType, 
					 childName,
                     childName.getNameLocalPart(),
                     sink, nsStack, xjmr, ctx);
          }
        sink.write(StringUtils.lineSeparator);
        sink.write("</" + context + '>' + "\n");
      }
      sink.write(StringUtils.lineSeparator);
      nsStack.popScope();
  }

  
  /**
   * Deserializer for JROM values. For simple types, the deserializing is handed to the
   * parent SOAPMappingRegistry of the xjmr, which contains the internal schema to java
   * mappings.  Otherwise, a JROMComplexValue is created with the elementType as its name.
   * The children of the src node are then also deserialized into JROMValues and set as that
   * JROMComplexValue's Elements. Attributes are ignored.
   * @param elementType QName containing type information. 
   * @param xjmr should be a SOAPMappingRegistry instance.
   * 
   **/
  public org.apache.soap.util.Bean unmarshall(String inScopeEncStyle, QName elementType, Node src,
                         XMLJavaMappingRegistry xjmr, SOAPContext ctx)
    throws IllegalArgumentException {
    Element root = (Element)src;
    Element tempEl;
    boolean complex = true;
    String typeNameNSURI = elementType.getNamespaceURI();
   
    String typeNameLP = elementType.getLocalPart();
    String elementNameNamespace = root.getNamespaceURI();
    //get the prefix, if there is one,  out of the tag name
    String[] prefAndLP = ConvertorUtils.splitStringOnColon(root.getTagName());
    String elementNameLocalPart = prefAndLP[1];

    if(typeNameNSURI.equals(Constants.NS_URI_1999_SCHEMA_XSD) ||
       typeNameNSURI.equals(Constants.NS_URI_2000_SCHEMA_XSD) ||
       typeNameNSURI.equals(Constants.NS_URI_2001_SCHEMA_XSD)) {
      complex = false;
    }
    
    if(simpleTypeSMR == null) {
      simpleTypeSMR = ((SOAPMappingRegistry)xjmr).getBaseRegistry(Constants.NS_URI_CURRENT_SCHEMA_XSD);
      if(simpleTypeSMR==null)
        throw new IllegalArgumentException("smr.getBaseRegistry() in JROMSerializer unmarshall returned null");
    }
    Class jromType = null;
    JROMValue jromVal = null;
    JROMType jromValueType;
    try {
	    JROMFactory factory = JROMFactory.newInstance();
	    if(!complex) {  
	      //simple types are to be unmarshalled by the regular 
	      //SOAPMappingRegistry because it can do all the 
	      //Schema to Java work with all the proper type2Qname mappings.
	      org.apache.soap.util.Bean paramBean = 
	        simpleTypeSMR.unmarshall(inScopeEncStyle,
	                                 RPCConstants.Q_ELEM_PARAMETER,
	                                 root, ctx);
	      Parameter param = (Parameter)paramBean.value;
	      Class paramClass = param.getValue().getClass();
	      
	      //special case for QName
	      if(paramClass.equals(org.apache.soap.util.xml.QName.class)) {
	        jromValueType = JROMType.JROM_QNAME_VALUE;
	        javax.wsdl.QName qname = toWsdlQName((QName)param.getValue());
	        jromVal = factory.newJROMQNameValue();
	        jromVal.setNameNamespace(elementNameNamespace);
	        jromVal.setNameLocalPart(elementNameLocalPart);
	        ((JROMQNameValue)jromVal).setValue(qname);
	      }
	      else {
	        jromValueType = 
	          ConvertorUtils.getJROMType(param.getValue().getClass());
	        jromVal = 
	          ConvertorUtils.createSimpleJrom(factory,
	                                          jromValueType, 
	                                          param.getValue(),
	                                          elementNameNamespace,
	                                          elementNameLocalPart);
	      }
	      jromType = jromVal.getClass();
	         
	    }
	    else {
	      jromType = JROMComplexValue.class;
	      //still need to set this guy's name
	      jromVal =  factory.newJROMComplexValue(typeNameNSURI, typeNameLP);
	      jromVal.setNameNamespace(elementNameNamespace);
	      jromVal.setNameLocalPart(elementNameLocalPart);
	
	      tempEl = DOMUtils.getFirstChildElement(root);
	      parent = (JROMComplexValue)jromVal;
	      while (tempEl != null) {
	        QName tempElType;
	        tempElType = SoapEncUtils.getTypeQName(tempEl);
	      
	        JROMValue childVal = (JROMValue)unmarshall(inScopeEncStyle,
	                                                   tempElType,
	                                                   tempEl,
	                                                   xjmr, ctx).value;
	      
	        ((JROMComplexValue)jromVal).addElementValue(childVal);
	        //unmarshall it into one of the parent's elements
	        tempEl = DOMUtils.getNextSiblingElement(tempEl);
	      }
	      parent = null; //clear parent for the next value
	    }
    } catch (JROMException je) {}

    return new org.apache.soap.util.Bean(jromType, jromVal);     
  }
    
  private static void generateStructureHeader(String inScopeEncStyle,
                                              QName elementType, 
                                              Object context,
                                              Writer sink, NSStack nsStack,
                                              XMLJavaMappingRegistry xjmr)
    throws IllegalArgumentException, IOException {
      
    sink.write('<' + context.toString());

    // Get prefixes for the needed namespaces.
    String xsiNSPrefix = 
      nsStack.getPrefixFromURI(Constants.NS_URI_CURRENT_SCHEMA_XSI, 
                               sink);
    String elementTypeNSPrefix = 
      nsStack.getPrefixFromURI(elementType.getNamespaceURI(),
                               sink);
      
    sink.write(' ' + xsiNSPrefix + ':' + Constants.ATTR_TYPE + "=\"" +
               elementTypeNSPrefix + ':' +
               elementType.getLocalPart() + '\"');
      
    if (inScopeEncStyle == null
        || !inScopeEncStyle.equals(Constants.NS_URI_SOAP_ENC))
      {
        // Determine the prefix associated with the NS_URI_SOAP_ENV
        // namespace URI.
        String soapEnvNSPrefix =
          nsStack.getPrefixFromURI(Constants.NS_URI_SOAP_ENV, sink);

        sink.write(' ' + soapEnvNSPrefix + ':' +
                   Constants.ATTR_ENCODING_STYLE + "=\"" +
                   Constants.NS_URI_SOAP_ENC + '\"');
      }
      
    sink.write('>');
  }

  private QName toSoapQName(javax.wsdl.QName name){
    return new QName(name.getNamespaceURI(), name.getLocalPart());
  }
  private javax.wsdl.QName toWsdlQName(QName name)
  {
    return new javax.wsdl.QName(name.getNamespaceURI(), 
                                name.getLocalPart());
  }
}

