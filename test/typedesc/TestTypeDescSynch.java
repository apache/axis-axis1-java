package test.typedesc;

import junit.framework.TestCase;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.TypeDesc;

import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;

import javax.xml.namespace.QName;

/**
 * Confirm TypeDesc bugs are indeed fixed.
 * 
 * See http://nagoya.apache.org/bugzilla/show_bug.cgi?id=22819 for details
 * 
 * @author gawor@mcs.anl.gov
 */ 
public class TestTypeDescSynch extends TestCase { 
    
    public TestTypeDescSynch(String name) {
        super(name);
    }
    
    public TestTypeDescSynch() {
        super("Test TypeDesc Synch");
    }
    
    // with won't fail
    public void testFields() throws Exception {
        TypeDesc desc= TypeDesc.getTypeDescForClass(ComplexBean2.class);
        for (int i=0; i < 10; i++) {
            desc.getFields(true);
            
            // Check to make sure we keep to the constant 5 fields (this
            // used to grow each time we called getFields(true)).
            assertEquals(5, desc.getFields().length);
        }
    }
    
    // this should fail
    public void testFields2() throws Exception {
        Service service = new Service();
        Call call  = (Call)service.createCall();
        
        call.registerTypeMapping(ComplexBean3.class, 
                                 new QName("foo2", "bar2"),
                                 BeanSerializerFactory.class,
                                 BeanDeserializerFactory.class,
                                 false);
        
        TypeDesc desc= TypeDesc.getTypeDescForClass(ComplexBean3.class);
        assertEquals(4, desc.getFields().length);
    }
    
    // this might fail
    public void testSynch() throws Exception {
        
        int threads = 30;
        
        ThreadTest[] thread = new ThreadTest[threads];
        for (int i=0;i<threads;i++) {
            thread[i] = new ThreadTest();
            thread[i].start();
        }
        
        Throwable ex = null;
        
        for (int i=0;i<threads;i++) {
            thread[i].join();
            if (ex == null && thread[i].getException() != null) {
                ex = thread[i].getException();
            }
        }
        
        // either assertion will fail or ArrayIndexOutOfBoundException will
        // be raised (or it might be ok)
        
        TypeDesc desc= TypeDesc.getTypeDescForClass(ComplexBean.class);
        
        assertEquals(6, desc.getFields().length);
        
        if (ex != null) {
            fail(ex.getMessage());
        }
    }
    
    class ThreadTest extends Thread {
        
        Throwable ex = null;
        
        public Throwable getException() {
            return this.ex;
        }
        
        public void run() {
            
            try {
                
                /*
                Service service = new Service();
                Call call  = (Call)service.createCall();
                
                call.registerTypeMapping(ComplexBean.class, 
                new QName("foo", "bar"),
                BeanSerializerFactory.class,
                BeanDeserializerFactory.class,
                false);
                */
                
                // this - demonstrates the same problem as above
                // just can check the ArrayIndexOutOfBoundsException
                BeanSerializerFactory f =
                        new BeanSerializerFactory(ComplexBean.class,
                                                  new QName("foo", "bar"));
            } catch (Exception e) {
                ex = e;
            }
            
            
        }
    }
    
    public static void main(String args[])
    {
        try {
            TestTypeDescSynch tester = new TestTypeDescSynch("TypeDesc Test");
            tester.testSynch();
            tester.testFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
