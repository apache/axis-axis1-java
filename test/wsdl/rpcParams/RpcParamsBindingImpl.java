/**
 * RpcParamsBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.rpcParams;

public class RpcParamsBindingImpl implements RpcParamsTest {
    /**
     * Just return a struct with the first and second fields
     * filled out with the arguments.
     */ 
    public EchoStruct echo(String first, String second) {
        final EchoStruct result = new EchoStruct();
        result.setFirst(first);
        result.setSecond(second);
        return result;
    }
}
