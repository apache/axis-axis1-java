package test.faults;

public class BeanFault2 extends Exception {
    public BeanFault2(String s)
    {
        super(s);
        message = s;
    }

    public String getMessage()
    {
        return message;
    }

    private String message;
}
