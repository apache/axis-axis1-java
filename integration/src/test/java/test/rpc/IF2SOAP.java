
package test.rpc;

import java.util.Calendar;

public interface IF2SOAP
{
    public static final int KEYWORD_EXP = 0;
    public static final int CONTENT_EXP = 1;

    public IF1 getBeanById(String id)
        throws Exception;

    public IF1[] getAllBeans()
        throws Exception;

    public IF1[] getAllBeans(String[] filter)
        throws Exception;

    public String[] getAllCategories()
        throws Exception;

    public IF1[] getBeansByCategory(String category)
        throws Exception;

    public IF1[] getBeansByCategory(String category, String[] filter)
        throws Exception;

    public IF1[] getBeansByDate(Calendar[] dates)
        throws Exception;

    public IF1[] getBeansByDate(Calendar[] dates, String[] filter)
        throws Exception;

    public IF1[] getBeansByExpression(int expType, String expression)
        throws Exception;

    public IF1[] getBeansByExpression(int expType, String expression, String[] filter)
        throws Exception;

    public String getXMLForBean(IF1 bean)
        throws Exception;
}

