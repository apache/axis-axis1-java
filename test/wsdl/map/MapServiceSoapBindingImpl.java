/**
 * MapServiceSoapBindingImpl.java
 *
 * Implementation of the Map service used after we convert from Java to WSDL
 * then back to Java again.  Should be identical to MapService.java
 */

package test.wsdl.map;

import java.util.Map;
import java.util.HashMap;

public class MapServiceSoapBindingImpl implements test.wsdl.map.MapService{
    /**
     * echo the input Map
     */
    public HashMap echoMap(HashMap in) {
	    return in;
    }
}
