package test.wsdl.webref;
/**
 * Test the wsdl2java option implementationClassName
 *
 * @author Mans Tanneryd (mans@tanneryd.com)
 */

public class DoubleBack
{
	public String echo(String message)
	{
		return message+message;    
	}
}
