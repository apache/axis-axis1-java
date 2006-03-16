package org.apache.axis.wsa;

import org.w3c.dom.Element;

  public class RelatesToProperty {
     String uri;
     String type;

     RelatesToProperty() {
        // default constructor
     }

     public RelatesToProperty(String uri, String type) {
        this.uri = uri;
        this.type = type;
     }

     static RelatesToProperty newInstance(Element el, String namespace) {
        String uri = Util.getText(el);
        String type = el.getAttributeNS(namespace, "RelationshipType");
        RelatesToProperty rp = new RelatesToProperty(uri, type);
        return rp;
     }

     public String getURI() { return uri ; }
     public String getType() { return type ; }

     public String toString() {
       return "Rel:[" + uri + ":" + type + "]" ;
     }
  }
