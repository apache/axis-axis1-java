package test.inheritance;

public class Child extends Parent {
    public static final String HELLO_MSG = "Hello from the Child class";
    public static final String OVERLOAD_MSG = "The Child got ";

    public String normal()
    {
        return HELLO_MSG;
    }

    public String overloaded(int i)
    {
        return OVERLOAD_MSG + i;
    }
}
