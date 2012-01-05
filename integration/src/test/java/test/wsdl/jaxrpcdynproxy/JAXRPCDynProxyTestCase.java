package test.wsdl.jaxrpcdynproxy;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import java.net.URL;
import test.wsdl.jaxrpcdynproxy.holders.AddressBeanHolder;
import junit.framework.TestCase;
import java.net.URL;
import java.lang.reflect.Proxy;

import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.client.AxisClientProxy;

public class JAXRPCDynProxyTestCase extends TestCase {

  /**
   * Default constructor for use as service
   */
  public JAXRPCDynProxyTestCase() {
      super("JAXRPCDynProxyTest");
  }

  public JAXRPCDynProxyTestCase(String name) {
      super(name);
  }

  public void testInOut() throws Exception {
      URL urlWsdl = new URL("http://localhost:8080/axis/services/AddressInOut?wsdl");
      String nameSpaceUri = "http://jaxrpcdynproxy.wsdl.test";
      String serviceName = "AddressInOut";
      String portName = "AddressInOut";
      ServiceFactory serviceFactory = ServiceFactory.newInstance();
      Service service = serviceFactory.createService(urlWsdl, new
              QName(nameSpaceUri, serviceName));
      AddressService myProxy = (AddressService) service.getPort(new
              QName(nameSpaceUri, portName), AddressService.class);
      AddressBean addressBean = new AddressBean();
      addressBean.setStreet("55, rue des Lilas");
      AddressBeanHolder addressBeanHolder = new AddressBeanHolder(addressBean);
        
      QName qName = new QName("http://jaxrpcdynproxy.wsdl.test", "AddressBean");
      AxisClientProxy clientProxy = (AxisClientProxy) Proxy.getInvocationHandler(myProxy);      
      clientProxy.getCall().registerTypeMapping(AddressBean.class,
                                                qName,
                                                BeanSerializerFactory.class,
                                                BeanDeserializerFactory.class,
                                                false);
      myProxy.updateAddress(addressBeanHolder, 75005);
      addressBean = addressBeanHolder.value;
      assertEquals("New postcode is not the expected 75005", addressBean.getPostcode(), 75005);
  }

  public static void main(String[] args) throws Exception {
      JAXRPCDynProxyTestCase test = new JAXRPCDynProxyTestCase("test");
      test.testInOut();
  }
}
