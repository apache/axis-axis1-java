package org.apache.axis.message;

/** A <code>SimpleHandlerFactory</code>
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */
import java.util.*;
import java.lang.reflect.*;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.utils.QName;
import org.xml.sax.*;

public class SimpleHandlerFactory implements HandlerFactory
{
    HashMap map = new HashMap();
    SOAPHandler defaultHandler;
    
    public void addHandlerForQName(QName qName, Class cls)
    {
        map.put(qName, cls);
    }
    
    public void addDefaultHandler(SOAPHandler handler)
    {
        defaultHandler = handler;
    }
    
    public void removeDefaultHandler()
    {
        defaultHandler = null;
    }
    
    public SOAPHandler getHandler(String namespace,
                                  String localName,
                                  Attributes attributes,
                                  DeserializationContext context)
        throws SAXException
    {
        SOAPHandler handler = null;
        QName qName = new QName(namespace, localName);
        Class cls = (Class)map.get(qName);

        if (cls != null) {
            try {
                handler = (SOAPHandler)cls.newInstance();
            } catch (Exception e) {
                throw new SAXException("Coudldn't create class " + cls.getName());
            }
        }
        
        if (handler == null)
            handler = defaultHandler;
        
        if (handler == null)
            throw new SAXException("No handler for QName " + qName);
        return handler;
    }
}
