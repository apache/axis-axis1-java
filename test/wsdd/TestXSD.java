package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 *  Make sure that WSDD.xsd is up-to-date
 */
public class TestXSD extends TestCase {

    static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA =
            "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public TestXSD(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestXSD.class);
    }

    protected void setUp() throws Exception {
        String schemaSource = "wsdd/WSDD.xsd";

        // Set namespaceAware to true to get a DOM Level 2 tree with nodes
        // containing namesapce information.  This is necessary because the
        // default value from JAXP 1.0 was defined to be false.
        dbf.setNamespaceAware(true);

        dbf.setValidating(true);
        dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        // Specify other factory configuration settings
        File f = new File(schemaSource);
        dbf.setAttribute(JAXP_SCHEMA_SOURCE, f.toURL().toExternalForm());
    }

    public static void main(String[] args) throws Exception {
        TestXSD tester = new TestXSD("TestXSD");
        tester.setUp();
        tester.testWSDD();
    }

    public void testWSDD() throws Exception {
        File f = new File(".");
        recurse(f);
    }

    private void recurse(File f) throws Exception {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                recurse(files[i]);
            }
        } else if (f.getName().endsWith(".wsdd")) {
            checkValidity(f);
        }
    }

    private void checkValidity(File f) throws Exception {
        System.out.println("========== Checking " + f.getAbsolutePath() + "=================");
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(f);
        assertTrue(doc != null);
    }
}
