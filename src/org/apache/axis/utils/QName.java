package org.apache.axis.utils ;

public class QName {
  /**
   * Template code for QName.
   */
  private String namespaceURI ;
  private String localPart ;

  public QName() {};
   
  public void setNamespaceURI(String nsu) {
    namespaceURI = nsu ;
  };

  public String getNamespaceURI() { 
    return( namespaceURI );
  };

  public void setLocalPart(String lp) {
    localPart = lp ;
  };

  public String getLocalPart() {
    return( localPart );
  };

};
