/**
 * CPWSImplServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Dec 09, 2003 (01:27:53 EST) WSDL2Java emitter.
 */

package test.wsdl.xsd;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.DefinedType;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.xml.namespace.QName;
import java.util.Vector;

public class CPWSImplServiceTestCase extends TestCase {
    public CPWSImplServiceTestCase(java.lang.String name) {
        super(name);
    }

    /** Test case for Bug 25161
	  	Axis 1.2 alpha WSDL xsd types problem prevent .Net integration */
    public void testCPWebServicesWSDL() throws Exception {
        String url = new test.wsdl.xsd.CPWSImplServiceLocator().getCPWebServicesAddress();
        Parser wsdlParser = new Parser();
        System.out.println("Reading WSDL document from '" + url + "?WSDL'");
        wsdlParser.run(url + "?WSDL");
        SymbolTable symbolTable = wsdlParser.getSymbolTable();
        Vector v = symbolTable.getSymbols(new QName("http://datatypes.cs.amdocs.com", "CSText"));
        DefinedType type = (DefinedType) v.get(0);
        assertNotNull(type);
        Vector v2 = SchemaUtils.getContainedElementDeclarations(
                type.getNode(), symbolTable);
        ElementDecl element = (ElementDecl) v2.get(0);
        assertNotNull(element);
        assertEquals(Constants.XSD_STRING, element.getType().getQName());
    }
}
