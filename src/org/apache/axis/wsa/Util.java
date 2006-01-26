package org.apache.axis.wsa ;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Util{
  private Util(){};

   /**
    * Performs a utility function.
    * Returns text contained in child elements of the
    * passed in element.
    *
    * @param el     Element
    * @return java.lang.String
    */
   static String getText(Node el) {
      NodeList nl = el.getChildNodes();
      String result = "";
      for (int i = 0; i < nl.getLength(); i++) {
         if (nl.item(i).getNodeType()==Element.TEXT_NODE) {
            result += nl.item(i).getNodeValue();
         }
      }
      // Trim result, to remove whitespace.
      return result.trim();
   }

     /* Is "base" like "cmpStr" - in other words "cmpStr" is the one  */
  /* that can contain wildcards  *, ? and \ for esc                */
  static public boolean isLike(String base, String cmpStr) {
    int     bi = 0 , ci = 0 ;
    boolean skip = false ;
    char bChar, cChar ;
    char nextCChar ;

    if ( base == null ) base = "" ;
    if ( cmpStr == null ) cmpStr = "" ;

    for ( ; ; ) {
      bChar = (bi == base.length() ? '\0' : base.charAt(bi));
      cChar = (ci == cmpStr.length() ? '\0' : cmpStr.charAt(ci));

      if ( cChar == '\\' ) {
        skip = true ;
        ci++ ;
        cChar = (ci == cmpStr.length() ? '\0' : cmpStr.charAt(ci));
      }

      if ( !skip && cChar == '*' ) {
        while( cChar == '*' ) {
          ci++ ;
          cChar = (ci == cmpStr.length() ? '\0' : cmpStr.charAt(ci));
        }
        for ( ; bChar != '\0' ;
                bi++,bChar = (bi==base.length() ? '\0' : base.charAt(bi)) ) {
          nextCChar = (ci+1 >= cmpStr.length() ? '\0' : cmpStr.charAt(ci+1));
          if ( cChar != '?' &&
               ( ( cChar != '\\' && bChar != cChar) ||
                 ( cChar == '\\' && bChar != nextCChar) ))
            continue ;
            if ( isLike(base.substring(bi), cmpStr.substring(ci)) )
              return( true );
        }
        nextCChar = (ci+1 >= cmpStr.length() ? '\0' : cmpStr.charAt(ci+1));
        return( cChar == '\0' || (cChar == '\\' && nextCChar != '\0') );
      }
      if ( bChar != cChar ) {
        if ( cChar != '?' || skip ) return( false );
        if ( bChar == '\0' ) return( false );
      }
      if ( bChar == '\0' ) return( true );
      bi++ ;
      ci++ ;
      skip = false ;
    }
  }
}
