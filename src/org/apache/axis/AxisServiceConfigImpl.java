/*
 * Created by IntelliJ IDEA.
 * User: Glen
 * Date: Feb 13, 2002
 * Time: 3:43:06 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.apache.axis;

public class AxisServiceConfigImpl implements AxisServiceConfig {
    private String methods;

    /**
     * Set the allowed method names.
     */
    public void setAllowedMethods(String methods)
    {
        this.methods = methods;
    }

    /** Get the allowed method names.
     *
     * @return a space-delimited list of method names which may be called
     *         via SOAP.
     */
    public String getAllowedMethods() {
        return methods;
    }
}
