package test.inheritance;

public class Parent {
    public static final String HELLO_MSG = "Hello from the Parent class!";
    public static final String OVERLOAD_MSG = "The Parent got ";

    public String inherited()
    {
        return HELLO_MSG;
    }

    public String overloaded(String param)
    {
        return OVERLOAD_MSG + param;
    }
}
