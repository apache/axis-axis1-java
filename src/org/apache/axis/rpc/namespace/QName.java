// temporary Service interface definition - replace with JAX-RPC
// when it is ready.
// for now - stolen from the Axis version of QName

// package javax.xml.rpc ;
package org.apache.axis.rpc.namespace ;

public class QName {
    private String namespaceURI ;
    private String localPart ;

    public QName() {};
    public QName(String namespaceURI, String localPart) {
        setNamespaceURI(namespaceURI);
        setLocalPart(localPart);
    }
    
    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI ;
    };

    public String getNamespaceURI() { 
        return( namespaceURI );
    };

    public void setLocalPart(String localPart) {
        this.localPart = localPart ;
    };

    public String getLocalPart() {
        return( localPart );
    };
    
    public String toString() {
        if (namespaceURI == null) {
            return localPart;
        } else {
            return namespaceURI + ":" + localPart;
        }
    };
    
    public boolean equals(Object p1) {
        if (!(p1 instanceof QName)) return false;

        if (namespaceURI == null) {
            if (((QName)p1).namespaceURI != null) return false;
        } else {
            if (!namespaceURI.equals(((QName)p1).namespaceURI)) return false;
        }

        return localPart.equals(((QName)p1).localPart);
    };

    public int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }

    // temporary!!
    public QName(String qName, org.w3c.dom.Element element) {
        if (qName != null){
            int i = qName.indexOf(":");
            if (i < 0) {
                setLocalPart(qName);
                setNamespaceURI(null);
            } else {
                String prefix = qName.substring(0,i);
                String local = qName.substring(i+1);
                setLocalPart(local);
                setNamespaceURI(org.apache.axis.utils.XMLUtils.getNamespace(prefix, element));
            }
        }
    }
}
