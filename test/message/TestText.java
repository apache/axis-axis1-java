package test.message;

import junit.framework.TestCase;

import org.apache.axis.message.Text;

/**
 * Test case for {@link Text}.
 *
 * @author Ian P. Springer
 */
public class TestText extends TestCase
{

    private static final String VANILLA = "vanilla";
    private static final String CHOCOLATE = "chocolate";
    private static final String NULL = null;

    private Text vanillaText;
    private Text chocolateText;
    private Text nullText;
    private Text vanillaText2;

    protected void setUp() throws Exception
    {
        vanillaText = new org.apache.axis.message.Text( VANILLA );
        vanillaText2 = new org.apache.axis.message.Text( VANILLA );
        chocolateText = new org.apache.axis.message.Text( CHOCOLATE );
        nullText = new org.apache.axis.message.Text( NULL );
    }

    /**
     * Test for {@link org.apache.axis.message.Text#toString()}.
     *
     * @throws Exception on error
     */
    public void testToString() throws Exception
    {
        assertEquals( VANILLA, vanillaText.toString() );
        assertEquals( NULL, nullText.toString() );
    }

    /**
     * Test for {@link org.apache.axis.message.Text#hashCode()}.
     *
     * @throws Exception on error
     */
    public void testHashCode() throws Exception
    {
        assertEquals( VANILLA.hashCode(), vanillaText.hashCode() );
        assertEquals( 0, nullText.hashCode() );
    }

    /**
     * Test for {@link org.apache.axis.message.Text#equals(Object)}.
     *
     * @throws Exception on error
     */
    public void testEquals() throws Exception
    {
        assertEquals( vanillaText, vanillaText2 );
        assertEquals( vanillaText2, vanillaText );
        assertTrue( !vanillaText.equals( chocolateText ) );
        assertTrue( !chocolateText.equals( vanillaText ) );
        assertTrue( !vanillaText.equals( null ) );
        assertTrue( !vanillaText.equals( VANILLA ) );
    }

}
