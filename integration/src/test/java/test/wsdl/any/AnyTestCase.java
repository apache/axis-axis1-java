
package test.wsdl.any;

/**
 * @author Ashutosh Shahi
 *
 * Test to check if MessageElement [] for <any>
 * includes all elements 
 */
public class AnyTestCase extends junit.framework.TestCase{
	
    public AnyTestCase(java.lang.String name) {
        super(name);
    }
    
    public void testAny1() throws Exception{
    	AnyServiceLocator loc = new AnyServiceLocator(); 
    	Soap svc = loc.getSoap();
    	
    	QueryResult qr = svc.query("blah");
    	
    	assertTrue("Records size mismatch", qr.getRecords().length == 1);
		 for (int i =0; i < qr.getRecords().length; i++ ) { 
		 	SObject s = qr.getRecords(i);
		 	System.out.println("id::" + s.getId());
		 	org.apache.axis.message.MessageElement [] e= s.get_any();
		 	System.out.println("MessageElement array size is " + e.length);
		 	 for (int j = 0; j < e.length; j++ ){ 
		 	 	 System.out.print(" " + e[j].getValue()); 
		 	 }
		 	 System.out.println(""); 
		 	assertTrue("Any should have three elements", e.length == 3);
		 }
    }
    
    public static void main(String[] args) throws Exception{
    	AnyTestCase at = new AnyTestCase("test");
    	at.testAny1();
    }
}
