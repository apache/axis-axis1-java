package test.utils;

import junit.framework.*;
import org.apache.axis.utils.Options;
import java.net.MalformedURLException;

public class TestOptions extends TestCase
{
    public TestOptions (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestOptions.class);
    }

    protected void setup() {
    }

    public void testOptionsConstructor() throws MalformedURLException
    {
        String[] fake_args = { "-h 127.0.0.1","-p 8081","-u scott","-w tiger" };
        Options ops = new Options(fake_args); 
    }   


    /* 
    * Note - by Java conventions, the isFlagSet and isValueSet should either
    * return a boolean value, or be given more descriptive names.  I might
    * suggest getFlagFrequency and getArgValue or something.
    */
    public void testIsFlagSet() throws MalformedURLException 
    {
        String[] fake_args = { "-w tiger" };
        Options ops = new Options(fake_args); 
        String result = ops.isValueSet('w');
        assertEquals("Result was: " + result + ", not tiger", "tiger", result);
    }   

}
