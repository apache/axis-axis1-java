package test.encoding;

import test.GenericLocalTest;

import java.util.Vector;

import org.apache.axis.client.Call;
import org.apache.axis.AxisFault;

public class TestCircularRefs extends GenericLocalTest {
    public TestCircularRefs() {
        super("foo");
    }

    public TestCircularRefs(String s) {
        super(s);
    }

	public void testCircularVectors() throws Exception {
        try {
            Call call = getCall();
            Object result = call.invoke("getCircle", null);
        } catch (AxisFault af){
            return;
        }
        fail("Expected a fault");
        // This just tests that we don't get exceptions during deserialization
        // for now.  We're still getting nulls for some reason, and once that's
        // fixed we should uncomment this next line
        
        // assertTrue("Result wasn't an array", result.getClass().isArray());
	}
    
    /**
     * Service method.  Return a Vector containing an object graph with a loop.
     * 
     * @return a Vector with circular references
     */ 
    public Vector getCircle() {
        Vector vector1 = new Vector();
        vector1.addElement("AString");
        Vector vector2 = new Vector();
        vector2.addElement(vector1);
        vector1.addElement(vector2);
        return vector2;
    }    
}
