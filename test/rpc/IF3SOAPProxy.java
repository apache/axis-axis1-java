
package test.rpc;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.Constants;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.util.Calendar;

public final class IF3SOAPProxy implements IF3SOAP
{
    private String m_service;
    private String m_url;
    private QName m_beanQName;
    private QName m_arrayQName;

    public IF3SOAPProxy(String service, String url)
    {
        m_service = service;
        m_url = url;
        m_beanQName = new QName("urn:" + m_service, "Bean");
        m_arrayQName = Constants.SOAP_ARRAY;
    }

    public IF1 getBeanById(String id)
        throws Exception
    {
        IF1 bean = null;

        if (id == null)
            throw new Exception("invalid id");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getBeanById"));
            call.addParameter("id", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            call.setReturnType(m_beanQName);
            bean = (IF1) call.invoke(new Object[] { id });

        return bean;
    }

    public IF1[] getAllBeans()
        throws Exception
    {
        return getAllBeans(null);
    }

    public IF1[] getAllBeans(String[] filter)
        throws Exception
    {
        IF1[] beans = null;

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getAllBeans"));
            call.setReturnType(m_arrayQName);
            if (filter == null)
                beans = (IF1[]) call.invoke(new Object[0]);
            else
            {
                call.addParameter("filter", m_arrayQName, ParameterMode.IN);
                beans = (IF1[]) call.invoke(new Object[] { filter });
            }

        return beans;
    }

    public String[] getAllCategories()
        throws Exception
    {
        String[] categories = null;

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getAllCategories"));
            call.setReturnType(m_arrayQName);
            categories = (String[]) call.invoke(new Object[0]);

        return categories;
    }

    public IF1[] getBeansByCategory(String category)
        throws Exception
    {
        return getBeansByCategory(category, (String[]) null);
    }

    public IF1[] getBeansByCategory(String category, String[] filter)
        throws Exception
    {
        IF1[] beans = null;

        if (category == null)
            throw new Exception("invalid category");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getBeansByCategory"));
            call.setReturnType(m_arrayQName);
            call.addParameter("category", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            if (filter == null)
                beans = (IF1[]) call.invoke(new Object[] { category });
            else
            {
                call.addParameter("filter", m_arrayQName, ParameterMode.IN);
                beans = (IF1[]) call.invoke(new Object[] { category, filter });
            }

        return beans;
    }

    public IF1[] getBeansByDate(Calendar[] dates)
        throws Exception
    {
        return getBeansByDate(dates, null);
    }

    public IF1[] getBeansByDate(Calendar[] dates, String[] filter)
        throws Exception
    {
        IF1[] beans = null;

        if (dates == null)
            throw new Exception("invalid dates");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getBeansByDate"));
            call.setReturnType(m_arrayQName);
            call.addParameter("dates", m_arrayQName, ParameterMode.IN);
            if (filter == null)
                beans = (IF1[]) call.invoke(new Object[] { dates });
            else
            {
                call.addParameter("filter", m_arrayQName, ParameterMode.IN);
                beans = (IF1[]) call.invoke(new Object[] { dates, filter });
            }

        return beans;
    }

    public IF1[] getBeansByExpression(int expType, String expression)
        throws Exception
    {
        return getBeansByExpression(expType, expression, null);
    }

    public IF1[] getBeansByExpression(int expType, String expression, String[] filter)
        throws Exception
    {
        IF1[] beans = null;

        if (expression == null)
            throw new Exception("invalid expression");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getBeansByExpression"));
            call.setReturnType(m_arrayQName);
            call.addParameter("expType", org.apache.axis.Constants.XSD_INT, ParameterMode.IN);
            call.addParameter("expression", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            if (filter == null)
                beans = (IF1[]) call.invoke(new Object[] { new Integer(expType), expression });
            else
            {
                call.addParameter("filter", m_arrayQName, ParameterMode.IN);
                beans = (IF1[]) call.invoke(new Object[] { new Integer(expType), expression, filter });
            }

        return beans;
    }

    public String getXMLForBean(IF1 bean)
        throws Exception
    {
        String xml = null;

        if (bean == null)
            throw new Exception("invalid bean");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getXMLForBean"));
            call.addParameter("bean", m_beanQName, ParameterMode.IN);
            call.setReturnType(org.apache.axis.Constants.XSD_STRING);
            xml = (String) call.invoke(new Object[] { bean });

        return xml;
    }

    public IF1[] getBeansByCategory(String ifId, String category)
        throws Exception
    {
        return getBeansByCategory(ifId, category, null);
    }

    public IF1[] getBeansByCategory(String ifId, String category, String[] filter)
        throws Exception
    {
        IF1[] beans = null;

        if (ifId == null)
            throw new Exception("invalid ifId");
        if (category == null)
            throw new Exception("invalid category");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getBeansByCategory"));
            call.setReturnType(m_arrayQName);
            call.addParameter("ifId", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            call.addParameter("category", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            if (filter == null)
                beans = (IF1[]) call.invoke(new Object[] { ifId, category });
            else
            {
                call.addParameter("filter", m_arrayQName, ParameterMode.IN);
                beans = (IF1[]) call.invoke(new Object[] { ifId, category, filter });
            }

        return beans;
    }

    public IF1[] getBeansByDate(String ifId, Calendar[] dates)
        throws Exception
    {
        return getBeansByDate(ifId, dates, null);
    }

    public IF1[] getBeansByDate(String ifId, Calendar[] dates, String[] filter)
        throws Exception
    {
        IF1[] beans = null;

        if (ifId == null)
            throw new Exception("invalid ifId");
        if (dates == null)
            throw new Exception("invalid dates");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getBeansByDate"));
            call.setReturnType(m_arrayQName);
            call.addParameter("ifId", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            call.addParameter("dates", m_arrayQName, ParameterMode.IN);
            if (filter == null)
                beans = (IF1[]) call.invoke(new Object[] { ifId, dates });
            else
            {
                call.addParameter("filter", m_arrayQName, ParameterMode.IN);
                beans = (IF1[]) call.invoke(new Object[] { ifId, dates, filter });
            }

        return beans;
    }

    public IF1[] getBeansByExpression(String ifId, int expType, String expression)
        throws Exception
    {
        return getBeansByExpression(ifId, expType, expression, null);
    }

    public IF1[] getBeansByExpression(String ifId, int expType, String expression, String[] filter)
        throws Exception
    {
        IF1[] beans = null;

        if (ifId == null)
            throw new Exception("invalid ifId");
        if (expression == null)
            throw new Exception("invalid expression");

            Call call = getCall();
            call.setTargetEndpointAddress(m_url);
            call.setOperationName(new QName(m_service, "getBeansByExpression"));
            call.setReturnType(m_arrayQName);
            call.addParameter("ifId", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            call.addParameter("expType", org.apache.axis.Constants.XSD_INT, ParameterMode.IN);
            call.addParameter("expression", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
            if (filter == null)
                beans = (IF1[]) call.invoke(new Object[] { ifId, new Integer(expType), expression });
            else
            {
                call.addParameter("filter", m_arrayQName, ParameterMode.IN);
                beans = (IF1[]) call.invoke(new Object[] { ifId, new Integer(expType), expression, filter });
            }

        return beans;
    }

    private Call getCall()
        throws Exception
    {
        Call call = null;
            Service service = new Service();
            call = (Call) service.createCall();

            call.registerTypeMapping(Bean.class, m_beanQName,
                new BeanSerializerFactory(Bean.class, m_beanQName),
                new BeanDeserializerFactory(Bean.class, m_beanQName));
        return call;
    }
}

