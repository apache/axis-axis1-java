package test.saaj;

/**
 * SAAJ PackageTests tests
 */
public class PackageTests extends junit.framework.TestCase {
    public PackageTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() throws Exception {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        // Attachments service test.
        try{
          if( null != org.apache.axis.utils.ClassUtils.forName("javax.activation.DataHandler") &&
              null != org.apache.axis.utils.ClassUtils.forName("javax.mail.internet.MimeMultipart")){
                suite.addTestSuite(test.saaj.TestAttachment.class);
                suite.addTestSuite(test.saaj.TestAttachmentSerialization.class);
          }
        }catch( Throwable t){;}
        suite.addTestSuite(test.saaj.TestEnvelope.class);
        suite.addTestSuite(test.saaj.TestSOAPFaultDetail.class);
        suite.addTestSuite(test.saaj.TestHeaders.class);
        suite.addTestSuite(test.saaj.TestPrefixes.class);
        suite.addTestSuite(test.saaj.TestSOAPFaults.class);
        return suite;
    }
}

