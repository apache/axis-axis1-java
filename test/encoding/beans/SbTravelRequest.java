package test.encoding.beans;

public class SbTravelRequest implements java.io.Serializable
{
    public SbTravelRequest(){}
    public String                requestOr;
    public String                homeCountry;

    public String                departureLocation;
    public String                destinationLocation;

    public java.util.GregorianCalendar     startDate;
    public java.util.GregorianCalendar     endDate;

    public String                searchTypes;
    public String                searchParams;
    public String                searchHints;

    public SbSupplier[]          supPliers;
}
