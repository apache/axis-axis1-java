/**
 * AttrGroupServiceTestCase.java
 */

package test.wsdl.attrGroup;

public class AttrGroupServiceTestCase extends junit.framework.TestCase {
    public AttrGroupServiceTestCase(java.lang.String name) {
        super(name);
    }
    public void test1AttrGroupService() throws Exception {
        // make sure WSDL2Java generated the right stuff
        
        // we don't really need to test sending a request
        //   and getting a response, since many other tests comprehend that
        // all we need to do is make sure WSDL2Java generated
        //   the attributes needed from the attributeGroup definitions
        // so, basically, if this compiles, we are good to go
        // but running it won't hurt anything

        test.wsdl.attrGroup._Record1 rec1 = new test.wsdl.attrGroup._Record1();

        // an element defined within Record1
        rec1.setElem1(1);
        assertTrue("elem1 should be 1, but is "+rec1.getElem1(), rec1.getElem1()==1);

        // an attribute defined in an attributeGroup
        rec1.setAttr1(2);
        assertTrue("attr1 should be 2, but is "+rec1.getAttr1(), rec1.getAttr1()==2);

        // an attribute defined in Record1 itself
        rec1.setAttr3("x");
        assertTrue("attr3 should be x, but is "+rec1.getAttr3(), rec1.getAttr3().equals("x"));
        
        // two attributes from a known 1.1 soap encoding schema
        rec1.setId(new org.apache.axis.types.Id("theId"));
        rec1.setHref(new org.apache.axis.types.URI("a", "b"));

        test.wsdl.attrGroup._Record2 rec2 = new test.wsdl.attrGroup._Record2();
        
        // an element defined within Record2
        rec2.setElem2("2");

        // an attribute defined in a referenced attributeGroup within an attributeGroup
        rec2.setAttr1(2);
        
        // an attribute defined in an attributeGroup that has another nested reference
        rec2.setAttr2(1.2);
        
        // an attribute from a known 1.2 soap encoding schema
        rec2.setId(new org.apache.axis.types.Id("theId"));
    }

    /*
    public static void main(String[] args)
    {
        AttrGroupServiceTestCase test = new AttrGroupServiceTestCase("x");
        try {
            test.test1AttrGroupService();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    */
}
