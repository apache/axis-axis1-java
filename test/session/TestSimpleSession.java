package test.session;

import org.apache.axis.session.SimpleSession;

import junit.framework.TestCase;

/** 
 * Test deserialization of SOAP responses
 */
public class TestSimpleSession extends TestCase {
    public TestSimpleSession(String name)
    {
        super(name);
    }
    
    public void testSession() {
        SimpleSession session = new SimpleSession();
        Object val = new Float(5.6666);
        session.set("test", val);
        
        assertEquals(val, session.get("test"));
        
        session.remove("test");
        
        assertNull(session.get("test"));
        
    }
    
    public static void main(String args[])
    {
        TestSimpleSession test = new TestSimpleSession("test");
        test.testSession();
    }
}
