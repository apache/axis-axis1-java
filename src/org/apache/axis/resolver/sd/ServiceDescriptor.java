package org.apache.axis.resolver.sd;

import org.apache.axis.Handler;
import org.apache.axis.resolver.Resolver;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.sd.schema.Fault;
import org.apache.axis.resolver.sd.schema.HandlerList;
import org.apache.axis.resolver.sd.schema.Provider;
import org.apache.axis.resolver.sd.schema.SDConstants;
import javax.rpc.namespace.QName;
import org.apache.axis.utils.NSStack;
import org.apache.axis.resolver.sd.schema.SDElement;
import org.apache.axis.resolver.sd.schema.Service;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.NSStack;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * A simplified deployment descriptor format.  It uses SAX to read
 * in the format and to create a very simple object model for the 
 * service.
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public class ServiceDescriptor {

    protected Service service = null;
    protected Resolver resolver = null;

    /**
     * Create the service descriptor from a resolver context
     */    
    public ServiceDescriptor(ResolverContext context) {
        setResolver(context.getResolver());
        parse(context.getKey());
    }
    
    /**
     * Create the service descriptor from an input stream
     */
    public ServiceDescriptor(InputStream in, Resolver resolver) {
        setResolver(resolver);
        parse(in);
    }
    
    /**
     * Create the service descriptor from a URL/filepath
     */
    public ServiceDescriptor(String in, Resolver resolver) {
        setResolver(resolver);
        parse(in);
    }
    
    /**
     * Return the top level resolver
     */
    public Resolver getResolver() {
        return this.resolver;
    }
    
    /**
     * Set the top level resolver
     */
    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }
    
    /**
     * Return a new instance of this service (this will be a
     * SimpleTargetedChain)
     */
    public Handler newInstance() {
        if (service == null) return null;
        return service.newInstance();
    }
    
    /**
     * Return the Service object
     */
    public Service getService() {
        return this.service;
    }
    
    /**
     * Set the service object
     */
    public void setService(Service service) {
        this.service = service;
    }
    
    /**
     * Parse the service descriptor
     */
    private void parse(InputStream in) {
        try {
            SAXParser parser = org.apache.axis.utils.XMLUtils.getSAXParser();
            parser.parse(in, new SDHandler());
        } catch (Exception e) {}
    }
    
    /**
     * Parse the service descriptor
     */
    private void parse(String in) {
        try {
            SAXParser parser = org.apache.axis.utils.XMLUtils.getSAXParser();
            parser.parse(in, new SDHandler());
        } catch (Exception e) {}
    }
    
    /**
     * SAX handler that deals with parsing the service descriptor
     */
    private class SDHandler extends DefaultHandler {
        
        private Stack stack = new Stack();
        private DefaultHandler handler = null;
        private int depth = 0;
        private NSStack namespaces = new NSStack();
        private ArrayList ns = null;
        
        public void startElement(String uri, String ln, String rn, org.xml.sax.Attributes attr) throws SAXException {
            if (ns == null) ns = new ArrayList();
            namespaces.push(ns);
            if (handler != null) {
                handler.startElement(uri, ln, rn, attr);
                depth++;
                return;
            }
            SDElement current = null;
            if (stack.size() > 0) {
                current = (SDElement)stack.peek();
            }
            if (SDConstants.SDNS.equals(uri)) {
                if ("service".equals(ln)) {
                    if (current != null) {
                        throw new SAXException("Unexpected service descriptor");
                    }
                    current = new Service();
                    stack.push(current);
                    service = (Service)current;
                }
                if ("request".equals(ln)) {
                    if (current == null)
                        throw new SAXException("Unexpected request descriptor");
                    if (!(current instanceof Service))
                        throw new SAXException("Unexpected request descriptor");
                    Service service = (Service)current;
                    current = new HandlerList();
                    service.setRequest((HandlerList)current);
                    stack.push(current);
                }
                if ("response".equals(ln)) {
                    if (current == null)
                        throw new SAXException("Unexpected response descriptor");
                    if (!(current instanceof Service))
                        throw new SAXException("Unexpected response descriptor");
                    Service service = (Service)current;
                    current = new HandlerList();
                    service.setResponse((HandlerList)current);
                    stack.push(current);
                }
                if ("fault".equals(ln)) {
                    if (current == null)
                        throw new SAXException("Unexpected fault descriptor");
                    if (!(current instanceof Service))
                        throw new SAXException("Unexpected fault descriptor");
                    Service service = (Service)current;
                    Fault fault = new Fault();
                    String qname = attr.getValue("faultCode");
                    if (qname != null) {
                        fault.setFaultCode(getQName(qname));
                    }
                    service.setFault(fault.getFaultCode(), fault);
                    current = fault;
                    stack.push(current);
                }
                if ("handler".equals(ln)) {
                    if (current == null) 
                        throw new SAXException("Unexpected handler descriptor");
                    if (!(current instanceof HandlerList))
                        throw new SAXException("Unexpected handler descriptor");
                    HandlerList handlerList = (HandlerList)current;
                    try {
                        String key = attr.getValue("key");
                        if (key != null) {
                            ResolverContext context = new ResolverContext(key);
                            Handler h = resolver.resolve(context);
                            if (h != null) {
                                handlerList.addHandler(h);
                            } else {
                                throw new SAXException("Cannot resolve handler");
                            }
                        } else {
                            throw new SAXException("Handler key not specified");
                        }
                    } catch (Exception e) {
                        throw new SAXException("Cannot resolve handler");
                    }
                }
                if ("chain".equals(ln)) {
                    if (current == null)
                        throw new SAXException("Unexpected chain descriptor");
                    if (!(current instanceof HandlerList))
                        throw new SAXException("Unexpected chain descriptor");
                    HandlerList handlerList = (HandlerList)current;
                    current = new HandlerList();
                    stack.push(current);
                    handlerList.addHandlerList((HandlerList)current);
                    try {
                        String key = attr.getValue("key");
                        if (key != null) {
                            ResolverContext context = new ResolverContext(key);
                            Handler h = resolver.resolve(context);
                            if (h != null) {
                                handlerList.addHandler(h);
                            } else {
                                throw new SAXException("Cannot resolve chain");
                            }
                        }
                    } catch (Exception e) {
                        throw new SAXException("Cannot resolve chain");
                    }
                }
            } else {
                if (current == null)
                    throw new SAXException("Unexpected unknown element");
                if (!(current instanceof Service))
                    throw new SAXException("Unexpected unknown element");
                QName type = new QName(uri, ln);
                Provider provider = Provider.newProvider(type);
                provider.setResolver(resolver);
                if (provider != null) {
                    service.setProvider(provider);
                    handler = provider.getDefaultHandler();
                    handler.startElement(uri, ln, rn, attr);
                } else {
                    throw new SAXException("Unexpected unknown element");
                }
            }
        }

        public void endElement(String uri, String ln, String rn) throws SAXException {
            if (SDConstants.SDNS.equals(uri)) {
                if ("service".equals(ln)  ||
                    "request".equals(ln)  ||
                    "response".equals(ln) ||
                    "fault".equals(ln)    ||
                    "chain".equals(ln)) {
                    stack.pop();
                }
            } else {
                if (handler != null && depth == 0) {
                    handler = null;
                    depth = 0;
                }
                if (handler != null && depth > 0) {
                    depth--;
                }
            }
            namespaces.pop();
            ns = null;
        }
        
        public void characters(char[] arr, int start, int len) throws SAXException {
            if (handler != null) 
                handler.characters(arr, start,len);
        }
        
        public void startPrefixMapping(String px, String uri) throws SAXException {
            if (ns == null) ns = new ArrayList();
            ns.add(new Mapping(uri, px));
            if (handler != null) {
                handler.startPrefixMapping(px,uri);
            }
        }
        
        public void endPrefixMapping(String px) throws SAXException {
            if (handler != null) {
                handler.endPrefixMapping(px);
            }
        }
        
        private QName getQName(String qname) {
            try {
                String prefix = qname.substring(0, qname.indexOf(":"));
                String local = qname.substring(qname.lastIndexOf(":")+1);
                return new QName(namespaces.getNamespaceURI(prefix), local);
            } catch (Exception e) {
                return new QName(null, qname);
            }
        }
        
    }
}
