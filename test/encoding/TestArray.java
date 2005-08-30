package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.commons.logging.Log;


/**
 *  Test the serialization of an array. Test case for Bug 14666
 *  (NullPointerException taken in ArraySerializer when specifying array as input parameter.)
 */
public class TestArray extends TestCase {
    static Log log =
            LogFactory.getLog(TestArray.class.getName());

    public TestArray(String name) {
        super(name);
    }
    
    public void testArray1() {
        String tab_items [] = new String[4];
        tab_items[0] = "table item 1";
        tab_items[1] = "table item 2";
        tab_items[2] = "table item 3";
        tab_items[3] = "table item 4";

        RPCParam in_table = new RPCParam("http://local_service.com/", "Input_Array", tab_items);
        RPCElement input = new RPCElement("http://localhost:8000/tester", "echoString",
                                  new Object[]{in_table});
        SOAPEnvelope env = new SOAPEnvelope();
        env.addBodyElement(input);
        String text = env.toString();
        assertTrue(text != null);
        for(int i=0;i<tab_items.length;i++){
            assertTrue(text.indexOf(tab_items[i])!=-1);
        }
    }
}
