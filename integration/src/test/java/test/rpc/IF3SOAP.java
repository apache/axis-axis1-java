
package test.rpc;

import java.util.Calendar;

public interface IF3SOAP extends IF2SOAP
{
    public IF1[] getBeansByCategory(String ifId, String category)
        throws Exception;

    public IF1[] getBeansByCategory(String ifId, String category, String[] filter)
        throws Exception;

    public IF1[] getBeansByDate(String ifId, Calendar[] dates)
        throws Exception;

    public IF1[] getBeansByDate(String ifId, Calendar[] dates, String[] filter)
        throws Exception;

    public IF1[] getBeansByExpression(String ifId, int expType, String expression)
        throws Exception;

    public IF1[] getBeansByExpression(String ifId, int expType, String expression, String[] filter)
        throws Exception;
}

