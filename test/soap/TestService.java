/*
 * Created by IntelliJ IDEA.
 * User: gdaniels
 * Date: Jan 14, 2002
 * Time: 2:12:43 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package test.soap;

import org.apache.axis.MessageContext;

/**
 * Trivial test service.
 */ 
public class TestService {
    /**
     * Calculate and return the length of the passed String.  If a
     * MessageContext property indicates that we've received a particular
     * header, then double the result before returning it.
     */ 
    public int countChars(String str)
    {
        int ret = str.length();
        MessageContext mc = MessageContext.getCurrentContext();
        if (mc.isPropertyTrue(TestHeaderAttrs.PROP_DOUBLEIT)) {
            ret = ret * 2;
        }
        return ret;
    }
}
