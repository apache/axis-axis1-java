package test.wsdl.gateway.org;

public interface Gateway {
	public String test1(MyClass myClass);
	public MyClass test2();
	public String[][] test3();
	public String test4(String[][] param);
}