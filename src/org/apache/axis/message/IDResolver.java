package org.apache.axis.message;

/** 
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

public interface IDResolver
{
    public Object getReferencedObject(String href);
}
