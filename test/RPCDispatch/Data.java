package test.RPCDispatch;
 
/**
 * Test structure used by the RPCDispatch test
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class Data {

    // items of the structure.  Defined as Objects so as to permit nulls
    private int field1;
    private String field2;
    private int field3;

    /**
     * null constructor
     */
    public Data() {}

    /**
     * convenience constructor that sets all of the fields
     */
    public Data(int field1, String field2, int field3) {
        this.field1=field1;
        this.field2=field2;
        this.field3=field3;
    }

    /**
     * bean getter for field1
     */
    public int getField1() {
        return field1;
    }

    /**
     * bean setter for field1
     */
    public void setField1 (int field1) {
        this.field1=field1;
    }

    /**
     * bean getter for field2
     */
    public String getField2() {
        return field2;
    }

    /**
     * bean setter for field2
     */
    public void setField2 (String field2) {
        this.field2=field2;
    }

    /**
     * bean getter for field3
     */
    public int getField3() {
        return field3;
    }

    /**
     * bean setter for field3
     */
    public void setField3 (int field3) {
        this.field3=field3;
    }

    /**
     * Equality comparison.  
     */
    public boolean equals(Object object) {
        if (!(object instanceof Data)) return false;

        Data that= (Data) object;

        if (this.field1 != that.field1) return false;
        if (this.field3 != that.field3) return false;

        if (this.field2 == null) {
            if (that.field2 != null) return false;
        } else {
            if (!this.field2.equals(that.field2)) return false;
        }

        return true;
    };
}
