// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.format;

import javax.wsdl.*;
import com.ibm.wsdl.*;

public class FormatBindingConstants {
	// Namespace URIs.
	public static final String NS_URI_FORMAT = "http://schemas.xmlsoap.org/wsdl/formatbinding/";

	// Element names.
	public static final String ELEM_FORMAT_BINDING = "typeMapping";
	public static final String ELEM_FORMAT_BINDING_MAP = "typeMap";

	// Qualified element names.
	public static final QName Q_ELEM_FORMAT_BINDING = new QName(NS_URI_FORMAT, ELEM_FORMAT_BINDING);
	public static final QName Q_ELEM_FORMAT_BINDING_MAP = new QName(NS_URI_FORMAT, ELEM_FORMAT_BINDING_MAP);
}
