package test.functional;

import junit.framework.TestCase;
import org.apache.axis.client.AdminClient;
import test.rpc.IF1;
import test.rpc.IF2SOAP;
import test.rpc.IF3SOAP;
import test.rpc.IF3SOAPProxy;

import java.util.Calendar;
import java.util.GregorianCalendar;

public final class TestIF3SOAP extends TestCase {
    private static final String URL = "http://localhost:8080/axis/services/IF3SOAP";
    private static final String Service = "IF3SOAP";

    private IF3SOAPProxy m_soap;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(TestIF3SOAP.class);
    }

    public TestIF3SOAP(String name) {
        super(name);
    }

    protected void setUp() {
        if (m_soap == null)
            m_soap = new IF3SOAPProxy(Service, URL);
    } // setUp


    protected void tearDown() {
    } // tearDown

    public void testGetBeanById() {
        try {
            IF2SOAP soap = m_soap;
            IF1 bean = soap.getBeanById("42042042042");
            assertNotNull("bean is null", bean);
            System.out.println("beanById:");
            System.out.println("id: " + bean.getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetAllBeans() {
        try {
            IF2SOAP soap = m_soap;
            IF1[] beans = soap.getAllBeans();
            assertNotNull("beans is null", beans);
            System.out.println("allBeans:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetAllBeansFiltered() {
        try {
            IF2SOAP soap = m_soap;
            String[] filter = new String[1];
            filter[0] = "11011011011";
            IF1[] beans = soap.getAllBeans(filter);
            assertNotNull("beans is null", beans);
            System.out.println("allBeansFiltered:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetAllCategories() {
        try {
            IF2SOAP soap = m_soap;
            String[] categories = soap.getAllCategories();
            assertNotNull("categories is null", categories);
            System.out.println("allCategories:");
            for (int i = 0; i < categories.length; i++)
                System.out.println("cat[" + i + "]: " + categories[i]);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansByCategory() {
        try {
            IF2SOAP soap = m_soap;
            IF1[] beans = soap.getBeansByCategory("Test");
            assertNotNull("beans is null", beans);
            System.out.println("beansByCategory:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansByCategoryFiltered() {
        try {
            IF2SOAP soap = m_soap;
            String[] filter = new String[1];
            filter[0] = "11011011011";
            IF1[] beans = soap.getBeansByCategory("Test", filter);
            assertNotNull("beans is null", beans);
            System.out.println("beansByCategoryFiltered:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansByDate() {
        try {
            IF2SOAP soap = m_soap;
            Calendar[] dates = new Calendar[1];
            dates[0] = new GregorianCalendar();
            IF1[] beans = soap.getBeansByDate(dates);
            assertNotNull("beans is null", beans);
            System.out.println("beansByDate:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansByDateFiltered() {
        try {
            IF2SOAP soap = m_soap;
            String[] filter = new String[1];
            filter[0] = "11011011011";
            Calendar[] dates = new Calendar[1];
            dates[0] = new GregorianCalendar();
            IF1[] beans = soap.getBeansByDate(dates, filter);
            assertNotNull("beans is null", beans);
            System.out.println("beansByDateFiltered:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansByExpression() {
        try {
            IF2SOAP soap = m_soap;
            IF1[] beans = soap.getBeansByExpression(IF2SOAP.KEYWORD_EXP, "keyword");
            assertNotNull("beans is null", beans);
            System.out.println("beansByExpression:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansByExpressionFiltered() {
        try {
            IF2SOAP soap = m_soap;
            String[] filter = new String[1];
            filter[0] = "11011011011";
            IF1[] beans = soap.getBeansByExpression(IF2SOAP.KEYWORD_EXP, "keyword", filter);
            assertNotNull("beans is null", beans);
            System.out.println("beansByExpressionFiltered:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetXMLForBean() {
        try {
            IF2SOAP soap = m_soap;
            IF1 bean = soap.getBeanById("42042042042");
            String xml = soap.getXMLForBean(bean);
            assertNotNull("xml is null", xml);
            System.out.println("xmlForBean:");
            System.out.println("xml: " + xml);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansForIFByCategory() {
        try {
            IF3SOAP soap = m_soap;
            IF1[] beans = soap.getBeansByCategory("if", "Test");
            assertNotNull("beans is null", beans);
            System.out.println("beansForIFByCategory:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansForIFByCategoryFiltered() {
        try {
            IF3SOAP soap = m_soap;
            String[] filter = new String[1];
            filter[0] = "11011011011";
            IF1[] beans = soap.getBeansByCategory("if", "Test", filter);
            assertNotNull("beans is null", beans);
            System.out.println("beansForIFByCategoryFiltered:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansForIFByDate() {
        try {
            IF3SOAP soap = m_soap;
            Calendar[] dates = new Calendar[1];
            dates[0] = new GregorianCalendar();
            IF1[] beans = soap.getBeansByDate("if", dates);
            assertNotNull("beans is null", beans);
            System.out.println("beansForIFByDate:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansForIFByDateFiltered() {
        try {
            IF3SOAP soap = m_soap;
            String[] filter = new String[1];
            filter[0] = "11011011011";
            Calendar[] dates = new Calendar[1];
            dates[0] = new GregorianCalendar();
            IF1[] beans = soap.getBeansByDate("if", dates, filter);
            assertNotNull("beans is null", beans);
            System.out.println("beansForIFByDateFiltered:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansForIFByExpression() {
        try {
            IF3SOAP soap = m_soap;
            IF1[] beans = soap.getBeansByExpression("if", IF2SOAP.KEYWORD_EXP, "keyword");
            assertNotNull("beans is null", beans);
            System.out.println("beansForIFByExpression:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testGetBeansForIFByExpressionFiltered() {
        try {
            IF3SOAP soap = m_soap;
            String[] filter = new String[1];
            filter[0] = "11011011011";
            IF1[] beans = soap.getBeansByExpression("if", IF2SOAP.KEYWORD_EXP, "keyword", filter);
            assertNotNull("beans is null", beans);
            System.out.println("beansForIFByExpressionFiltered:");
            for (int i = 0; i < beans.length; i++)
                System.out.println("id[" + i + "]: " + beans[i].getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
}

