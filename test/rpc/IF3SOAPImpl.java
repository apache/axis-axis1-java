
package test.rpc;

import java.util.Calendar;
import java.util.GregorianCalendar;

//import org.jdom.Document;
//import org.jdom.output.XMLOutputter;

public final class IF3SOAPImpl implements IF3SOAP
{
    private Bean[] m_beans;
    private String[] m_categories;

    public IF3SOAPImpl()
    {
        Bean bean1 = new Bean();
        bean1.setId("42042042042");
        bean1.setTitle("Test Bean");
        bean1.setCategory("Test");
        Calendar date = new GregorianCalendar();
        bean1.setDate(date);
        Bean bean2 = new Bean();
        bean2.setId("11011011011");
        bean2.setTitle("Test Bean 2");
        bean2.setCategory("Test 2");
        bean2.setDate(date);
        m_beans = new Bean[2];
        m_beans[0] = bean1;
        m_beans[1] = bean2;
        m_categories = new String[2];
        m_categories[0] = "Test";
        m_categories[1] = "Std";
    }

    public IF1 getBeanById(String id)
        throws Exception
    {
        return m_beans[0];
    }

    public IF1[] getAllBeans()
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getAllBeans(String[] filter)
        throws Exception
    {
        return m_beans;
    }

    public String[] getAllCategories()
        throws Exception
    {
        return m_categories;
    }

    public IF1[] getBeansByCategory(String category)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByCategory(String category, String[] filter)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByDate(Calendar[] dates)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByDate(Calendar[] dates, String[] filter)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByExpression(int expType, String expression)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByExpression(int expType, String expression, String[] filter)
        throws Exception
    {
        return m_beans;
    }

    public String getXMLForBean(IF1 bean)
        throws Exception
    {
        return "<bean>\n</bean>";
    }

    public IF1[] getBeansByCategory(String ifId, String category)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByCategory(String ifId, String category, String[] filter)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByDate(String ifId, Calendar[] dates)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByDate(String ifId, Calendar[] dates, String[] filter)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByExpression(String ifId, int expType, String expression)
        throws Exception
    {
        return m_beans;
    }

    public IF1[] getBeansByExpression(String ifId, int expType, String expression, String[] filter)
        throws Exception
    {
        return m_beans;
    }
}

