package test.wsdl.interop3;

import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.axis.utils.ClassUtils;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class Interop3TestCase {

    public static void usage() {
        System.out.println("java test.wsdl.interop3.Interop3TestCase <URL property file>");
    } // usage

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                usage();
                System.exit(0);
            }
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));
            Iterator it = props.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                URL value = new URL((String) props.get(key));
                try {
                    Class test = ClassUtils.forName(key);
                    Field urlField = test.getField("url");
                    urlField.set(null, value);
                    TestRunner.run(new TestSuite(test));                    
                }
                catch (Throwable t) {
                    System.err.println("Failure running " + key);
                    t.printStackTrace();
                }
            }
        }
        catch (Throwable t) {
        }
    } // main
} // class Interop3TestCase

