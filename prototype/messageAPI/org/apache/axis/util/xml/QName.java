package org.apache.axis.util.xml;

import org.w3c.dom.Node;

public class QName implements java.io.Serializable
{
  private String namespaceURI;
  private String localPart;

  public QName() {}
  public QName(Node node) throws IllegalArgumentException {
    String namespaceURI = node.getNamespaceURI();
    if (namespaceURI == null) {throw new IllegalArgumentException("Can't create QName: NamespaceURI must not be null.");}
    String localPart = node.getLocalName();
    if (localPart == null) {throw new IllegalArgumentException("Can't create QName: LocalName must not be null.");}
    setNamespaceURI(namespaceURI);
    setLocalPart(localPart);
  }

  public QName(String namespaceURI, String localPart) {
    setNamespaceURI(namespaceURI);
    setLocalPart(localPart);
  }
  
  public void setNamespaceURI(String namespaceURI) {
    this.namespaceURI = (namespaceURI == null ? "" : namespaceURI).intern();
  }

  public String getNamespaceURI() {
    return namespaceURI;
  }

  public void setLocalPart(String localPart) {
    this.localPart = localPart.intern();
  }

  public String getLocalPart() {
    return localPart;
  }

  public int hashCode() {
    String hash1 = namespaceURI.hashCode() + "";
    String hash2 = localPart.hashCode() + "";
    String hash3 = hash1 + '_' + hash2;
    return hash3.hashCode();
  }

  public boolean equals(Object obj) {
    return (obj != null
            && namespaceURI == ((QName)obj).getNamespaceURI()
            && localPart == ((QName)obj).getLocalPart());
  }

  public boolean matches(Node node) {
    try {return (node != null && this.equals(new QName(node)));}
    catch (IllegalArgumentException e) {return false;}
  }

  public String toString() {return namespaceURI + ':' + localPart;}
}
