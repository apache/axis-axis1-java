package test.wsdl.gateway;

/**
 * To recreate the Bug 14033 - bean property multi-dimensional arrays don't deserialize
 * problem, just comment out the getValues and setValues methods
 */ 
public class MyClass {
	public String[][] Values;

	public String[][] getValues() {
		return Values;
	}
	public void setValues(String[][] values) {
		Values=values;
	}
}
