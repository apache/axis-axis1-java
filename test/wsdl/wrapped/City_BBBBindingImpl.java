/**
 * CityBBBBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.wrapped;



public class City_BBBBindingImpl implements City_BBBPortType {
    public static final String OID_STRING = "Attraction@cityCF::1028:1028";

    public Attraction getAttraction(String attname) throws java.rmi.RemoteException {
        // The only acceptable attNames are Christmas and Xmas
        // Apparently no one ones to go to New Orleans for Mardi Gras. :-)
        if (attname == null ||
            !(attname.equals("Christmas") ||
              attname.equals("Xmas"))) {
            return null;
        }
        Attraction attraction = new Attraction();
        attraction.set_OID(OID_STRING);
        attraction.setFacts("New Orleans at Christmastime is a city with the best food in the world, the best music" +
                            " in the world, international shopping, the French Quarter -- America&apos;s most " +
                            " romantic neighborhood, and the friendliest, most big-hearted people you&apos;d ever " +
                            " want to share a rousing celebration with. New Orleans is a natural place for Christmas " +
                            " merry making, and if it is not, then, to quote a New Orleans R&amp;B classic, " +
                            " &apos;grits ain&apos;t groceries, eggs ain&apos;t poultry, and Mona Lisa was a " +
                            " man.&apos; Indeed, Christmas is an especially great time to come because New Orleans " +
                            " hotels have attractive Papa  Noel rates. Throughout the month of December, New Orleans " +
                            " will be decorated like never before, which is saying a lot for a town that loves " +
                            " exhibitionism. From the quaint, light-entwined cast iron lamp posts to the historic " +
                            " houses and museums bright in their period holiday garb, the French Quarter will sparkle "+
                            " like an antique toy store. The twinkling lights and the cheery voices of carolers will " +
                            " have you dancing and prancing through Jackson Square, in the jingle bell air. Riverwalk "+
                            " shopping center is a leader in the celebrations, launching brass band parades twice " +
                            " daily that include the grand ol&apos; rogue, Papa Noel, and putting on a light show " +
                            " every evening in Spanish Plaza. Woldenberg Park, on the riverfront, will decorate the " +
                            " night with commissioned light sculptures by local artists. First National Bank of " +
                            " Commerce is sponsoring free nightly concerts of soul-stirring gospel music in the " +
                            " stately St. Louis Cathedral. And New Orleans restaurants have revived the tradition of "+
                            " &apos;Reveillon&apos; dinners, a name that comes from the French word for " +
                            " &apos;awakening&apos; because it was a meal that was eaten in the early morning " +
                            " immediately after Christmas Midnight Mass, in celebration of the good news, of course, " +
                            " but, just as happily, in celebration of the end of the Advent fast. You, however, do " +
                            " not have to wait til midnight, nor do you have to observe the Advent fast. All you have"+
                            " to do is walk into a New Orleans restaurant and order food so sublime, it is in itself" +
                            " a proof for the existence of heaven. And as every New Orleanian knows, Heaven is " +
                            " presided over by a French-Italian-Creole chef with a gumbo-pot belly and a laugh " +
                            " that fills that human heart with gladness. Merry Christmas to ya, New Orleans style.");
        return attraction;
    }

    public Attraction[] getAttractions(String[] attnames) throws java.rmi.RemoteException {
        Attraction[] attractions = null;
        if (attnames != null) {
            attractions = new Attraction[attnames.length]; 
            for (int i=0; i < attnames.length; i++) {
                attractions[i] = getAttraction(attnames[i]);
            }
        }
        return attractions;
    }

    public Attraction[] getAttractions2(Query[] attnames) throws java.rmi.RemoteException {
        Attraction[] attractions = null;
        if (attnames != null) {
            attractions = new Attraction[attnames.length]; 
            for (int i=0; i < attnames.length; i++) {
                if (attnames[i] != null) {
                    attractions[i] = getAttraction(attnames[i].getValue());
                }
            }
        }
        return attractions;
    }
}
