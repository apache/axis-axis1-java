package org.apache.axis;

/** If a Java class which acts as the target for an Axis service
 * implements this interface, it may convey metadata about its
 * configuration to the Axis engine.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public interface AxisServiceConfig
{
  /** Get the allowed method names.
   * 
   * (The only method right now)
   * 
   * @return a space-delimited list of method names which may be called
   *         via SOAP.
   */
  public String getMethods();
}
